package repository

import model._
import connection.MongoDBConnection
import org.bson.types.ObjectId
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString, ObjectId}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Try;

object TeacherRepository {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global//для асинхронных операции

  def getAllTeachers():Future[List[Teacher]] = {
    val futureTeachers = MongoDBConnection.teacherCollection.find().toFuture()

    // преобразования данных из BSON-документов MongoDB в объекты Scala

    futureTeachers.map { docs =>
      Option(docs).map(_.map { doc =>
        Teacher(
          _id = doc.getString("_id"),
          disciplinesIds = Option(Option(doc.getList("DisciplinesID", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
          education = Option(doc.getString("Education")),
          qualification = Option(Qualification.withName(doc.getString("Qualification"))),
          experience = Option(doc.getInteger("Experience")),
          scheduleId = Option(doc.getInteger("ScheduleId")),
          studentsIds = Option(Option(doc.getList("StudentsID", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
          salary = Option(doc.getInteger("Salary")),
          newsId = Option(doc.getInteger("NewsID")),
          materialsId = Option(doc.getInteger("MaterialsId")),
          position = Option(Position.withName(doc.getString("Position"))),
          awards = Option(doc.getString("Awards")),
          certificationId = Option(doc.getInteger("CertificationId")),
          attestationId = Option(doc.getInteger("АттестацияID"))
        )
      }.toList).getOrElse(List.empty)
    }

  }

  def getTeachersById(teacherId:String):Future[Option[Teacher]] = {

    val teacherDocument = Document("_id" -> teacherId)

    MongoDBConnection.teacherCollection.find(teacherDocument).headOption().map {
      case Some(doc) =>
        Some(
          Teacher(
            _id = doc.getString("_id"),
            disciplinesIds = Option(Option(doc.getList("DisciplinesID", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
            education = Option(doc.getString("Education")),
            qualification = Option(Qualification.withName(doc.getString("Qualification"))),
            experience = Option(doc.getInteger("Experience")),
            scheduleId = Option(doc.getInteger("ScheduleId")),
            studentsIds = Option(Option(doc.getList("StudentsID", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
            salary = Option(doc.getInteger("Salary")),
            newsId = Option(doc.getInteger("NewsID")),
            materialsId = Option(doc.getInteger("MaterialsId")),
            position = Option(Position.withName(doc.getString("Position"))),
            awards = Option(doc.getString("Awards")),
            certificationId = Option(doc.getInteger("CertificationId")),
            attestationId = Option(doc.getInteger("АттестацияID"))
          )
        )

      case None => None
    }

  }

  def getTeachersByAnyParametr(param: String): Future[List[Teacher]] = {
    val keyValue = param.split("=")
    if (keyValue.length == 2) {
      val key = keyValue(0)
      val value = keyValue(1)

      val teacherDocument = Document(key -> value)

      MongoDBConnection.teacherCollection.find(teacherDocument).toFuture().map { docs =>
        Option(docs).map(_.map { doc =>
          Teacher(
            _id = doc.getString("_id"),
            disciplinesIds = Option(Option(doc.getList("DisciplinesID", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
            education = Option(doc.getString("Education")),
            qualification = Option(Qualification.withName(doc.getString("Qualification"))),
            experience = Option(doc.getInteger("Experience")),
            scheduleId = Option(doc.getInteger("ScheduleId")),
            studentsIds = Option(Option(doc.getList("StudentsID", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
            salary = Option(doc.getInteger("Salary")),
            newsId = Option(doc.getInteger("NewsID")),
            materialsId = Option(doc.getInteger("MaterialsId")),
            position = Option(Position.withName(doc.getString("Position"))),
            awards = Option(doc.getString("Awards")),
            certificationId = Option(doc.getInteger("CertificationId")),
            attestationId = Option(doc.getInteger("АттестацияID"))
          )
        }.toList).getOrElse(List.empty)
      }
    } else {
      // Обработка некорректного ввода
      Future.failed(new IllegalArgumentException("Неверный формат параметра"))
    }
  }


  def addTeacher(teacher: Teacher): Future[String] = {
    val teacherDocument = BsonDocument(
      "_id" -> teacher._id,
      "DisciplinesID" -> BsonArray(teacher.disciplinesIds.getOrElse(List.empty).map(id => BsonInt32(id))) ,
       "Education" -> BsonString(teacher.education.getOrElse("")),
      "Qualification" -> BsonString(teacher.qualification.getOrElse(Qualification.MeD).toString),
      "Experience" -> BsonInt32(teacher.experience.getOrElse(0)),
      "ScheduleId" -> BsonInt32(teacher.scheduleId.getOrElse(0)),
      "StudentsID" -> BsonArray(teacher.studentsIds.getOrElse(List.empty).map(id => BsonInt32(id))) ,
      "Salary" -> BsonInt32(teacher.salary.getOrElse(0)),
      "NewsID" -> BsonInt32(teacher.newsId.getOrElse(0)),
      "MaterialsId" -> BsonInt32(teacher.materialsId.getOrElse(0)),
      "Position" -> BsonString(teacher.position.getOrElse(Position.Docent).toString),
      "Awards" -> BsonString(teacher.awards.getOrElse("")),
      "CertificationId" -> BsonInt32(teacher.certificationId.getOrElse(0)),
      "АттестацияID" -> BsonInt32(teacher.attestationId.getOrElse(0))
    )

    MongoDBConnection.teacherCollection.insertOne(teacherDocument).toFuture().map { result =>
      if (result.wasAcknowledged() && result.getInsertedId != null) {
        s"Преподаватель ${teacher._id} был успешно добавлен"
      } else {
        throw new RuntimeException(s"Не удалось добавить преподавателя ${teacher._id}")
      }
    }.recover {
      case e: Throwable => s"Ошибка при добавлении преподавателя: ${e.getMessage}"
    }
  }


  def deleteTeacher(teacherId:String): Future[String] = {

    val teacherDocument = Document("_id" -> teacherId)


    MongoDBConnection.teacherCollection.deleteOne(teacherDocument).toFuture().map {
      _ => s"Преподаватель с Id ${teacherId} был добавлен "
    }


  }

  def updateTeacher(teacherId:String , updatedTeacher:Teacher): Future[String] = {
    val filter = Document("_id" -> teacherId)

    var teacherDocument = BsonDocument(
      "$set" ->BsonDocument(
        "DisciplinesID" -> BsonArray(updatedTeacher.disciplinesIds.getOrElse(List.empty).map(id => BsonInt32(id))),
        "Education" -> BsonString(updatedTeacher.education.getOrElse("")),
        "Qualification" -> BsonString(updatedTeacher.qualification.getOrElse(Qualification.MeD).toString),
        "Experience" -> BsonInt32(updatedTeacher.experience.getOrElse(0)),
        "ScheduleId" -> BsonInt32(updatedTeacher.scheduleId.getOrElse(0)),
        "StudentsID" -> BsonArray(updatedTeacher.studentsIds.getOrElse(List.empty).map(id => BsonInt32(id))),
        "Salary" -> BsonInt32(updatedTeacher.salary.getOrElse(0)),
        "NewsID" -> BsonInt32(updatedTeacher.newsId.getOrElse(0)),
        "MaterialsId" -> BsonInt32(updatedTeacher.materialsId.getOrElse(0)),
        "Position" -> BsonString(updatedTeacher.position.getOrElse(Position.Docent).toString),
        "Awards" -> BsonString(updatedTeacher.awards.getOrElse("")),
        "CertificationId" -> BsonInt32(updatedTeacher.certificationId.getOrElse(0)),
        "АттестацияID" -> BsonInt32(updatedTeacher.attestationId.getOrElse(0))

      )
    )

    MongoDBConnection.teacherCollection.updateOne(filter, teacherDocument).toFuture().map{
      updatedResult =>
        if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
          s"Преподаватель был обновлен, id: ${filter} - проверь в БД ;)"
        } else {
          "Обновление преподавателя не выполнено: Проблема либо в базе, либо в тебе ;)"
        }
    }




  }
}
