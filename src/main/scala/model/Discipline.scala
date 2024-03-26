package model

import model.importModels.ImportTeacher

case class Discipline(
                       _id: String,
                       disciplineName: Option[String],
                       description: Option[String],
                       credits: Option[Int],
                       hours: Option[Int],
                       disciplineType: String,
                       teacherIds: Option[List[String]],
                       department: Option[String],
                       studentIds: Option[List[String]],
                       topics: Option[List[String]],
                       classrooms: Option[List[Int]],
                       language: String,
                       scheduleIds: Option[List[String]] ,
                       teachers: Option[List[ImportTeacher]]
                     )