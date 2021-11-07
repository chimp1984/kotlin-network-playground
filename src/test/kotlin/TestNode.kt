import bisq2.net.Address
import bisq2.net.Message
import bisq2.net.node.Node
import bisq2.net.socket.ClearNetSocketFactory
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.junit.Test
import java.util.concurrent.CountDownLatch

private val log = KotlinLogging.logger {}

class TestNode {

    @Test
    fun test() {
        runBlocking {
            val node1111 = Node(ClearNetSocketFactory())
            withContext(Dispatchers.Default) {
                node1111.startServer(1111)
            }

            val node2222 = Node(ClearNetSocketFactory())
            withContext(Dispatchers.Default) {
                node2222.startServer(2222)
            }

            val msg1 = "message from 1111 to 2222"
            val msg2 = "message from 2222 to 1111"

            val latch = CountDownLatch(2)
            launch {
                for (message in node1111.messageChannel) {
                    log.info { "Received: $message on node1111" }
                    assert(message.value == msg2)
                    latch.countDown()
                }
            }
            launch {
                for (message in node2222.messageChannel) {
                    log.info { "Received: $message on node2222" }
                    assert(message.value == msg1)
                    latch.countDown()
                }
            }

            node1111.send(Address.localhost(2222), Message(msg1))
            node2222.send(Address.localhost(1111), Message(msg2))

            async {
                kotlin.runCatching {
                    latch.await()
                }
            }
        }
    }
}