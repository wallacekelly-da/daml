/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_ledger_configuration_service_pb from '../../../../../com/daml/ledger/api/v1/ledger_configuration_service_pb';


export class LedgerConfigurationServiceClient {
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

  methodInfoGetLedgerConfiguration = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.LedgerConfigurationService/GetLedgerConfiguration',
    grpcWeb.MethodType.SERVER_STREAMING,
    com_daml_ledger_api_v1_ledger_configuration_service_pb.GetLedgerConfigurationRequest,
    com_daml_ledger_api_v1_ledger_configuration_service_pb.GetLedgerConfigurationResponse,
    (request: com_daml_ledger_api_v1_ledger_configuration_service_pb.GetLedgerConfigurationRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_ledger_configuration_service_pb.GetLedgerConfigurationResponse.deserializeBinary
  );

  getLedgerConfiguration(
    request: com_daml_ledger_api_v1_ledger_configuration_service_pb.GetLedgerConfigurationRequest,
    metadata?: grpcWeb.Metadata) {
    return this.client_.serverStreaming(
      this.hostname_ +
        '/com.daml.ledger.api.v1.LedgerConfigurationService/GetLedgerConfiguration',
      request,
      metadata || {},
      this.methodInfoGetLedgerConfiguration);
  }

}

