package models

import slick.driver.JdbcProfile
import spatutorial.shared.models._


trait DeverrorTable {
  protected val driver: JdbcProfile
  import driver.api._
  class Deverrors(tag: Tag) extends Table[Deverror](tag, "DEVERROR") {

    def id = column[String]("Id", O.PrimaryKey)
    def snippet = column[String]("Snippet")
    def body = column[String]("Body")
    def date = column[Long]("Date")

    def * = (id, snippet, body, date) <> (Deverror.tupled, Deverror.unapply   _)
  }
}