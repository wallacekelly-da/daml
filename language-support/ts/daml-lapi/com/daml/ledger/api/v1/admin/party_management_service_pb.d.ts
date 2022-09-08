import * as jspb from 'google-protobuf'



export class GetParticipantIdRequest extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetParticipantIdRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetParticipantIdRequest): GetParticipantIdRequest.AsObject;
  static serializeBinaryToWriter(message: GetParticipantIdRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetParticipantIdRequest;
  static deserializeBinaryFromReader(message: GetParticipantIdRequest, reader: jspb.BinaryReader): GetParticipantIdRequest;
}

export namespace GetParticipantIdRequest {
  export type AsObject = {
  }
}

export class GetParticipantIdResponse extends jspb.Message {
  getParticipantId(): string;
  setParticipantId(value: string): GetParticipantIdResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetParticipantIdResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetParticipantIdResponse): GetParticipantIdResponse.AsObject;
  static serializeBinaryToWriter(message: GetParticipantIdResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetParticipantIdResponse;
  static deserializeBinaryFromReader(message: GetParticipantIdResponse, reader: jspb.BinaryReader): GetParticipantIdResponse;
}

export namespace GetParticipantIdResponse {
  export type AsObject = {
    participantId: string,
  }
}

export class GetPartiesRequest extends jspb.Message {
  getPartiesList(): Array<string>;
  setPartiesList(value: Array<string>): GetPartiesRequest;
  clearPartiesList(): GetPartiesRequest;
  addParties(value: string, index?: number): GetPartiesRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetPartiesRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetPartiesRequest): GetPartiesRequest.AsObject;
  static serializeBinaryToWriter(message: GetPartiesRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetPartiesRequest;
  static deserializeBinaryFromReader(message: GetPartiesRequest, reader: jspb.BinaryReader): GetPartiesRequest;
}

export namespace GetPartiesRequest {
  export type AsObject = {
    partiesList: Array<string>,
  }
}

export class GetPartiesResponse extends jspb.Message {
  getPartyDetailsList(): Array<PartyDetails>;
  setPartyDetailsList(value: Array<PartyDetails>): GetPartiesResponse;
  clearPartyDetailsList(): GetPartiesResponse;
  addPartyDetails(value?: PartyDetails, index?: number): PartyDetails;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetPartiesResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GetPartiesResponse): GetPartiesResponse.AsObject;
  static serializeBinaryToWriter(message: GetPartiesResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetPartiesResponse;
  static deserializeBinaryFromReader(message: GetPartiesResponse, reader: jspb.BinaryReader): GetPartiesResponse;
}

export namespace GetPartiesResponse {
  export type AsObject = {
    partyDetailsList: Array<PartyDetails.AsObject>,
  }
}

export class ListKnownPartiesRequest extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListKnownPartiesRequest.AsObject;
  static toObject(includeInstance: boolean, msg: ListKnownPartiesRequest): ListKnownPartiesRequest.AsObject;
  static serializeBinaryToWriter(message: ListKnownPartiesRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListKnownPartiesRequest;
  static deserializeBinaryFromReader(message: ListKnownPartiesRequest, reader: jspb.BinaryReader): ListKnownPartiesRequest;
}

export namespace ListKnownPartiesRequest {
  export type AsObject = {
  }
}

export class ListKnownPartiesResponse extends jspb.Message {
  getPartyDetailsList(): Array<PartyDetails>;
  setPartyDetailsList(value: Array<PartyDetails>): ListKnownPartiesResponse;
  clearPartyDetailsList(): ListKnownPartiesResponse;
  addPartyDetails(value?: PartyDetails, index?: number): PartyDetails;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListKnownPartiesResponse.AsObject;
  static toObject(includeInstance: boolean, msg: ListKnownPartiesResponse): ListKnownPartiesResponse.AsObject;
  static serializeBinaryToWriter(message: ListKnownPartiesResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListKnownPartiesResponse;
  static deserializeBinaryFromReader(message: ListKnownPartiesResponse, reader: jspb.BinaryReader): ListKnownPartiesResponse;
}

export namespace ListKnownPartiesResponse {
  export type AsObject = {
    partyDetailsList: Array<PartyDetails.AsObject>,
  }
}

export class AllocatePartyRequest extends jspb.Message {
  getPartyIdHint(): string;
  setPartyIdHint(value: string): AllocatePartyRequest;

  getDisplayName(): string;
  setDisplayName(value: string): AllocatePartyRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): AllocatePartyRequest.AsObject;
  static toObject(includeInstance: boolean, msg: AllocatePartyRequest): AllocatePartyRequest.AsObject;
  static serializeBinaryToWriter(message: AllocatePartyRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): AllocatePartyRequest;
  static deserializeBinaryFromReader(message: AllocatePartyRequest, reader: jspb.BinaryReader): AllocatePartyRequest;
}

export namespace AllocatePartyRequest {
  export type AsObject = {
    partyIdHint: string,
    displayName: string,
  }
}

export class AllocatePartyResponse extends jspb.Message {
  getPartyDetails(): PartyDetails | undefined;
  setPartyDetails(value?: PartyDetails): AllocatePartyResponse;
  hasPartyDetails(): boolean;
  clearPartyDetails(): AllocatePartyResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): AllocatePartyResponse.AsObject;
  static toObject(includeInstance: boolean, msg: AllocatePartyResponse): AllocatePartyResponse.AsObject;
  static serializeBinaryToWriter(message: AllocatePartyResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): AllocatePartyResponse;
  static deserializeBinaryFromReader(message: AllocatePartyResponse, reader: jspb.BinaryReader): AllocatePartyResponse;
}

export namespace AllocatePartyResponse {
  export type AsObject = {
    partyDetails?: PartyDetails.AsObject,
  }
}

export class PartyDetails extends jspb.Message {
  getParty(): string;
  setParty(value: string): PartyDetails;

  getDisplayName(): string;
  setDisplayName(value: string): PartyDetails;

  getIsLocal(): boolean;
  setIsLocal(value: boolean): PartyDetails;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): PartyDetails.AsObject;
  static toObject(includeInstance: boolean, msg: PartyDetails): PartyDetails.AsObject;
  static serializeBinaryToWriter(message: PartyDetails, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): PartyDetails;
  static deserializeBinaryFromReader(message: PartyDetails, reader: jspb.BinaryReader): PartyDetails;
}

export namespace PartyDetails {
  export type AsObject = {
    party: string,
    displayName: string,
    isLocal: boolean,
  }
}

