/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_command_service_pb from '../../../../../com/daml/ledger/api/v1/command_service_pb';
import * as google_protobuf_empty_pb from 'google-protobuf/google/protobuf/empty_pb';


export class CommandServiceClient {
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

  methodInfoSubmitAndWait = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.CommandService/SubmitAndWait',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    google_protobuf_empty_pb.Empty,
    (request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest) => {
      return request.serializeBinary();
    },
    google_protobuf_empty_pb.Empty.deserializeBinary
  );

  submitAndWait(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null): Promise<google_protobuf_empty_pb.Empty>;

  submitAndWait(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: google_protobuf_empty_pb.Empty) => void): grpcWeb.ClientReadableStream<google_protobuf_empty_pb.Empty>;

  submitAndWait(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: google_protobuf_empty_pb.Empty) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.CommandService/SubmitAndWait',
        request,
        metadata || {},
        this.methodInfoSubmitAndWait,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.CommandService/SubmitAndWait',
    request,
    metadata || {},
    this.methodInfoSubmitAndWait);
  }

  methodInfoSubmitAndWaitForTransactionId = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.CommandService/SubmitAndWaitForTransactionId',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionIdResponse,
    (request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionIdResponse.deserializeBinary
  );

  submitAndWaitForTransactionId(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionIdResponse>;

  submitAndWaitForTransactionId(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionIdResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionIdResponse>;

  submitAndWaitForTransactionId(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionIdResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.CommandService/SubmitAndWaitForTransactionId',
        request,
        metadata || {},
        this.methodInfoSubmitAndWaitForTransactionId,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.CommandService/SubmitAndWaitForTransactionId',
    request,
    metadata || {},
    this.methodInfoSubmitAndWaitForTransactionId);
  }

  methodInfoSubmitAndWaitForTransaction = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.CommandService/SubmitAndWaitForTransaction',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionResponse,
    (request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionResponse.deserializeBinary
  );

  submitAndWaitForTransaction(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionResponse>;

  submitAndWaitForTransaction(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionResponse>;

  submitAndWaitForTransaction(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.CommandService/SubmitAndWaitForTransaction',
        request,
        metadata || {},
        this.methodInfoSubmitAndWaitForTransaction,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.CommandService/SubmitAndWaitForTransaction',
    request,
    metadata || {},
    this.methodInfoSubmitAndWaitForTransaction);
  }

  methodInfoSubmitAndWaitForTransactionTree = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.CommandService/SubmitAndWaitForTransactionTree',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionTreeResponse,
    (request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionTreeResponse.deserializeBinary
  );

  submitAndWaitForTransactionTree(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionTreeResponse>;

  submitAndWaitForTransactionTree(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionTreeResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionTreeResponse>;

  submitAndWaitForTransactionTree(
    request: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_command_service_pb.SubmitAndWaitForTransactionTreeResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.CommandService/SubmitAndWaitForTransactionTree',
        request,
        metadata || {},
        this.methodInfoSubmitAndWaitForTransactionTree,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.CommandService/SubmitAndWaitForTransactionTree',
    request,
    metadata || {},
    this.methodInfoSubmitAndWaitForTransactionTree);
  }

}

