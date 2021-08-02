-- Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
-- SPDX-License-Identifier: Apache-2.0

---------------------------------------------------------------------------------------------------
-- V100: Append-only schema
--
-- This is a major redesign of the index database schema. Updates from the ReadService are
-- now written into the append-only table participant_events, and the set of active contracts is
-- reconstructed from the log of create and archive events.
---------------------------------------------------------------------------------------------------

CREATE TABLE parties
(
    -- The unique identifier of the party
    party         NVARCHAR2(1000) primary key not null,
    -- A human readable name of the party, might not be unique
    display_name  NVARCHAR2(1000),
    -- True iff the party was added explicitly through an API call
    explicit      NUMBER(1, 0)                not null,
    -- For implicitly added parties: the offset of the transaction that introduced the party
    -- For explicitly added parties: the ledger end at the time when the party was added
    ledger_offset VARCHAR2(4000),
    is_local      NUMBER(1, 0)                not null
);
CREATE INDEX parties_ledger_offset_idx ON parties(ledger_offset);

CREATE TABLE packages
(
    -- The unique identifier of the package (the hash of its content)
    package_id         VARCHAR2(4000) primary key not null,
    -- Packages are uploaded as DAR files (i.e., in groups)
    -- This field can be used to find out which packages were uploaded together
    upload_id          NVARCHAR2(1000)            not null,
    -- A human readable description of the package source
    source_description NVARCHAR2(1000),
    -- The size of the archive payload (i.e., the serialized DAML-LF package), in bytes
    package_size       NUMBER                     not null,
    -- The time when the package was added
    known_since        TIMESTAMP                  not null,
    -- The ledger end at the time when the package was added
    ledger_offset      VARCHAR2(4000)             not null,
    -- The DAML-LF archive, serialized using the protobuf message `daml_lf.Archive`.
    --  See also `daml-lf/archive/da/daml_lf.proto`.
    package            BLOB                       not null
);
CREATE INDEX packages_ledger_offset_idx ON packages(ledger_offset);



CREATE TABLE configuration_entries
(
    ledger_offset    VARCHAR2(4000)  not null primary key,
    recorded_at      TIMESTAMP       not null,
    submission_id    NVARCHAR2(1000) not null,
    -- The type of entry, one of 'accept' or 'reject'.
    typ              NVARCHAR2(1000) not null,
    -- The configuration that was proposed and either accepted or rejected depending on the type.
    -- Encoded according to participant-state/protobuf/ledger_configuration.proto.
    -- Add the current configuration column to parameters.
    configuration    BLOB            not null,

    -- If the type is 'rejection', then the rejection reason is set.
    -- Rejection reason is a human-readable description why the change was rejected.
    rejection_reason NVARCHAR2(1000),

    -- Check that fields are correctly set based on the type.
    constraint configuration_entries_check_entry
        check (
                (typ = 'accept' and rejection_reason is null) or
                (typ = 'reject' and rejection_reason is not null))
);

CREATE INDEX idx_configuration_submission ON configuration_entries (submission_id);

CREATE TABLE package_entries
(
    ledger_offset    VARCHAR2(4000)  not null primary key,
    recorded_at      TIMESTAMP       not null,
    -- SubmissionId for package to be uploaded
    submission_id    NVARCHAR2(1000),
    -- The type of entry, one of 'accept' or 'reject'
    typ              NVARCHAR2(1000) not null,
    -- If the type is 'reject', then the rejection reason is set.
    -- Rejection reason is a human-readable description why the change was rejected.
    rejection_reason NVARCHAR2(1000),

    constraint check_package_entry_type
        check (
                (typ = 'accept' and rejection_reason is null) or
                (typ = 'reject' and rejection_reason is not null)
            )
);

-- Index for retrieving the package entry by submission id
CREATE INDEX idx_package_entries ON package_entries (submission_id);

CREATE TABLE party_entries
(
    -- The ledger end at the time when the party allocation was added
    -- cannot BLOB add as primary key with oracle
    ledger_offset    VARCHAR2(4000)  primary key not null,
    recorded_at      TIMESTAMP       not null,
    -- SubmissionId for the party allocation
    submission_id    NVARCHAR2(1000),
    -- party
    party            NVARCHAR2(1000),
    -- displayName
    display_name     NVARCHAR2(1000),
    -- The type of entry, 'accept' or 'reject'
    typ              NVARCHAR2(1000) not null,
    -- If the type is 'reject', then the rejection reason is set.
    -- Rejection reason is a human-readable description why the change was rejected.
    rejection_reason NVARCHAR2(1000),
    -- true if the party was added on participantId node that owns the party
    is_local         NUMBER(1, 0),

    constraint check_party_entry_type
        check (
                (typ = 'accept' and rejection_reason is null and party is not null) or
                (typ = 'reject' and rejection_reason is not null)
            )
);
CREATE INDEX idx_party_entries ON party_entries(submission_id);

CREATE TABLE participant_command_completions
(
    completion_offset VARCHAR2(4000)  PRIMARY KEY,
    record_time       TIMESTAMP       not null,

    application_id    NVARCHAR2(1000) not null,
    submitters        CLOB NOT NULL CONSTRAINT ensure_json_submitters CHECK (submitters IS JSON),
    command_id        NVARCHAR2(1000) not null,

    transaction_id    NVARCHAR2(1000), -- null if the command was rejected and checkpoints
    status_code       INTEGER,         -- null for successful command and checkpoints
    status_message    NVARCHAR2(1000)  -- null for successful command and checkpoints
);

CREATE INDEX participant_command_completions_idx ON participant_command_completions(completion_offset, application_id);
CREATE MATERIALIZED VIEW LOG ON participant_command_completions WITH PRIMARY KEY, ROWID;

create materialized view participant_command_completions_submitters
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
AS SELECT completion_offset, submitter
   FROM participant_command_completions,
       JSON_TABLE(submitters, '$[*]' columns (submitter PATH '$'));
create index participant_command_completions_submitters_index on participant_command_completions_submitters(submitter);

CREATE TABLE participant_command_submissions
(
    -- The deduplication key
    deduplication_key NVARCHAR2(1000) primary key not null,
    -- The time the command will stop being deduplicated
    deduplicate_until TIMESTAMP                   not null
);

---------------------------------------------------------------------------------------------------
-- Events table: divulgence
---------------------------------------------------------------------------------------------------
CREATE TABLE participant_events_divulgence (
    -- * event identification
    event_sequential_id NUMBER PRIMARY KEY,
    -- NOTE: this must be assigned sequentially by the indexer such that
    -- for all events ev1, ev2 it holds that '(ev1.offset < ev2.offset) <=> (ev1.event_sequential_id < ev2.event_sequential_id)
    event_offset VARCHAR2(4000) NOT NULL, -- offset of the transaction that divulged the contract

    -- * transaction metadata
    command_id VARCHAR2(4000),
    workflow_id VARCHAR2(4000),
    application_id VARCHAR2(4000),
    submitters CLOB CONSTRAINT ensure_json_ped_submitters CHECK (submitters IS JSON),

    -- * shared event information
    contract_id VARCHAR2(4000) NOT NULL,
    template_id VARCHAR2(4000),
    tree_event_witnesses CLOB DEFAULT '[]' NOT NULL CONSTRAINT ensure_json_tree_event_witnesses CHECK (tree_event_witnesses IS JSON),       -- informees for create, exercise, and divulgance events

    -- * divulgence and create events
    create_argument BLOB,

    -- * compression flags
    create_argument_compression SMALLINT
);

-- offset index: used to translate to sequential_id
CREATE INDEX participant_events_divulgence_event_offset ON participant_events_divulgence(event_offset);

-- sequential_id index for paging
--CREATE INDEX participant_events_divulgence_event_sequential_id ON participant_events_divulgence(event_sequential_id);

-- filtering by template
CREATE INDEX participant_events_divulgence_template_id_idx ON participant_events_divulgence(template_id);

-- filtering by witnesses (visibility) for some queries used in the implementation of
-- GetActiveContracts (flat), GetTransactions (flat) and GetTransactionTrees.
-- Note that Potsgres has trouble using these indices effectively with our paged access.
-- We might decide to drop them.
CREATE INDEX participant_events_divulgence_tree_event_witnesses_idx ON participant_events_divulgence(JSON_ARRAY(tree_event_witnesses));

-- lookup divulgance events, in order of ingestion
CREATE INDEX participant_events_divulgence_contract_id_idx ON participant_events_divulgence(contract_id, event_sequential_id);

CREATE MATERIALIZED VIEW LOG ON participant_events_divulgence WITH PRIMARY KEY, ROWID;

---------------------------------------------------------------------------------------------------
-- Events table: create
---------------------------------------------------------------------------------------------------
CREATE TABLE participant_events_create (
    -- * event identification
    event_sequential_id NUMBER PRIMARY KEY,
    -- NOTE: this must be assigned sequentially by the indexer such that
    --     -- for all events ev1, ev2 it holds that '(ev1.offset < ev2.offset) <=> (ev1.event_sequential_id < ev2.event_sequential_id)
    ledger_effective_time TIMESTAMP NOT NULL,
    node_index INTEGER NOT NULL,
    event_offset VARCHAR2(4000) NOT NULL,

    -- * transaction metadata
    transaction_id VARCHAR2(4000) NOT NULL,
    workflow_id VARCHAR2(4000),
    command_id  VARCHAR2(4000),
    application_id VARCHAR2(4000),
    submitters CLOB CONSTRAINT ensure_json_pec_submitters CHECK (submitters IS JSON),

    -- * event metadata
    event_id VARCHAR2(4000) NOT NULL,        -- string representation of (transaction_id, node_index)

    -- * shared event information
    contract_id VARCHAR2(4000) NOT NULL,
    template_id VARCHAR2(4000) NOT NULL,
    flat_event_witnesses CLOB DEFAULT '[]' NOT NULL CONSTRAINT ensure_json_pec_flat_event_witnesses CHECK (flat_event_witnesses IS JSON),       -- stakeholders of create events and consuming exercise events
    tree_event_witnesses CLOB DEFAULT '[]' NOT NULL CONSTRAINT ensure_json_pec_tree_event_witnesses CHECK (tree_event_witnesses IS JSON),       -- informees for create, exercise, and divulgance events

    -- * divulgence and create events
    create_argument BLOB NOT NULL,

    -- * create events only
    create_signatories CLOB NOT NULL CONSTRAINT ensure_json_create_signatories CHECK (create_signatories IS JSON),
    create_observers CLOB NOT NULL CONSTRAINT ensure_json_create_observers CHECK (create_observers is JSON),
    create_agreement_text VARCHAR2(4000),
    create_key_value BLOB,
    create_key_hash VARCHAR2(4000),

    -- * compression flags
    create_argument_compression SMALLINT,
    create_key_value_compression SMALLINT
);

-- offset index: used to translate to sequential_id
CREATE INDEX participant_events_create_event_offset ON participant_events_create(event_offset);

-- sequential_id index for paging
--CREATE INDEX participant_events_create_event_sequential_id ON participant_events_create(event_sequential_id);

-- lookup by event-id
CREATE INDEX participant_events_create_event_id_idx ON participant_events_create(event_id);

-- lookup by transaction id
CREATE INDEX participant_events_create_transaction_id_idx ON participant_events_create(transaction_id);

-- filtering by template
CREATE INDEX participant_events_create_template_id_idx ON participant_events_create(template_id);

-- filtering by witnesses (visibility) for some queries used in the implementation of
-- GetActiveContracts (flat), GetTransactions (flat) and GetTransactionTrees.
-- Note that Potsgres has trouble using these indices effectively with our paged access.
-- We might decide to drop them.
-- TODO https://github.com/digital-asset/daml/issues/9975 these indices are never hit
CREATE INDEX participant_events_create_flat_event_witnesses_idx ON participant_events_create(JSON_ARRAY(flat_event_witnesses));
CREATE INDEX participant_events_create_tree_event_witnesses_idx ON participant_events_create(JSON_ARRAY(tree_event_witnesses));

-- lookup by contract id
-- TODO https://github.com/digital-asset/daml/issues/10125 double-check how the HASH should work and that it is actually hit
CREATE INDEX participant_events_create_contract_id_idx ON participant_events_create(ORA_HASH(contract_id));

-- lookup by contract_key
CREATE INDEX participant_events_create_create_key_hash_idx ON participant_events_create(create_key_hash, event_sequential_id);

---------------------------------------------------------------------------------------------------
-- Events table: consuming exercise
---------------------------------------------------------------------------------------------------
CREATE TABLE participant_events_consuming_exercise (
    -- * event identification
    event_sequential_id NUMBER PRIMARY KEY,
    -- NOTE: this must be assigned sequentially by the indexer such that
    -- for all events ev1, ev2 it holds that '(ev1.offset < ev2.offset) <=> (ev1.event_sequential_id < ev2.event_sequential_id)

    event_offset VARCHAR2(4000) NOT NULL,

    -- * transaction metadata
    transaction_id VARCHAR2(4000) NOT NULL,
    ledger_effective_time TIMESTAMP NOT NULL,
    command_id VARCHAR2(4000),
    workflow_id VARCHAR2(4000),
    application_id VARCHAR2(4000),
    submitters CLOB CONSTRAINT ensure_json_pece_submitters CHECK (submitters is JSON),

    -- * event metadata
    node_index INTEGER NOT NULL,
    event_id VARCHAR2(4000) NOT NULL,        -- string representation of (transaction_id, node_index)

    -- * shared event information
    contract_id VARCHAR2(4000) NOT NULL,
    template_id VARCHAR2(4000) NOT NULL,
    flat_event_witnesses CLOB DEFAULT '[]' NOT NULL CONSTRAINT ensure_json_pece_flat_event_witnesses CHECK (flat_event_witnesses IS JSON),       -- stakeholders of create events and consuming exercise events
    tree_event_witnesses CLOB DEFAULT '[]' NOT NULL CONSTRAINT ensure_json_pece_tree_event_witnesses CHECK (tree_event_witnesses IS JSON),       -- informees for create, exercise, and divulgance events

    -- * information about the corresponding create event
    create_key_value BLOB,        -- used for the mutable state cache

    -- * exercise events (consuming and non_consuming)
    exercise_choice VARCHAR2(4000) NOT NULL,
    exercise_argument BLOB NOT NULL,
    exercise_result BLOB,
    exercise_actors CLOB NOT NULL CONSTRAINT ensure_json_pece_exercise_actors CHECK (exercise_actors IS JSON),
    exercise_child_event_ids CLOB NOT NULL CONSTRAINT ensure_json_pece_exercise_child_event_ids CHECK (exercise_child_event_ids IS JSON),

    -- * compression flags
    create_key_value_compression SMALLINT,
    exercise_argument_compression SMALLINT,
    exercise_result_compression SMALLINT
);

-- offset index: used to translate to sequential_id
CREATE INDEX participant_events_consuming_exercise_event_offset ON participant_events_consuming_exercise(event_offset);

-- sequential_id index for paging
--CREATE INDEX participant_events_consuming_exercise_event_sequential_id ON participant_events_consuming_exercise(event_sequential_id);

-- lookup by event-id
CREATE INDEX participant_events_consuming_exercise_event_id_idx ON participant_events_consuming_exercise(event_id);

-- lookup by transaction id
CREATE INDEX participant_events_consuming_exercise_transaction_id_idx ON participant_events_consuming_exercise(transaction_id);

-- filtering by template
CREATE INDEX participant_events_consuming_exercise_template_id_idx ON participant_events_consuming_exercise(template_id);

-- filtering by witnesses (visibility) for some queries used in the implementation of
-- GetActiveContracts (flat), GetTransactions (flat) and GetTransactionTrees.
-- Note that Potsgres has trouble using these indices effectively with our paged access.
-- We might decide to drop them.
-- TODO https://github.com/digital-asset/daml/issues/9975 these indices are never hit
CREATE INDEX participant_events_consuming_exercise_flat_event_witnesses_idx ON participant_events_consuming_exercise (JSON_ARRAY(flat_event_witnesses));
CREATE INDEX participant_events_consuming_exercise_tree_event_witnesses_idx ON participant_events_consuming_exercise (JSON_ARRAY(tree_event_witnesses));

-- lookup by contract id
-- TODO https://github.com/digital-asset/daml/issues/10125 double-check how the HASH should work and that it is actually hit
CREATE INDEX participant_events_consuming_exercise_contract_id_idx ON participant_events_consuming_exercise (ORA_HASH(contract_id));

CREATE MATERIALIZED VIEW LOG ON participant_events_consuming_exercise WITH PRIMARY KEY, ROWID;
CREATE MATERIALIZED VIEW LOG ON participant_events_create WITH PRIMARY KEY, ROWID;

---------------------------------------------------------------------------------------------------
-- Events table: non-consuming exercise
---------------------------------------------------------------------------------------------------
CREATE TABLE participant_events_non_consuming_exercise (
    -- * event identification
    event_sequential_id NUMBER PRIMARY KEY,
    -- NOTE: this must be assigned sequentially by the indexer such that
    -- for all events ev1, ev2 it holds that '(ev1.offset < ev2.offset) <=> (ev1.event_sequential_id < ev2.event_sequential_id)

    ledger_effective_time TIMESTAMP NOT NULL,
    node_index INTEGER NOT NULL,
    event_offset VARCHAR2(4000) NOT NULL,

    -- * transaction metadata
    transaction_id VARCHAR2(4000) NOT NULL,
    workflow_id VARCHAR2(4000),
    command_id VARCHAR2(4000),
    application_id VARCHAR2(4000),
    submitters CLOB CONSTRAINT ensure_json_pence_submitters CHECK (submitters IS JSON),

    -- * event metadata
    event_id VARCHAR2(4000) NOT NULL,                                   -- string representation of (transaction_id, node_index)

    -- * shared event information
    contract_id VARCHAR2(4000) NOT NULL,
    template_id VARCHAR2(4000) NOT NULL,
    flat_event_witnesses CLOB DEFAULT '{}' NOT NULL CONSTRAINT ensure_json_pence_flat_event_witnesses CHECK (flat_event_witnesses IS JSON),       -- stakeholders of create events and consuming exercise events
    tree_event_witnesses CLOB DEFAULT '{}' NOT NULL CONSTRAINT ensure_json_pence_tree_event_witnesses CHECK (tree_event_witnesses IS JSON),       -- informees for create, exercise, and divulgance events

    -- * information about the corresponding create event
    create_key_value BLOB,        -- used for the mutable state cache

    -- * exercise events (consuming and non_consuming)
    exercise_choice VARCHAR2(4000) NOT NULL,
    exercise_argument BLOB NOT NULL,
    exercise_result BLOB,
    exercise_actors CLOB NOT NULL CONSTRAINT ensure_json_exercise_actors CHECK (exercise_actors IS JSON),
    exercise_child_event_ids CLOB NOT NULL CONSTRAINT ensure_json_exercise_child_event_ids CHECK (exercise_child_event_ids IS JSON),

    -- * compression flags
    create_key_value_compression SMALLINT,
    exercise_argument_compression SMALLINT,
    exercise_result_compression SMALLINT
);

-- offset index: used to translate to sequential_id
CREATE INDEX participant_events_non_consuming_exercise_event_offset ON participant_events_non_consuming_exercise(event_offset);

-- sequential_id index for paging
--CREATE INDEX participant_events_non_consuming_exercise_event_sequential_id ON participant_events_non_consuming_exercise(event_sequential_id);

-- lookup by event-id
CREATE INDEX participant_events_non_consuming_exercise_event_id_idx ON participant_events_non_consuming_exercise(event_id);

-- lookup by transaction id
CREATE INDEX participant_events_non_consuming_exercise_transaction_id_idx ON participant_events_non_consuming_exercise(transaction_id);

-- filtering by template
CREATE INDEX participant_events_non_consuming_exercise_template_id_idx ON participant_events_non_consuming_exercise(template_id);

-- filtering by witnesses (visibility) for some queries used in the implementation of
-- GetActiveContracts (flat), GetTransactions (flat) and GetTransactionTrees.
-- There is no equivalent to GIN index for oracle, but we explicitly mark as a JSON column for indexing
-- NOTE: index name truncated because the full name exceeds the 63 characters length limit
-- TODO https://github.com/digital-asset/daml/issues/9975 these indices are never hit
CREATE INDEX participant_events_non_consuming_exercise_flat_event_witness_idx ON participant_events_non_consuming_exercise(JSON_ARRAY(flat_event_witnesses));
CREATE INDEX participant_events_non_consuming_exercise_tree_event_witness_idx ON participant_events_non_consuming_exercise(JSON_ARRAY(tree_event_witnesses));

CREATE MATERIALIZED VIEW LOG ON participant_events_non_consuming_exercise WITH PRIMARY KEY, ROWID;

CREATE VIEW participant_events AS
SELECT cast(0 as SMALLINT)          AS event_kind,
       participant_events_divulgence.event_sequential_id,
       cast(NULL as VARCHAR2(4000)) AS event_offset,
       cast(NULL as VARCHAR2(4000)) AS transaction_id,
       cast(NULL as TIMESTAMP)      AS ledger_effective_time,
       participant_events_divulgence.command_id,
       participant_events_divulgence.workflow_id,
       participant_events_divulgence.application_id,
       participant_events_divulgence.submitters,
       cast(NULL as INTEGER)        as node_index,
       cast(NULL as VARCHAR2(4000)) as event_id,
       participant_events_divulgence.contract_id,
       participant_events_divulgence.template_id,
       to_clob('[]')                AS flat_event_witnesses,
       participant_events_divulgence.tree_event_witnesses,
       participant_events_divulgence.create_argument,
       to_clob('[]')                AS create_signatories,
       to_clob('[]')                AS create_observers,
       cast(NULL as VARCHAR2(4000)) AS create_agreement_text,
       NULL                 AS create_key_value,
       cast(NULL as VARCHAR2(4000)) AS create_key_hash,
       cast(NULL as VARCHAR2(4000)) AS exercise_choice,
       NULL AS exercise_argument,
       NULL AS exercise_result,
       to_clob('[]')                AS exercise_actors,
       to_clob('[]')                AS exercise_child_event_ids,
       participant_events_divulgence.create_argument_compression,
       cast(NULL as SMALLINT)       AS create_key_value_compression,
       cast(NULL as SMALLINT)       AS exercise_argument_compression,
       cast(NULL as SMALLINT)       AS exercise_result_compression
FROM participant_events_divulgence
UNION ALL
SELECT (10)                         AS event_kind,
       participant_events_create.event_sequential_id,
       participant_events_create.event_offset,
       participant_events_create.transaction_id,
       participant_events_create.ledger_effective_time,
       participant_events_create.command_id,
       participant_events_create.workflow_id,
       participant_events_create.application_id,
       participant_events_create.submitters,
       participant_events_create.node_index,
       participant_events_create.event_id,
       participant_events_create.contract_id,
       participant_events_create.template_id,
       participant_events_create.flat_event_witnesses,
       participant_events_create.tree_event_witnesses,
       participant_events_create.create_argument,
       participant_events_create.create_signatories,
       participant_events_create.create_observers,
       participant_events_create.create_agreement_text,
       participant_events_create.create_key_value,
       participant_events_create.create_key_hash,
       cast(NULL as VARCHAR2(4000)) AS exercise_choice,
       NULL AS exercise_argument,
       NULL AS exercise_result,
       to_clob('[]')                AS exercise_actors,
       to_clob('[]')                AS exercise_child_event_ids,
       participant_events_create.create_argument_compression,
       participant_events_create.create_key_value_compression,
       cast(NULL as SMALLINT)       AS exercise_argument_compression,
       cast(NULL as SMALLINT)       AS exercise_result_compression
FROM participant_events_create
UNION ALL
SELECT (20)          AS event_kind,
       participant_events_consuming_exercise.event_sequential_id,
       participant_events_consuming_exercise.event_offset,
       participant_events_consuming_exercise.transaction_id,
       participant_events_consuming_exercise.ledger_effective_time,
       participant_events_consuming_exercise.command_id,
       participant_events_consuming_exercise.workflow_id,
       participant_events_consuming_exercise.application_id,
       participant_events_consuming_exercise.submitters,
       participant_events_consuming_exercise.node_index,
       participant_events_consuming_exercise.event_id,
       participant_events_consuming_exercise.contract_id,
       participant_events_consuming_exercise.template_id,
       participant_events_consuming_exercise.flat_event_witnesses,
       participant_events_consuming_exercise.tree_event_witnesses,
       NULL  AS create_argument,
       to_clob('[]') AS create_signatories,
       to_clob('[]') AS create_observers,
       NULL          AS create_agreement_text,
       participant_events_consuming_exercise.create_key_value,
       NULL          AS create_key_hash,
       participant_events_consuming_exercise.exercise_choice,
       participant_events_consuming_exercise.exercise_argument,
       participant_events_consuming_exercise.exercise_result,
       participant_events_consuming_exercise.exercise_actors,
       participant_events_consuming_exercise.exercise_child_event_ids,
       NULL          AS create_argument_compression,
       participant_events_consuming_exercise.create_key_value_compression,
       participant_events_consuming_exercise.exercise_argument_compression,
       participant_events_consuming_exercise.exercise_result_compression
FROM participant_events_consuming_exercise
UNION ALL
SELECT (25)          AS event_kind,
       participant_events_non_consuming_exercise.event_sequential_id,
       participant_events_non_consuming_exercise.event_offset,
       participant_events_non_consuming_exercise.transaction_id,
       participant_events_non_consuming_exercise.ledger_effective_time,
       participant_events_non_consuming_exercise.command_id,
       participant_events_non_consuming_exercise.workflow_id,
       participant_events_non_consuming_exercise.application_id,
       participant_events_non_consuming_exercise.submitters,
       participant_events_non_consuming_exercise.node_index,
       participant_events_non_consuming_exercise.event_id,
       participant_events_non_consuming_exercise.contract_id,
       participant_events_non_consuming_exercise.template_id,
       participant_events_non_consuming_exercise.flat_event_witnesses,
       participant_events_non_consuming_exercise.tree_event_witnesses,
       NULL  AS create_argument,
       to_clob('[]') AS create_signatories,
       to_clob('[]') AS create_observers,
       NULL          AS create_agreement_text,
       participant_events_non_consuming_exercise.create_key_value,
       NULL          AS create_key_hash,
       participant_events_non_consuming_exercise.exercise_choice,
       participant_events_non_consuming_exercise.exercise_argument,
       participant_events_non_consuming_exercise.exercise_result,
       participant_events_non_consuming_exercise.exercise_actors,
       participant_events_non_consuming_exercise.exercise_child_event_ids,
       NULL          AS create_argument_compression,
       participant_events_non_consuming_exercise.create_key_value_compression,
       participant_events_non_consuming_exercise.exercise_argument_compression,
       participant_events_non_consuming_exercise.exercise_result_compression
FROM participant_events_non_consuming_exercise;




---------------------------------------------------------------------------------------------------
-- Parameters table
---------------------------------------------------------------------------------------------------

-- new field: the sequential_event_id up to which all events have been ingested
CREATE TABLE parameters
-- this table is meant to have a single row storing all the parameters we have
(
    -- the generated or configured id identifying the ledger
    ledger_id                          NVARCHAR2(1000) not null,
    -- stores the head offset, meant to change with every new ledger entry
    ledger_end                         VARCHAR2(4000),
    external_ledger_end                NVARCHAR2(1000),
    participant_id                     NVARCHAR2(1000),
    participant_pruned_up_to_inclusive VARCHAR2(4000),
    ledger_end_sequential_id           NUMBER
);

--
create materialized view PARTICIPANT_EVENTS_DIVULGENCE_tree_event_witness_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
        WITH PRIMARY KEY
AS SELECT event_sequential_id, tree_event_witness
   FROM PARTICIPANT_EVENTS_DIVULGENCE,
       JSON_TABLE(TREE_EVENT_WITNESSES, '$[*]' columns (tree_event_witness PATH '$'));

create materialized view PARTICIPANT_EVENTS_CREATE_tree_event_witness_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
        WITH PRIMARY KEY
AS
SELECT event_sequential_id, tree_event_witness
FROM PARTICIPANT_EVENTS_CREATE,
    JSON_TABLE(TREE_EVENT_WITNESSES, '$[*]' columns (tree_event_witness PATH '$'));

create materialized view PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_tree_event_witness_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
        WITH PRIMARY KEY
AS
SELECT event_sequential_id, tree_event_witness
FROM PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE,
    JSON_TABLE(TREE_EVENT_WITNESSES, '$[*]' columns (tree_event_witness PATH '$'));

create materialized view PARTICIPANT_EVENTS_CONSUMING_EXERCISE_tree_event_witness_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
        WITH PRIMARY KEY
AS
SELECT event_sequential_id, tree_event_witness
FROM PARTICIPANT_EVENTS_CONSUMING_EXERCISE,
    JSON_TABLE(TREE_EVENT_WITNESSES, '$[*]' columns (tree_event_witness PATH '$'));

CREATE INDEX PARTICIPANT_EVENTS_DIVULGENCE_tree_event_witness_mv_index ON PARTICIPANT_EVENTS_DIVULGENCE_tree_event_witness_mv(tree_event_witness);
CREATE INDEX PARTICIPANT_EVENTS_CREATE_tree_event_witness_mv_index ON PARTICIPANT_EVENTS_CREATE_tree_event_witness_mv(tree_event_witness);
CREATE INDEX PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_tree_event_witness_mv_index ON PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_tree_event_witness_mv(tree_event_witness);
CREATE INDEX PARTICIPANT_EVENTS_CONSUMING_EXERCISE_tree_event_witness_mv_index ON PARTICIPANT_EVENTS_CONSUMING_EXERCISE_tree_event_witness_mv(tree_event_witness);

create view PARTICIPANT_EVENTS_TREE_WITNESS as
select * from PARTICIPANT_EVENTS_DIVULGENCE_tree_event_witness_mv
UNION
select * from PARTICIPANT_EVENTS_CREATE_tree_event_witness_mv
UNION
select * from PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_tree_event_witness_mv
UNION
select * from PARTICIPANT_EVENTS_CONSUMING_EXERCISE_tree_event_witness_mv;





create materialized view PARTICIPANT_EVENTS_CREATE_flat_event_witness_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
        WITH PRIMARY KEY
AS
SELECT event_sequential_id, flat_event_witness
FROM PARTICIPANT_EVENTS_CREATE,
    JSON_TABLE(flat_event_witnesses, '$[*]' columns (flat_event_witness PATH '$'));

create materialized view PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_flat_event_witness_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
        WITH PRIMARY KEY
AS
SELECT event_sequential_id, flat_event_witness
FROM PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE,
    JSON_TABLE(flat_event_witnesses, '$[*]' columns (flat_event_witness PATH '$'));

create materialized view PARTICIPANT_EVENTS_CONSUMING_EXERCISE_flat_event_witness_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
        WITH PRIMARY KEY
AS
SELECT event_sequential_id, flat_event_witness
FROM PARTICIPANT_EVENTS_CONSUMING_EXERCISE,
    JSON_TABLE(flat_event_witnesses, '$[*]' columns (flat_event_witness PATH '$'));
CREATE INDEX PARTICIPANT_EVENTS_CREATE_flat_event_witness_mv_index ON PARTICIPANT_EVENTS_CREATE_flat_event_witness_mv(flat_event_witness);
CREATE INDEX PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_flat_event_witness_mv_index ON PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_flat_event_witness_mv(flat_event_witness);
CREATE INDEX PARTICIPANT_EVENTS_CONSUMING_EXERCISE_flat_event_witness_mv_index ON PARTICIPANT_EVENTS_CONSUMING_EXERCISE_flat_event_witness_mv(flat_event_witness);



create view PARTICIPANT_EVENTS_FLAT_WITNESS as
select * from PARTICIPANT_EVENTS_CREATE_flat_event_witness_mv
UNION
select * from PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_flat_event_witness_mv
UNION
select * from PARTICIPANT_EVENTS_CONSUMING_EXERCISE_flat_event_witness_mv;



-- SUBMITTERS

create materialized view PARTICIPANT_EVENTS_DIVULGENCE_submitters_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
WITH PRIMARY KEY
AS SELECT event_sequential_id, submitter
   FROM PARTICIPANT_EVENTS_DIVULGENCE,
       JSON_TABLE(submitters, '$[*]' columns (submitter PATH '$'));

create materialized view PARTICIPANT_EVENTS_CREATE_submitters_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
WITH PRIMARY KEY
AS
SELECT event_sequential_id, submitter
FROM PARTICIPANT_EVENTS_CREATE,
    JSON_TABLE(SUBMITTERS, '$[*]' columns (submitter PATH '$'));

create materialized view PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_submitters_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
WITH PRIMARY KEY
AS
SELECT event_sequential_id, submitter
FROM PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE,
    JSON_TABLE(SUBMITTERS, '$[*]' columns (submitter PATH '$'));

create materialized view PARTICIPANT_EVENTS_CONSUMING_EXERCISE_submitters_mv
    BUILD IMMEDIATE REFRESH FAST ON STATEMENT
WITH PRIMARY KEY
AS
SELECT event_sequential_id, submitter
FROM PARTICIPANT_EVENTS_CONSUMING_EXERCISE,
    JSON_TABLE(SUBMITTERS, '$[*]' columns (submitter PATH '$'));

CREATE INDEX PARTICIPANT_EVENTS_DIVULGENCE_submitter_mv_index ON PARTICIPANT_EVENTS_DIVULGENCE_submitters_mv(submitter);
CREATE INDEX PARTICIPANT_EVENTS_CREATE_submitter_mv_index ON PARTICIPANT_EVENTS_CREATE_submitters_mv(submitter);
CREATE INDEX PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_submitter_mv_index ON PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_submitters_mv(submitter);
CREATE INDEX PARTICIPANT_EVENTS_CONSUMING_EXERCISE_submitter_mv_index ON PARTICIPANT_EVENTS_CONSUMING_EXERCISE_submitters_mv(submitter);

create view PARTICIPANT_EVENTS_SUBMITTERS as
select * from PARTICIPANT_EVENTS_DIVULGENCE_submitters_mv
UNION
select * from PARTICIPANT_EVENTS_CREATE_submitters_mv
UNION
select * from PARTICIPANT_EVENTS_NON_CONSUMING_EXERCISE_submitters_mv
UNION
select * from PARTICIPANT_EVENTS_CONSUMING_EXERCISE_submitters_mv;
