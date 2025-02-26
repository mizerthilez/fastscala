package com.fastscala.db.keyed.uuid

import java.util.UUID

import scalikejdbc.*

import com.fastscala.db.RowWithIdBase

trait RowWithUuidIdBase extends RowWithIdBase:
  def table: TableWithUUIDBase[?]

  var uuid: Option[UUID] = None

  def id: UUID = uuid.get

  def isPersisted_? : Boolean = uuid.isDefined

  def saveSQL(): SQL[Nothing, NoExtractor]
