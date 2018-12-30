package ru.rpuxa.internalserver.wireless

import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput

class Passport() : Externalizable {

    var id: Long = 0

    var name: String = ""

    constructor(id: Long, name: String) : this() {
        this.id = id
        this.name = name
    }

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Passport) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        private const val serialVersionUID = -5723538437214719377L
    }

}