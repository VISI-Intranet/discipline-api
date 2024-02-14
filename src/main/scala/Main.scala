import RabbitMQ.RabbitMQConsumer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import routes.{DiRoute, DisciplineRoutes, TeacherRoute}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.io.StdIn


object Main {
  implicit val system: ActorSystem = ActorSystem("web-service")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()






  def main(args: Array[String]): Unit = {



    // Routes
    val routes = TeacherRoute.route ~ DisciplineRoutes.route ~ DiRoute.route


    // Start the server
    val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

    println("Server is online at http://localhost:8080/\nPress RETURN to stop...")


    StdIn.readLine()



    bindingFuture
      .flatMap(_ => bindingFuture.flatMap(_.unbind()))
      .onComplete(_ => {
        system.terminate()
      })
  }
}
