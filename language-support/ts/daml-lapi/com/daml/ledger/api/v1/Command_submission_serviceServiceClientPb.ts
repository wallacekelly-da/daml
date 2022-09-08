/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_command_submission_service_pb from '../../../../../com/daml/ledger/api/v1/command_submission_service_pb';
import * as google_protobuf_empty_pb from 'google-protobuf/google/protobuf/empty_pb';


export class CommandSubmissionServiceClient {
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

  methodInfoSubmit = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.CommandSubmissionService/Submit',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_command_submission_service_pb.SubmitRequest,
    google_protobuf_empty_pb.Empty,
    (request: com_daml_ledger_api_v1_command_submission_service_pb.SubmitRequest) => {
      return request.serializeBinary();
    },
    google_protobuf_empty_pb.Empty.deserializeBinary
  );

  submit(
    request: com_daml_ledger_api_v1_command_submission_service_pb.SubmitRequest,
    metadata: grpcWeb.Metadata | null): Promise<google_protobuf_empty_pb.Empty>;

  submit(
    request: com_daml_ledger_api_v1_command_submission_service_pb.SubmitRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: google_protobuf_empty_pb.Empty) => void): grpcWeb.ClientReadableStream<google_protobuf_empty_pb.Empty>;

  submit(
    request: com_daml_ledger_api_v1_command_submission_service_pb.SubmitRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: google_protobuf_empty_pb.Empty) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.CommandSubmissionService/Submit',
        request,
        metadata || {},
        this.methodInfoSubmit,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.CommandSubmissionService/Submit',
    request,
    metadata || {},
    this.methodInfoSubmit);
  }

}

