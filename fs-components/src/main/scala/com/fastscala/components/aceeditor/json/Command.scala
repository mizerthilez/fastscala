package com.fastscala.components.aceeditor.json

import io.circe.generic.semiauto.*
import io.circe.Encoder

import com.fastscala.js.Js

case class BindKey(win: String, mac: String)

case class Command(
  name: String,
  bindKey: BindKey,
  exec: Js,
)

object Command:
  given Encoder[Js] = Encoder.encodeString.contramap[Js](_.cmd)
  given Encoder[BindKey] = deriveEncoder[BindKey]
  given Encoder[Command] = deriveEncoder[Command]

  extension (config: String)
    def trimQuoteInData =
      config.replace(""""function""", "function").replace("""}"""", "}").replace("""\"""", "'")
