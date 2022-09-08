import * as jspb from 'google-protobuf'

import * as google_protobuf_timestamp_pb from 'google-protobuf/google/protobuf/timestamp_pb';


export class ListKnownPackagesRequest extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListKnownPackagesRequest.AsObject;
  static toObject(includeInstance: boolean, msg: ListKnownPackagesRequest): ListKnownPackagesRequest.AsObject;
  static serializeBinaryToWriter(message: ListKnownPackagesRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListKnownPackagesRequest;
  static deserializeBinaryFromReader(message: ListKnownPackagesRequest, reader: jspb.BinaryReader): ListKnownPackagesRequest;
}

export namespace ListKnownPackagesRequest {
  export type AsObject = {
  }
}

export class ListKnownPackagesResponse extends jspb.Message {
  getPackageDetailsList(): Array<PackageDetails>;
  setPackageDetailsList(value: Array<PackageDetails>): ListKnownPackagesResponse;
  clearPackageDetailsList(): ListKnownPackagesResponse;
  addPackageDetails(value?: PackageDetails, index?: number): PackageDetails;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListKnownPackagesResponse.AsObject;
  static toObject(includeInstance: boolean, msg: ListKnownPackagesResponse): ListKnownPackagesResponse.AsObject;
  static serializeBinaryToWriter(message: ListKnownPackagesResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListKnownPackagesResponse;
  static deserializeBinaryFromReader(message: ListKnownPackagesResponse, reader: jspb.BinaryReader): ListKnownPackagesResponse;
}

export namespace ListKnownPackagesResponse {
  export type AsObject = {
    packageDetailsList: Array<PackageDetails.AsObject>,
  }
}

export class PackageDetails extends jspb.Message {
  getPackageId(): string;
  setPackageId(value: string): PackageDetails;

  getPackageSize(): number;
  setPackageSize(value: number): PackageDetails;

  getKnownSince(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setKnownSince(value?: google_protobuf_timestamp_pb.Timestamp): PackageDetails;
  hasKnownSince(): boolean;
  clearKnownSince(): PackageDetails;

  getSourceDescription(): string;
  setSourceDescription(value: string): PackageDetails;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): PackageDetails.AsObject;
  static toObject(includeInstance: boolean, msg: PackageDetails): PackageDetails.AsObject;
  static serializeBinaryToWriter(message: PackageDetails, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): PackageDetails;
  static deserializeBinaryFromReader(message: PackageDetails, reader: jspb.BinaryReader): PackageDetails;
}

export namespace PackageDetails {
  export type AsObject = {
    packageId: string,
    packageSize: number,
    knownSince?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    sourceDescription: string,
  }
}

export class UploadDarFileRequest extends jspb.Message {
  getDarFile(): Uint8Array | string;
  getDarFile_asU8(): Uint8Array;
  getDarFile_asB64(): string;
  setDarFile(value: Uint8Array | string): UploadDarFileRequest;

  getSubmissionId(): string;
  setSubmissionId(value: string): UploadDarFileRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): UploadDarFileRequest.AsObject;
  static toObject(includeInstance: boolean, msg: UploadDarFileRequest): UploadDarFileRequest.AsObject;
  static serializeBinaryToWriter(message: UploadDarFileRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): UploadDarFileRequest;
  static deserializeBinaryFromReader(message: UploadDarFileRequest, reader: jspb.BinaryReader): UploadDarFileRequest;
}

export namespace UploadDarFileRequest {
  export type AsObject = {
    darFile: Uint8Array | string,
    submissionId: string,
  }
}

export class UploadDarFileResponse extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): UploadDarFileResponse.AsObject;
  static toObject(includeInstance: boolean, msg: UploadDarFileResponse): UploadDarFileResponse.AsObject;
  static serializeBinaryToWriter(message: UploadDarFileResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): UploadDarFileResponse;
  static deserializeBinaryFromReader(message: UploadDarFileResponse, reader: jspb.BinaryReader): UploadDarFileResponse;
}

export namespace UploadDarFileResponse {
  export type AsObject = {
  }
}

