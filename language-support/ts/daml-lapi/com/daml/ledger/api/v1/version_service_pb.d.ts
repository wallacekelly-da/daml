import * as jspb from 'google-protobuf'

import * as com_daml_ledger_api_v1_experimental_features_pb from '../../../../../com/daml/ledger/api/v1/experimental_features_pb';


export class GetLedgerApiVersionRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetLedgerApiVersionRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetLedgerApiVersionRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetLedgerApiVersionRequest): GetLedgerApiVersionRequest.AsObject;
  static serializeBinaryToWriter(message: GetLedgerApiVersionRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetLedgerApiVersionRequest;
  static deserializeBinaryFromReader(message: GetLedgerApiVersionRequest, reader: jspb.BinaryReader): GetLedgerApiVersionRequest;
}

export namespace GetLedgerApiVersionRequest {
  export type AsObject = {
    ledgerId: string,
  }
}

export class GetLedgerApiVersionResponse extends jspb.Message {
  getVersion(): string;
  setVersion(value: string): GetLedgerApiVersionResponse;

  getFeatures(): FeaturesDescriptor | undefined;
  setFeatures(value?: FeaturesDescriptor): GetLedgerApiVersionResponse;
  hasFeatures(): boolean;
  clearFeatures(): GetLedgerApiVersionResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetLedgerApiVersionResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetLedgerApiVersionResponse): GetLedgerApiVersionResponse.AsObject;
  static serializeBinaryToWriter(message: GetLedgerApiVersionResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetLedgerApiVersionResponse;
  static deserializeBinaryFromReader(message: GetLedgerApiVersionResponse, reader: jspb.BinaryReader): GetLedgerApiVersionResponse;
}

export namespace GetLedgerApiVersionResponse {
  export type AsObject = {
    version: string,
    features?: FeaturesDescriptor.AsObject,
  }
}

export class FeaturesDescriptor extends jspb.Message {
  getUserManagement(): UserManagementFeature | undefined;
  setUserManagement(value?: UserManagementFeature): FeaturesDescriptor;
  hasUserManagement(): boolean;
  clearUserManagement(): FeaturesDescriptor;

  getExperimental(): com_daml_ledger_api_v1_experimental_features_pb.ExperimentalFeatures | undefined;
  setExperimental(value?: com_daml_ledger_api_v1_experimental_features_pb.ExperimentalFeatures): FeaturesDescriptor;
  hasExperimental(): boolean;
  clearExperimental(): FeaturesDescriptor;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): FeaturesDescriptor.AsObject;
  static toObject(includeInstance: boolean, msg: FeaturesDescriptor): FeaturesDescriptor.AsObject;
  static serializeBinaryToWriter(message: FeaturesDescriptor, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): FeaturesDescriptor;
  static deserializeBinaryFromReader(message: FeaturesDescriptor, reader: jspb.BinaryReader): FeaturesDescriptor;
}

export namespace FeaturesDescriptor {
  export type AsObject = {
    userManagement?: UserManagementFeature.AsObject,
    experimental?: com_daml_ledger_api_v1_experimental_features_pb.ExperimentalFeatures.AsObject,
  }
}

export class UserManagementFeature extends jspb.Message {
  getSupported(): boolean;
  setSupported(value: boolean): UserManagementFeature;

  getMaxRightsPerUser(): number;
  setMaxRightsPerUser(value: number): UserManagementFeature;

  getMaxUsersPageSize(): number;
  setMaxUsersPageSize(value: number): UserManagementFeature;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): UserManagementFeature.AsObject;
  static toObject(includeInstance: boolean, msg: UserManagementFeature): UserManagementFeature.AsObject;
  static serializeBinaryToWriter(message: UserManagementFeature, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): UserManagementFeature;
  static deserializeBinaryFromReader(message: UserManagementFeature, reader: jspb.BinaryReader): UserManagementFeature;
}

export namespace UserManagementFeature {
  export type AsObject = {
    supported: boolean,
    maxRightsPerUser: number,
    maxUsersPageSize: number,
  }
}

