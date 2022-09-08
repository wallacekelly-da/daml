/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1.admin
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_admin_metering_report_service_pb from '../../../../../../com/daml/ledger/api/v1/admin/metering_report_service_pb';


export class MeteringReportServiceClient {
  client_: grpcWeb.AbstractClientBase;
  hostname_: string;
  credentials_: null | { [index: string]: string; };
  options_: null | { [index: string]: any; };

  constructor (hostname: string,
               credentials?: null | { [index: string]: string; },
               options?: null | { [index: string]: any; }) {
    if (!options) options = {};
    if (!credentials) credentials = {};
    options['format'] = 'text';

    this.client_ = new grpcWeb.GrpcWebClientBase(options);
    this.hostname_ = hostname;
    this.credentials_ = credentials;
    this.options_ = options;
  }

  methodInfoGetMeteringReport = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.MeteringReportService/GetMeteringReport',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportRequest,
    com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportResponse,
    (request: com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportResponse.deserializeBinary
  );

  getMeteringReport(
    request: com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportResponse>;

  getMeteringReport(
    request: com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportResponse>;

  getMeteringReport(
    request: com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_metering_report_service_pb.GetMeteringReportResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.MeteringReportService/GetMeteringReport',
        request,
        metadata || {},
        this.methodInfoGetMeteringReport,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.MeteringReportService/GetMeteringReport',
    request,
    metadata || {},
    this.methodInfoGetMeteringReport);
  }

}

