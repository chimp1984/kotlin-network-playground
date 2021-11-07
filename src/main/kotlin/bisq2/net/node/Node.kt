package bisq2.net.node

import bisq2.net.Address
import bisq2.net.Message
import bisq2.net.connection.Connection
import bisq2.net.connection.InboundConnection
import bisq2.net.connection.OutboundConnection
import bisq2.net.socket.ClearNetSocketFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.net.Socket

private val log = KotlinLogging.logger {}

class Node(private val socketFactory: ClearNetSocketFactory) {
    private val inboundConnections: MutableList<InboundConnection> = ArrayList()
    private val outboundConnections: HashMap<Address, OutboundConnection> = HashMap()
    val messageChannel = Channel<Message>()

    suspend fun startServer(port: Int) {
        val result = getServerAsync(port).await()
        val server = Server(result.serverSocket)
        startListen(server)
    }

    private fun getServerAsync(port: Int) =
        CoroutineScope(IO).async {
            socketFactory.initialize()
            return@async socketFactory.createServerSocket("", port).getOrThrow()
        }

    private fun startListen(server: Server) {
        val socketChannel = Channel<Socket>()
        CoroutineScope(IO).launch {
            log.info { "Awaiting new sockets from server via socketChannel" }
            for (socket in socketChannel) {
                val inboundConnection = InboundConnection(socket)
                inboundConnections.add(inboundConnection)
                log.info { "Created new inbound connection $inboundConnection with local port: ${socket.port}" }

                listenForMessageOnConnection(inboundConnection)
            }
        }

        CoroutineScope(IO).launch {
            log.info("Start listing on port ${server.serverSocket.localPort}")
            server.listen(socketChannel)
        }
    }

    private fun CoroutineScope.listenForMessageOnConnection(connection: Connection) {
        launch(IO) {
            log.info { "Start listening for messages on $connection" }
            connection.startListen(messageChannel)
        }

        launch {
            log.info { "Awaiting new messages from $connection" }
            // todo provide msg to listeners
            /* for (message in messageChannel) {
                 log.info { "Received: $message on $connection" }
             }*/
        }
    }

    suspend fun connect(address: Address): OutboundConnection =
        coroutineScope {
            if (outboundConnections.containsKey(address)) {
                return@coroutineScope outboundConnections[address]!!
            }

            log.info { "Connect to $address" }
            val socket: Socket = socketFactory.createSocket(Address.localhost(address.port)).getOrThrow()
            val outboundConnection = OutboundConnection(address, socket)
            outboundConnections[address] = outboundConnection
            log.info { "Created new outbound connection $outboundConnection to ${address}" }
            CoroutineScope(IO).launch {
                listenForMessageOnConnection(outboundConnection)
            }
            return@coroutineScope outboundConnection
        }

    suspend fun send(address: Address, message: Message) =
        coroutineScope {
            log.info { "Send $message to $address" }
            val outboundConnection = outboundConnections.getOrDefault(address, connect(address))
            outboundConnection.send(message)
        }
}