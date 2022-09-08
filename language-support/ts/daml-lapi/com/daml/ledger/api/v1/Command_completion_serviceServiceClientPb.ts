/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_command_completion_service_pb from '../../../../../com/daml/ledger/api/v1/command_completion_service_pb';


export class CommandCompletionServiceClient {
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

  methodInfoCompletionStream = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.CommandCompletionService/CompletionStream',
    grpcWeb.MethodType.SERVER_STREAMING,
    com_daml_ledger_api_v1_command_completion_service_pb.CompletionStreamRequest,
    com_daml_ledger_api_v1_command_completion_service_pb.CompletionStreamResponse,
    (request: com_daml_ledger_api_v1_command_completion_service_pb.CompletionStreamRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_command_completion_service_pb.CompletionStreamResponse.deserializeBinary
  );

  completionStream(
    request: com_daml_ledger_api_v1_command_completion_service_pb.CompletionStreamRequest,
    metadata?: grpcWeb.Metadata) {
    return this.client_.serverStreaming(
      this.hostname_ +
        '/com.daml.ledger.api.v1.CommandCompletionService/CompletionStream',
      request,
      metadata || {},
      this.methodInfoCompletionStream);
  }

  methodInfoCompletionEnd = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.CommandCompletionService/CompletionEnd',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndRequest,
    com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndResponse,
    (request: com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndResponse.deserializeBinary
  );

  completionEnd(
    request: com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndResponse>;

  completionEnd(
    request: com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndResponse>;

  completionEnd(
    request: com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_command_completion_service_pb.CompletionEndResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.CommandCompletionService/CompletionEnd',
        request,
        metadata || {},
        this.methodInfoCompletionEnd,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.CommandCompletionService/CompletionEnd',
    request,
    metadata || {},
    this.methodInfoCompletionEnd);
  }

}

