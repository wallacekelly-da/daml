import * as jspb from 'google-protobuf'

import * as google_protobuf_duration_pb from 'google-protobuf/google/protobuf/duration_pb';
import * as google_rpc_status_pb from '../../../../../google/rpc/status_pb';


export class Completion extends jspb.Message {
  getCommandId(): string;
  setCommandId(value: string): Completion;

  getStatus(): google_rpc_status_pb.Status | undefined;
  setStatus(value?: google_rpc_status_pb.Status): Completion;
  hasStatus(): boolean;
  clearStatus(): Completion;

  getTransactionId(): string;
  setTransactionId(value: string): Completion;

  getApplicationId(): string;
  setApplicationId(value: string): Completion;

  getActAsList(): Array<string>;
  setActAsList(value: Array<string>): Completion;
  clearActAsList(): Completion;
  addActAs(value: string, index?: number): Completion;

  getSubmissionId(): string;
  setSubmissionId(value: string): Completion;

  getDeduplicationOffset(): string;
  setDeduplicationOffset(value: string): Completion;

  getDeduplicationDuration(): google_protobuf_duration_pb.Duration | undefined;
  setDeduplicationDuration(value?: google_protobuf_duration_pb.Duration): Completion;
  hasDeduplicationDuration(): boolean;
  clearDeduplicationDuration(): Completion;

  getDeduplicationPeriodCase(): Completion.DeduplicationPeriodCase;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Completion.AsObject;
  static toObject(includeInstance: boolean, msg: Completion): Completion.AsObject;
  static serializeBinaryToWriter(message: Completion, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Completion;
  static deserializeBinaryFromReader(message: Completion, reader: jspb.BinaryReader): Completion;
}

export namespace Completion {
  export type AsObject = {
    commandId: string,
    status?: google_rpc_status_pb.Status.AsObject,
    transactionId: string,
    applicationId: string,
    actAsList: Array<string>,
    submissionId: string,
    deduplicationOffset: string,
    deduplicationDuration?: google_protobuf_duration_pb.Duration.AsObject,
  }

  export enum DeduplicationPeriodCase { 
    DEDUPLICATION_PERIOD_NOT_SET = 0,
    DEDUPLICATION_OFFSET = 8,
    DEDUPLICATION_DURATION = 9,
  }
}

