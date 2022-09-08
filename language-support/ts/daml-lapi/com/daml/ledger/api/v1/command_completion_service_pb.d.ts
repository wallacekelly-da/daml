import * as jspb from 'google-protobuf'

import * as com_daml_ledger_api_v1_completion_pb from '../../../../../com/daml/ledger/api/v1/completion_pb';
import * as com_daml_ledger_api_v1_ledger_offset_pb from '../../../../../com/daml/ledger/api/v1/ledger_offset_pb';
import * as google_protobuf_timestamp_pb from 'google-protobuf/google/protobuf/timestamp_pb';


export class CompletionStreamRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): CompletionStreamRequest;

  getApplicationId(): string;
  setApplicationId(value: string): CompletionStreamRequest;

  getPartiesList(): Array<string>;
  setPartiesList(value: Array<string>): CompletionStreamRequest;
  clearPartiesList(): CompletionStreamRequest;
  addParties(value: string, index?: number): CompletionStreamRequest;

  getOffset(): com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset | undefined;
  setOffset(value?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset): CompletionStreamRequest;
  hasOffset(): boolean;
  clearOffset(): CompletionStreamRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CompletionStreamRequest.AsObject;
  static toObject(includeInstance: boolean, msg: CompletionStreamRequest): CompletionStreamRequest.AsObject;
  static serializeBinaryToWriter(message: CompletionStreamRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CompletionStreamRequest;
  static deserializeBinaryFromReader(message: CompletionStreamRequest, reader: jspb.BinaryReader): CompletionStreamRequest;
}

export namespace CompletionStreamRequest {
  export type AsObject = {
    ledgerId: string,
    applicationId: string,
    partiesList: Array<string>,
    offset?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset.AsObject,
  }
}

export class CompletionStreamResponse extends jspb.Message {
  getCheckpoint(): Checkpoint | undefined;
  setCheckpoint(value?: Checkpoint): CompletionStreamResponse;
  hasCheckpoint(): boolean;
  clearCheckpoint(): CompletionStreamResponse;

  getCompletionsList(): Array<com_daml_ledger_api_v1_completion_pb.Completion>;
  setCompletionsList(value: Array<com_daml_ledger_api_v1_completion_pb.Completion>): CompletionStreamResponse;
  clearCompletionsList(): CompletionStreamResponse;
  addCompletions(value?: com_daml_ledger_api_v1_completion_pb.Completion, index?: number): com_daml_ledger_api_v1_completion_pb.Completion;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CompletionStreamResponse.AsObject;
  static toObject(includeInstance: boolean, msg: CompletionStreamResponse): CompletionStreamResponse.AsObject;
  static serializeBinaryToWriter(message: CompletionStreamResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CompletionStreamResponse;
  static deserializeBinaryFromReader(message: CompletionStreamResponse, reader: jspb.BinaryReader): CompletionStreamResponse;
}

export namespace CompletionStreamResponse {
  export type AsObject = {
    checkpoint?: Checkpoint.AsObject,
    completionsList: Array<com_daml_ledger_api_v1_completion_pb.Completion.AsObject>,
  }
}

export class Checkpoint extends jspb.Message {
  getRecordTime(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setRecordTime(value?: google_protobuf_timestamp_pb.Timestamp): Checkpoint;
  hasRecordTime(): boolean;
  clearRecordTime(): Checkpoint;

  getOffset(): com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset | undefined;
  setOffset(value?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset): Checkpoint;
  hasOffset(): boolean;
  clearOffset(): Checkpoint;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Checkpoint.AsObject;
  static toObject(includeInstance: boolean, msg: Checkpoint): Checkpoint.AsObject;
  static serializeBinaryToWriter(message: Checkpoint, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Checkpoint;
  static deserializeBinaryFromReader(message: Checkpoint, reader: jspb.BinaryReader): Checkpoint;
}

export namespace Checkpoint {
  export type AsObject = {
    recordTime?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    offset?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset.AsObject,
  }
}

export class CompletionEndRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): CompletionEndRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CompletionEndRequest.AsObject;
  static toObject(includeInstance: boolean, msg: CompletionEndRequest): CompletionEndRequest.AsObject;
  static serializeBinaryToWriter(message: CompletionEndRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CompletionEndRequest;
  static deserializeBinaryFromReader(message: CompletionEndRequest, reader: jspb.BinaryReader): CompletionEndRequest;
}

export namespace CompletionEndRequest {
  export type AsObject = {
    ledgerId: string,
  }
}

export class CompletionEndResponse extends jspb.Message {
  getOffset(): com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset | undefined;
  setOffset(value?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset): CompletionEndResponse;
  hasOffset(): boolean;
  clearOffset(): CompletionEndResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CompletionEndResponse.AsObject;
  static toObject(includeInstance: boolean, msg: CompletionEndResponse): CompletionEndResponse.AsObject;
  static serializeBinaryToWriter(message: CompletionEndResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CompletionEndResponse;
  static deserializeBinaryFromReader(message: CompletionEndResponse, reader: jspb.BinaryReader): CompletionEndResponse;
}

export namespace CompletionEndResponse {
  export type AsObject = {
    offset?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset.AsObject,
  }
}

