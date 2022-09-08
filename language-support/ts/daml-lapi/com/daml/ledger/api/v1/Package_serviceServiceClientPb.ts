/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_package_service_pb from '../../../../../com/daml/ledger/api/v1/package_service_pb';


export class PackageServiceClient {
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

  methodInfoListPackages = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.PackageService/ListPackages',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_package_service_pb.ListPackagesRequest,
    com_daml_ledger_api_v1_package_service_pb.ListPackagesResponse,
    (request: com_daml_ledger_api_v1_package_service_pb.ListPackagesRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_package_service_pb.ListPackagesResponse.deserializeBinary
  );

  listPackages(
    request: com_daml_ledger_api_v1_package_service_pb.ListPackagesRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_package_service_pb.ListPackagesResponse>;

  listPackages(
    request: com_daml_ledger_api_v1_package_service_pb.ListPackagesRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_package_service_pb.ListPackagesResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_package_service_pb.ListPackagesResponse>;

  listPackages(
    request: com_daml_ledger_api_v1_package_service_pb.ListPackagesRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_package_service_pb.ListPackagesResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.PackageService/ListPackages',
        request,
        metadata || {},
        this.methodInfoListPackages,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.PackageService/ListPackages',
    request,
    metadata || {},
    this.methodInfoListPackages);
  }

  methodInfoGetPackage = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.PackageService/GetPackage',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_package_service_pb.GetPackageRequest,
    com_daml_ledger_api_v1_package_service_pb.GetPackageResponse,
    (request: com_daml_ledger_api_v1_package_service_pb.GetPackageRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_package_service_pb.GetPackageResponse.deserializeBinary
  );

  getPackage(
    request: com_daml_ledger_api_v1_package_service_pb.GetPackageRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_package_service_pb.GetPackageResponse>;

  getPackage(
    request: com_daml_ledger_api_v1_package_service_pb.GetPackageRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_package_service_pb.GetPackageResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_package_service_pb.GetPackageResponse>;

  getPackage(
    request: com_daml_ledger_api_v1_package_service_pb.GetPackageRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_package_service_pb.GetPackageResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.PackageService/GetPackage',
        request,
        metadata || {},
        this.methodInfoGetPackage,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.PackageService/GetPackage',
    request,
    metadata || {},
    this.methodInfoGetPackage);
  }

  methodInfoGetPackageStatus = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.PackageService/GetPackageStatus',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_package_service_pb.GetPackageStatusRequest,
    com_daml_ledger_api_v1_package_service_pb.GetPackageStatusResponse,
    (request: com_daml_ledger_api_v1_package_service_pb.GetPackageStatusRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_package_service_pb.GetPackageStatusResponse.deserializeBinary
  );

  getPackageStatus(
    request: com_daml_ledger_api_v1_package_service_pb.GetPackageStatusRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_package_service_pb.GetPackageStatusResponse>;

  getPackageStatus(
    request: com_daml_ledger_api_v1_package_service_pb.GetPackageStatusRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_package_service_pb.GetPackageStatusResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_package_service_pb.GetPackageStatusResponse>;

  getPackageStatus(
    request: com_daml_ledger_api_v1_package_service_pb.GetPackageStatusRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_package_service_pb.GetPackageStatusResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.PackageService/GetPackageStatus',
        request,
        metadata || {},
        this.methodInfoGetPackageStatus,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.PackageService/GetPackageStatus',
    request,
    metadata || {},
    this.methodInfoGetPackageStatus);
  }

}

