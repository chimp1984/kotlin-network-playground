package bisq2.net

import java.io.Serializable

class Message(val value: String) : Serializable {
    override fun toString(): String = "Message '$value'"
}