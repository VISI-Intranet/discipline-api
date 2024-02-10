package repository


import model._
import connection.MongoDBConnection
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString, ObjectId}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Try;


object DisciplineRepository {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  def getSortByHourDiscipline(): Future[List[Discipline]] = {
    val futureDiscipline = MongoDBConnection.disciplineCollection.find().toFuture();

    futureDiscipline.map{ docs =>
      Option(docs).map(_.map { doc =>
        Discipline (
          _id = doc.getString("_id"),
          disciplineName = Option(doc.getString("DisciplineName")),
          description = Option(doc.getString("Description")),
          credits = Option(doc.getInteger("Credits")),
          hours = Option(doc.getInteger("Hours")),
          disciplineType = Option(TypeOfDiscipline.withName(doc.getString("DisciplineType"))),
          teacherIds = Option(Option(doc.getList("TeacherIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
          department = Option(doc.getString("Department")),
          studentIds = Option(Option(doc.getList("StudentIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
          topics = Option(Option(doc.getList("Topics", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
          classrooms = Option( Option(doc.getList("Classrooms", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
          language = Option(Languages.withName(doc.getString("Language"))),
          scheduleIds = Option(Option(doc.getList("ScheduleIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
        )

      }.toList.sortBy(_.hours)).getOrElse(List.empty)

    }

  }


  def getAllDiscipline(): Future[List[Discipline]] = {
    val futureDiscipline = MongoDBConnection.disciplineCollection.find().toFuture();

    futureDiscipline.map { docs =>
      Option(docs).map(_.map { doc =>
        Discipline(
          _id = doc.getString("_id"),
          disciplineName = Option(doc.getString("DisciplineName")),
          description = Option(doc.getString("Description")),
          credits =Option( doc.getInteger("Credits")),
          hours = Option(doc.getInteger("Hours")),
          disciplineType = Option(TypeOfDiscipline.withName(doc.getString("DisciplineType"))),
          teacherIds = Option(Option(doc.getList("TeacherIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
          department = Option(doc.getString("Department")),
          studentIds = Option(Option(doc.getList("StudentIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
          topics = Option(Option(doc.getList("Topics", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
          classrooms = Option(Option(doc.getList("Classrooms", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
          language = Option(Languages.withName(doc.getString("Language"))),
          scheduleIds = Option(Option(doc.getList("ScheduleIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
        )

      }.toList).getOrElse(List.empty)

    }

  }
  def getDisciplineById(disciplineId : String): Future[Option[Discipline]] = {
    val disciplineDocument = Document("_id" -> disciplineId)

    MongoDBConnection.disciplineCollection.find(disciplineDocument).headOption().map {
      case Some(doc) =>
        Some(
          Discipline(
            _id = doc.getString("_id"),
            disciplineName = Option(doc.getString("DisciplineName")),
            description = Option(doc.getString("Description")),
            credits = Option(doc.getInteger("Credits")),
            hours = Option(doc.getInteger("Hours")),
            disciplineType = Option(TypeOfDiscipline.withName(doc.getString("DisciplineType"))),
            teacherIds = Option(Option(doc.getList("TeacherIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
            department = Option(doc.getString("Department")),
            studentIds = Option(Option(doc.getList("StudentIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
            topics = Option(Option(doc.getList("Topics", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
            classrooms = Option(Option(doc.getList("Classrooms", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
            language = Option(Languages.withName(doc.getString("Language"))),
            scheduleIds = Option(Option(doc.getList("ScheduleIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
          )
        )
      case None => None;
    }
  }

  def findDisciplineByParams(param: String): Future[List[Discipline]] = {
    val keyValue = param.split("=")

    if (keyValue.length == 2) {
      val key = keyValue(0)
      val value = Try(keyValue(1).toInt).toOption

      val disciplineDocument = Document(key -> value)

      MongoDBConnection.disciplineCollection
        .find(disciplineDocument)
        .toFuture()
        .map { docs =>
          docs.map { doc =>
            Discipline(
              _id = doc.getString("_id"),
              disciplineName = Option(doc.getString("DisciplineName")),
              description = Option(doc.getString("Description")),
              credits = Option(doc.getInteger("Credits")),
              hours = Option(doc.getInteger("Hours")),
              disciplineType = Option(TypeOfDiscipline.withName(doc.getString("DisciplineType"))),
              teacherIds = Option(Option(doc.getList("TeacherIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
              department = Option(doc.getString("Department")),
              studentIds = Option(Option(doc.getList("StudentIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
              topics = Option(Option(doc.getList("Topics", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)),
              classrooms = Option(Option(doc.getList("Classrooms", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)),
              language = Option(Languages.withName(doc.getString("Language"))),
              scheduleIds = Option(Option(doc.getList("ScheduleIds", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty))
            )
          }.toList
        }
    } else {
      // Обработка некорректного ввода
      Future.failed(new IllegalArgumentException("Неверный формат параметра"))
    }
  }


  def addDiscipline(discipline:Discipline) : Future[String] = {
    val disciplineDocument = BsonDocument(
      "_id"-> BsonString(discipline._id) ,
      "DisciplineName" -> BsonString(discipline.disciplineName.getOrElse("")) ,
      "Description" -> BsonString(discipline.description.getOrElse("")) ,
      "Credits" -> BsonInt32(discipline.credits.getOrElse(1)) ,
      "Hours" -> BsonInt32(discipline.hours.getOrElse(1)) ,
      "DisciplineType" -> TypeOfDiscipline.toBsonStringDiscipline(discipline.disciplineType.getOrElse(TypeOfDiscipline.Unknown)) ,
      "TeacherIds" -> BsonArray(discipline.teacherIds.getOrElse(List.empty).map(id => BsonInt32(id))),
      "Department" -> BsonString(discipline.department.getOrElse("")) ,
      "StudentIds" -> BsonArray(discipline.studentIds.getOrElse(List.empty).map(id => BsonInt32(id))),
      "Topics" -> BsonArray(discipline.topics.getOrElse(List.empty).map(BsonString(_))),
      "Classrooms" -> BsonArray(discipline.classrooms.getOrElse(List.empty).map(BsonInt32(_))),
      "Language" -> Languages.toBsonStringLanguage(discipline.language.getOrElse(Languages.Unknown)) ,
      "ScheduleIds" -> BsonArray(discipline.scheduleIds.getOrElse(List.empty).map(BsonInt32(_)))
    )
    MongoDBConnection.disciplineCollection.insertOne(disciplineDocument).toFuture().map(_=> s"Дисциплина - ${discipline.disciplineName} была добавлена , проверь БД ;)")
  }


  private var disciplineData: List[DiModel] = List()

  def addDi(discipline: DiModel): Unit = {
    // Логика добавления Discipline в репозиторий
    disciplineData = DiModel(discipline.disciplineId, discipline.disciplineName) :: disciplineData
  }

  def getDisciplineData(): List[DiModel] = disciplineData.reverse


  def deleteDiscipline(disciplineId:String) : Future[String] = {
    val disciplineDocument = Document("_id" -> disciplineId);
    MongoDBConnection.disciplineCollection.deleteOne(disciplineDocument).toFuture().map(_=>s"Дисциплина с id ${disciplineId} была удалена , проверь БД ;)")
  }

  def updateDiscipline(disciplineId:String , updatedDiscipline:Discipline): Future[String] = {
    val filter = Document("_id" -> disciplineId);

    val disciplineDocument = BsonDocument(
      "$set"-> BsonDocument(
        "DisciplineName" -> BsonString(updatedDiscipline.disciplineName.getOrElse("")),
        "Description" -> BsonString(updatedDiscipline.description.getOrElse("")),
        "Credits" -> BsonInt32(updatedDiscipline.credits.getOrElse(1)),
        "Hours" -> BsonInt32(updatedDiscipline.hours.getOrElse(1)),
        "DisciplineType" -> TypeOfDiscipline.toBsonStringDiscipline(updatedDiscipline.disciplineType.getOrElse(TypeOfDiscipline.Unknown)),
        "TeacherIds" -> BsonArray(updatedDiscipline.teacherIds.getOrElse(List.empty).map(id => BsonInt32(id))),
        "Department" -> BsonString(updatedDiscipline.department.getOrElse("")),
        "StudentIds" -> BsonArray(updatedDiscipline.studentIds.getOrElse(List.empty).map(id => BsonInt32(id))),
        "Topics" -> BsonArray(updatedDiscipline.topics.getOrElse(List.empty).map(BsonString(_))),
        "Classrooms" -> BsonArray(updatedDiscipline.classrooms.getOrElse(List.empty).map(BsonInt32(_))),
        "Language" -> Languages.toBsonStringLanguage(updatedDiscipline.language.getOrElse(Languages.Unknown)),
        "ScheduleIds" -> BsonArray(updatedDiscipline.scheduleIds.getOrElse(List.empty).map(BsonInt32(_)))

      )

    )

    MongoDBConnection.disciplineCollection.updateOne(filter , disciplineDocument).toFuture().map{updatedResult =>
      if(updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Дисциплина была обновлена , id : ${filter} - не знаю что изменилось , проверяй в БД ;)"
      }else {
        "Обновление дисциплины не выполнено : Проблема либо в базе , либо в тебе ;)"
      }
    }

  }

}