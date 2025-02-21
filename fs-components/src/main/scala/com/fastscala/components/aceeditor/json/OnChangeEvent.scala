package com.fastscala.components.aceeditor.json

import io.circe.Decoder
import io.circe.generic.semiauto.*

case class TextPosition(row: Int, column: Int)

case class OnChangeEvent(
  start: TextPosition,
  end: TextPosition,
  action: String,
  lines: List[String],
  id: Long,
)

object OnChangeEvent:
  given Decoder[TextPosition] = deriveDecoder[TextPosition]
  given Decoder[OnChangeEvent] = deriveDecoder[OnChangeEvent]
