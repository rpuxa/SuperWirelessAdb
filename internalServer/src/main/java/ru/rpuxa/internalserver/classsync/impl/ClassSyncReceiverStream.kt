package ru.rpuxa.internalserver.classsync.impl

import ru.rpuxa.internalserver.classsync.ClassSyncReceiver
import ru.rpuxa.internalserver.stream.MessageInputStream
import ru.rpuxa.internalserver.stream.MessageOutputStream
import java.io.IOException
import java.io.Serializable

abstract class ClassSyncReceiverStream(
        val output: MessageOutputStream,
        val input: MessageInputStream
) : ClassSyncReceiver() {

    override fun close() {
        try {
            output.close()
        } catch (e: IOException) {
        }

        try {
            input.close()
        } catch (e: IOException) {
        }
    }

    class Message(val name: String, val args: Array<out Any?>) : Serializable
}