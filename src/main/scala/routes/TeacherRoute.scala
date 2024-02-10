package routes


import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.TeacherRepository
import model.{JsonFormats, Teacher}




object TeacherRoute extends Json4sSupport {

  implicit val serialization = jackson.Serialization
  implicit val formats = JsonFormats.formats;

  val route = pathPrefix("teacher"){
    concat(
      get {
        parameter("param") { param =>
          complete(TeacherRepository.getTeachersByAnyParametr(param.toString))
        }
      },
      pathEnd{
        concat(
          get{
            complete(TeacherRepository.getAllTeachers())
          },
          post{
            entity(as[Teacher]){teacher =>
            complete(TeacherRepository.addTeacher(teacher))

            }

          }
        )
      } ,
      path(Segment){ teacheId=>
        concat(
          get {
            complete(TeacherRepository.getTeachersById(teacheId))
          },
          put{
            entity(as[Teacher]) { updatedTeacher =>
              complete(TeacherRepository.updateTeacher(teacheId , updatedTeacher))

            }
          },
          delete{
            complete(TeacherRepository.deleteTeacher(teacheId))
          }
        )

      }
    )
  }

}
