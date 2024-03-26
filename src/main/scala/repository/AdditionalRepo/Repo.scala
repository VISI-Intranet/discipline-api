package repository.AdditionalRepo

import model._
import connection.MongoDBConnection
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.mongodb.scala.model.Filters.in
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Filters.and
import scala.collection.JavaConverters._

import com.rabbitmq.client.ConnectionFactory


object Repo {

  def getDisciplineNamesByIds(disciplineIds: List[String]): Future[List[String]] = {
    val filter = in("_id", disciplineIds: _*)

    val futureDisciplines = MongoDBConnection.disciplineCollection.find(filter).toFuture()

    futureDisciplines.map { documents =>
      documents.flatMap { doc =>
        Option(doc.getString("disciplineName"))
      }.toList
    }
  }


}
