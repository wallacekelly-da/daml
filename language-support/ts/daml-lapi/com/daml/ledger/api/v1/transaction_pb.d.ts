import * as jspb from 'google-protobuf'

import * as com_daml_ledger_api_v1_event_pb from '../../../../../com/daml/ledger/api/v1/event_pb';
import * as google_protobuf_timestamp_pb from 'google-protobuf/google/protobuf/timestamp_pb';


export class TransactionTree extends jspb.Message {
  getTransactionId(): string;
  setTransactionId(value: string): TransactionTree;

  getCommandId(): string;
  setCommandId(value: string): TransactionTree;

  getWorkflowId(): string;
  setWorkflowId(value: string): TransactionTree;

  getEffectiveAt(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setEffectiveAt(value?: google_protobuf_timestamp_pb.Timestamp): TransactionTree;
  hasEffectiveAt(): boolean;
  clearEffectiveAt(): TransactionTree;

  getOffset(): string;
  setOffset(value: string): TransactionTree;

  getEventsByIdMap(): jspb.Map<string, TreeEvent>;
  clearEventsByIdMap(): TransactionTree;

  getRootEventIdsList(): Array<string>;
  setRootEventIdsList(value: Array<string>): TransactionTree;
  clearRootEventIdsList(): TransactionTree;
  addRootEventIds(value: string, index?: number): TransactionTree;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): TransactionTree.AsObject;
  static toObject(includeInstance: boolean, msg: TransactionTree): TransactionTree.AsObject;
  static serializeBinaryToWriter(message: TransactionTree, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): TransactionTree;
  static deserializeBinaryFromReader(message: TransactionTree, reader: jspb.BinaryReader): TransactionTree;
}

export namespace TransactionTree {
  export type AsObject = {
    transactionId: string,
    commandId: string,
    workflowId: string,
    effectiveAt?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    offset: string,
    eventsByIdMap: Array<[string, TreeEvent.AsObject]>,
    rootEventIdsList: Array<string>,
  }
}

export class TreeEvent extends jspb.Message {
  getCreated(): com_daml_ledger_api_v1_event_pb.CreatedEvent | undefined;
  setCreated(value?: com_daml_ledger_api_v1_event_pb.CreatedEvent): TreeEvent;
  hasCreated(): boolean;
  clearCreated(): TreeEvent;

  getExercised(): com_daml_ledger_api_v1_event_pb.ExercisedEvent | undefined;
  setExercised(value?: com_daml_ledger_api_v1_event_pb.ExercisedEvent): TreeEvent;
  hasExercised(): boolean;
  clearExercised(): TreeEvent;

  getKindCase(): TreeEvent.KindCase;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): TreeEvent.AsObject;
  static toObject(includeInstance: boolean, msg: TreeEvent): TreeEvent.AsObject;
  static serializeBinaryToWriter(message: TreeEvent, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): TreeEvent;
  static deserializeBinaryFromReader(message: TreeEvent, reader: jspb.BinaryReader): TreeEvent;
}

export namespace TreeEvent {
  export type AsObject = {
    created?: com_daml_ledger_api_v1_event_pb.CreatedEvent.AsObject,
    exercised?: com_daml_ledger_api_v1_event_pb.ExercisedEvent.AsObject,
  }

  export enum KindCase { 
    KIND_NOT_SET = 0,
    CREATED = 1,
    EXERCISED = 2,
  }
}

export class Transaction extends jspb.Message {
  getTransactionId(): string;
  setTransactionId(value: string): Transaction;

  getCommandId(): string;
  setCommandId(value: string): Transaction;

  getWorkflowId(): string;
  setWorkflowId(value: string): Transaction;

  getEffectiveAt(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setEffectiveAt(value?: google_protobuf_timestamp_pb.Timestamp): Transaction;
  hasEffectiveAt(): boolean;
  clearEffectiveAt(): Transaction;

  getEventsList(): Array<com_daml_ledger_api_v1_event_pb.Event>;
  setEventsList(value: Array<com_daml_ledger_api_v1_event_pb.Event>): Transaction;
  clearEventsList(): Transaction;
  addEvents(value?: com_daml_ledger_api_v1_event_pb.Event, index?: number): com_daml_ledger_api_v1_event_pb.Event;

  getOffset(): string;
  setOffset(value: string): Transaction;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Transaction.AsObject;
  static toObject(includeInstance: boolean, msg: Transaction): Transaction.AsObject;
  static serializeBinaryToWriter(message: Transaction, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Transaction;
  static deserializeBinaryFromReader(message: Transaction, reader: jspb.BinaryReader): Transaction;
}

export namespace Transaction {
  export type AsObject = {
    transactionId: string,
    commandId: string,
    workflowId: string,
    effectiveAt?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    eventsList: Array<com_daml_ledger_api_v1_event_pb.Event.AsObject>,
    offset: string,
  }
}

