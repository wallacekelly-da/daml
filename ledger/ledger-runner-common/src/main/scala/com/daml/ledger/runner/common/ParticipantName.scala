package com.daml.ledger.runner.common

import com.daml.lf.data.Ref

case class ParticipantName(value: String) extends AnyVal

object ParticipantName {
  def fromParticipantId(participantId: Ref.ParticipantId, shardName: Option[String] = None) =
    ParticipantName(participantId + shardName.map("-" + _).getOrElse(""))
}
