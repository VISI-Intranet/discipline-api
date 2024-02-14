package RabbitMQ
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.rabbitmq.client.{AMQP, DefaultConsumer, Envelope}
import RabbitMQ.RabbitMQConnection

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

object RabbitMQConsumer {
  implicit val system: ActorSystem = ActorSystem("RabbitMQConsumer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  def listenAndProcessMessages(queueName: String, routingKey: String): Future[String] = {
    val (connection, channel) = RabbitMQConnection.createConnection()
    val messagePromise = Promise[String]()

    try {
      // Ensure that the queue exists and is bound to the exchange with the specified routing key
      channel.queueDeclare(queueName, true, false, false, null)
      channel.queueBind(queueName, "TeacherExchange", routingKey)

      // Create a consumer and set up the callback
      val consumer = new DefaultConsumer(channel) {
        override def handleDelivery(
                                     consumerTag: String,
                                     envelope: Envelope,
                                     properties: AMQP.BasicProperties,
                                     body: Array[Byte]
                                   ): Unit = {
          val message = new String(body, "UTF-8")

          if (message != null) {
            // Complete the promise with the received message
            messagePromise.success(message)
          } else {
            // Handle the case when the message is null
            messagePromise.failure(new RuntimeException("Received null message from RabbitMQ"))
          }

          // Complete the promise with the received message
          messagePromise.success(message)
        }
      }

      // Start consuming messages using Akka Streams
      Source.single(consumer)
        .mapAsync(1)(_ => Future(channel.basicConsume(queueName, true, consumer)))
        .runWith(Sink.ignore)
        .flatMap(_ => messagePromise.future)
    } finally {
      // Don't forget to close the connection when done
      // This is just an example; in a real application, you might want to keep the connection open
      connection.close()
    }
  }
}
