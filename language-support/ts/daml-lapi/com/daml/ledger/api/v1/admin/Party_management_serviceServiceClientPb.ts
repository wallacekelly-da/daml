/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1.admin
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_admin_party_management_service_pb from '../../../../../../com/daml/ledger/api/v1/admin/party_management_service_pb';


export class PartyManagementServiceClient {
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

  methodInfoGetParticipantId = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.PartyManagementService/GetParticipantId',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdRequest,
    com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdResponse,
    (request: com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdResponse.deserializeBinary
  );

  getParticipantId(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdResponse>;

  getParticipantId(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdResponse>;

  getParticipantId(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_party_management_service_pb.GetParticipantIdResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.PartyManagementService/GetParticipantId',
        request,
        metadata || {},
        this.methodInfoGetParticipantId,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.PartyManagementService/GetParticipantId',
    request,
    metadata || {},
    this.methodInfoGetParticipantId);
  }

  methodInfoGetParties = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.PartyManagementService/GetParties',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesRequest,
    com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesResponse,
    (request: com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesResponse.deserializeBinary
  );

  getParties(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesResponse>;

  getParties(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesResponse>;

  getParties(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_party_management_service_pb.GetPartiesResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.PartyManagementService/GetParties',
        request,
        metadata || {},
        this.methodInfoGetParties,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.PartyManagementService/GetParties',
    request,
    metadata || {},
    this.methodInfoGetParties);
  }

  methodInfoListKnownParties = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.PartyManagementService/ListKnownParties',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesRequest,
    com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesResponse,
    (request: com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesResponse.deserializeBinary
  );

  listKnownParties(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesResponse>;

  listKnownParties(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesResponse>;

  listKnownParties(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_party_management_service_pb.ListKnownPartiesResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.PartyManagementService/ListKnownParties',
        request,
        metadata || {},
        this.methodInfoListKnownParties,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.PartyManagementService/ListKnownParties',
    request,
    metadata || {},
    this.methodInfoListKnownParties);
  }

  methodInfoAllocateParty = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.PartyManagementService/AllocateParty',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyRequest,
    com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyResponse,
    (request: com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyResponse.deserializeBinary
  );

  allocateParty(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyResponse>;

  allocateParty(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyResponse>;

  allocateParty(
    request: com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_party_management_service_pb.AllocatePartyResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.PartyManagementService/AllocateParty',
        request,
        metadata || {},
        this.methodInfoAllocateParty,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.PartyManagementService/AllocateParty',
    request,
    metadata || {},
    this.methodInfoAllocateParty);
  }

}

