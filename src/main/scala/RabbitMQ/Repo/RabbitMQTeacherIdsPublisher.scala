package RabbitMQ.Repo
import com.rabbitmq.client.{Channel, Connection, ConnectionFactory, MessageProperties}
import model.ImportTeachModel
import org.json4s._
import org.json4s.jackson.JsonMethods._

object RabbitMQTeacherIdsPublisher {

  // Конфигурации сервера RabbitMQ
  val rabbitMQHost = "localhost"
  val rabbitMQPort = 5672
  val rabbitMQUsername = "guest"
  val rabbitMQPassword = "guest"
  val exchangeName = "DisciplineExchange"
  val routingKey = "DisciplinePutRoutingKey"

  implicit val formats: Formats = DefaultFormats

  def publishTeacherIds(teacherIds: Option[List[Int]]): Option[List[ImportTeachModel]] = {
    // Создаем фабрику подключения
    val factory = new ConnectionFactory()
    factory.setHost(rabbitMQHost)
    factory.setPort(rabbitMQPort)
    factory.setUsername(rabbitMQUsername)
    factory.setPassword(rabbitMQPassword)

    // Создаем соединение и канал
    val connection: Connection = factory.newConnection()
    val channel: Channel = connection.createChannel()

    try {
      // Объявляем обмен
      channel.exchangeDeclare(exchangeName, "direct", true)

      // Преобразуем teacherIds в строку
      val teacherIdsString = teacherIds.map(ids => ids.mkString(" ")).getOrElse("")

      // Отправляем сообщение в обмен с указанным маршрутизационным ключом
      channel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, teacherIdsString.getBytes("UTF-8"))
      println(s" [x] Отправлены teacherIds: $teacherIdsString")

      // Возвращаем Some, чтобы указать успешное выполнение
      None
    } catch {
      case e: Exception =>
        e.printStackTrace()
        // Возвращаем None в случае ошибки
        None
    } finally {
      // Закрываем канал и соединение
      channel.close()
      connection.close()
    }
  }
}
