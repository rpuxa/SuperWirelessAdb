package ru.rpuxa.internalserver.stream

import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

typealias MessageListener = (Int, Any?) -> Any?

class TwoWayMessageStream(input: InputStream, output: OutputStream) {

    private val ois = ObjectInputStream(input)
    private val oos = ObjectOutputStream(output)
    private val promises: MutableMap<Long, PromiseImpl<*>> = ConcurrentHashMap()
    private var timeToCheck = TIMEOUT
    private var listener: MessageListener? = null

    var isClosed = false

    init {
        thread {
            try {
                while (!isClosed) {
                    val msg = ois.readObject() as Message
                    if (msg != CHECK_MESSAGE) {
                        val promise = promises.remove(msg.id)
                        if (promise == null) {
                            thread {
                                val ans = listener?.invoke(msg.command, msg.data)
                                send(Message(msg.id, -1, ans))
                            }
                        } else {
                            promise.message(msg)
                        }
                    }
                }
            } catch (e: Exception) {
                close()
            }
        }

        thread {
            try {
                while (true) {
                    while (!isClosed && timeToCheck > 0) {
                        Thread.sleep(10)
                        timeToCheck -= 10
                    }

                    if (isClosed)
                        return@thread

                    send(CHECK_MESSAGE)
                }
            } catch (e: Exception) {
                close()
            }
        }
    }

    fun <T> sendMessage(command: Int, data: Any? = null): Promise<T> {
        val promise = PromiseImpl<T>()

        val msg = Message(random.nextLong(), command, data)

        thread {
            Thread.sleep(5000)
            val p = promises.remove(msg.id) ?: return@thread
            p.timeout()
        }

        send(msg)

        return promise
    }

    fun onMessage(listener: MessageListener) {
        this.listener = listener
    }

    @Synchronized
    private fun send(obj: Any) {
        timeToCheck = 2 * TIMEOUT
        try {
            oos.writeObject(obj)
            oos.flush()
        } catch (e: IOException) {
            close()
        }
        timeToCheck = 2 * TIMEOUT
    }

    fun close() {
        isClosed = true
        try {
            ois.close()
        } catch (e: IOException) {
        }
        try {
            oos.close()
        } catch (e: IOException) {
        }
    }

    interface Promise<T> {
        fun onMessage(callback: (T) -> Unit): Promise<T>

        fun onTimeout(callback: () -> Unit): Promise<T>
    }

    private class PromiseImpl<T> : Promise<T> {
        var messageCallback: ((T) -> Unit)? = null
        var timeoutCallback: (() -> Unit)? = null

        override fun onMessage(callback: (T) -> Unit): Promise<T> {
            messageCallback = callback

            return this
        }

        override fun onTimeout(callback: () -> Unit): Promise<T> {
            timeoutCallback = callback

            return this
        }

        fun message(msg: Any?) {
            @Suppress("UNCHECKED_CAST")
            messageCallback?.invoke(msg as T)
        }

        fun timeout() {
            timeoutCallback?.invoke()
        }
    }


    private data class Message(val id: Long, val command: Int, val data: Any?)

    companion object {
        private const val TIMEOUT = 300
        private val random = Random()
        private val CHECK_MESSAGE = Message(-91623681, -1, null)
    }
}