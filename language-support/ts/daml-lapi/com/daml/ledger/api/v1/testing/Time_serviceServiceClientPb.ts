/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1.testing
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_testing_time_service_pb from '../../../../../../com/daml/ledger/api/v1/testing/time_service_pb';
import * as google_protobuf_empty_pb from 'google-protobuf/google/protobuf/empty_pb';


export class TimeServiceClient {
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

  methodInfoGetTime = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.testing.TimeService/GetTime',
    grpcWeb.MethodType.SERVER_STREAMING,
    com_daml_ledger_api_v1_testing_time_service_pb.GetTimeRequest,
    com_daml_ledger_api_v1_testing_time_service_pb.GetTimeResponse,
    (request: com_daml_ledger_api_v1_testing_time_service_pb.GetTimeRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_testing_time_service_pb.GetTimeResponse.deserializeBinary
  );

  getTime(
    request: com_daml_ledger_api_v1_testing_time_service_pb.GetTimeRequest,
    metadata?: grpcWeb.Metadata) {
    return this.client_.serverStreaming(
      this.hostname_ +
        '/com.daml.ledger.api.v1.testing.TimeService/GetTime',
      request,
      metadata || {},
      this.methodInfoGetTime);
  }

  methodInfoSetTime = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.testing.TimeService/SetTime',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_testing_time_service_pb.SetTimeRequest,
    google_protobuf_empty_pb.Empty,
    (request: com_daml_ledger_api_v1_testing_time_service_pb.SetTimeRequest) => {
      return request.serializeBinary();
    },
    google_protobuf_empty_pb.Empty.deserializeBinary
  );

  setTime(
    request: com_daml_ledger_api_v1_testing_time_service_pb.SetTimeRequest,
    metadata: grpcWeb.Metadata | null): Promise<google_protobuf_empty_pb.Empty>;

  setTime(
    request: com_daml_ledger_api_v1_testing_time_service_pb.SetTimeRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: google_protobuf_empty_pb.Empty) => void): grpcWeb.ClientReadableStream<google_protobuf_empty_pb.Empty>;

  setTime(
    request: com_daml_ledger_api_v1_testing_time_service_pb.SetTimeRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: google_protobuf_empty_pb.Empty) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.testing.TimeService/SetTime',
        request,
        metadata || {},
        this.methodInfoSetTime,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.testing.TimeService/SetTime',
    request,
    metadata || {},
    this.methodInfoSetTime);
  }

}

