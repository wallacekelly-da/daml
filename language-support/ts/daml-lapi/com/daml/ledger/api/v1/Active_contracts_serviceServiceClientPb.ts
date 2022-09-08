/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_active_contracts_service_pb from '../../../../../com/daml/ledger/api/v1/active_contracts_service_pb';


export class ActiveContractsServiceClient {
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

  methodInfoGetActiveContracts = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.ActiveContractsService/GetActiveContracts',
    grpcWeb.MethodType.SERVER_STREAMING,
    com_daml_ledger_api_v1_active_contracts_service_pb.GetActiveContractsRequest,
    com_daml_ledger_api_v1_active_contracts_service_pb.GetActiveContractsResponse,
    (request: com_daml_ledger_api_v1_active_contracts_service_pb.GetActiveContractsRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_active_contracts_service_pb.GetActiveContractsResponse.deserializeBinary
  );

  getActiveContracts(
    request: com_daml_ledger_api_v1_active_contracts_service_pb.GetActiveContractsRequest,
    metadata?: grpcWeb.Metadata) {
    return this.client_.serverStreaming(
      this.hostname_ +
        '/com.daml.ledger.api.v1.ActiveContractsService/GetActiveContracts',
      request,
      metadata || {},
      this.methodInfoGetActiveContracts);
  }

}

