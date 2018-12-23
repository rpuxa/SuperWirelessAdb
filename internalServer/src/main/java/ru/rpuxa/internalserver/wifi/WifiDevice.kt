package ru.rpuxa.internalserver.wifi

import ru.rpuxa.internalserver.stream.MessageInputStream
import ru.rpuxa.internalserver.stream.MessageOutputStream

class WifiDevice(
        val output: MessageOutputStream,
        val input: MessageInputStream,
        val lastAddressByte: Int
) {
    var isClosed = output.isClosed || input.isClosed

    fun close() {
        output.close()
        input.close()
    }
}