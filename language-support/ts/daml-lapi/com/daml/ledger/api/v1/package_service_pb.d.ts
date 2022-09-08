import * as jspb from 'google-protobuf'



export class ListPackagesRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): ListPackagesRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListPackagesRequest.AsObject;
  static toObject(includeInstance: boolean, msg: ListPackagesRequest): ListPackagesRequest.AsObject;
  static serializeBinaryToWriter(message: ListPackagesRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListPackagesRequest;
  static deserializeBinaryFromReader(message: ListPackagesRequest, reader: jspb.BinaryReader): ListPackagesRequest;
}

export namespace ListPackagesRequest {
  export type AsObject = {
    ledgerId: string,
  }
}

export class ListPackagesResponse extends jspb.Message {
  getPackageIdsList(): Array<string>;
  setPackageIdsList(value: Array<string>): ListPackagesResponse;
  clearPackageIdsList(): ListPackagesResponse;
  addPackageIds(value: string, index?: number): ListPackagesResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListPackagesResponse.AsObject;
  static toObject(includeInstance: boolean, msg: ListPackagesResponse): ListPackagesResponse.AsObject;
  static serializeBinaryToWriter(message: ListPackagesResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListPackagesResponse;
  static deserializeBinaryFromReader(message: ListPackagesResponse, reader: jspb.BinaryReader): ListPackagesResponse;
}

export namespace ListPackagesResponse {
  export type AsObject = {
    packageIdsList: Array<string>,
  }
}

export class GetPackageRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetPackageRequest;

  getPackageId(): string;
  setPackageId(value: string): GetPackageRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetPackageRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetPackageRequest): GetPackageRequest.AsObject;
  static serializeBinaryToWriter(message: GetPackageRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetPackageRequest;
  static deserializeBinaryFromReader(message: GetPackageRequest, reader: jspb.BinaryReader): GetPackageRequest;
}

export namespace GetPackageRequest {
  export type AsObject = {
    ledgerId: string,
    packageId: string,
  }
}

export class GetPackageResponse extends jspb.Message {
  getHashFunction(): HashFunction;
  setHashFunction(value: HashFunction): GetPackageResponse;

  getArchivePayload(): Uint8Array | string;
  getArchivePayload_asU8(): Uint8Array;
  getArchivePayload_asB64(): string;
  setArchivePayload(value: Uint8Array | string): GetPackageResponse;

  getHash(): string;
  setHash(value: string): GetPackageResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetPackageResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetPackageResponse): GetPackageResponse.AsObject;
  static serializeBinaryToWriter(message: GetPackageResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetPackageResponse;
  static deserializeBinaryFromReader(message: GetPackageResponse, reader: jspb.BinaryReader): GetPackageResponse;
}

export namespace GetPackageResponse {
  export type AsObject = {
    hashFunction: HashFunction,
    archivePayload: Uint8Array | string,
    hash: string,
  }
}

export class GetPackageStatusRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetPackageStatusRequest;

  getPackageId(): string;
  setPackageId(value: string): GetPackageStatusRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetPackageStatusRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetPackageStatusRequest): GetPackageStatusRequest.AsObject;
  static serializeBinaryToWriter(message: GetPackageStatusRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetPackageStatusRequest;
  static deserializeBinaryFromReader(message: GetPackageStatusRequest, reader: jspb.BinaryReader): GetPackageStatusRequest;
}

export namespace GetPackageStatusRequest {
  export type AsObject = {
    ledgerId: string,
    packageId: string,
  }
}

export class GetPackageStatusResponse extends jspb.Message {
  getPackageStatus(): PackageStatus;
  setPackageStatus(value: PackageStatus): GetPackageStatusResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetPackageStatusResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetPackageStatusResponse): GetPackageStatusResponse.AsObject;
  static serializeBinaryToWriter(message: GetPackageStatusResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetPackageStatusResponse;
  static deserializeBinaryFromReader(message: GetPackageStatusResponse, reader: jspb.BinaryReader): GetPackageStatusResponse;
}

export namespace GetPackageStatusResponse {
  export type AsObject = {
    packageStatus: PackageStatus,
  }
}

export enum PackageStatus { 
  UNKNOWN = 0,
  REGISTERED = 1,
}
export enum HashFunction { 
  SHA256 = 0,
}
