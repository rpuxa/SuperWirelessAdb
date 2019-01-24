package ru.rpuxa.desktop

fun setActions(action: IActions) {
    actions = action
}

private lateinit var actions: IActions

interface IActions {

    fun showToast(msg: String)

    fun log(line: String) {
        log(line.split('\n'))
    }

    fun log(lines: Iterable<String>)
}

object Actions : IActions by actions