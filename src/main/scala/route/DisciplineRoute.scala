package route

import Alpakka.Operations.SendMessageAndWaitForResponsAlpakka
import Alpakka.RabbitMQModel.RabbitMQModel
import RabbitMQ.RabbitMQOperation.Operations.Formatter.{extractContentList, parseTeacherString}
import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.stream.alpakka.amqp.AmqpConnectionProvider
import com.rabbitmq.client.ConnectionFactory
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import model.Discipline
import org.json4s.{DefaultFormats, jackson}
import repository._

import scala.concurrent.{ExecutionContext, Future}

class DisciplineRoutes( amqpConnectionProvider: AmqpConnectionProvider) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats
  implicit lazy val system: ActorSystem = ActorSystem("web-system")
  implicit lazy val mat: Materializer = Materializer(system)
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  val pubDisciplineTeacherMQModel: RabbitMQModel = RabbitMQModel("DisciplinePublisher", "UniverSystem", "univer.discipline-api.teacherForDisciplineByIdGet")
  val replyDisciplineTeacherMQModel: RabbitMQModel = RabbitMQModel("DisciplineSubscription", "UniverSystem", "univer.teacher-api.teacherForDisciplineByIdGet")


  val route =
    pathPrefix("discipline") {
      concat(
        pathEnd {
          concat(
            get {
              complete(DisciplineRepository.getAllDisciplines())
            },
            post {
              entity(as[Discipline]) { discipline =>
                complete(DisciplineRepository.addDiscipline(discipline))
              }
            }
          )
        },
        path(Segment) { disciplineId =>
          concat(
            get {
              complete(DisciplineRepository.getDisciplineById(disciplineId))
            },
            put {
              entity(as[Discipline]) { updatedDiscipline =>
                complete(DisciplineRepository.updateDiscipline(disciplineId, updatedDiscipline))
              }
            },
            delete {
              complete(DisciplineRepository.deleteDiscipline(disciplineId))
            }
          )
        }
      )
    }~
      pathPrefix("disciplineGetTeacher"){
        path(Segment) { disciplineId =>
          concat(
            get {
              val disciplineFuture: Future[Option[Discipline]] = DisciplineRepository.getDisciplineById(disciplineId)

              val resultFuture: Future[Option[Discipline]] = disciplineFuture.flatMap {
                case Some(student) =>
                  val sendResultFuture = SendMessageAndWaitForResponsAlpakka.sendMessageAndWaitForResponse(extractContentList(student.teacherIds), pubDisciplineTeacherMQModel, replyDisciplineTeacherMQModel, amqpConnectionProvider)()
                  sendResultFuture.map { result =>

                    println("Результат в виде строки " + result)

                    val resultList = result.stripPrefix("List(").stripSuffix(")").split(",").map(_.trim).map(_.stripPrefix("Some(").stripSuffix(")")).toList



                    val updatedStudent = student.copy(teachers = Option(resultList))

                    Some(updatedStudent)
                  }

              }

              onSuccess(resultFuture) {
                case Some(teacher) => complete(teacher)
              }
            },
          )
        }

      }
}

object DisciplineRoutes {
  def apply( amqpConnectionProvider: AmqpConnectionProvider): DisciplineRoutes = new DisciplineRoutes(amqpConnectionProvider)
}

