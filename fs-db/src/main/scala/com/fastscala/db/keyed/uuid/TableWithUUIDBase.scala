package com.fastscala.db.keyed.uuid

import java.util.UUID

import com.fastscala.db.{ Table, TableWithId }

trait TableWithUUIDBase[R <: RowWithUuidIdBase] extends Table[R] with TableWithId[R, UUID]:
  def getForIdOpt(key: UUID): Option[R]

  def getForIds(uuid: UUID*): List[R]
