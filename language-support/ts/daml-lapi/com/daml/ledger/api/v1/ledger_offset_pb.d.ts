import * as jspb from 'google-protobuf'



export class LedgerOffset extends jspb.Message {
  getAbsolute(): string;
  setAbsolute(value: string): LedgerOffset;

  getBoundary(): LedgerOffset.LedgerBoundary;
  setBoundary(value: LedgerOffset.LedgerBoundary): LedgerOffset;

  getValueCase(): LedgerOffset.ValueCase;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): LedgerOffset.AsObject;
  static toObject(includeInstance: boolean, msg: LedgerOffset): LedgerOffset.AsObject;
  static serializeBinaryToWriter(message: LedgerOffset, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): LedgerOffset;
  static deserializeBinaryFromReader(message: LedgerOffset, reader: jspb.BinaryReader): LedgerOffset;
}

export namespace LedgerOffset {
  export type AsObject = {
    absolute: string,
    boundary: LedgerOffset.LedgerBoundary,
  }

  export enum LedgerBoundary { 
    LEDGER_BEGIN = 0,
    LEDGER_END = 1,
  }

  export enum ValueCase { 
    VALUE_NOT_SET = 0,
    ABSOLUTE = 1,
    BOUNDARY = 2,
  }
}

