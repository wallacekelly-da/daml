/**
 * @fileoverview gRPC-Web generated client stub for com.daml.ledger.api.v1.admin
 * @enhanceable
 * @public
 */

// GENERATED CODE -- DO NOT EDIT!


/* eslint-disable */
// @ts-nocheck


import * as grpcWeb from 'grpc-web';

import * as com_daml_ledger_api_v1_admin_user_management_service_pb from '../../../../../../com/daml/ledger/api/v1/admin/user_management_service_pb';


export class UserManagementServiceClient {
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

  methodInfoCreateUser = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.UserManagementService/CreateUser',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserRequest,
    com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserResponse,
    (request: com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserResponse.deserializeBinary
  );

  createUser(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserResponse>;

  createUser(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserResponse>;

  createUser(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.CreateUserResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.UserManagementService/CreateUser',
        request,
        metadata || {},
        this.methodInfoCreateUser,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.UserManagementService/CreateUser',
    request,
    metadata || {},
    this.methodInfoCreateUser);
  }

  methodInfoGetUser = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.UserManagementService/GetUser',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserRequest,
    com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserResponse,
    (request: com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserResponse.deserializeBinary
  );

  getUser(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserResponse>;

  getUser(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserResponse>;

  getUser(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.GetUserResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.UserManagementService/GetUser',
        request,
        metadata || {},
        this.methodInfoGetUser,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.UserManagementService/GetUser',
    request,
    metadata || {},
    this.methodInfoGetUser);
  }

  methodInfoDeleteUser = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.UserManagementService/DeleteUser',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserRequest,
    com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserResponse,
    (request: com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserResponse.deserializeBinary
  );

  deleteUser(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserResponse>;

  deleteUser(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserResponse>;

  deleteUser(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.DeleteUserResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.UserManagementService/DeleteUser',
        request,
        metadata || {},
        this.methodInfoDeleteUser,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.UserManagementService/DeleteUser',
    request,
    metadata || {},
    this.methodInfoDeleteUser);
  }

  methodInfoListUsers = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.UserManagementService/ListUsers',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersRequest,
    com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersResponse,
    (request: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersResponse.deserializeBinary
  );

  listUsers(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersResponse>;

  listUsers(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersResponse>;

  listUsers(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUsersResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.UserManagementService/ListUsers',
        request,
        metadata || {},
        this.methodInfoListUsers,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.UserManagementService/ListUsers',
    request,
    metadata || {},
    this.methodInfoListUsers);
  }

  methodInfoGrantUserRights = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.UserManagementService/GrantUserRights',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsRequest,
    com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsResponse,
    (request: com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsResponse.deserializeBinary
  );

  grantUserRights(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsResponse>;

  grantUserRights(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsResponse>;

  grantUserRights(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.GrantUserRightsResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.UserManagementService/GrantUserRights',
        request,
        metadata || {},
        this.methodInfoGrantUserRights,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.UserManagementService/GrantUserRights',
    request,
    metadata || {},
    this.methodInfoGrantUserRights);
  }

  methodInfoRevokeUserRights = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.UserManagementService/RevokeUserRights',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsRequest,
    com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsResponse,
    (request: com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsResponse.deserializeBinary
  );

  revokeUserRights(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsResponse>;

  revokeUserRights(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsResponse>;

  revokeUserRights(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.RevokeUserRightsResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.UserManagementService/RevokeUserRights',
        request,
        metadata || {},
        this.methodInfoRevokeUserRights,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.UserManagementService/RevokeUserRights',
    request,
    metadata || {},
    this.methodInfoRevokeUserRights);
  }

  methodInfoListUserRights = new grpcWeb.MethodDescriptor(
    '/com.daml.ledger.api.v1.admin.UserManagementService/ListUserRights',
    grpcWeb.MethodType.UNARY,
    com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsRequest,
    com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsResponse,
    (request: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsRequest) => {
      return request.serializeBinary();
    },
    com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsResponse.deserializeBinary
  );

  listUserRights(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsRequest,
    metadata: grpcWeb.Metadata | null): Promise<com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsResponse>;

  listUserRights(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsRequest,
    metadata: grpcWeb.Metadata | null,
    callback: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsResponse) => void): grpcWeb.ClientReadableStream<com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsResponse>;

  listUserRights(
    request: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsRequest,
    metadata: grpcWeb.Metadata | null,
    callback?: (err: grpcWeb.RpcError,
               response: com_daml_ledger_api_v1_admin_user_management_service_pb.ListUserRightsResponse) => void) {
    if (callback !== undefined) {
      return this.client_.rpcCall(
        this.hostname_ +
          '/com.daml.ledger.api.v1.admin.UserManagementService/ListUserRights',
        request,
        metadata || {},
        this.methodInfoListUserRights,
        callback);
    }
    return this.client_.unaryCall(
    this.hostname_ +
      '/com.daml.ledger.api.v1.admin.UserManagementService/ListUserRights',
    request,
    metadata || {},
    this.methodInfoListUserRights);
  }

}

