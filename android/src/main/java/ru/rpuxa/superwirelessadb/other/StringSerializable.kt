package ru.rpuxa.superwirelessadb.other

import android.content.Context
import android.support.annotation.CallSuper
import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*

interface StringSerializable : Externalizable {

    @CallSuper
    fun save(context: Context, name: String) {
        try {
            javaClass.getConstructor()
        } catch (e: Exception) {
            throw Error("Empty constructor not found!")
        }

        val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val string = toSerializeString()
        println(string.length)
        println(string.hashCode())
        editor.putString(SAVE_FIELD_NAME, string)
        editor.apply()
    }


    @CallSuper
    fun toSerializeString(): String {
        fun Byte.byteToUnsignedInt(): Int {
            var int = toInt()
            if (int < 0) int += 1 shl Byte.SIZE_BITS
            return int
        }

        val byteStream = ByteArrayOutputStream()
        val oos = ObjectOutputStream(byteStream)
        oos.writeObject(this)
        oos.flush()
        val byteArray = byteStream.toByteArray()
        val builder = StringBuilder()
        for (i in byteArray.indices) {
            val char = byteArray[i].byteToUnsignedInt()
            builder.append(char.toChar())
        }

        val string = builder.toString()


        return string
    }

    @CallSuper
    override fun readExternal(input: ObjectInput) {
        val clazz = javaClass
        @Suppress("UNCHECKED_CAST")
        val fields = input.readObject() as Map<String, Any?>
        val classFields = clazz.declaredFields

        for (field in fields) {
            val classField = classFields.find { it.fieldName == field.key } ?: continue
            classField.set(this, field.value)
        }
    }

    @CallSuper
    override fun writeExternal(output: ObjectOutput) {
        val clazz = javaClass
        val classFields = clazz.declaredFields
        val fields = HashMap<String, Any?>(classFields.size)

        for (classField in classFields) {
            val name = classField.fieldName ?: continue
            fields[name] = classField.get(this)
        }

        output.writeObject(fields)
    }

    private val Field.fieldName: String?
        get() {
            if (declaringClass.name != this@StringSerializable.javaClass.name)
                return null
            isAccessible = true
            val modifiers = modifiers
            if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers))
                return null
            val annotation = getAnnotation(Name::class.java)
            return annotation?.name ?: name
        }

    @Target(AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Name(val name: String)

    companion object {
        private const val SAVE_FIELD_NAME = "savefieldname"

        fun fromSerializeString(string: String): StringSerializable {
            println(string.length)
            println(string.hashCode())
            val buf = ArrayList<Byte>()
            for (char in string) {
                val byte = char.toInt()
                if (byte !in 0..255)
                    throw Error("byte !in 0..255")
                buf.add(byte.toByte())

            }
            val byteArray = buf.toByteArray()

            val oos = ObjectInputStream(ByteArrayInputStream(byteArray))
            return oos.readObject() as StringSerializable
        }


        fun load(saveName: String, context: Context): StringSerializable? {
            val preferences = context.getSharedPreferences(saveName, Context.MODE_PRIVATE)
            val string = preferences.getString(SAVE_FIELD_NAME, null) ?: return null
            return fromSerializeString(string)
        }
    }
}