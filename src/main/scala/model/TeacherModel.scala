package model

import org.bson.types.ObjectId
import org.json4s.JsonAST.JString
import org.json4s.{CustomSerializer, DefaultFormats, Formats, JsonAST, MappingException}


object Qualification extends Enumeration {
  type Qualification = Value
  val PhD, MeD, Certified = Value
}

object Position extends Enumeration {
  type Position = Value
  val Lecturer, Docent, Professor = Value
}

object EnumSerializer extends CustomSerializer[Enumeration#Value](format => (
  {
    case JString(s) =>
      // Проверяем, есть ли среди значений Qualification
      if (Qualification.values.exists(_.toString == s)) {
        Qualification.withName(s)
      } else if (Position.values.exists(_.toString == s)) {
        Position.withName(s)
      } else {
        throw new MappingException(s"Unknown enumeration value: $s")
      }
    case value =>
      throw new MappingException(s"Can't convert $value to Enumeration")
  },
  {
    case enumValue: Enumeration#Value =>
      JString(enumValue.toString)
  }
))

object JsonFormats {
  implicit val formats: Formats = DefaultFormats + EnumSerializer
}


case class Teacher(
                    _id: String,
                    disciplinesIds: Option[List[Int]],
                    education: Option[String],
                    qualification: Option[Qualification.Qualification],
                    experience: Option[Int],
                    scheduleId: Option[Int],
                    studentsIds: Option[List[Int]],
                    salary: Option[Int],
                    newsId: Option[Int],
                    materialsId: Option[Int],
                    position: Option[Position.Position],
                    awards: Option[String],
                    certificationId: Option[Int],
                    attestationId: Option[Int]
                  )



