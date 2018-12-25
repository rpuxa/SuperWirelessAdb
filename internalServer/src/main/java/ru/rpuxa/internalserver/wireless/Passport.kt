package ru.rpuxa.internalserver.wireless

import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput

interface Passport : Externalizable {

    var id: Long

    var name: String

    override fun readExternal(input: ObjectInput) {
        try {
            val array = input.readObject() as Array<*>
            id = array[0] as Long
            name = array[1] as String
        } catch (e: ClassCastException) {
        } catch (e: ArrayIndexOutOfBoundsException) {
        }
    }

    override fun writeExternal(output: ObjectOutput) {
        output.writeObject(arrayOf(
                id,
                name
        ))
    }
}