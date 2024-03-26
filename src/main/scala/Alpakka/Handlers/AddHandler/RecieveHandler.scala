package Alpakka.Handlers.AddHandler

import Alpakka.Operations.SendMessageWithCorrelationIdAlpakka
import Alpakka.RabbitMQModel.RabbitMQModel
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.amqp.{AmqpConnectionProvider, AmqpLocalConnectionProvider}
import repository.AdditionalRepo.Repo

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object RecieveHandler {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit lazy val system: ActorSystem = ActorSystem("web-system")
  implicit lazy val mat: Materializer = Materializer(system)

  val handler: (String, String) => Unit = (message, routingKey) => {

    routingKey match {
      case "univer.student-api.disciplinesForStudentByIdGet" =>
        val messageList = message.split(",").toList

        val replyStudentDisciplineMQModel: RabbitMQModel = RabbitMQModel("StudentSubscription", "UniverSystem", "univer.discipline-api.disciplinesForStudentByIdGet")
        val amqpConnectionProvider :AmqpConnectionProvider = AmqpLocalConnectionProvider

        val futureResult = Repo.getDisciplineNamesByIds(messageList)

        futureResult.onComplete {
          case Success(result) =>
            println("Результат:")
            result.foreach(println)

            SendMessageWithCorrelationIdAlpakka.sendMessageWithCorrelationId(result,replyStudentDisciplineMQModel,amqpConnectionProvider)()
          case Failure(exception) =>
            println(s"Произошла ошибка: ${exception.getMessage}")
        }
        println(messageList)
      case "key2" =>
        // Обработка для ключа "key2"
        println(s"Received message for key2: $message")
      case _ =>
        // Обработка для всех остальных случаев
        println(s"Received message with unknown routing key: $routingKey")
    }

  }

}
