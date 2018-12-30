package ru.rpuxa.internalserver

import kotlin.concurrent.thread

class Monitoring<T>(
        private val getter: () -> T,
        private val setter: (T) -> Unit,
        private val time: Int = 500,
        private var defaultValue: T? = null
) {
    var isRunning = false

    fun start(): Monitoring<T> {
        isRunning = true
        thread {
            while (isRunning) {
                val get = getter()
                if (defaultValue != get) {
                    defaultValue = get
                    setter(get)
                }
                Thread.sleep(time.toLong())
            }
        }

        return this
    }

    fun stop() {
        isRunning = false
    }
}