import * as jspb from 'google-protobuf'



export class GetLedgerIdentityRequest extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetLedgerIdentityRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetLedgerIdentityRequest): GetLedgerIdentityRequest.AsObject;
  static serializeBinaryToWriter(message: GetLedgerIdentityRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetLedgerIdentityRequest;
  static deserializeBinaryFromReader(message: GetLedgerIdentityRequest, reader: jspb.BinaryReader): GetLedgerIdentityRequest;
}

export namespace GetLedgerIdentityRequest {
  export type AsObject = {
  }
}

export class GetLedgerIdentityResponse extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetLedgerIdentityResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetLedgerIdentityResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetLedgerIdentityResponse): GetLedgerIdentityResponse.AsObject;
  static serializeBinaryToWriter(message: GetLedgerIdentityResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetLedgerIdentityResponse;
  static deserializeBinaryFromReader(message: GetLedgerIdentityResponse, reader: jspb.BinaryReader): GetLedgerIdentityResponse;
}

export namespace GetLedgerIdentityResponse {
  export type AsObject = {
    ledgerId: string,
  }
}

