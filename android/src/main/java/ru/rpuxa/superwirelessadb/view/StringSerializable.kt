package ru.rpuxa.superwirelessadb.view

import android.content.Context
import android.support.annotation.CallSuper
import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.Modifier

interface StringSerializable : Externalizable {

    fun getSaveName(): String

    @CallSuper
    fun save(context: Context) {
        val preferences = context.getSharedPreferences(getSaveName(), Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(SAVE_FIELD_NAME, toSerializeString())
        editor.apply()
    }


    @CallSuper
    fun toSerializeString(): String {
        val byteStream = ByteArrayOutputStream()
        val oos = ObjectOutputStream(byteStream)
        oos.writeObject(this)
        oos.flush()
        val byteArray = byteStream.toByteArray()
        val builder = StringBuilder()
        for (i in byteArray.indices step 2) {
            val char = (byteArray[i].toInt() + 128) or ((byteArray[i + 1].toInt() + 128) shl 8)
            builder.append(char.toChar())
        }

        return builder.toString()
    }

    @CallSuper
    override fun readExternal(input: ObjectInput) {
        val clazz = javaClass
        @Suppress("UNCHECKED_CAST")
        val fields = input.readObject() as Map<String, Any?>
        val classFields = clazz.fields

        for (classField in classFields) {
            val name = classField.fieldName ?: continue
            if (name !in fields) continue
            val field = fields[name]
            classField.set(this, field)
        }
    }

    @CallSuper
    override fun writeExternal(output: ObjectOutput) {
        val clazz = javaClass
        val classFields = clazz.fields
        val fields = HashMap<String, Any?>(classFields.size)

        for (classField in classFields) {
            val name = classField.fieldName ?: continue
            fields[name] = classField.get(this)
        }

        output.writeObject(fields)
    }

    private val Field.fieldName: String?
        get() {
            if (declaringClass.name != javaClass.name)
                return null
            isAccessible = true
            val modifiers = modifiers
            if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers))
                return null
            val annotation = annotations.find { it is Name }
            return if (annotation != null && annotation is Name) {
                annotation.name
            } else {
                name
            }
        }

    annotation class Name(val name: String)

    companion object {
        private const val SAVE_FIELD_NAME = "savefieldname"

        fun fromSerializeString(string: String): StringSerializable {
            val buf = ArrayList<Byte>()
            for (c in string) {
                val firstByte = c.toInt() and 0b1111_1111
                val secondByte = c.toInt() shr 8
                buf.add(firstByte.toByte())
                buf.add(secondByte.toByte())
            }
            val oos = ObjectInputStream(ByteArrayInputStream(buf.toByteArray()))
            return oos.readObject() as StringSerializable
        }

        @CallSuper
        fun load(saveName: String, context: Context): StringSerializable? {
            val preferences = context.getSharedPreferences(saveName, Context.MODE_PRIVATE)
            val string = preferences.getString(SAVE_FIELD_NAME, null) ?: return null
            return fromSerializeString(string)
        }
    }
}