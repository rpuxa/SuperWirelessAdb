package ru.rpuxa.internalserver.classsync

import java.util.*
import kotlin.concurrent.thread

class ClassSyncSender(private val receiver: ClassSyncReceiver) : AutoCloseable {

    private val requests = ArrayDeque<Request<*>>()
    private val lock = Object()

    private var thread = thread {
        try {
            while (true) {
                if (requests.isEmpty())
                    synchronized(lock) {
                        lock.wait()
                    }
                if (Thread.currentThread().isInterrupted)
                    return@thread
                val request = requests.pollFirst()
                val result = receiver.invokeMethod(request.name, request.args)
                if (result is ClassSyncErrors)
                    request.promise.error(result)
                else {
                    try {
                        request.promise.complete(result)
                    } catch (e: ClassCastException) {
                        request.promise.error(ClassSyncErrors.ILLEGAL_RETURN_TYPE)
                    }
                }
            }
        } catch (e: InterruptedException) {
        }
    }


    fun <Answer> invokeMethod(name: String, vararg args: Any?): Promise<Answer> {
        val promise = PromiseImplementation<Answer>()
        addRequest(Request(promise, name, args))
        return promise
    }

    fun <Answer> getValue(name: String): Promise<Answer> {
        val getterName = "get" + name[0].toUpperCase() + name.substring(1)
        return invokeMethod(getterName)
    }

    fun <T> setValue(name: String, value: T): Promise<Unit> {
        val setterName = "set" + name[0].toUpperCase() + name.substring(1)
        return invokeMethod(setterName, value)
    }

    private fun <Answer> addRequest(request: Request<Answer>) {
        requests.addLast(request)
        try {
            synchronized(lock) {
                lock.notify()
            }
        } catch (e: Exception) {
        }
    }

    private class PromiseImplementation<Answer> : Promise<Answer> {
        private var onCompleteCallback: ((Answer) -> Unit)? = null
        private var onErrorCallback: ((ClassSyncErrors) -> Unit)? = null

        override fun onComplete(callback: (Answer) -> Unit): Promise<Answer> {
            onCompleteCallback = callback
            return this
        }

        override fun onError(callback: (ClassSyncErrors) -> Unit): Promise<Answer> {
            onErrorCallback = callback
            return this
        }

        fun complete(answer: Any?) {
            @Suppress("UNCHECKED_CAST")
            onCompleteCallback?.invoke(answer as Answer)
        }

        fun error(error: ClassSyncErrors) {
            onErrorCallback?.invoke(error)
        }
    }

    override fun close() {
        receiver.close()
        thread.interrupt()
        try {
            lock.notify()
        } catch (e: Exception) {
        }
    }

    private class Request<Answer>(val promise: PromiseImplementation<Answer>, val name: String, val args: Array<out Any?>)
}