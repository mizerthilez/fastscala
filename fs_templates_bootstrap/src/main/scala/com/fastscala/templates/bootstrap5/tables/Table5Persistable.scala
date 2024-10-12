package com.fastscala.templates.bootstrap5.tables

import io.circe.JsonObject

trait Table5Persistable:
  def persistedState: JsonObject

  def persistedState_=(persistedState: JsonObject): Unit
