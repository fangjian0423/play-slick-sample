package model

import play.api.libs.json.Json

/**
 * Created by format on 15/8/20.
 */
case class Student(id: Long, name: String, age: Int)

object Student {

  implicit val studentFormat = Json.format[Student]

}