package ru.rpuxa.internalserver.classsync


interface Promise<Answer> {

    fun onComplete(callback: (Answer) -> Unit): Promise<Answer>

    fun onError(callback: (ClassSyncErrors) -> Unit): Promise<Answer>
}
