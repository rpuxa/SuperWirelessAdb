package ru.rpuxa.desktop

private var logger: ((String) -> Unit)? = null

fun log(message: String) {
    logger?.invoke(message)
}

fun setLogger(log: (String) -> Unit) {
    logger = log
}