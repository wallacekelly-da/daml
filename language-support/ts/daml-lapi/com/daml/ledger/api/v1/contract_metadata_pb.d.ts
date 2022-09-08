import * as jspb from 'google-protobuf'

import * as google_protobuf_timestamp_pb from 'google-protobuf/google/protobuf/timestamp_pb';


export class ContractMetadata extends jspb.Message {
  getCreatedAt(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setCreatedAt(value?: google_protobuf_timestamp_pb.Timestamp): ContractMetadata;
  hasCreatedAt(): boolean;
  clearCreatedAt(): ContractMetadata;

  getContractKeyHash(): Uint8Array | string;
  getContractKeyHash_asU8(): Uint8Array;
  getContractKeyHash_asB64(): string;
  setContractKeyHash(value: Uint8Array | string): ContractMetadata;

  getDriverMetadata(): Uint8Array | string;
  getDriverMetadata_asU8(): Uint8Array;
  getDriverMetadata_asB64(): string;
  setDriverMetadata(value: Uint8Array | string): ContractMetadata;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ContractMetadata.AsObject;
  static toObject(includeInstance: boolean, msg: ContractMetadata): ContractMetadata.AsObject;
  static serializeBinaryToWriter(message: ContractMetadata, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ContractMetadata;
  static deserializeBinaryFromReader(message: ContractMetadata, reader: jspb.BinaryReader): ContractMetadata;
}

export namespace ContractMetadata {
  export type AsObject = {
    createdAt?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    contractKeyHash: Uint8Array | string,
    driverMetadata: Uint8Array | string,
  }
}

