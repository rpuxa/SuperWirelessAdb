package ru.rpuxa.internalserver.classsync.impl

import ru.rpuxa.internalserver.stream.MessageInputStream
import ru.rpuxa.internalserver.stream.MessageOutputStream

class ClassSyncReceiverStreamInput(
        private val receiver: ClassSyncReceiverLocal,
        output: MessageOutputStream,
        input: MessageInputStream
) : ClassSyncReceiverStream(output, input) {

    init {
        input.onMessage {
            val message = it as Message
            val answer = invokeMethodImpl(message.name, message.args)
            output.sendMessage(answer)
        }
    }

    override fun invokeMethodImpl(name: String, args: Array<out Any?>) =
            receiver.invokeMethod(name, args)
}
