package ru.rpuxa.internalserver.stream

import java.io.InputStream
import java.io.ObjectInputStream
import kotlin.concurrent.thread

class MessageInputStream(inputStream: InputStream) : ObjectInputStream(inputStream) {
    private var callback: ((Any) -> Unit)? = null
    var isClosed = false

    fun onMessage(callback: (Any) -> Unit) {
        this.callback = callback
    }

    init {
        thread {
            try {
                while (!isClosed) {
                    val msg = readObject()
                    if (msg !is MessageOutputStream.Check)
                        callback?.invoke(msg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                close()
            }
        }
    }

    fun getMessageBlocking(): Any? {
        val lock = Object()
        var answer: Any? = null
        onMessage {
            answer = it
            try {
                synchronized(lock) {
                    lock.notify()
                }
            } catch (e: Exception) {
            }
        }

        if (answer == null)
            synchronized(lock) {
                lock.wait()
            }

        return answer
    }

    override fun close() {
        isClosed = true
        super.close()
    }
}