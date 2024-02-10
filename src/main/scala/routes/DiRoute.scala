package routes

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import model.{DiModel, Discipline, JsonFormatsDiscipline}
import org.json4s.{DefaultFormats, jackson}
import repository._


object DiRoute extends Json4sSupport {
  implicit val serialization = jackson.Serialization;
  implicit val formats = JsonFormatsDiscipline.formats;

  val route = path("getDisciplineData") {
    get {
      complete {
        // Получение данных из Spark и преобразование в формат JSON
        val disciplineData: List[DiModel] = DisciplineRepository.getDisciplineData()

        // Преобразование данных в JSON
        import org.json4s.DefaultFormats
        import org.json4s.jackson.Serialization.write

        implicit val formats: DefaultFormats.type = DefaultFormats
        val jsonData: String = write(disciplineData)

        // Возвращение данных в ответе HTTP
        jsonData
      }
    }
  }
}