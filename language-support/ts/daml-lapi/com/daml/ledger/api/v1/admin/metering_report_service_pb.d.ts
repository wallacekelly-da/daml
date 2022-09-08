import * as jspb from 'google-protobuf'

import * as google_protobuf_timestamp_pb from 'google-protobuf/google/protobuf/timestamp_pb';
import * as google_protobuf_struct_pb from 'google-protobuf/google/protobuf/struct_pb';


export class GetMeteringReportRequest extends jspb.Message {
  getFrom(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setFrom(value?: google_protobuf_timestamp_pb.Timestamp): GetMeteringReportRequest;
  hasFrom(): boolean;
  clearFrom(): GetMeteringReportRequest;

  getTo(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setTo(value?: google_protobuf_timestamp_pb.Timestamp): GetMeteringReportRequest;
  hasTo(): boolean;
  clearTo(): GetMeteringReportRequest;

  getApplicationId(): string;
  setApplicationId(value: string): GetMeteringReportRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetMeteringReportRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetMeteringReportRequest): GetMeteringReportRequest.AsObject;
  static serializeBinaryToWriter(message: GetMeteringReportRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetMeteringReportRequest;
  static deserializeBinaryFromReader(message: GetMeteringReportRequest, reader: jspb.BinaryReader): GetMeteringReportRequest;
}

export namespace GetMeteringReportRequest {
  export type AsObject = {
    from?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    to?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    applicationId: string,
  }
}

export class GetMeteringReportResponse extends jspb.Message {
  getRequest(): GetMeteringReportRequest | undefined;
  setRequest(value?: GetMeteringReportRequest): GetMeteringReportResponse;
  hasRequest(): boolean;
  clearRequest(): GetMeteringReportResponse;

  getParticipantReport(): ParticipantMeteringReport | undefined;
  setParticipantReport(value?: ParticipantMeteringReport): GetMeteringReportResponse;
  hasParticipantReport(): boolean;
  clearParticipantReport(): GetMeteringReportResponse;

  getReportGenerationTime(): google_protobuf_timestamp_pb.Timestamp | undefined;
  setReportGenerationTime(value?: google_protobuf_timestamp_pb.Timestamp): GetMeteringReportResponse;
  hasReportGenerationTime(): boolean;
  clearReportGenerationTime(): GetMeteringReportResponse;

  getMeteringReportJson(): google_protobuf_struct_pb.Struct | undefined;
  setMeteringReportJson(value?: google_protobuf_struct_pb.Struct): GetMeteringReportResponse;
  hasMeteringReportJson(): boolean;
  clearMeteringReportJson(): GetMeteringReportResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetMeteringReportResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetMeteringReportResponse): GetMeteringReportResponse.AsObject;
  static serializeBinaryToWriter(message: GetMeteringReportResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetMeteringReportResponse;
  static deserializeBinaryFromReader(message: GetMeteringReportResponse, reader: jspb.BinaryReader): GetMeteringReportResponse;
}

export namespace GetMeteringReportResponse {
  export type AsObject = {
    request?: GetMeteringReportRequest.AsObject,
    participantReport?: ParticipantMeteringReport.AsObject,
    reportGenerationTime?: google_protobuf_timestamp_pb.Timestamp.AsObject,
    meteringReportJson?: google_protobuf_struct_pb.Struct.AsObject,
  }
}

export class ParticipantMeteringReport extends jspb.Message {
  getParticipantId(): string;
  setParticipantId(value: string): ParticipantMeteringReport;

  getIsFinal(): boolean;
  setIsFinal(value: boolean): ParticipantMeteringReport;

  getApplicationReportsList(): Array<ApplicationMeteringReport>;
  setApplicationReportsList(value: Array<ApplicationMeteringReport>): ParticipantMeteringReport;
  clearApplicationReportsList(): ParticipantMeteringReport;
  addApplicationReports(value?: ApplicationMeteringReport, index?: number): ApplicationMeteringReport;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ParticipantMeteringReport.AsObject;
  static toObject(includeInstance: boolean, msg: ParticipantMeteringReport): ParticipantMeteringReport.AsObject;
  static serializeBinaryToWriter(message: ParticipantMeteringReport, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ParticipantMeteringReport;
  static deserializeBinaryFromReader(message: ParticipantMeteringReport, reader: jspb.BinaryReader): ParticipantMeteringReport;
}

export namespace ParticipantMeteringReport {
  export type AsObject = {
    participantId: string,
    isFinal: boolean,
    applicationReportsList: Array<ApplicationMeteringReport.AsObject>,
  }
}

export class ApplicationMeteringReport extends jspb.Message {
  getApplicationId(): string;
  setApplicationId(value: string): ApplicationMeteringReport;

  getEventCount(): number;
  setEventCount(value: number): ApplicationMeteringReport;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ApplicationMeteringReport.AsObject;
  static toObject(includeInstance: boolean, msg: ApplicationMeteringReport): ApplicationMeteringReport.AsObject;
  static serializeBinaryToWriter(message: ApplicationMeteringReport, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ApplicationMeteringReport;
  static deserializeBinaryFromReader(message: ApplicationMeteringReport, reader: jspb.BinaryReader): ApplicationMeteringReport;
}

export namespace ApplicationMeteringReport {
  export type AsObject = {
    applicationId: string,
    eventCount: number,
  }
}

