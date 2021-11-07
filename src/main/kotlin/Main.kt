import bisq2.net.Address
import bisq2.net.Message
import bisq2.net.node.Node
import bisq2.net.socket.ClearNetSocketFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

fun main() {
    runBlocking {
        val node1111 = Node(ClearNetSocketFactory())
        node1111.startServer(1111)

        val node2222 = Node(ClearNetSocketFactory())
        node2222.startServer(2222)

        node1111.send(Address.localhost(2222), Message("message from 1111 to 2222"))
        node1111.send(Address.localhost(2222), Message("message #2 from 1111 to 2222"))
        node2222.send(Address.localhost(1111), Message("message from 2222 to 1111"))
        val job1 = launch {
            for (message in node1111.messageChannel) {
                log.info { "Received: $message on node1111" }
            }
        }
        val job2 = launch {
            for (message in node2222.messageChannel) {
                log.info { "Received: $message on node2222" }
            }
        }

        launch {
            delay(100)
            job1.cancel()
            job2.cancel()
        }
    }
}


