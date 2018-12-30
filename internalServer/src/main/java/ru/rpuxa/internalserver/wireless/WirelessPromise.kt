package ru.rpuxa.internalserver.wireless

import java.util.concurrent.CountDownLatch

interface WirelessPromise<T> {
    fun onAnswer(callback: (T) -> Unit): WirelessPromise<T>

    fun onError(callback: (WirelessErrors) -> Unit): WirelessPromise<T>

    fun getAnswerBlocking(): T? {
        var answer: T? = null
        val lock = CountDownLatch(1)

        onAnswer {
            answer = it
            lock.countDown()
        }

        onError {
            lock.countDown()
        }

        lock.await()

        return answer
    }
}