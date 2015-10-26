package spatutorial.shared.models

import com.github.nscala_time.time.Imports._

case class Deverror(id: String, snippet: String, body: String, dateLong: Long) {
  def date : DateTime = new DateTime(dateLong)
}