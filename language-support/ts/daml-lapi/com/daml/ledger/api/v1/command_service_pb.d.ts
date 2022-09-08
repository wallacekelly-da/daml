import * as jspb from 'google-protobuf'

import * as com_daml_ledger_api_v1_commands_pb from '../../../../../com/daml/ledger/api/v1/commands_pb';
import * as com_daml_ledger_api_v1_transaction_pb from '../../../../../com/daml/ledger/api/v1/transaction_pb';
import * as google_protobuf_empty_pb from 'google-protobuf/google/protobuf/empty_pb';


export class SubmitAndWaitRequest extends jspb.Message {
  getCommands(): com_daml_ledger_api_v1_commands_pb.Commands | undefined;
  setCommands(value?: com_daml_ledger_api_v1_commands_pb.Commands): SubmitAndWaitRequest;
  hasCommands(): boolean;
  clearCommands(): SubmitAndWaitRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SubmitAndWaitRequest.AsObject;
  static toObject(includeInstance: boolean, msg: SubmitAndWaitRequest): SubmitAndWaitRequest.AsObject;
  static serializeBinaryToWriter(message: SubmitAndWaitRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SubmitAndWaitRequest;
  static deserializeBinaryFromReader(message: SubmitAndWaitRequest, reader: jspb.BinaryReader): SubmitAndWaitRequest;
}

export namespace SubmitAndWaitRequest {
  export type AsObject = {
    commands?: com_daml_ledger_api_v1_commands_pb.Commands.AsObject,
  }
}

export class SubmitAndWaitForTransactionIdResponse extends jspb.Message {
  getTransactionId(): string;
  setTransactionId(value: string): SubmitAndWaitForTransactionIdResponse;

  getCompletionOffset(): string;
  setCompletionOffset(value: string): SubmitAndWaitForTransactionIdResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SubmitAndWaitForTransactionIdResponse.AsObject;
  static toObject(includeInstance: boolean, msg: SubmitAndWaitForTransactionIdResponse): SubmitAndWaitForTransactionIdResponse.AsObject;
  static serializeBinaryToWriter(message: SubmitAndWaitForTransactionIdResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SubmitAndWaitForTransactionIdResponse;
  static deserializeBinaryFromReader(message: SubmitAndWaitForTransactionIdResponse, reader: jspb.BinaryReader): SubmitAndWaitForTransactionIdResponse;
}

export namespace SubmitAndWaitForTransactionIdResponse {
  export type AsObject = {
    transactionId: string,
    completionOffset: string,
  }
}

export class SubmitAndWaitForTransactionResponse extends jspb.Message {
  getTransaction(): com_daml_ledger_api_v1_transaction_pb.Transaction | undefined;
  setTransaction(value?: com_daml_ledger_api_v1_transaction_pb.Transaction): SubmitAndWaitForTransactionResponse;
  hasTransaction(): boolean;
  clearTransaction(): SubmitAndWaitForTransactionResponse;

  getCompletionOffset(): string;
  setCompletionOffset(value: string): SubmitAndWaitForTransactionResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SubmitAndWaitForTransactionResponse.AsObject;
  static toObject(includeInstance: boolean, msg: SubmitAndWaitForTransactionResponse): SubmitAndWaitForTransactionResponse.AsObject;
  static serializeBinaryToWriter(message: SubmitAndWaitForTransactionResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SubmitAndWaitForTransactionResponse;
  static deserializeBinaryFromReader(message: SubmitAndWaitForTransactionResponse, reader: jspb.BinaryReader): SubmitAndWaitForTransactionResponse;
}

export namespace SubmitAndWaitForTransactionResponse {
  export type AsObject = {
    transaction?: com_daml_ledger_api_v1_transaction_pb.Transaction.AsObject,
    completionOffset: string,
  }
}

export class SubmitAndWaitForTransactionTreeResponse extends jspb.Message {
  getTransaction(): com_daml_ledger_api_v1_transaction_pb.TransactionTree | undefined;
  setTransaction(value?: com_daml_ledger_api_v1_transaction_pb.TransactionTree): SubmitAndWaitForTransactionTreeResponse;
  hasTransaction(): boolean;
  clearTransaction(): SubmitAndWaitForTransactionTreeResponse;

  getCompletionOffset(): string;
  setCompletionOffset(value: string): SubmitAndWaitForTransactionTreeResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SubmitAndWaitForTransactionTreeResponse.AsObject;
  static toObject(includeInstance: boolean, msg: SubmitAndWaitForTransactionTreeResponse): SubmitAndWaitForTransactionTreeResponse.AsObject;
  static serializeBinaryToWriter(message: SubmitAndWaitForTransactionTreeResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SubmitAndWaitForTransactionTreeResponse;
  static deserializeBinaryFromReader(message: SubmitAndWaitForTransactionTreeResponse, reader: jspb.BinaryReader): SubmitAndWaitForTransactionTreeResponse;
}

export namespace SubmitAndWaitForTransactionTreeResponse {
  export type AsObject = {
    transaction?: com_daml_ledger_api_v1_transaction_pb.TransactionTree.AsObject,
    completionOffset: string,
  }
}

