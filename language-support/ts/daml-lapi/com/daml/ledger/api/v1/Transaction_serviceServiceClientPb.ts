/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_transaction_service_pb from '../../../../../com/daml/ledger/api/v1/transaction_service_pb';


export class TransactionServiceClient {
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

  methodInfoGetTransactions = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.TransactionService/GetTransactions',
    grpcWeb.MethodType.SERVER_STREAMING,
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionsRequest,
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionsResponse,
    (request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionsRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionsResponse.deserializeBinary
  );

  getTransactions(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionsRequest,
    metadata?: grpcWeb.Metadata) {
    return this.client_.serverStreaming(
      this.hostname_ +
        '/com.daml.ledger.api.v1.TransactionService/GetTransactions',
      request,
      metadata || {},
      this.methodInfoGetTransactions);
  }

  methodInfoGetTransactionTrees = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.TransactionService/GetTransactionTrees',
    grpcWeb.MethodType.SERVER_STREAMING,
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionsRequest,
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionTreesResponse,
    (request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionsRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionTreesResponse.deserializeBinary
  );

  getTransactionTrees(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionsRequest,
    metadata?: grpcWeb.Metadata) {
    return this.client_.serverStreaming(
      this.hostname_ +
        '/com.daml.ledger.api.v1.TransactionService/GetTransactionTrees',
      request,
      metadata || {},
      this.methodInfoGetTransactionTrees);
  }

  methodInfoGetTransactionByEventId = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.TransactionService/GetTransactionByEventId',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByEventIdRequest,
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse,
    (request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByEventIdRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse.deserializeBinary
  );

  getTransactionByEventId(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByEventIdRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse>;

  getTransactionByEventId(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByEventIdRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse>;

  getTransactionByEventId(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByEventIdRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.TransactionService/GetTransactionByEventId',
        request,
        metadata || {},
        this.methodInfoGetTransactionByEventId,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.TransactionService/GetTransactionByEventId',
    request,
    metadata || {},
    this.methodInfoGetTransactionByEventId);
  }

  methodInfoGetTransactionById = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.TransactionService/GetTransactionById',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByIdRequest,
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse,
    (request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByIdRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse.deserializeBinary
  );

  getTransactionById(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByIdRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse>;

  getTransactionById(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByIdRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse>;

  getTransactionById(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByIdRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.TransactionService/GetTransactionById',
        request,
        metadata || {},
        this.methodInfoGetTransactionById,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.TransactionService/GetTransactionById',
    request,
    metadata || {},
    this.methodInfoGetTransactionById);
  }

  methodInfoGetFlatTransactionByEventId = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.TransactionService/GetFlatTransactionByEventId',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByEventIdRequest,
    com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse,
    (request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByEventIdRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse.deserializeBinary
  );

  getFlatTransactionByEventId(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByEventIdRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse>;

  getFlatTransactionByEventId(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByEventIdRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse>;

  getFlatTransactionByEventId(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByEventIdRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.TransactionService/GetFlatTransactionByEventId',
        request,
        metadata || {},
        this.methodInfoGetFlatTransactionByEventId,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.TransactionService/GetFlatTransactionByEventId',
    request,
    metadata || {},
    this.methodInfoGetFlatTransactionByEventId);
  }

  methodInfoGetFlatTransactionById = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.TransactionService/GetFlatTransactionById',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByIdRequest,
    com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse,
    (request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByIdRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse.deserializeBinary
  );

  getFlatTransactionById(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByIdRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse>;

  getFlatTransactionById(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByIdRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse>;

  getFlatTransactionById(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetTransactionByIdRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_transaction_service_pb.GetFlatTransactionResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.TransactionService/GetFlatTransactionById',
        request,
        metadata || {},
        this.methodInfoGetFlatTransactionById,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.TransactionService/GetFlatTransactionById',
    request,
    metadata || {},
    this.methodInfoGetFlatTransactionById);
  }

  methodInfoGetLedgerEnd = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.TransactionService/GetLedgerEnd',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndRequest,
    com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndResponse,
    (request: com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndResponse.deserializeBinary
  );

  getLedgerEnd(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndResponse>;

  getLedgerEnd(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndResponse>;

  getLedgerEnd(
    request: com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_transaction_service_pb.GetLedgerEndResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.TransactionService/GetLedgerEnd',
        request,
        metadata || {},
        this.methodInfoGetLedgerEnd,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.TransactionService/GetLedgerEnd',
    request,
    metadata || {},
    this.methodInfoGetLedgerEnd);
  }

}

