package com.heqiying.fundmng.settle.model

import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import slick.profile.SqlProfile.ColumnOption.Nullable

case class Appendant(id: Option[Long], uuid: String, tpe: String, name: Option[String], fileuuid: String)

class Appendants(tag: Tag) extends Table[Appendant](tag, "appendants") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)

  def uuid = column[String]("uuid", O.Length(63, varying = false))

  def tpe = column[String]("type", O.Length(127))

  def name = column[String]("name", O.Length(127), Nullable)

  def fileuuid = column[String]("fileuuid", O.Length(63, varying = false))

  def uniqUuid = index("uniq_uuid", uuid, unique = true)

  def * = (id.?, uuid, tpe, name.?, fileuuid) <> (Appendant.tupled, Appendant.unapply)
}
