--  Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
--  SPDX-License-Identifier: Apache-2.0

ALTER TABLE participant_events_divulgence
    ALTER COLUMN event_offset SET STORAGE EXTERNAL,
    ALTER COLUMN workflow_id SET STORAGE EXTERNAL,
    ALTER COLUMN command_id SET STORAGE EXTERNAL,
    ALTER COLUMN application_id SET STORAGE EXTERNAL,
    ALTER COLUMN contract_id SET STORAGE EXTERNAL,
    ALTER COLUMN create_argument SET STORAGE EXTERNAL;

ALTER TABLE participant_events_create
    ALTER COLUMN event_offset SET STORAGE EXTERNAL,
    ALTER COLUMN transaction_id SET STORAGE EXTERNAL,
    ALTER COLUMN workflow_id SET STORAGE EXTERNAL,
    ALTER COLUMN command_id SET STORAGE EXTERNAL,
    ALTER COLUMN application_id SET STORAGE EXTERNAL,
    ALTER COLUMN event_id SET STORAGE EXTERNAL,
    ALTER COLUMN contract_id SET STORAGE EXTERNAL,
    ALTER COLUMN create_argument SET STORAGE EXTERNAL,
    ALTER COLUMN create_agreement_text SET STORAGE EXTERNAL,
    ALTER COLUMN create_key_value SET STORAGE EXTERNAL,
    ALTER COLUMN create_key_hash SET STORAGE EXTERNAL;

ALTER TABLE participant_events_consuming_exercise
    ALTER COLUMN event_offset SET STORAGE EXTERNAL,
    ALTER COLUMN transaction_id SET STORAGE EXTERNAL,
    ALTER COLUMN workflow_id SET STORAGE EXTERNAL,
    ALTER COLUMN command_id SET STORAGE EXTERNAL,
    ALTER COLUMN application_id SET STORAGE EXTERNAL,
    ALTER COLUMN event_id SET STORAGE EXTERNAL,
    ALTER COLUMN contract_id SET STORAGE EXTERNAL,
    ALTER COLUMN create_key_value SET STORAGE EXTERNAL,
    ALTER COLUMN exercise_choice SET STORAGE EXTERNAL,
    ALTER COLUMN exercise_argument SET STORAGE EXTERNAL,
    ALTER COLUMN exercise_result SET STORAGE EXTERNAL,
    ALTER COLUMN exercise_child_event_ids SET STORAGE EXTERNAL;

ALTER TABLE participant_events_non_consuming_exercise
    ALTER COLUMN event_offset SET STORAGE EXTERNAL,
    ALTER COLUMN transaction_id SET STORAGE EXTERNAL,
    ALTER COLUMN workflow_id SET STORAGE EXTERNAL,
    ALTER COLUMN command_id SET STORAGE EXTERNAL,
    ALTER COLUMN application_id SET STORAGE EXTERNAL,
    ALTER COLUMN event_id SET STORAGE EXTERNAL,
    ALTER COLUMN contract_id SET STORAGE EXTERNAL,
    ALTER COLUMN create_key_value SET STORAGE EXTERNAL,
    ALTER COLUMN exercise_choice SET STORAGE EXTERNAL,
    ALTER COLUMN exercise_argument SET STORAGE EXTERNAL,
    ALTER COLUMN exercise_result SET STORAGE EXTERNAL,
    ALTER COLUMN exercise_child_event_ids SET STORAGE EXTERNAL;
