package model.importModels

case class ImportTeacher(
                          teacherId: Option[Int],
                          education: String,
                          qualification: String,
                          experience: Int,
                          scheduleId: Int,
                          salary: Int,
                          position: String,
                          awards: String,
                          certificationId: Int,
                          attestationId: Int
                        )
