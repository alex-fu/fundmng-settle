package com.heqiying.fundmng.settle.model

import com.heqiying.fundmng.settle.database.MainDBProfile.profile.api._
import slick.profile.SqlProfile.ColumnOption.Nullable

case class Appendant(id: Option[Long], uuid: String, version: Int, tpe: String, name: Option[String], fileuuid: String)

class AppendantTable(tag: Tag) extends Table[Appendant](tag, "appendants") {
  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)

  def uuid = column[String]("uuid", O.Length(63, varying = false))

  def version = column[Int]("version")

  def tpe = column[String]("type", O.Length(127))

  def name = column[String]("name", O.Length(127), Nullable)

  def fileuuid = column[String]("fileuuid", O.Length(63, varying = false))

  def uniqUuid = index("uniq_uuid", uuid, unique = true)

  def * = (id.?, uuid, version, tpe, name.?, fileuuid) <> (Appendant.tupled, Appendant.unapply)
}
