package bisq2.net.connection

import java.net.Socket

class InboundConnection(socket: Socket) : Connection(socket) {

    override fun toString(): String = "InboundConnection '$uid'"
}