/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_version_service_pb from '../../../../../com/daml/ledger/api/v1/version_service_pb';


export class VersionServiceClient {
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

  methodInfoGetLedgerApiVersion = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.VersionService/GetLedgerApiVersion',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionRequest,
    com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionResponse,
    (request: com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionResponse.deserializeBinary
  );

  getLedgerApiVersion(
    request: com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionResponse>;

  getLedgerApiVersion(
    request: com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionResponse>;

  getLedgerApiVersion(
    request: com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_version_service_pb.GetLedgerApiVersionResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.VersionService/GetLedgerApiVersion',
        request,
        metadata || {},
        this.methodInfoGetLedgerApiVersion,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.VersionService/GetLedgerApiVersion',
    request,
    metadata || {},
    this.methodInfoGetLedgerApiVersion);
  }

}

