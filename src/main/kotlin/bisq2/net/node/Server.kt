package bisq2.net.node

import kotlinx.coroutines.channels.Channel
import mu.KotlinLogging
import java.net.ServerSocket
import java.net.Socket

private val log = KotlinLogging.logger {}

class Server(val serverSocket: ServerSocket) {

    suspend fun listen(channel: Channel<Socket>) {
        runCatching {
            while (true) {
                log.info { "Wait for connection at $this" }
                val socket: Socket = serverSocket.accept()
                log.info { "Accepted new connection at $this" }
                channel.send(socket)
            }
        }
    }

    override fun toString(): String = "Server with port ${serverSocket.localPort}"
}