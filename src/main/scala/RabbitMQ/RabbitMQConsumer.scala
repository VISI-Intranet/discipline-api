package RabbitMQ

import RabbitMQ.RabbitMQConnection.createConnection
import RabbitMQ.{RabbitMQConnection, RabbitMQProducer}
import com.rabbitmq.client.{AMQP, DefaultConsumer, Envelope}
import org.json4s.{DefaultFormats, jackson}

import java.util.concurrent.ArrayBlockingQueue
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

object RabbitMQConsumer {

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
          // Complete the promise with the received message
          messagePromise.success(message)
        }
      }

      // Start consuming messages
      channel.basicConsume(queueName, true, consumer)

      // Wait for the promise to be completed and return the result
      messagePromise.future
    } finally {
      // Don't forget to close the connection when done
      // This is just an example; in a real application, you might want to keep the connection open
      connection.close()
    }
  }

}
