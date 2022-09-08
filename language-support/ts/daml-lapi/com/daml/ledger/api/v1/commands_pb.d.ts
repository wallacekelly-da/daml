import * as jspb from 'google-protobuf'

import * as com_daml_ledger_api_v1_contract_metadata_pb from '../../../../../com/daml/ledger/api/v1/contract_metadata_pb';
import * as com_daml_ledger_api_v1_value_pb from '../../../../../com/daml/ledger/api/v1/value_pb';
import * as google_protobuf_timestamp_pb from 'google-protobuf/google/protobuf/timestamp_pb';
import * as google_protobuf_duration_pb from 'google-protobuf/google/protobuf/duration_pb';


export class Commands extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): Commands;

  getWorkflowId(): string;
  setWorkflowId(value: string): Commands;

  getApplicationId(): string;
  setApplicationId(value: string): Commands;

  getCommandId(): string;
  setCommandId(value: string): Commands;

  getParty(): string;
  setParty(value: string): Commands;

  getCommandsList(): Array<Command>;
  setCommandsList(value: Array<Command>): Commands;
  clearCommandsList(): Commands;
  addCommands(value?: Command, index?: number): Command;

  getDeduplicationTime(): google_protobuf_duration_pb.Duration | undefined;
  setDeduplicationTime(value?: google_protobuf_duration_pb.Duration): Commands;
  hasDeduplicationTime(): boolean;
  clearDeduplicationTime(): Commands;

  getDeduplicationDuration(): google_protobuf_duration_pb.Duration | undefined;
  setDeduplicationDuration(value?: google_protobuf_duration_pb.Duration): Commands;
  hasDeduplicationDuration(): boolean;
  clearDeduplicationDuration(): Commands;

  getDeduplicationOffset(): string;
  setDeduplicationOffset(value: string): Commands;

  getMinLedgerTimeAbs(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setMinLedgerTimeAbs(value?: google_protobuf_timestamp_pb.Timestamp): Commands;
  hasMinLedgerTimeAbs(): boolean;
  clearMinLedgerTimeAbs(): Commands;

  getMinLedgerTimeRel(): google_protobuf_duration_pb.Duration | undefined;
  setMinLedgerTimeRel(value?: google_protobuf_duration_pb.Duration): Commands;
  hasMinLedgerTimeRel(): boolean;
  clearMinLedgerTimeRel(): Commands;

  getActAsList(): Array<string>;
  setActAsList(value: Array<string>): Commands;
  clearActAsList(): Commands;
  addActAs(value: string, index?: number): Commands;

  getReadAsList(): Array<string>;
  setReadAsList(value: Array<string>): Commands;
  clearReadAsList(): Commands;
  addReadAs(value: string, index?: number): Commands;

  getSubmissionId(): string;
  setSubmissionId(value: string): Commands;

  getDisclosedContractsList(): Array<DisclosedContract>;
  setDisclosedContractsList(value: Array<DisclosedContract>): Commands;
  clearDisclosedContractsList(): Commands;
  addDisclosedContracts(value?: DisclosedContract, index?: number): DisclosedContract;

  getDeduplicationPeriodCase(): Commands.DeduplicationPeriodCase;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Commands.AsObject;
  static toObject(includeInstance: boolean, msg: Commands): Commands.AsObject;
  static serializeBinaryToWriter(message: Commands, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Commands;
  static deserializeBinaryFromReader(message: Commands, reader: jspb.BinaryReader): Commands;
}

export namespace Commands {
  export type AsObject = {
    ledgerId: string,
    workflowId: string,
    applicationId: string,
    commandId: string,
    party: string,
    commandsList: Array<Command.AsObject>,
    deduplicationTime?: google_protobuf_duration_pb.Duration.AsObject,
    deduplicationDuration?: google_protobuf_duration_pb.Duration.AsObject,
    deduplicationOffset: string,
    minLedgerTimeAbs?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    minLedgerTimeRel?: google_protobuf_duration_pb.Duration.AsObject,
    actAsList: Array<string>,
    readAsList: Array<string>,
    submissionId: string,
    disclosedContractsList: Array<DisclosedContract.AsObject>,
  }

  export enum DeduplicationPeriodCase { 
    DEDUPLICATION_PERIOD_NOT_SET = 0,
    DEDUPLICATION_TIME = 9,
    DEDUPLICATION_DURATION = 15,
    DEDUPLICATION_OFFSET = 16,
  }
}

export class Command extends jspb.Message {
  getCreate(): CreateCommand | undefined;
  setCreate(value?: CreateCommand): Command;
  hasCreate(): boolean;
  clearCreate(): Command;

  getExercise(): ExerciseCommand | undefined;
  setExercise(value?: ExerciseCommand): Command;
  hasExercise(): boolean;
  clearExercise(): Command;

  getExercisebykey(): ExerciseByKeyCommand | undefined;
  setExercisebykey(value?: ExerciseByKeyCommand): Command;
  hasExercisebykey(): boolean;
  clearExercisebykey(): Command;

  getCreateandexercise(): CreateAndExerciseCommand | undefined;
  setCreateandexercise(value?: CreateAndExerciseCommand): Command;
  hasCreateandexercise(): boolean;
  clearCreateandexercise(): Command;

  getCommandCase(): Command.CommandCase;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Command.AsObject;
  static toObject(includeInstance: boolean, msg: Command): Command.AsObject;
  static serializeBinaryToWriter(message: Command, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Command;
  static deserializeBinaryFromReader(message: Command, reader: jspb.BinaryReader): Command;
}

export namespace Command {
  export type AsObject = {
    create?: CreateCommand.AsObject,
    exercise?: ExerciseCommand.AsObject,
    exercisebykey?: ExerciseByKeyCommand.AsObject,
    createandexercise?: CreateAndExerciseCommand.AsObject,
  }

  export enum CommandCase { 
    COMMAND_NOT_SET = 0,
    CREATE = 1,
    EXERCISE = 2,
    EXERCISEBYKEY = 4,
    CREATEANDEXERCISE = 3,
  }
}

export class CreateCommand extends jspb.Message {
  getTemplateId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setTemplateId(value?: com_daml_ledger_api_v1_value_pb.Identifier): CreateCommand;
  hasTemplateId(): boolean;
  clearTemplateId(): CreateCommand;

  getCreateArguments(): com_daml_ledger_api_v1_value_pb.Record | undefined;
  setCreateArguments(value?: com_daml_ledger_api_v1_value_pb.Record): CreateCommand;
  hasCreateArguments(): boolean;
  clearCreateArguments(): CreateCommand;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CreateCommand.AsObject;
  static toObject(includeInstance: boolean, msg: CreateCommand): CreateCommand.AsObject;
  static serializeBinaryToWriter(message: CreateCommand, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CreateCommand;
  static deserializeBinaryFromReader(message: CreateCommand, reader: jspb.BinaryReader): CreateCommand;
}

export namespace CreateCommand {
  export type AsObject = {
    templateId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    createArguments?: com_daml_ledger_api_v1_value_pb.Record.AsObject,
  }
}

export class ExerciseCommand extends jspb.Message {
  getTemplateId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setTemplateId(value?: com_daml_ledger_api_v1_value_pb.Identifier): ExerciseCommand;
  hasTemplateId(): boolean;
  clearTemplateId(): ExerciseCommand;

  getContractId(): string;
  setContractId(value: string): ExerciseCommand;

  getChoice(): string;
  setChoice(value: string): ExerciseCommand;

  getChoiceArgument(): com_daml_ledger_api_v1_value_pb.Value | undefined;
  setChoiceArgument(value?: com_daml_ledger_api_v1_value_pb.Value): ExerciseCommand;
  hasChoiceArgument(): boolean;
  clearChoiceArgument(): ExerciseCommand;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ExerciseCommand.AsObject;
  static toObject(includeInstance: boolean, msg: ExerciseCommand): ExerciseCommand.AsObject;
  static serializeBinaryToWriter(message: ExerciseCommand, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ExerciseCommand;
  static deserializeBinaryFromReader(message: ExerciseCommand, reader: jspb.BinaryReader): ExerciseCommand;
}

export namespace ExerciseCommand {
  export type AsObject = {
    templateId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    contractId: string,
    choice: string,
    choiceArgument?: com_daml_ledger_api_v1_value_pb.Value.AsObject,
  }
}

export class ExerciseByKeyCommand extends jspb.Message {
  getTemplateId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setTemplateId(value?: com_daml_ledger_api_v1_value_pb.Identifier): ExerciseByKeyCommand;
  hasTemplateId(): boolean;
  clearTemplateId(): ExerciseByKeyCommand;

  getContractKey(): com_daml_ledger_api_v1_value_pb.Value | undefined;
  setContractKey(value?: com_daml_ledger_api_v1_value_pb.Value): ExerciseByKeyCommand;
  hasContractKey(): boolean;
  clearContractKey(): ExerciseByKeyCommand;

  getChoice(): string;
  setChoice(value: string): ExerciseByKeyCommand;

  getChoiceArgument(): com_daml_ledger_api_v1_value_pb.Value | undefined;
  setChoiceArgument(value?: com_daml_ledger_api_v1_value_pb.Value): ExerciseByKeyCommand;
  hasChoiceArgument(): boolean;
  clearChoiceArgument(): ExerciseByKeyCommand;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ExerciseByKeyCommand.AsObject;
  static toObject(includeInstance: boolean, msg: ExerciseByKeyCommand): ExerciseByKeyCommand.AsObject;
  static serializeBinaryToWriter(message: ExerciseByKeyCommand, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ExerciseByKeyCommand;
  static deserializeBinaryFromReader(message: ExerciseByKeyCommand, reader: jspb.BinaryReader): ExerciseByKeyCommand;
}

export namespace ExerciseByKeyCommand {
  export type AsObject = {
    templateId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    contractKey?: com_daml_ledger_api_v1_value_pb.Value.AsObject,
    choice: string,
    choiceArgument?: com_daml_ledger_api_v1_value_pb.Value.AsObject,
  }
}

export class CreateAndExerciseCommand extends jspb.Message {
  getTemplateId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setTemplateId(value?: com_daml_ledger_api_v1_value_pb.Identifier): CreateAndExerciseCommand;
  hasTemplateId(): boolean;
  clearTemplateId(): CreateAndExerciseCommand;

  getCreateArguments(): com_daml_ledger_api_v1_value_pb.Record | undefined;
  setCreateArguments(value?: com_daml_ledger_api_v1_value_pb.Record): CreateAndExerciseCommand;
  hasCreateArguments(): boolean;
  clearCreateArguments(): CreateAndExerciseCommand;

  getChoice(): string;
  setChoice(value: string): CreateAndExerciseCommand;

  getChoiceArgument(): com_daml_ledger_api_v1_value_pb.Value | undefined;
  setChoiceArgument(value?: com_daml_ledger_api_v1_value_pb.Value): CreateAndExerciseCommand;
  hasChoiceArgument(): boolean;
  clearChoiceArgument(): CreateAndExerciseCommand;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CreateAndExerciseCommand.AsObject;
  static toObject(includeInstance: boolean, msg: CreateAndExerciseCommand): CreateAndExerciseCommand.AsObject;
  static serializeBinaryToWriter(message: CreateAndExerciseCommand, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CreateAndExerciseCommand;
  static deserializeBinaryFromReader(message: CreateAndExerciseCommand, reader: jspb.BinaryReader): CreateAndExerciseCommand;
}

export namespace CreateAndExerciseCommand {
  export type AsObject = {
    templateId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    createArguments?: com_daml_ledger_api_v1_value_pb.Record.AsObject,
    choice: string,
    choiceArgument?: com_daml_ledger_api_v1_value_pb.Value.AsObject,
  }
}

export class DisclosedContract extends jspb.Message {
  getTemplateId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setTemplateId(value?: com_daml_ledger_api_v1_value_pb.Identifier): DisclosedContract;
  hasTemplateId(): boolean;
  clearTemplateId(): DisclosedContract;

  getContractId(): string;
  setContractId(value: string): DisclosedContract;

  getArguments(): com_daml_ledger_api_v1_value_pb.Record | undefined;
  setArguments(value?: com_daml_ledger_api_v1_value_pb.Record): DisclosedContract;
  hasArguments(): boolean;
  clearArguments(): DisclosedContract;

  getMetadata(): com_daml_ledger_api_v1_contract_metadata_pb.ContractMetadata | undefined;
  setMetadata(value?: com_daml_ledger_api_v1_contract_metadata_pb.ContractMetadata): DisclosedContract;
  hasMetadata(): boolean;
  clearMetadata(): DisclosedContract;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): DisclosedContract.AsObject;
  static toObject(includeInstance: boolean, msg: DisclosedContract): DisclosedContract.AsObject;
  static serializeBinaryToWriter(message: DisclosedContract, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): DisclosedContract;
  static deserializeBinaryFromReader(message: DisclosedContract, reader: jspb.BinaryReader): DisclosedContract;
}

export namespace DisclosedContract {
  export type AsObject = {
    templateId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    contractId: string,
    arguments?: com_daml_ledger_api_v1_value_pb.Record.AsObject,
    metadata?: com_daml_ledger_api_v1_contract_metadata_pb.ContractMetadata.AsObject,
  }
}

