package bisq2.net.connection

import bisq2.net.Address
import java.net.Socket

class OutboundConnection(val address: Address, socket: Socket) : Connection(socket) {
    override fun toString(): String = "OutboundConnection '$uid'"
}