import * as jspb from 'google-protobuf'

import * as com_daml_ledger_api_v1_commands_pb from '../../../../../com/daml/ledger/api/v1/commands_pb';
import * as google_protobuf_empty_pb from 'google-protobuf/google/protobuf/empty_pb';


export class SubmitRequest extends jspb.Message {
  getCommands(): com_daml_ledger_api_v1_commands_pb.Commands | undefined;
  setCommands(value?: com_daml_ledger_api_v1_commands_pb.Commands): SubmitRequest;
  hasCommands(): boolean;
  clearCommands(): SubmitRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SubmitRequest.AsObject;
  static toObject(includeInstance: boolean, msg: SubmitRequest): SubmitRequest.AsObject;
  static serializeBinaryToWriter(message: SubmitRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SubmitRequest;
  static deserializeBinaryFromReader(message: SubmitRequest, reader: jspb.BinaryReader): SubmitRequest;
}

export namespace SubmitRequest {
  export type AsObject = {
    commands?: com_daml_ledger_api_v1_commands_pb.Commands.AsObject,
  }
}

