import * as jspb from 'google-protobuf'

import * as google_protobuf_empty_pb from 'google-protobuf/google/protobuf/empty_pb';
import * as google_protobuf_timestamp_pb from 'google-protobuf/google/protobuf/timestamp_pb';


export class GetTimeRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetTimeRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetTimeRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetTimeRequest): GetTimeRequest.AsObject;
  static serializeBinaryToWriter(message: GetTimeRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetTimeRequest;
  static deserializeBinaryFromReader(message: GetTimeRequest, reader: jspb.BinaryReader): GetTimeRequest;
}

export namespace GetTimeRequest {
  export type AsObject = {
    ledgerId: string,
  }
}

export class GetTimeResponse extends jspb.Message {
  getCurrentTime(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setCurrentTime(value?: google_protobuf_timestamp_pb.Timestamp): GetTimeResponse;
  hasCurrentTime(): boolean;
  clearCurrentTime(): GetTimeResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetTimeResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetTimeResponse): GetTimeResponse.AsObject;
  static serializeBinaryToWriter(message: GetTimeResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetTimeResponse;
  static deserializeBinaryFromReader(message: GetTimeResponse, reader: jspb.BinaryReader): GetTimeResponse;
}

export namespace GetTimeResponse {
  export type AsObject = {
    currentTime?: google_protobuf_timestamp_pb.Timestamp.AsObject,
  }
}

export class SetTimeRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): SetTimeRequest;

  getCurrentTime(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setCurrentTime(value?: google_protobuf_timestamp_pb.Timestamp): SetTimeRequest;
  hasCurrentTime(): boolean;
  clearCurrentTime(): SetTimeRequest;

  getNewTime(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setNewTime(value?: google_protobuf_timestamp_pb.Timestamp): SetTimeRequest;
  hasNewTime(): boolean;
  clearNewTime(): SetTimeRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SetTimeRequest.AsObject;
  static toObject(includeInstance: boolean, msg: SetTimeRequest): SetTimeRequest.AsObject;
  static serializeBinaryToWriter(message: SetTimeRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SetTimeRequest;
  static deserializeBinaryFromReader(message: SetTimeRequest, reader: jspb.BinaryReader): SetTimeRequest;
}

export namespace SetTimeRequest {
  export type AsObject = {
    ledgerId: string,
    currentTime?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    newTime?: google_protobuf_timestamp_pb.Timestamp.AsObject,
  }
}

