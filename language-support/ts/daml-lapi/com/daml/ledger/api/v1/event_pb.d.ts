import * as jspb from 'google-protobuf'

import * as com_daml_ledger_api_v1_contract_metadata_pb from '../../../../../com/daml/ledger/api/v1/contract_metadata_pb';
import * as com_daml_ledger_api_v1_value_pb from '../../../../../com/daml/ledger/api/v1/value_pb';
import * as google_protobuf_wrappers_pb from 'google-protobuf/google/protobuf/wrappers_pb';
import * as google_protobuf_any_pb from 'google-protobuf/google/protobuf/any_pb';
import * as google_rpc_status_pb from '../../../../../google/rpc/status_pb';


export class Event extends jspb.Message {
  getCreated(): CreatedEvent | undefined;
  setCreated(value?: CreatedEvent): Event;
  hasCreated(): boolean;
  clearCreated(): Event;

  getArchived(): ArchivedEvent | undefined;
  setArchived(value?: ArchivedEvent): Event;
  hasArchived(): boolean;
  clearArchived(): Event;

  getEventCase(): Event.EventCase;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Event.AsObject;
  static toObject(includeInstance: boolean, msg: Event): Event.AsObject;
  static serializeBinaryToWriter(message: Event, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Event;
  static deserializeBinaryFromReader(message: Event, reader: jspb.BinaryReader): Event;
}

export namespace Event {
  export type AsObject = {
    created?: CreatedEvent.AsObject,
    archived?: ArchivedEvent.AsObject,
  }

  export enum EventCase { 
    EVENT_NOT_SET = 0,
    CREATED = 1,
    ARCHIVED = 3,
  }
}

export class CreatedEvent extends jspb.Message {
  getEventId(): string;
  setEventId(value: string): CreatedEvent;

  getContractId(): string;
  setContractId(value: string): CreatedEvent;

  getTemplateId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setTemplateId(value?: com_daml_ledger_api_v1_value_pb.Identifier): CreatedEvent;
  hasTemplateId(): boolean;
  clearTemplateId(): CreatedEvent;

  getContractKey(): com_daml_ledger_api_v1_value_pb.Value | undefined;
  setContractKey(value?: com_daml_ledger_api_v1_value_pb.Value): CreatedEvent;
  hasContractKey(): boolean;
  clearContractKey(): CreatedEvent;

  getCreateArguments(): com_daml_ledger_api_v1_value_pb.Record | undefined;
  setCreateArguments(value?: com_daml_ledger_api_v1_value_pb.Record): CreatedEvent;
  hasCreateArguments(): boolean;
  clearCreateArguments(): CreatedEvent;

  getInterfaceViewsList(): Array<InterfaceView>;
  setInterfaceViewsList(value: Array<InterfaceView>): CreatedEvent;
  clearInterfaceViewsList(): CreatedEvent;
  addInterfaceViews(value?: InterfaceView, index?: number): InterfaceView;

  getWitnessPartiesList(): Array<string>;
  setWitnessPartiesList(value: Array<string>): CreatedEvent;
  clearWitnessPartiesList(): CreatedEvent;
  addWitnessParties(value: string, index?: number): CreatedEvent;

  getSignatoriesList(): Array<string>;
  setSignatoriesList(value: Array<string>): CreatedEvent;
  clearSignatoriesList(): CreatedEvent;
  addSignatories(value: string, index?: number): CreatedEvent;

  getObserversList(): Array<string>;
  setObserversList(value: Array<string>): CreatedEvent;
  clearObserversList(): CreatedEvent;
  addObservers(value: string, index?: number): CreatedEvent;

  getAgreementText(): google_protobuf_wrappers_pb.StringValue | undefined;
  setAgreementText(value?: google_protobuf_wrappers_pb.StringValue): CreatedEvent;
  hasAgreementText(): boolean;
  clearAgreementText(): CreatedEvent;

  getMetadata(): com_daml_ledger_api_v1_contract_metadata_pb.ContractMetadata | undefined;
  setMetadata(value?: com_daml_ledger_api_v1_contract_metadata_pb.ContractMetadata): CreatedEvent;
  hasMetadata(): boolean;
  clearMetadata(): CreatedEvent;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CreatedEvent.AsObject;
  static toObject(includeInstance: boolean, msg: CreatedEvent): CreatedEvent.AsObject;
  static serializeBinaryToWriter(message: CreatedEvent, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CreatedEvent;
  static deserializeBinaryFromReader(message: CreatedEvent, reader: jspb.BinaryReader): CreatedEvent;
}

export namespace CreatedEvent {
  export type AsObject = {
    eventId: string,
    contractId: string,
    templateId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    contractKey?: com_daml_ledger_api_v1_value_pb.Value.AsObject,
    createArguments?: com_daml_ledger_api_v1_value_pb.Record.AsObject,
    interfaceViewsList: Array<InterfaceView.AsObject>,
    witnessPartiesList: Array<string>,
    signatoriesList: Array<string>,
    observersList: Array<string>,
    agreementText?: google_protobuf_wrappers_pb.StringValue.AsObject,
    metadata?: com_daml_ledger_api_v1_contract_metadata_pb.ContractMetadata.AsObject,
  }
}

export class InterfaceView extends jspb.Message {
  getInterfaceId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setInterfaceId(value?: com_daml_ledger_api_v1_value_pb.Identifier): InterfaceView;
  hasInterfaceId(): boolean;
  clearInterfaceId(): InterfaceView;

  getViewStatus(): google_rpc_status_pb.Status | undefined;
  setViewStatus(value?: google_rpc_status_pb.Status): InterfaceView;
  hasViewStatus(): boolean;
  clearViewStatus(): InterfaceView;

  getViewValue(): com_daml_ledger_api_v1_value_pb.Record | undefined;
  setViewValue(value?: com_daml_ledger_api_v1_value_pb.Record): InterfaceView;
  hasViewValue(): boolean;
  clearViewValue(): InterfaceView;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): InterfaceView.AsObject;
  static toObject(includeInstance: boolean, msg: InterfaceView): InterfaceView.AsObject;
  static serializeBinaryToWriter(message: InterfaceView, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): InterfaceView;
  static deserializeBinaryFromReader(message: InterfaceView, reader: jspb.BinaryReader): InterfaceView;
}

export namespace InterfaceView {
  export type AsObject = {
    interfaceId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    viewStatus?: google_rpc_status_pb.Status.AsObject,
    viewValue?: com_daml_ledger_api_v1_value_pb.Record.AsObject,
  }
}

export class ArchivedEvent extends jspb.Message {
  getEventId(): string;
  setEventId(value: string): ArchivedEvent;

  getContractId(): string;
  setContractId(value: string): ArchivedEvent;

  getTemplateId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setTemplateId(value?: com_daml_ledger_api_v1_value_pb.Identifier): ArchivedEvent;
  hasTemplateId(): boolean;
  clearTemplateId(): ArchivedEvent;

  getWitnessPartiesList(): Array<string>;
  setWitnessPartiesList(value: Array<string>): ArchivedEvent;
  clearWitnessPartiesList(): ArchivedEvent;
  addWitnessParties(value: string, index?: number): ArchivedEvent;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ArchivedEvent.AsObject;
  static toObject(includeInstance: boolean, msg: ArchivedEvent): ArchivedEvent.AsObject;
  static serializeBinaryToWriter(message: ArchivedEvent, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ArchivedEvent;
  static deserializeBinaryFromReader(message: ArchivedEvent, reader: jspb.BinaryReader): ArchivedEvent;
}

export namespace ArchivedEvent {
  export type AsObject = {
    eventId: string,
    contractId: string,
    templateId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    witnessPartiesList: Array<string>,
  }
}

export class ExercisedEvent extends jspb.Message {
  getEventId(): string;
  setEventId(value: string): ExercisedEvent;

  getContractId(): string;
  setContractId(value: string): ExercisedEvent;

  getTemplateId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setTemplateId(value?: com_daml_ledger_api_v1_value_pb.Identifier): ExercisedEvent;
  hasTemplateId(): boolean;
  clearTemplateId(): ExercisedEvent;

  getInterfaceId(): com_daml_ledger_api_v1_value_pb.Identifier | undefined;
  setInterfaceId(value?: com_daml_ledger_api_v1_value_pb.Identifier): ExercisedEvent;
  hasInterfaceId(): boolean;
  clearInterfaceId(): ExercisedEvent;

  getChoice(): string;
  setChoice(value: string): ExercisedEvent;

  getChoiceArgument(): com_daml_ledger_api_v1_value_pb.Value | undefined;
  setChoiceArgument(value?: com_daml_ledger_api_v1_value_pb.Value): ExercisedEvent;
  hasChoiceArgument(): boolean;
  clearChoiceArgument(): ExercisedEvent;

  getActingPartiesList(): Array<string>;
  setActingPartiesList(value: Array<string>): ExercisedEvent;
  clearActingPartiesList(): ExercisedEvent;
  addActingParties(value: string, index?: number): ExercisedEvent;

  getConsuming(): boolean;
  setConsuming(value: boolean): ExercisedEvent;

  getWitnessPartiesList(): Array<string>;
  setWitnessPartiesList(value: Array<string>): ExercisedEvent;
  clearWitnessPartiesList(): ExercisedEvent;
  addWitnessParties(value: string, index?: number): ExercisedEvent;

  getChildEventIdsList(): Array<string>;
  setChildEventIdsList(value: Array<string>): ExercisedEvent;
  clearChildEventIdsList(): ExercisedEvent;
  addChildEventIds(value: string, index?: number): ExercisedEvent;

  getExerciseResult(): com_daml_ledger_api_v1_value_pb.Value | undefined;
  setExerciseResult(value?: com_daml_ledger_api_v1_value_pb.Value): ExercisedEvent;
  hasExerciseResult(): boolean;
  clearExerciseResult(): ExercisedEvent;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ExercisedEvent.AsObject;
  static toObject(includeInstance: boolean, msg: ExercisedEvent): ExercisedEvent.AsObject;
  static serializeBinaryToWriter(message: ExercisedEvent, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ExercisedEvent;
  static deserializeBinaryFromReader(message: ExercisedEvent, reader: jspb.BinaryReader): ExercisedEvent;
}

export namespace ExercisedEvent {
  export type AsObject = {
    eventId: string,
    contractId: string,
    templateId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    interfaceId?: com_daml_ledger_api_v1_value_pb.Identifier.AsObject,
    choice: string,
    choiceArgument?: com_daml_ledger_api_v1_value_pb.Value.AsObject,
    actingPartiesList: Array<string>,
    consuming: boolean,
    witnessPartiesList: Array<string>,
    childEventIdsList: Array<string>,
    exerciseResult?: com_daml_ledger_api_v1_value_pb.Value.AsObject,
  }
}

