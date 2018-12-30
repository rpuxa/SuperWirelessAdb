package ru.rpuxa.internalserver.wifi

import ru.rpuxa.internalserver.stream.TwoWayMessageStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress

class WifiDevice(
        input: InputStream,
        output: OutputStream,
        val ip: InetAddress
) {
    val lastAddressByte: Int = ip.address[3].toInt()

    val stream = TwoWayMessageStream(input, output)

    val isClosed: Boolean get() = stream.isClosed

    fun close() {
        stream.close()
    }


}
