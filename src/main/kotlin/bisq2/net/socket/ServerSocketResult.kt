package bisq2.net.socket

import bisq2.net.Address
import java.net.ServerSocket

class ServerSocketResult(val serverId: String, val serverSocket: ServerSocket, val address: Address) 
