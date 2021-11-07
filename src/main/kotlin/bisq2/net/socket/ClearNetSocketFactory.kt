package bisq2.net.socket

import bisq2.net.Address
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.net.ServerSocket
import java.net.Socket

val log = KotlinLogging.logger {}

class ClearNetSocketFactory {
    suspend fun initialize() {
        log.info { "initialize" }
        delay(20L)
    }

    suspend fun createServerSocket(serverId: String, serverPort: Int) =
        withContext(IO) {
            runCatching {
                val serverSocket = ServerSocket(serverPort)
                val address = Address.localhost(serverPort)
                log.info { "Create serverSocket on $serverPort" }
                ServerSocketResult(serverId, serverSocket, address)
            }
        }

    suspend fun createSocket(address: Address) =
        withContext(IO) {
            runCatching {
                log.info { "Create socket to $address" }
                Socket(address.host, address.port)
            }
        }

    suspend fun shutdown() {
        log.info("shutdown")
        delay(20L)
    }
}