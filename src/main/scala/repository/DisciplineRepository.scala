package repository

import model._
import connection.MongoDBConnection
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Try
import com.rabbitmq.client.ConnectionFactory
import org.mongodb.scala.model.Filters

object DisciplineRepository {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  def getDisciplineById(disciplineId: String): Future[Option[Discipline]] = {
    val filter = Filters.eq("_id", disciplineId)
    val futureDiscipline = MongoDBConnection.disciplineCollection.find(filter).headOption()

    futureDiscipline.map {
      case Some(doc) =>
        Some(
          Discipline(
            _id = doc.getString("_id"),
            disciplineName = Option(doc.getString("disciplineName")),
            description = Option(doc.getString("description")),
            credits = Option(doc.getInteger("credits")),
            hours = Option(doc.getInteger("hours")),
            disciplineType = doc.getString("disciplineType"),
            teacherIds = Option(Option(doc.getList("teacherIds", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
            department = Option(doc.getString("department")),
            studentIds = Option(Option(doc.getList("studentIds", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
            topics = Option(Option(doc.getList("topics", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
            classrooms = Option(Option(doc.getList("classrooms", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
            language = doc.getString("language"),
            scheduleIds = Option(Option(doc.getList("scheduleIds", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
            teachers = None
          )
        )
      case None => None
    }
  }

  def getAllDisciplines(): Future[List[Discipline]] = {
    val futureDisciplines = MongoDBConnection.disciplineCollection.find().toFuture()

    futureDisciplines.map { docs =>
      Option(docs).map(_.map { doc =>
        Discipline(
          _id = doc.getString("_id"),
          disciplineName = Option(doc.getString("disciplineName")),
          description = Option(doc.getString("description")),
          credits = Option(doc.getInteger("credits")),
          hours = Option(doc.getInteger("hours")),
          disciplineType = doc.getString("disciplineType"),
          teacherIds = Option(Option(doc.getList("teacherIds", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
          department = Option(doc.getString("department")),
          studentIds = Option(Option(doc.getList("studentIds", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
          topics = Option(Option(doc.getList("topics", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
          classrooms = Option(Option(doc.getList("classrooms", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
          language = doc.getString("language"),
          scheduleIds = Option(Option(doc.getList("scheduleIds", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
          teachers = None
        )
      }.toList).getOrElse(List.empty)
    }
  }


  def addDiscipline(discipline: Discipline): Future[String] = {
    val disciplineDocument = BsonDocument(
      "_id" -> BsonString(discipline._id),
      "disciplineName" -> BsonString(discipline.disciplineName.getOrElse("")),
      "description" -> BsonString(discipline.description.getOrElse("")),
      "credits" -> BsonInt32(discipline.credits.getOrElse(0)),
      "hours" -> BsonInt32(discipline.hours.getOrElse(0)),
      "disciplineType" -> BsonString(discipline.disciplineType),
      "teacherIds" -> BsonArray(discipline.teacherIds.getOrElse(List.empty).map(BsonString(_))),
      "department" -> BsonString(discipline.department.getOrElse("")),
      "studentIds" -> BsonArray(discipline.studentIds.getOrElse(List.empty).map(BsonString(_))),
      "topics" -> BsonArray(discipline.topics.getOrElse(List.empty).map(BsonString(_))),
      "classrooms" -> BsonArray(discipline.classrooms.getOrElse(List.empty).map(BsonInt32(_))),
      "language" -> BsonString(discipline.language),
      "scheduleIds" -> BsonArray(discipline.scheduleIds.getOrElse(List.empty).map(BsonString(_)))
    )

    MongoDBConnection.disciplineCollection.insertOne(disciplineDocument).toFuture().map(_ => s"Дисциплина - ${discipline._id} была добавлена в базу данных ;)")
  }


  def deleteDiscipline(disciplineId: String): Future[String] = {
    val disciplineDocument = Document("_id" -> disciplineId)
    MongoDBConnection.disciplineCollection.deleteOne(disciplineDocument).toFuture().map(_ => s"Дисциплина с id $disciplineId была удалена, проверьте БД ;)")
  }

  def updateDiscipline(disciplineId: String, updatedDiscipline: Discipline): Future[String] = {
    val filter = Document("_id" -> disciplineId)

    val disciplineDocument = BsonDocument(
      "$set" -> BsonDocument(
        "disciplineName" -> BsonString(updatedDiscipline.disciplineName.getOrElse("")),
        "description" -> BsonString(updatedDiscipline.description.getOrElse("")),
        "credits" -> BsonInt32(updatedDiscipline.credits.getOrElse(0)),
        "hours" -> BsonInt32(updatedDiscipline.hours.getOrElse(0)),
        "disciplineType" -> BsonString(updatedDiscipline.disciplineType),
        "teacherIds" -> BsonArray(updatedDiscipline.teacherIds.getOrElse(List.empty).map(BsonString(_))),
        "department" -> BsonString(updatedDiscipline.department.getOrElse("")),
        "studentIds" -> BsonArray(updatedDiscipline.studentIds.getOrElse(List.empty).map(BsonString(_))),
        "topics" -> BsonArray(updatedDiscipline.topics.getOrElse(List.empty).map(BsonString(_))),
        "classrooms" -> BsonArray(updatedDiscipline.classrooms.getOrElse(List.empty).map(BsonInt32(_))),
        "language" -> BsonString(updatedDiscipline.language),
        "scheduleIds" -> BsonArray(updatedDiscipline.scheduleIds.getOrElse(List.empty).map(BsonString(_))),
      )
    )

    MongoDBConnection.disciplineCollection.updateOne(filter, disciplineDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Дисциплина была обновлена, id: $disciplineId - не знаю что изменилось, проверяй в БД ;)"
      } else {
        "Обновление дисциплины не выполнено: Проблема либо в базе, либо в тебе ;)"
      }
    }
  }




}
