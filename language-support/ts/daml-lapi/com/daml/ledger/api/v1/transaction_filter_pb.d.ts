import * as jspb from 'google-protobuf'

import * as com_daml_ledger_api_v1_value_pb from '../../../../../com/daml/ledger/api/v1/value_pb';


export class TransactionFilter extends jspb.Message {
  getFiltersByPartyMap(): jspb.Map<string, Filters>;
  clearFiltersByPartyMap(): TransactionFilter;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): TransactionFilter.AsObject;
  static toObject(includeInstance: boolean, msg: TransactionFilter): TransactionFilter.AsObject;
  static serializeBinaryToWriter(message: TransactionFilter, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): TransactionFilter;
  static deserializeBinaryFromReader(message: TransactionFilter, reader: jspb.BinaryReader): TransactionFilter;
}

export namespace TransactionFilter {
  export type AsObject = {
    filtersByPartyMap: Array<[string, Filters.AsObject]>,
  }
}

export class Filters extends jspb.Message {
  getInclusive(): InclusiveFilters | undefined;
  setInclusive(value?: InclusiveFilters): Filters;
  hasInclusive(): boolean;
  clearInclusive(): Filters;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Filters.AsObject;
  static toObject(includeInstance: boolean, msg: Filters): Filters.AsObject;
  static serializeBinaryToWriter(message: Filters, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Filters;
  static deserializeBinaryFromReader(message: Filters, reader: jspb.BinaryReader): Filters;
}

export namespace Filters {
  export type AsObject = {
    inclusive?: InclusiveFilters.AsObject,
  }
}

export class InclusiveFilters extends jspb.Message {
  getTemplateIdsList(): Array<com_daml_ledger_api_v1_value_pb.Identifier>;
  setTemplateIdsList(value: Array<com_daml_ledger_api_v1_value_pb.Identifier>): InclusiveFilters;
  clearTemplateIdsList(): InclusiveFilters;
  addTemplateIds(value?: com_daml_ledger_api_v1_value_pb.Identifier, index?: number): com_daml_ledger_api_v1_value_pb.Identifier;

  getInterfaceFiltersList(): Array<InterfaceFilter>;
  setInterfaceFiltersList(value: Array<InterfaceFilter>): InclusiveFilters;
  clearInterfaceFiltersList(): InclusiveFilters;
  addInterfaceFilters(value?: InterfaceFilter, index?: number): InterfaceFilter;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): InclusiveFilters.AsObject;
  static toObject(includeInstance: boolean, msg: InclusiveFilters): InclusiveFilters.AsObject;
  static serializeBinaryToWriter(message: InclusiveFilters, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): InclusiveFilters;
  static deserializeBinaryFromReader(message: InclusiveFilters, reader: jspb.BinaryReader): InclusiveFilters;
}

export namespace InclusiveFilters {
  export type AsObject = {
    templateIdsList: Array<com_daml_ledger_api_v1_value_pb.Identifier.AsObject>,
    interfaceFiltersList: Array<InterfaceFilter.AsObject>,
  }
}

export class InterfaceFilter extends jspb.Message {
  getInterfaceId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setInterfaceId(value?: com_daml_ledger_api_v1_value_pb.Identifier): InterfaceFilter;
  hasInterfaceId(): boolean;
  clearInterfaceId(): InterfaceFilter;

  getIncludeInterfaceView(): boolean;
  setIncludeInterfaceView(value: boolean): InterfaceFilter;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): InterfaceFilter.AsObject;
  static toObject(includeInstance: boolean, msg: InterfaceFilter): InterfaceFilter.AsObject;
  static serializeBinaryToWriter(message: InterfaceFilter, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): InterfaceFilter;
  static deserializeBinaryFromReader(message: InterfaceFilter, reader: jspb.BinaryReader): InterfaceFilter;
}

export namespace InterfaceFilter {
  export type AsObject = {
    interfaceId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    includeInterfaceView: boolean,
  }
}

