import * as jspb from 'google-protobuf'

import * as com_daml_ledger_api_v1_event_pb from '../../../../../com/daml/ledger/api/v1/event_pb';
import * as com_daml_ledger_api_v1_transaction_filter_pb from '../../../../../com/daml/ledger/api/v1/transaction_filter_pb';


export class GetActiveContractsRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetActiveContractsRequest;

  getFilter(): com_daml_ledger_api_v1_transaction_filter_pb.TransactionFilter | undefined;
  setFilter(value?: com_daml_ledger_api_v1_transaction_filter_pb.TransactionFilter): GetActiveContractsRequest;
  hasFilter(): boolean;
  clearFilter(): GetActiveContractsRequest;

  getVerbose(): boolean;
  setVerbose(value: boolean): GetActiveContractsRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetActiveContractsRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetActiveContractsRequest): GetActiveContractsRequest.AsObject;
  static serializeBinaryToWriter(message: GetActiveContractsRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetActiveContractsRequest;
  static deserializeBinaryFromReader(message: GetActiveContractsRequest, reader: jspb.BinaryReader): GetActiveContractsRequest;
}

export namespace GetActiveContractsRequest {
  export type AsObject = {
    ledgerId: string,
    filter?: com_daml_ledger_api_v1_transaction_filter_pb.TransactionFilter.AsObject,
    verbose: boolean,
  }
}

export class GetActiveContractsResponse extends jspb.Message {
  getOffset(): string;
  setOffset(value: string): GetActiveContractsResponse;

  getWorkflowId(): string;
  setWorkflowId(value: string): GetActiveContractsResponse;

  getActiveContractsList(): Array<com_daml_ledger_api_v1_event_pb.CreatedEvent>;
  setActiveContractsList(value: Array<com_daml_ledger_api_v1_event_pb.CreatedEvent>): GetActiveContractsResponse;
  clearActiveContractsList(): GetActiveContractsResponse;
  addActiveContracts(value?: com_daml_ledger_api_v1_event_pb.CreatedEvent, index?: number): com_daml_ledger_api_v1_event_pb.CreatedEvent;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetActiveContractsResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetActiveContractsResponse): GetActiveContractsResponse.AsObject;
  static serializeBinaryToWriter(message: GetActiveContractsResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetActiveContractsResponse;
  static deserializeBinaryFromReader(message: GetActiveContractsResponse, reader: jspb.BinaryReader): GetActiveContractsResponse;
}

export namespace GetActiveContractsResponse {
  export type AsObject = {
    offset: string,
    workflowId: string,
    activeContractsList: Array<com_daml_ledger_api_v1_event_pb.CreatedEvent.AsObject>,
  }
}

