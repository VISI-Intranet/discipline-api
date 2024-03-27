
import Alpakka.Operations.RecieveMessageAlpakka
import Alpakka.RabbitMQModel.RabbitMQModel

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.io.StdIn
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.stream.alpakka.amqp.{AmqpConnectionProvider, AmqpLocalConnectionProvider}
import com.rabbitmq.client.ConnectionFactory
import route.DisciplineRoutes

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("web-service")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val amqpConnectionProvider: AmqpConnectionProvider = AmqpLocalConnectionProvider


  val routes = DisciplineRoutes(amqpConnectionProvider).route

  val subMQModel: RabbitMQModel = RabbitMQModel("StudentPublisher", "", "")

  RecieveMessageAlpakka.subscription(subMQModel,amqpConnectionProvider)

  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8082)

  println("Server is online at http://localhost:8081/\nPress RETURN to stop...")

  StdIn.readLine()

  bindingFuture
    .flatMap(_ => bindingFuture.flatMap(_.unbind()))
    .onComplete(_ => {
      system.terminate()
    })



}