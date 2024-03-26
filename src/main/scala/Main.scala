
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.io.StdIn
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.rabbitmq.client.ConnectionFactory
import route.DisciplineRoutes

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("web-service")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()


  val connectionFactory = new  ConnectionFactory


  val routes = DisciplineRoutes(connectionFactory).route

  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8081)

  println("Server is online at http://localhost:8081/\nPress RETURN to stop...")

  StdIn.readLine()

  bindingFuture
    .flatMap(_ => bindingFuture.flatMap(_.unbind()))
    .onComplete(_ => {
      system.terminate()
    })



}