import * as jspb from 'google-protobuf'



export class User extends jspb.Message {
  getId(): string;
  setId(value: string): User;

  getPrimaryParty(): string;
  setPrimaryParty(value: string): User;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): User.AsObject;
  static toObject(includeInstance: boolean, msg: User): User.AsObject;
  static serializeBinaryToWriter(message: User, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): User;
  static deserializeBinaryFromReader(message: User, reader: jspb.BinaryReader): User;
}

export namespace User {
  export type AsObject = {
    id: string,
    primaryParty: string,
  }
}

export class Right extends jspb.Message {
  getParticipantAdmin(): Right.ParticipantAdmin | undefined;
  setParticipantAdmin(value?: Right.ParticipantAdmin): Right;
  hasParticipantAdmin(): boolean;
  clearParticipantAdmin(): Right;

  getCanActAs(): Right.CanActAs | undefined;
  setCanActAs(value?: Right.CanActAs): Right;
  hasCanActAs(): boolean;
  clearCanActAs(): Right;

  getCanReadAs(): Right.CanReadAs | undefined;
  setCanReadAs(value?: Right.CanReadAs): Right;
  hasCanReadAs(): boolean;
  clearCanReadAs(): Right;

  getKindCase(): Right.KindCase;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Right.AsObject;
  static toObject(includeInstance: boolean, msg: Right): Right.AsObject;
  static serializeBinaryToWriter(message: Right, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Right;
  static deserializeBinaryFromReader(message: Right, reader: jspb.BinaryReader): Right;
}

export namespace Right {
  export type AsObject = {
    participantAdmin?: Right.ParticipantAdmin.AsObject,
    canActAs?: Right.CanActAs.AsObject,
    canReadAs?: Right.CanReadAs.AsObject,
  }

  export class ParticipantAdmin extends jspb.Message {
    serializeBinary(): Uint8Array;
    toObject(includeInstance?: boolean): ParticipantAdmin.AsObject;
    static toObject(includeInstance: boolean, msg: ParticipantAdmin): ParticipantAdmin.AsObject;
    static serializeBinaryToWriter(message: ParticipantAdmin, writer: jspb.BinaryWriter): void;
    static deserializeBinary(bytes: Uint8Array): ParticipantAdmin;
    static deserializeBinaryFromReader(message: ParticipantAdmin, reader: jspb.BinaryReader): ParticipantAdmin;
  }

  export namespace ParticipantAdmin {
    export type AsObject = {
    }
  }


  export class CanActAs extends jspb.Message {
    getParty(): string;
    setParty(value: string): CanActAs;

    serializeBinary(): Uint8Array;
    toObject(includeInstance?: boolean): CanActAs.AsObject;
    static toObject(includeInstance: boolean, msg: CanActAs): CanActAs.AsObject;
    static serializeBinaryToWriter(message: CanActAs, writer: jspb.BinaryWriter): void;
    static deserializeBinary(bytes: Uint8Array): CanActAs;
    static deserializeBinaryFromReader(message: CanActAs, reader: jspb.BinaryReader): CanActAs;
  }

  export namespace CanActAs {
    export type AsObject = {
      party: string,
    }
  }


  export class CanReadAs extends jspb.Message {
    getParty(): string;
    setParty(value: string): CanReadAs;

    serializeBinary(): Uint8Array;
    toObject(includeInstance?: boolean): CanReadAs.AsObject;
    static toObject(includeInstance: boolean, msg: CanReadAs): CanReadAs.AsObject;
    static serializeBinaryToWriter(message: CanReadAs, writer: jspb.BinaryWriter): void;
    static deserializeBinary(bytes: Uint8Array): CanReadAs;
    static deserializeBinaryFromReader(message: CanReadAs, reader: jspb.BinaryReader): CanReadAs;
  }

  export namespace CanReadAs {
    export type AsObject = {
      party: string,
    }
  }


  export enum KindCase { 
    KIND_NOT_SET = 0,
    PARTICIPANT_ADMIN = 1,
    CAN_ACT_AS = 2,
    CAN_READ_AS = 3,
  }
}

export class CreateUserRequest extends jspb.Message {
  getUser(): User | undefined;
  setUser(value?: User): CreateUserRequest;
  hasUser(): boolean;
  clearUser(): CreateUserRequest;

  getRightsList(): Array<Right>;
  setRightsList(value: Array<Right>): CreateUserRequest;
  clearRightsList(): CreateUserRequest;
  addRights(value?: Right, index?: number): Right;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CreateUserRequest.AsObject;
  static toObject(includeInstance: boolean, msg: CreateUserRequest): CreateUserRequest.AsObject;
  static serializeBinaryToWriter(message: CreateUserRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CreateUserRequest;
  static deserializeBinaryFromReader(message: CreateUserRequest, reader: jspb.BinaryReader): CreateUserRequest;
}

export namespace CreateUserRequest {
  export type AsObject = {
    user?: User.AsObject,
    rightsList: Array<Right.AsObject>,
  }
}

export class CreateUserResponse extends jspb.Message {
  getUser(): User | undefined;
  setUser(value?: User): CreateUserResponse;
  hasUser(): boolean;
  clearUser(): CreateUserResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CreateUserResponse.AsObject;
  static toObject(includeInstance: boolean, msg: CreateUserResponse): CreateUserResponse.AsObject;
  static serializeBinaryToWriter(message: CreateUserResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CreateUserResponse;
  static deserializeBinaryFromReader(message: CreateUserResponse, reader: jspb.BinaryReader): CreateUserResponse;
}

export namespace CreateUserResponse {
  export type AsObject = {
    user?: User.AsObject,
  }
}

export class GetUserRequest extends jspb.Message {
  getUserId(): string;
  setUserId(value: string): GetUserRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetUserRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetUserRequest): GetUserRequest.AsObject;
  static serializeBinaryToWriter(message: GetUserRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetUserRequest;
  static deserializeBinaryFromReader(message: GetUserRequest, reader: jspb.BinaryReader): GetUserRequest;
}

export namespace GetUserRequest {
  export type AsObject = {
    userId: string,
  }
}

export class GetUserResponse extends jspb.Message {
  getUser(): User | undefined;
  setUser(value?: User): GetUserResponse;
  hasUser(): boolean;
  clearUser(): GetUserResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetUserResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetUserResponse): GetUserResponse.AsObject;
  static serializeBinaryToWriter(message: GetUserResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetUserResponse;
  static deserializeBinaryFromReader(message: GetUserResponse, reader: jspb.BinaryReader): GetUserResponse;
}

export namespace GetUserResponse {
  export type AsObject = {
    user?: User.AsObject,
  }
}

export class DeleteUserRequest extends jspb.Message {
  getUserId(): string;
  setUserId(value: string): DeleteUserRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): DeleteUserRequest.AsObject;
  static toObject(includeInstance: boolean, msg: DeleteUserRequest): DeleteUserRequest.AsObject;
  static serializeBinaryToWriter(message: DeleteUserRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): DeleteUserRequest;
  static deserializeBinaryFromReader(message: DeleteUserRequest, reader: jspb.BinaryReader): DeleteUserRequest;
}

export namespace DeleteUserRequest {
  export type AsObject = {
    userId: string,
  }
}

export class DeleteUserResponse extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): DeleteUserResponse.AsObject;
  static toObject(includeInstance: boolean, msg: DeleteUserResponse): DeleteUserResponse.AsObject;
  static serializeBinaryToWriter(message: DeleteUserResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): DeleteUserResponse;
  static deserializeBinaryFromReader(message: DeleteUserResponse, reader: jspb.BinaryReader): DeleteUserResponse;
}

export namespace DeleteUserResponse {
  export type AsObject = {
  }
}

export class ListUsersRequest extends jspb.Message {
  getPageToken(): string;
  setPageToken(value: string): ListUsersRequest;

  getPageSize(): number;
  setPageSize(value: number): ListUsersRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListUsersRequest.AsObject;
  static toObject(includeInstance: boolean, msg: ListUsersRequest): ListUsersRequest.AsObject;
  static serializeBinaryToWriter(message: ListUsersRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListUsersRequest;
  static deserializeBinaryFromReader(message: ListUsersRequest, reader: jspb.BinaryReader): ListUsersRequest;
}

export namespace ListUsersRequest {
  export type AsObject = {
    pageToken: string,
    pageSize: number,
  }
}

export class ListUsersResponse extends jspb.Message {
  getUsersList(): Array<User>;
  setUsersList(value: Array<User>): ListUsersResponse;
  clearUsersList(): ListUsersResponse;
  addUsers(value?: User, index?: number): User;

  getNextPageToken(): string;
  setNextPageToken(value: string): ListUsersResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListUsersResponse.AsObject;
  static toObject(includeInstance: boolean, msg: ListUsersResponse): ListUsersResponse.AsObject;
  static serializeBinaryToWriter(message: ListUsersResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListUsersResponse;
  static deserializeBinaryFromReader(message: ListUsersResponse, reader: jspb.BinaryReader): ListUsersResponse;
}

export namespace ListUsersResponse {
  export type AsObject = {
    usersList: Array<User.AsObject>,
    nextPageToken: string,
  }
}

export class GrantUserRightsRequest extends jspb.Message {
  getUserId(): string;
  setUserId(value: string): GrantUserRightsRequest;

  getRightsList(): Array<Right>;
  setRightsList(value: Array<Right>): GrantUserRightsRequest;
  clearRightsList(): GrantUserRightsRequest;
  addRights(value?: Right, index?: number): Right;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GrantUserRightsRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GrantUserRightsRequest): GrantUserRightsRequest.AsObject;
  static serializeBinaryToWriter(message: GrantUserRightsRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GrantUserRightsRequest;
  static deserializeBinaryFromReader(message: GrantUserRightsRequest, reader: jspb.BinaryReader): GrantUserRightsRequest;
}

export namespace GrantUserRightsRequest {
  export type AsObject = {
    userId: string,
    rightsList: Array<Right.AsObject>,
  }
}

export class GrantUserRightsResponse extends jspb.Message {
  getNewlyGrantedRightsList(): Array<Right>;
  setNewlyGrantedRightsList(value: Array<Right>): GrantUserRightsResponse;
  clearNewlyGrantedRightsList(): GrantUserRightsResponse;
  addNewlyGrantedRights(value?: Right, index?: number): Right;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GrantUserRightsResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GrantUserRightsResponse): GrantUserRightsResponse.AsObject;
  static serializeBinaryToWriter(message: GrantUserRightsResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GrantUserRightsResponse;
  static deserializeBinaryFromReader(message: GrantUserRightsResponse, reader: jspb.BinaryReader): GrantUserRightsResponse;
}

export namespace GrantUserRightsResponse {
  export type AsObject = {
    newlyGrantedRightsList: Array<Right.AsObject>,
  }
}

export class RevokeUserRightsRequest extends jspb.Message {
  getUserId(): string;
  setUserId(value: string): RevokeUserRightsRequest;

  getRightsList(): Array<Right>;
  setRightsList(value: Array<Right>): RevokeUserRightsRequest;
  clearRightsList(): RevokeUserRightsRequest;
  addRights(value?: Right, index?: number): Right;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RevokeUserRightsRequest.AsObject;
  static toObject(includeInstance: boolean, msg: RevokeUserRightsRequest): RevokeUserRightsRequest.AsObject;
  static serializeBinaryToWriter(message: RevokeUserRightsRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RevokeUserRightsRequest;
  static deserializeBinaryFromReader(message: RevokeUserRightsRequest, reader: jspb.BinaryReader): RevokeUserRightsRequest;
}

export namespace RevokeUserRightsRequest {
  export type AsObject = {
    userId: string,
    rightsList: Array<Right.AsObject>,
  }
}

export class RevokeUserRightsResponse extends jspb.Message {
  getNewlyRevokedRightsList(): Array<Right>;
  setNewlyRevokedRightsList(value: Array<Right>): RevokeUserRightsResponse;
  clearNewlyRevokedRightsList(): RevokeUserRightsResponse;
  addNewlyRevokedRights(value?: Right, index?: number): Right;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RevokeUserRightsResponse.AsObject;
  static toObject(includeInstance: boolean, msg: RevokeUserRightsResponse): RevokeUserRightsResponse.AsObject;
  static serializeBinaryToWriter(message: RevokeUserRightsResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RevokeUserRightsResponse;
  static deserializeBinaryFromReader(message: RevokeUserRightsResponse, reader: jspb.BinaryReader): RevokeUserRightsResponse;
}

export namespace RevokeUserRightsResponse {
  export type AsObject = {
    newlyRevokedRightsList: Array<Right.AsObject>,
  }
}

export class ListUserRightsRequest extends jspb.Message {
  getUserId(): string;
  setUserId(value: string): ListUserRightsRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListUserRightsRequest.AsObject;
  static toObject(includeInstance: boolean, msg: ListUserRightsRequest): ListUserRightsRequest.AsObject;
  static serializeBinaryToWriter(message: ListUserRightsRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListUserRightsRequest;
  static deserializeBinaryFromReader(message: ListUserRightsRequest, reader: jspb.BinaryReader): ListUserRightsRequest;
}

export namespace ListUserRightsRequest {
  export type AsObject = {
    userId: string,
  }
}

export class ListUserRightsResponse extends jspb.Message {
  getRightsList(): Array<Right>;
  setRightsList(value: Array<Right>): ListUserRightsResponse;
  clearRightsList(): ListUserRightsResponse;
  addRights(value?: Right, index?: number): Right;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListUserRightsResponse.AsObject;
  static toObject(includeInstance: boolean, msg: ListUserRightsResponse): ListUserRightsResponse.AsObject;
  static serializeBinaryToWriter(message: ListUserRightsResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListUserRightsResponse;
  static deserializeBinaryFromReader(message: ListUserRightsResponse, reader: jspb.BinaryReader): ListUserRightsResponse;
}

export namespace ListUserRightsResponse {
  export type AsObject = {
    rightsList: Array<Right.AsObject>,
  }
}

