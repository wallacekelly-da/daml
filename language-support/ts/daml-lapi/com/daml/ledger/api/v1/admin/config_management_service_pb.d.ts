import * as jspb from 'google-protobuf'

import * as google_protobuf_duration_pb from 'google-protobuf/google/protobuf/duration_pb';
import * as google_protobuf_timestamp_pb from 'google-protobuf/google/protobuf/timestamp_pb';


export class GetTimeModelRequest extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetTimeModelRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetTimeModelRequest): GetTimeModelRequest.AsObject;
  static serializeBinaryToWriter(message: GetTimeModelRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetTimeModelRequest;
  static deserializeBinaryFromReader(message: GetTimeModelRequest, reader: jspb.BinaryReader): GetTimeModelRequest;
}

export namespace GetTimeModelRequest {
  export type AsObject = {
  }
}

export class GetTimeModelResponse extends jspb.Message {
  getConfigurationGeneration(): number;
  setConfigurationGeneration(value: number): GetTimeModelResponse;

  getTimeModel(): TimeModel | undefined;
  setTimeModel(value?: TimeModel): GetTimeModelResponse;
  hasTimeModel(): boolean;
  clearTimeModel(): GetTimeModelResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetTimeModelResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetTimeModelResponse): GetTimeModelResponse.AsObject;
  static serializeBinaryToWriter(message: GetTimeModelResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetTimeModelResponse;
  static deserializeBinaryFromReader(message: GetTimeModelResponse, reader: jspb.BinaryReader): GetTimeModelResponse;
}

export namespace GetTimeModelResponse {
  export type AsObject = {
    configurationGeneration: number,
    timeModel?: TimeModel.AsObject,
  }
}

export class SetTimeModelRequest extends jspb.Message {
  getSubmissionId(): string;
  setSubmissionId(value: string): SetTimeModelRequest;

  getMaximumRecordTime(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setMaximumRecordTime(value?: google_protobuf_timestamp_pb.Timestamp): SetTimeModelRequest;
  hasMaximumRecordTime(): boolean;
  clearMaximumRecordTime(): SetTimeModelRequest;

  getConfigurationGeneration(): number;
  setConfigurationGeneration(value: number): SetTimeModelRequest;

  getNewTimeModel(): TimeModel | undefined;
  setNewTimeModel(value?: TimeModel): SetTimeModelRequest;
  hasNewTimeModel(): boolean;
  clearNewTimeModel(): SetTimeModelRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SetTimeModelRequest.AsObject;
  static toObject(includeInstance: boolean, msg: SetTimeModelRequest): SetTimeModelRequest.AsObject;
  static serializeBinaryToWriter(message: SetTimeModelRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SetTimeModelRequest;
  static deserializeBinaryFromReader(message: SetTimeModelRequest, reader: jspb.BinaryReader): SetTimeModelRequest;
}

export namespace SetTimeModelRequest {
  export type AsObject = {
    submissionId: string,
    maximumRecordTime?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    configurationGeneration: number,
    newTimeModel?: TimeModel.AsObject,
  }
}

export class SetTimeModelResponse extends jspb.Message {
  getConfigurationGeneration(): number;
  setConfigurationGeneration(value: number): SetTimeModelResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SetTimeModelResponse.AsObject;
  static toObject(includeInstance: boolean, msg: SetTimeModelResponse): SetTimeModelResponse.AsObject;
  static serializeBinaryToWriter(message: SetTimeModelResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SetTimeModelResponse;
  static deserializeBinaryFromReader(message: SetTimeModelResponse, reader: jspb.BinaryReader): SetTimeModelResponse;
}

export namespace SetTimeModelResponse {
  export type AsObject = {
    configurationGeneration: number,
  }
}

export class TimeModel extends jspb.Message {
  getAvgTransactionLatency(): google_protobuf_duration_pb.Duration | undefined;
  setAvgTransactionLatency(value?: google_protobuf_duration_pb.Duration): TimeModel;
  hasAvgTransactionLatency(): boolean;
  clearAvgTransactionLatency(): TimeModel;

  getMinSkew(): google_protobuf_duration_pb.Duration | undefined;
  setMinSkew(value?: google_protobuf_duration_pb.Duration): TimeModel;
  hasMinSkew(): boolean;
  clearMinSkew(): TimeModel;

  getMaxSkew(): google_protobuf_duration_pb.Duration | undefined;
  setMaxSkew(value?: google_protobuf_duration_pb.Duration): TimeModel;
  hasMaxSkew(): boolean;
  clearMaxSkew(): TimeModel;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): TimeModel.AsObject;
  static toObject(includeInstance: boolean, msg: TimeModel): TimeModel.AsObject;
  static serializeBinaryToWriter(message: TimeModel, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): TimeModel;
  static deserializeBinaryFromReader(message: TimeModel, reader: jspb.BinaryReader): TimeModel;
}

export namespace TimeModel {
  export type AsObject = {
    avgTransactionLatency?: google_protobuf_duration_pb.Duration.AsObject,
    minSkew?: google_protobuf_duration_pb.Duration.AsObject,
    maxSkew?: google_protobuf_duration_pb.Duration.AsObject,
  }
}

