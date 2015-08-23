package dal


import model.Student

import scala.concurrent.{Future, ExecutionContext}
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{ Inject, Singleton }

import slick.driver.JdbcProfile

/**
 * Created by format on 15/8/21.
 */
@Singleton
class StudentRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class StudentTable(tag: Tag) extends Table[Student](tag, "students") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def age = column[Int]("age")

    def * = (id, name, age) <> ((Student.apply _).tupled, Student.unapply)
  }

  private val students = TableQuery[StudentTable]


  def create(name: String, age: Int): Future[Student] = db.run {
    (students.map(s => (s.name, s.age))
      returning students.map(_.id)
      into ((nameAge, id) => Student(id, nameAge._1, nameAge._2))
      ) += (name, age)
  }


  def list(): Future[Seq[Student]] = db.run {
    students.result
  }

}
