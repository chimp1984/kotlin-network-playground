import bisq2.net.Address
import bisq2.net.Message
import bisq2.net.node.Node
import bisq2.net.socket.ClearNetSocketFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val log = KotlinLogging.logger {}


fun main(args: Array<String>) {
    runBlocking {
        val node_1111 = Node(ClearNetSocketFactory())
        async {
            node_1111.startServer(1111)
        }.await()

        val node_2222 = Node(ClearNetSocketFactory())
        async {
            node_2222.startServer(2222)
        }.await()

        node_1111.send(Address.localhost(2222), Message("message from 1111 to 2222"))
        delay(100)
        node_2222.send(Address.localhost(1111), Message("message from 2222 to 1111"))
    }
}


