/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_ledger_identity_service_pb from '../../../../../com/daml/ledger/api/v1/ledger_identity_service_pb';


export class LedgerIdentityServiceClient {
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

  methodInfoGetLedgerIdentity = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.LedgerIdentityService/GetLedgerIdentity',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityRequest,
    com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityResponse,
    (request: com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityResponse.deserializeBinary
  );

  getLedgerIdentity(
    request: com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityResponse>;

  getLedgerIdentity(
    request: com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityResponse>;

  getLedgerIdentity(
    request: com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_ledger_identity_service_pb.GetLedgerIdentityResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.LedgerIdentityService/GetLedgerIdentity',
        request,
        metadata || {},
        this.methodInfoGetLedgerIdentity,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.LedgerIdentityService/GetLedgerIdentity',
    request,
    metadata || {},
    this.methodInfoGetLedgerIdentity);
  }

}

