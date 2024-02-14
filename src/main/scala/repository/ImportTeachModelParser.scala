package repository

import io.circe.parser.decode
import io.circe.generic.auto._
import cats.syntax.either._
import model.ImportTeachModel

object ImportTeachModelParser {
  def parseMessage(message: String): Either[String, ImportTeachModel] = {
    decode[ImportTeachModel](message).leftMap(_.toString)
  }

//  def parseMessageList(message: String): Either[String, List[ImportTeachModel]] = {
//    decode[List[ImportTeachModel]](message).leftMap(_.toString)
//  }
def parseMessageList(message: String): List[ImportTeachModel] = {
  decode[List[ImportTeachModel]](message).getOrElse {
    // Обработка ошибки, например, вывод сообщения об ошибке
    println(s"Не удалось разобрать сообщение. Возвращен пустой список.")
    List.empty[ImportTeachModel]
  }
}

}
