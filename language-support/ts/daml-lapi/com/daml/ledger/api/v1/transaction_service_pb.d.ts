import * as jspb from 'google-protobuf'

import * as com_daml_ledger_api_v1_ledger_offset_pb from '../../../../../com/daml/ledger/api/v1/ledger_offset_pb';
import * as com_daml_ledger_api_v1_transaction_filter_pb from '../../../../../com/daml/ledger/api/v1/transaction_filter_pb';
import * as com_daml_ledger_api_v1_transaction_pb from '../../../../../com/daml/ledger/api/v1/transaction_pb';


export class GetTransactionsRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetTransactionsRequest;

  getBegin(): com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset | undefined;
  setBegin(value?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset): GetTransactionsRequest;
  hasBegin(): boolean;
  clearBegin(): GetTransactionsRequest;

  getEnd(): com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset | undefined;
  setEnd(value?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset): GetTransactionsRequest;
  hasEnd(): boolean;
  clearEnd(): GetTransactionsRequest;

  getFilter(): com_daml_ledger_api_v1_transaction_filter_pb.TransactionFilter | undefined;
  setFilter(value?: com_daml_ledger_api_v1_transaction_filter_pb.TransactionFilter): GetTransactionsRequest;
  hasFilter(): boolean;
  clearFilter(): GetTransactionsRequest;

  getVerbose(): boolean;
  setVerbose(value: boolean): GetTransactionsRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetTransactionsRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetTransactionsRequest): GetTransactionsRequest.AsObject;
  static serializeBinaryToWriter(message: GetTransactionsRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetTransactionsRequest;
  static deserializeBinaryFromReader(message: GetTransactionsRequest, reader: jspb.BinaryReader): GetTransactionsRequest;
}

export namespace GetTransactionsRequest {
  export type AsObject = {
    ledgerId: string,
    begin?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset.AsObject,
    end?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset.AsObject,
    filter?: com_daml_ledger_api_v1_transaction_filter_pb.TransactionFilter.AsObject,
    verbose: boolean,
  }
}

export class GetTransactionsResponse extends jspb.Message {
  getTransactionsList(): Array<com_daml_ledger_api_v1_transaction_pb.Transaction>;
  setTransactionsList(value: Array<com_daml_ledger_api_v1_transaction_pb.Transaction>): GetTransactionsResponse;
  clearTransactionsList(): GetTransactionsResponse;
  addTransactions(value?: com_daml_ledger_api_v1_transaction_pb.Transaction, index?: number): com_daml_ledger_api_v1_transaction_pb.Transaction;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetTransactionsResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetTransactionsResponse): GetTransactionsResponse.AsObject;
  static serializeBinaryToWriter(message: GetTransactionsResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetTransactionsResponse;
  static deserializeBinaryFromReader(message: GetTransactionsResponse, reader: jspb.BinaryReader): GetTransactionsResponse;
}

export namespace GetTransactionsResponse {
  export type AsObject = {
    transactionsList: Array<com_daml_ledger_api_v1_transaction_pb.Transaction.AsObject>,
  }
}

export class GetTransactionTreesResponse extends jspb.Message {
  getTransactionsList(): Array<com_daml_ledger_api_v1_transaction_pb.TransactionTree>;
  setTransactionsList(value: Array<com_daml_ledger_api_v1_transaction_pb.TransactionTree>): GetTransactionTreesResponse;
  clearTransactionsList(): GetTransactionTreesResponse;
  addTransactions(value?: com_daml_ledger_api_v1_transaction_pb.TransactionTree, index?: number): com_daml_ledger_api_v1_transaction_pb.TransactionTree;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetTransactionTreesResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetTransactionTreesResponse): GetTransactionTreesResponse.AsObject;
  static serializeBinaryToWriter(message: GetTransactionTreesResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetTransactionTreesResponse;
  static deserializeBinaryFromReader(message: GetTransactionTreesResponse, reader: jspb.BinaryReader): GetTransactionTreesResponse;
}

export namespace GetTransactionTreesResponse {
  export type AsObject = {
    transactionsList: Array<com_daml_ledger_api_v1_transaction_pb.TransactionTree.AsObject>,
  }
}

export class GetTransactionByEventIdRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetTransactionByEventIdRequest;

  getEventId(): string;
  setEventId(value: string): GetTransactionByEventIdRequest;

  getRequestingPartiesList(): Array<string>;
  setRequestingPartiesList(value: Array<string>): GetTransactionByEventIdRequest;
  clearRequestingPartiesList(): GetTransactionByEventIdRequest;
  addRequestingParties(value: string, index?: number): GetTransactionByEventIdRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetTransactionByEventIdRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetTransactionByEventIdRequest): GetTransactionByEventIdRequest.AsObject;
  static serializeBinaryToWriter(message: GetTransactionByEventIdRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetTransactionByEventIdRequest;
  static deserializeBinaryFromReader(message: GetTransactionByEventIdRequest, reader: jspb.BinaryReader): GetTransactionByEventIdRequest;
}

export namespace GetTransactionByEventIdRequest {
  export type AsObject = {
    ledgerId: string,
    eventId: string,
    requestingPartiesList: Array<string>,
  }
}

export class GetTransactionByIdRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetTransactionByIdRequest;

  getTransactionId(): string;
  setTransactionId(value: string): GetTransactionByIdRequest;

  getRequestingPartiesList(): Array<string>;
  setRequestingPartiesList(value: Array<string>): GetTransactionByIdRequest;
  clearRequestingPartiesList(): GetTransactionByIdRequest;
  addRequestingParties(value: string, index?: number): GetTransactionByIdRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetTransactionByIdRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetTransactionByIdRequest): GetTransactionByIdRequest.AsObject;
  static serializeBinaryToWriter(message: GetTransactionByIdRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetTransactionByIdRequest;
  static deserializeBinaryFromReader(message: GetTransactionByIdRequest, reader: jspb.BinaryReader): GetTransactionByIdRequest;
}

export namespace GetTransactionByIdRequest {
  export type AsObject = {
    ledgerId: string,
    transactionId: string,
    requestingPartiesList: Array<string>,
  }
}

export class GetTransactionResponse extends jspb.Message {
  getTransaction(): com_daml_ledger_api_v1_transaction_pb.TransactionTree | undefined;
  setTransaction(value?: com_daml_ledger_api_v1_transaction_pb.TransactionTree): GetTransactionResponse;
  hasTransaction(): boolean;
  clearTransaction(): GetTransactionResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetTransactionResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetTransactionResponse): GetTransactionResponse.AsObject;
  static serializeBinaryToWriter(message: GetTransactionResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetTransactionResponse;
  static deserializeBinaryFromReader(message: GetTransactionResponse, reader: jspb.BinaryReader): GetTransactionResponse;
}

export namespace GetTransactionResponse {
  export type AsObject = {
    transaction?: com_daml_ledger_api_v1_transaction_pb.TransactionTree.AsObject,
  }
}

export class GetFlatTransactionResponse extends jspb.Message {
  getTransaction(): com_daml_ledger_api_v1_transaction_pb.Transaction | undefined;
  setTransaction(value?: com_daml_ledger_api_v1_transaction_pb.Transaction): GetFlatTransactionResponse;
  hasTransaction(): boolean;
  clearTransaction(): GetFlatTransactionResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetFlatTransactionResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetFlatTransactionResponse): GetFlatTransactionResponse.AsObject;
  static serializeBinaryToWriter(message: GetFlatTransactionResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetFlatTransactionResponse;
  static deserializeBinaryFromReader(message: GetFlatTransactionResponse, reader: jspb.BinaryReader): GetFlatTransactionResponse;
}

export namespace GetFlatTransactionResponse {
  export type AsObject = {
    transaction?: com_daml_ledger_api_v1_transaction_pb.Transaction.AsObject,
  }
}

export class GetLedgerEndRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetLedgerEndRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetLedgerEndRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetLedgerEndRequest): GetLedgerEndRequest.AsObject;
  static serializeBinaryToWriter(message: GetLedgerEndRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetLedgerEndRequest;
  static deserializeBinaryFromReader(message: GetLedgerEndRequest, reader: jspb.BinaryReader): GetLedgerEndRequest;
}

export namespace GetLedgerEndRequest {
  export type AsObject = {
    ledgerId: string,
  }
}

export class GetLedgerEndResponse extends jspb.Message {
  getOffset(): com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset | undefined;
  setOffset(value?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset): GetLedgerEndResponse;
  hasOffset(): boolean;
  clearOffset(): GetLedgerEndResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetLedgerEndResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetLedgerEndResponse): GetLedgerEndResponse.AsObject;
  static serializeBinaryToWriter(message: GetLedgerEndResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetLedgerEndResponse;
  static deserializeBinaryFromReader(message: GetLedgerEndResponse, reader: jspb.BinaryReader): GetLedgerEndResponse;
}

export namespace GetLedgerEndResponse {
  export type AsObject = {
    offset?: com_daml_ledger_api_v1_ledger_offset_pb.LedgerOffset.AsObject,
  }
}

