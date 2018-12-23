package ru.rpuxa.internalserver.classsync.impl

import ru.rpuxa.internalserver.classsync.ClassSyncErrors
import ru.rpuxa.internalserver.classsync.ClassSyncInterface
import ru.rpuxa.internalserver.classsync.ClassSyncReceiver

open class ClassSyncReceiverLocal(val inter: ClassSyncInterface) : ClassSyncReceiver() {
    override fun invokeMethodImpl(name: String, args: Array<out Any?>): Any {
        try {
            val method = inter.javaClass.methods.find { it.name == name }
                    ?: return ClassSyncErrors.ILLEGAL_METHOD_NAME
            return method.invoke(inter, *args)
        } catch (e: IllegalArgumentException) {
            return ClassSyncErrors.ILLEGAL_ARGUMENTS
        }
    }

    override fun close() {
    }
}