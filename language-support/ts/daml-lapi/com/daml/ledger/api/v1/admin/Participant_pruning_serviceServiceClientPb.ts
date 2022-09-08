/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1.admin
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_admin_participant_pruning_service_pb from '../../../../../../com/daml/ledger/api/v1/admin/participant_pruning_service_pb';


export class ParticipantPruningServiceClient {
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

  methodInfoPrune = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.ParticipantPruningService/Prune',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneRequest,
    com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneResponse,
    (request: com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneResponse.deserializeBinary
  );

  prune(
    request: com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneResponse>;

  prune(
    request: com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneResponse>;

  prune(
    request: com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_participant_pruning_service_pb.PruneResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.ParticipantPruningService/Prune',
        request,
        metadata || {},
        this.methodInfoPrune,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.ParticipantPruningService/Prune',
    request,
    metadata || {},
    this.methodInfoPrune);
  }

}

