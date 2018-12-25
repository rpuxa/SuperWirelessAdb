package ru.rpuxa.internalserver.stream

import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

typealias MessageListener = (Int, Any?) -> Any?

class TwoWayMessageStream(input: InputStream, output: OutputStream) {

    private var oos: ObjectOutputStream? = null
    private var ois: ObjectInputStream? = null
    private val promises: MutableMap<Long, PromiseImpl<*>> = ConcurrentHashMap()
    private var timeToCheck = TIMEOUT
    private lateinit var listener: MessageListener

    var isClosed = false

    init {
        try {
            oos = ObjectOutputStream(output)
            ois = ObjectInputStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            close()
        }
    }

    fun open() {
        println("hey")

        if (isClosed)
            return

        thread {
            try {
                while (!isClosed) {
                    val msg = ois!!.readObject() as Message
                    if (msg != CHECK_MESSAGE) {
                        println(msg)
                        val promise = promises.remove(msg.id)
                        if (promise == null) {
                            thread {
                                val ans = listener.invoke(msg.command, msg.data)
                                send(Message(msg.id, -1, ans))
                            }
                        } else {
                            promise.message(msg.data)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
                e.printStackTrace()
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

        promises[msg.id] = promise

        send(msg)

        return promise
    }

    fun onMessage(listener: MessageListener) {
        this.listener = listener
    }

    @Synchronized
    private fun send(obj: Any) {
        println(obj)
        timeToCheck = 2 * TIMEOUT
        try {
            oos!!.writeObject(obj)
            oos!!.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            close()
        }
        timeToCheck = 2 * TIMEOUT
    }

    fun close() {
        isClosed = true
        try {
            ois?.close()
        } catch (e: IOException) {
        }
        try {
            oos?.close()
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


    private data class Message(val id: Long, val command: Int, val data: Any?) : Serializable

    companion object {
        private const val TIMEOUT = 300
        private val random = Random()
        private val CHECK_MESSAGE = Message(-1488228, -1, null)
    }
}