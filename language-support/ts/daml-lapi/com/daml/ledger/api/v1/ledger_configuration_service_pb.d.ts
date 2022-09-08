import * as jspb from 'google-protobuf'

import * as google_protobuf_duration_pb from 'google-protobuf/google/protobuf/duration_pb';


export class GetLedgerConfigurationRequest extends jspb.Message {
  getLedgerId(): string;
  setLedgerId(value: string): GetLedgerConfigurationRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetLedgerConfigurationRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetLedgerConfigurationRequest): GetLedgerConfigurationRequest.AsObject;
  static serializeBinaryToWriter(message: GetLedgerConfigurationRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetLedgerConfigurationRequest;
  static deserializeBinaryFromReader(message: GetLedgerConfigurationRequest, reader: jspb.BinaryReader): GetLedgerConfigurationRequest;
}

export namespace GetLedgerConfigurationRequest {
  export type AsObject = {
    ledgerId: string,
  }
}

export class GetLedgerConfigurationResponse extends jspb.Message {
  getLedgerConfiguration(): LedgerConfiguration | undefined;
  setLedgerConfiguration(value?: LedgerConfiguration): GetLedgerConfigurationResponse;
  hasLedgerConfiguration(): boolean;
  clearLedgerConfiguration(): GetLedgerConfigurationResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetLedgerConfigurationResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetLedgerConfigurationResponse): GetLedgerConfigurationResponse.AsObject;
  static serializeBinaryToWriter(message: GetLedgerConfigurationResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetLedgerConfigurationResponse;
  static deserializeBinaryFromReader(message: GetLedgerConfigurationResponse, reader: jspb.BinaryReader): GetLedgerConfigurationResponse;
}

export namespace GetLedgerConfigurationResponse {
  export type AsObject = {
    ledgerConfiguration?: LedgerConfiguration.AsObject,
  }
}

export class LedgerConfiguration extends jspb.Message {
  getMaxDeduplicationDuration(): google_protobuf_duration_pb.Duration | undefined;
  setMaxDeduplicationDuration(value?: google_protobuf_duration_pb.Duration): LedgerConfiguration;
  hasMaxDeduplicationDuration(): boolean;
  clearMaxDeduplicationDuration(): LedgerConfiguration;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): LedgerConfiguration.AsObject;
  static toObject(includeInstance: boolean, msg: LedgerConfiguration): LedgerConfiguration.AsObject;
  static serializeBinaryToWriter(message: LedgerConfiguration, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): LedgerConfiguration;
  static deserializeBinaryFromReader(message: LedgerConfiguration, reader: jspb.BinaryReader): LedgerConfiguration;
}

export namespace LedgerConfiguration {
  export type AsObject = {
    maxDeduplicationDuration?: google_protobuf_duration_pb.Duration.AsObject,
  }
}

