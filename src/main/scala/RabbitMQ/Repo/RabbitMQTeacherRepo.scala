//package RabbitMQ.Repo
//
//import Connection.PostgresConnection
//import model.{Discipline, Teacher}
//import repository.DisciplineRepository.disciplines
//import slick.jdbc.JdbcBackend.Database
//import slick.lifted.TableQuery
//import repository.TeacherRepository
//import repository.TeacherRepository.{Teachers, teachers}
//import slick.jdbc.PostgresProfile.api._
//
//import scala.concurrent.Future
//import scala.concurrent.ExecutionContext
//
//object RabbitMQTeacherRepo {
//
//  val db: Database = Connection.PostgresConnection.db
//  val teachersTable = TableQuery[TeacherRepository.Teachers]
//
//  val teachers = TableQuery[Teachers]
//
//
//  def getTeachersByIds(teacherIds: List[Int])(implicit ec: ExecutionContext): Future[Seq[Teacher]] = {
//    PostgresConnection.db.run(teachers.filter(_.teacherId.inSet(teacherIds)).result)
//  }
//
//
//  def convertMessageToIntList(message: String): List[Int] = {
//    try {
//      message.split(" ").map(_.toInt).toList
//    } catch {
//      case e: NumberFormatException =>
//        println(s"Unable to convert characters to integers: ${e.getMessage}")
//        List.empty[Int]
//    }
//  }
//
//}
