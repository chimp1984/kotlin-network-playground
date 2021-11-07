package bisq2.net.connection

import bisq2.net.Message
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.util.*

private val log = KotlinLogging.logger {}

abstract class Connection(socket: Socket) {
    val uid = UUID.randomUUID().toString()
    private val objectOutputStream: ObjectOutputStream = ObjectOutputStream(socket.getOutputStream())
    private val objectInputStream: ObjectInputStream = ObjectInputStream(socket.getInputStream())

    suspend fun startListen(channel: Channel<Message>) =
        withContext(IO) {
            runCatching {
                while (true) {
                    log.info { "Listening for incoming messages at ${this@Connection}" }
                    val msg = objectInputStream.readObject()
                    if (msg is Message) {
                        log.info { "Received $msg at ${this@Connection}" }
                        channel.send(msg)
                    } else {
                        log.error { "Received invalid message $msg at ${this@Connection}" }
                    }
                }
            }
        }

    suspend fun send(message: Message) =
        withContext(IO) {
            runCatching {
                log.info { "Send message: $message" }
                objectOutputStream.writeObject(message)
                objectOutputStream.flush()
            }
        }
}