import * as jspb from 'google-protobuf'



export class PruneRequest extends jspb.Message {
  getPruneUpTo(): string;
  setPruneUpTo(value: string): PruneRequest;

  getSubmissionId(): string;
  setSubmissionId(value: string): PruneRequest;

  getPruneAllDivulgedContracts(): boolean;
  setPruneAllDivulgedContracts(value: boolean): PruneRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): PruneRequest.AsObject;
  static toObject(includeInstance: boolean, msg: PruneRequest): PruneRequest.AsObject;
  static serializeBinaryToWriter(message: PruneRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): PruneRequest;
  static deserializeBinaryFromReader(message: PruneRequest, reader: jspb.BinaryReader): PruneRequest;
}

export namespace PruneRequest {
  export type AsObject = {
    pruneUpTo: string,
    submissionId: string,
    pruneAllDivulgedContracts: boolean,
  }
}

export class PruneResponse extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): PruneResponse.AsObject;
  static toObject(includeInstance: boolean, msg: PruneResponse): PruneResponse.AsObject;
  static serializeBinaryToWriter(message: PruneResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): PruneResponse;
  static deserializeBinaryFromReader(message: PruneResponse, reader: jspb.BinaryReader): PruneResponse;
}

export namespace PruneResponse {
  export type AsObject = {
  }
}

