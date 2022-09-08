/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1.admin
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_admin_config_management_service_pb from '../../../../../../com/daml/ledger/api/v1/admin/config_management_service_pb';


export class ConfigManagementServiceClient {
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

  methodInfoGetTimeModel = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.ConfigManagementService/GetTimeModel',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelRequest,
    com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelResponse,
    (request: com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelResponse.deserializeBinary
  );

  getTimeModel(
    request: com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelResponse>;

  getTimeModel(
    request: com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelResponse>;

  getTimeModel(
    request: com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_config_management_service_pb.GetTimeModelResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.ConfigManagementService/GetTimeModel',
        request,
        metadata || {},
        this.methodInfoGetTimeModel,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.ConfigManagementService/GetTimeModel',
    request,
    metadata || {},
    this.methodInfoGetTimeModel);
  }

  methodInfoSetTimeModel = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.ConfigManagementService/SetTimeModel',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelRequest,
    com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelResponse,
    (request: com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelResponse.deserializeBinary
  );

  setTimeModel(
    request: com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelResponse>;

  setTimeModel(
    request: com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelResponse>;

  setTimeModel(
    request: com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_config_management_service_pb.SetTimeModelResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.ConfigManagementService/SetTimeModel',
        request,
        metadata || {},
        this.methodInfoSetTimeModel,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.ConfigManagementService/SetTimeModel',
    request,
    metadata || {},
    this.methodInfoSetTimeModel);
  }

}

