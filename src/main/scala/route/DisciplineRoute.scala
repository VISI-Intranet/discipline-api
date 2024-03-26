package route

import akka.http.scaladsl.server.Directives._
import com.rabbitmq.client.ConnectionFactory
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import model.Discipline
import org.json4s.{DefaultFormats, jackson}
import repository._

class DisciplineRoutes(connectionFactory: ConnectionFactory) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

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
              complete(DisciplineRepository.getDisciplineById(disciplineId, connectionFactory))
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
    }
}

object DisciplineRoutes {
  def apply(connectionFactory: ConnectionFactory): DisciplineRoutes = new DisciplineRoutes(connectionFactory)
}

