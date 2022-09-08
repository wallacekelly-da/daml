import * as jspb from 'google-protobuf'



export class ExperimentalFeatures extends jspb.Message {
  getSelfServiceErrorCodes(): ExperimentalSelfServiceErrorCodes | undefined;
  setSelfServiceErrorCodes(value?: ExperimentalSelfServiceErrorCodes): ExperimentalFeatures;
  hasSelfServiceErrorCodes(): boolean;
  clearSelfServiceErrorCodes(): ExperimentalFeatures;

  getStaticTime(): ExperimentalStaticTime | undefined;
  setStaticTime(value?: ExperimentalStaticTime): ExperimentalFeatures;
  hasStaticTime(): boolean;
  clearStaticTime(): ExperimentalFeatures;

  getCommandDeduplication(): CommandDeduplicationFeatures | undefined;
  setCommandDeduplication(value?: CommandDeduplicationFeatures): ExperimentalFeatures;
  hasCommandDeduplication(): boolean;
  clearCommandDeduplication(): ExperimentalFeatures;

  getOptionalLedgerId(): ExperimentalOptionalLedgerId | undefined;
  setOptionalLedgerId(value?: ExperimentalOptionalLedgerId): ExperimentalFeatures;
  hasOptionalLedgerId(): boolean;
  clearOptionalLedgerId(): ExperimentalFeatures;

  getContractIds(): ExperimentalContractIds | undefined;
  setContractIds(value?: ExperimentalContractIds): ExperimentalFeatures;
  hasContractIds(): boolean;
  clearContractIds(): ExperimentalFeatures;

  getCommitterEventLog(): ExperimentalCommitterEventLog | undefined;
  setCommitterEventLog(value?: ExperimentalCommitterEventLog): ExperimentalFeatures;
  hasCommitterEventLog(): boolean;
  clearCommitterEventLog(): ExperimentalFeatures;

  getExplicitDisclosure(): ExperimentalExplicitDisclosure | undefined;
  setExplicitDisclosure(value?: ExperimentalExplicitDisclosure): ExperimentalFeatures;
  hasExplicitDisclosure(): boolean;
  clearExplicitDisclosure(): ExperimentalFeatures;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ExperimentalFeatures.AsObject;
  static toObject(includeInstance: boolean, msg: ExperimentalFeatures): ExperimentalFeatures.AsObject;
  static serializeBinaryToWriter(message: ExperimentalFeatures, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ExperimentalFeatures;
  static deserializeBinaryFromReader(message: ExperimentalFeatures, reader: jspb.BinaryReader): ExperimentalFeatures;
}

export namespace ExperimentalFeatures {
  export type AsObject = {
    selfServiceErrorCodes?: ExperimentalSelfServiceErrorCodes.AsObject,
    staticTime?: ExperimentalStaticTime.AsObject,
    commandDeduplication?: CommandDeduplicationFeatures.AsObject,
    optionalLedgerId?: ExperimentalOptionalLedgerId.AsObject,
    contractIds?: ExperimentalContractIds.AsObject,
    committerEventLog?: ExperimentalCommitterEventLog.AsObject,
    explicitDisclosure?: ExperimentalExplicitDisclosure.AsObject,
  }
}

export class ExperimentalSelfServiceErrorCodes extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ExperimentalSelfServiceErrorCodes.AsObject;
  static toObject(includeInstance: boolean, msg: ExperimentalSelfServiceErrorCodes): ExperimentalSelfServiceErrorCodes.AsObject;
  static serializeBinaryToWriter(message: ExperimentalSelfServiceErrorCodes, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ExperimentalSelfServiceErrorCodes;
  static deserializeBinaryFromReader(message: ExperimentalSelfServiceErrorCodes, reader: jspb.BinaryReader): ExperimentalSelfServiceErrorCodes;
}

export namespace ExperimentalSelfServiceErrorCodes {
  export type AsObject = {
  }
}

export class ExperimentalStaticTime extends jspb.Message {
  getSupported(): boolean;
  setSupported(value: boolean): ExperimentalStaticTime;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ExperimentalStaticTime.AsObject;
  static toObject(includeInstance: boolean, msg: ExperimentalStaticTime): ExperimentalStaticTime.AsObject;
  static serializeBinaryToWriter(message: ExperimentalStaticTime, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ExperimentalStaticTime;
  static deserializeBinaryFromReader(message: ExperimentalStaticTime, reader: jspb.BinaryReader): ExperimentalStaticTime;
}

export namespace ExperimentalStaticTime {
  export type AsObject = {
    supported: boolean,
  }
}

export class CommandDeduplicationFeatures extends jspb.Message {
  getDeduplicationPeriodSupport(): CommandDeduplicationPeriodSupport | undefined;
  setDeduplicationPeriodSupport(value?: CommandDeduplicationPeriodSupport): CommandDeduplicationFeatures;
  hasDeduplicationPeriodSupport(): boolean;
  clearDeduplicationPeriodSupport(): CommandDeduplicationFeatures;

  getDeduplicationType(): CommandDeduplicationType;
  setDeduplicationType(value: CommandDeduplicationType): CommandDeduplicationFeatures;

  getMaxDeduplicationDurationEnforced(): boolean;
  setMaxDeduplicationDurationEnforced(value: boolean): CommandDeduplicationFeatures;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CommandDeduplicationFeatures.AsObject;
  static toObject(includeInstance: boolean, msg: CommandDeduplicationFeatures): CommandDeduplicationFeatures.AsObject;
  static serializeBinaryToWriter(message: CommandDeduplicationFeatures, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CommandDeduplicationFeatures;
  static deserializeBinaryFromReader(message: CommandDeduplicationFeatures, reader: jspb.BinaryReader): CommandDeduplicationFeatures;
}

export namespace CommandDeduplicationFeatures {
  export type AsObject = {
    deduplicationPeriodSupport?: CommandDeduplicationPeriodSupport.AsObject,
    deduplicationType: CommandDeduplicationType,
    maxDeduplicationDurationEnforced: boolean,
  }
}

export class ExperimentalOptionalLedgerId extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ExperimentalOptionalLedgerId.AsObject;
  static toObject(includeInstance: boolean, msg: ExperimentalOptionalLedgerId): ExperimentalOptionalLedgerId.AsObject;
  static serializeBinaryToWriter(message: ExperimentalOptionalLedgerId, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ExperimentalOptionalLedgerId;
  static deserializeBinaryFromReader(message: ExperimentalOptionalLedgerId, reader: jspb.BinaryReader): ExperimentalOptionalLedgerId;
}

export namespace ExperimentalOptionalLedgerId {
  export type AsObject = {
  }
}

export class CommandDeduplicationPeriodSupport extends jspb.Message {
  getOffsetSupport(): CommandDeduplicationPeriodSupport.OffsetSupport;
  setOffsetSupport(value: CommandDeduplicationPeriodSupport.OffsetSupport): CommandDeduplicationPeriodSupport;

  getDurationSupport(): CommandDeduplicationPeriodSupport.DurationSupport;
  setDurationSupport(value: CommandDeduplicationPeriodSupport.DurationSupport): CommandDeduplicationPeriodSupport;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CommandDeduplicationPeriodSupport.AsObject;
  static toObject(includeInstance: boolean, msg: CommandDeduplicationPeriodSupport): CommandDeduplicationPeriodSupport.AsObject;
  static serializeBinaryToWriter(message: CommandDeduplicationPeriodSupport, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CommandDeduplicationPeriodSupport;
  static deserializeBinaryFromReader(message: CommandDeduplicationPeriodSupport, reader: jspb.BinaryReader): CommandDeduplicationPeriodSupport;
}

export namespace CommandDeduplicationPeriodSupport {
  export type AsObject = {
    offsetSupport: CommandDeduplicationPeriodSupport.OffsetSupport,
    durationSupport: CommandDeduplicationPeriodSupport.DurationSupport,
  }

  export enum OffsetSupport { 
    OFFSET_NOT_SUPPORTED = 0,
    OFFSET_NATIVE_SUPPORT = 1,
    OFFSET_CONVERT_TO_DURATION = 2,
  }

  export enum DurationSupport { 
    DURATION_NATIVE_SUPPORT = 0,
    DURATION_CONVERT_TO_OFFSET = 1,
  }
}

export class ExperimentalContractIds extends jspb.Message {
  getV1(): ExperimentalContractIds.ContractIdV1Support;
  setV1(value: ExperimentalContractIds.ContractIdV1Support): ExperimentalContractIds;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ExperimentalContractIds.AsObject;
  static toObject(includeInstance: boolean, msg: ExperimentalContractIds): ExperimentalContractIds.AsObject;
  static serializeBinaryToWriter(message: ExperimentalContractIds, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ExperimentalContractIds;
  static deserializeBinaryFromReader(message: ExperimentalContractIds, reader: jspb.BinaryReader): ExperimentalContractIds;
}

export namespace ExperimentalContractIds {
  export type AsObject = {
    v1: ExperimentalContractIds.ContractIdV1Support,
  }

  export enum ContractIdV1Support { 
    SUFFIXED = 0,
    NON_SUFFIXED = 1,
  }
}

export class ExperimentalCommitterEventLog extends jspb.Message {
  getEventLogType(): ExperimentalCommitterEventLog.CommitterEventLogType;
  setEventLogType(value: ExperimentalCommitterEventLog.CommitterEventLogType): ExperimentalCommitterEventLog;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ExperimentalCommitterEventLog.AsObject;
  static toObject(includeInstance: boolean, msg: ExperimentalCommitterEventLog): ExperimentalCommitterEventLog.AsObject;
  static serializeBinaryToWriter(message: ExperimentalCommitterEventLog, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ExperimentalCommitterEventLog;
  static deserializeBinaryFromReader(message: ExperimentalCommitterEventLog, reader: jspb.BinaryReader): ExperimentalCommitterEventLog;
}

export namespace ExperimentalCommitterEventLog {
  export type AsObject = {
    eventLogType: ExperimentalCommitterEventLog.CommitterEventLogType,
  }

  export enum CommitterEventLogType { 
    CENTRALIZED = 0,
    DISTRIBUTED = 1,
  }
}

export class ExperimentalExplicitDisclosure extends jspb.Message {
  getSupported(): boolean;
  setSupported(value: boolean): ExperimentalExplicitDisclosure;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ExperimentalExplicitDisclosure.AsObject;
  static toObject(includeInstance: boolean, msg: ExperimentalExplicitDisclosure): ExperimentalExplicitDisclosure.AsObject;
  static serializeBinaryToWriter(message: ExperimentalExplicitDisclosure, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ExperimentalExplicitDisclosure;
  static deserializeBinaryFromReader(message: ExperimentalExplicitDisclosure, reader: jspb.BinaryReader): ExperimentalExplicitDisclosure;
}

export namespace ExperimentalExplicitDisclosure {
  export type AsObject = {
    supported: boolean,
  }
}

export enum CommandDeduplicationType { 
  ASYNC_ONLY = 0,
  ASYNC_AND_CONCURRENT_SYNC = 1,
}
