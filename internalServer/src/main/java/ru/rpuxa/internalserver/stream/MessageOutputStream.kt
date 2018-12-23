package ru.rpuxa.internalserver.stream

import java.io.ObjectOutputStream
import java.io.OutputStream
import kotlin.concurrent.thread

class MessageOutputStream(outputStream: OutputStream, private val baseTimeout: Int = 300) : ObjectOutputStream(outputStream) {

    var isClosed = false
    private var timeout = 0

    init {
        thread {
            try {
                while (!isClosed) {
                    while (timeout > 0) {
                        Thread.sleep(1)
                        timeout--
                    }

                    if (timeout == 0)
                        writeObject(Check)

                    timeout = baseTimeout
                }
            } catch (e: Exception) {
                e.printStackTrace()
                close()
            }
        }
    }

    fun sendMessage(message: Any?) {
        timeout = 2 * baseTimeout
        writeObject(message)
        flush()
        timeout = 2 * baseTimeout
    }

    override fun close() {
        isClosed = true
        super.close()
    }

    object Check
}