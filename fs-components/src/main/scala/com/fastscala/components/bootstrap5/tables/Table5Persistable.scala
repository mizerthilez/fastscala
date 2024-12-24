package com.fastscala.components.bootstrap5.tables

import io.circe.JsonObject

trait Table5Persistable:
  def persistedState: JsonObject

  def persistedState_=(persistedState: JsonObject): Unit
