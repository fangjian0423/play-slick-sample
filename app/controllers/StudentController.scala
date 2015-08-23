package controllers

import javax.inject.Inject

import dal.StudentRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by format on 15/8/23.
 */
class StudentController @Inject() (repo: StudentRepository, val messagesApi: MessagesApi)
                                  (implicit ex: ExecutionContext) extends Controller with I18nSupport {

  val studentForm: Form[StudentForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "age" -> number.verifying(min(0), max(140))
    )(StudentForm.apply)(StudentForm.unapply)
  }


  def index = Action {
    Ok(views.html.index(studentForm))
  }

  def addStudent = Action.async { implicit request =>
    studentForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      student => {
        repo.create(student.name, student.age).map { _ =>
          Redirect(routes.StudentController.index)
        }
      }
    )
  }

  def getStudents = Action.async {
    repo.list().map { student =>
      Ok(Json.toJson(student))
    }
  }

}


case class StudentForm(name: String, age: Int)
