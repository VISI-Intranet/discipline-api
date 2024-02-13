package model

import io.circe.parser.decode
import io.circe.generic.auto._
import cats.syntax.either._

object ImportTeachModelParser {
  def parseMessage(message: String): Either[String, ImportTeachModel] = {
    decode[ImportTeachModel](message).leftMap(_.toString)
  }

  def parseMessageList(message: String): Either[String, List[ImportTeachModel]] = {
    decode[List[ImportTeachModel]](message).leftMap(_.toString)
  }
}
