package RabbitMQ.RabbitMQOperation.Operations

import model.importModels.ImportTeacher

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

object Formatter {

  def extractContentList[T: ClassTag](obj: T): String = {
    val value = obj match {
      case Some(lst: List[_]) => lst.map(_.toString).mkString(",")
      case lst: List[_] => lst.map(_.toString).mkString(",")
      case _ => ""
    }
    value
  }


  def parseTeacherString(input: String): List[ImportTeacher] = {
    val teacherList = input.stripPrefix("List(").stripSuffix(")").split("Teacher").toList.drop(1)

    teacherList.flatMap { teacher =>
      val fields = teacher.stripPrefix("(").stripSuffix(")").split(", ").toList
      if (fields.length >= 16) {
        Some(ImportTeacher(
          _id = fields(0),
          name = parseOptionString(fields(1)),
          age = parseOptionInt(fields(2)),
          email = parseOptionString(fields(3)),
          phoneNumber = parseOptionString(fields(4)),
          education = parseOptionString(fields(5)),
          qualification = parseOptionString(fields(6)),
          experience = parseOptionInt(fields(7)),
          scheduleId = parseOptionInt(fields(8)),
          disciplinesId = parseOptionListString(fields(9)),
          studentsId = parseOptionListString(fields(10)),
          salary = parseOptionInt(fields(11)),
          position = parseOptionString(fields(12)),
          awards = parseOptionString(fields(13)),
          certificationId = parseOptionString(fields(14)),
          attestationId = parseOptionString(fields(15)),
          discipline = None,
          students = None,
          studentsAverage = None,
          documents = None
        ))
      } else {
        None
      }
    }
  }

  def parseOptionString(input: String): Option[String] = {
    input match {
      case "None" => None
      case str => Some(str.stripPrefix("Some(").stripSuffix(")"))
    }
  }

  def parseOptionInt(input: String): Option[Int] = {
    input match {
      case "None" => None
      case str => Some(str.stripPrefix("Some(").stripSuffix(")").toInt)
    }
  }

  def parseOptionListString(input: String): Option[List[String]] = {
    input match {
      case "None" => None
      case str => Some(str.stripPrefix("Some(List(").stripSuffix("))").split(", ").toList)
    }
  }


  def extractContent[T: ClassTag](obj: T): String = {
    val fields = obj.getClass.getDeclaredFields.map(_.getName)
    val values = fields.map { fieldName =>
      val field = obj.getClass.getDeclaredField(fieldName)
      field.setAccessible(true)
      val value = field.get(obj)
      val cleanedValue = value match {
        case Some(v) => v.toString
        case other => other.toString
      }
      cleanedValue
    }
    values.mkString(",")
  }

  object StringToObjectConverter {
    // Оставляем только один метод stringToObject
    def stringToObject[T](str: String)(implicit classTag: ClassTag[T]): T = {
      val clazz = implicitly[ClassTag[T]].runtimeClass
      clazz.getDeclaredConstructor(classOf[String]).newInstance(str).asInstanceOf[T]
    }

    // Метод, который принимает Future[String] и возвращает Future[T]
    def processResult[T](futureString: Future[String])(implicit classTag: ClassTag[T], ex: ExecutionContext): Future[T] = {
      futureString.map(stringToObject[T])
    }

  }
}