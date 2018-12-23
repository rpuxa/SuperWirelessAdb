package ru.rpuxa.internalserver.classsync

import kotlin.concurrent.thread

abstract class ClassSyncReceiver(private val time: Int = 5_000) : AutoCloseable {

    fun invokeMethod(name: String, args: Array<out Any?>): Any? {
        var answer: Any? = null
        var completed = false

        val waitingThread = Thread {
            try {
                Thread.sleep(time.toLong())
                if (!completed)
                close()
            } catch (e: InterruptedException) {
            }
        }


        thread {
            answer = invokeMethodImpl(name, args)
            completed = true
            waitingThread.interrupt()
        }


        if (!completed) {
            waitingThread.start()
            waitingThread.join()
            return if (completed) answer else ClassSyncErrors.TIMEOUT

        }

        return answer
    }

    protected abstract fun invokeMethodImpl(name: String, args: Array<out Any?>): Any?
}