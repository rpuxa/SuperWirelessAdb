package ru.rpuxa.internalserver.classsync.impl

import ru.rpuxa.internalserver.stream.MessageInputStream
import ru.rpuxa.internalserver.stream.MessageOutputStream

class ClassSyncReceiverStreamOutput(
        output: MessageOutputStream,
        input: MessageInputStream
) : ClassSyncReceiverStream(output, input) {

    override fun invokeMethodImpl(name: String, args: Array<out Any?>): Any {
        val msg = Message(name, args)
        var answer: Any? = null

        input.onMessage { answer = it }

        output.sendMessage(msg)

        while (answer == null)
            Thread.sleep(1)

        return answer!!
    }
}