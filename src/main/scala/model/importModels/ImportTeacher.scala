package model.importModels

case class ImportTeacher(
                          _id: String,
                          name: Option[String],
                          age: Option[Int],
                          email: Option[String],
                          phoneNumber: Option[String],
                          education: Option[String],
                          qualification: Option[String],
                          experience: Option[Int],
                          scheduleId: Option[Int],
                          disciplinesId: Option[List[String]],
                          studentsId: Option[List[String]],
                          salary: Option[Int],
                          position: Option[String],
                          awards: Option[String],
                          certificationId: Option[String],
                          attestationId: Option[String],
                          discipline: Option[String],
                          students: Option[String],
                          studentsAverage: Option[String],
                          documents: Option[String]
                        )