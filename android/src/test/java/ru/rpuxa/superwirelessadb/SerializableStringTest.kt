package ru.rpuxa.superwirelessadb

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.rpuxa.superwirelessadb.other.StringSerializable

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SerializableStringTest {

    class OtherClass : StringSerializable {
        var r: Long = 2


        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as OtherClass

            if (r != other.r) return false

            return true
        }

        override fun hashCode(): Int {
            return r.hashCode()
        }


    }

    class MyClass : StringSerializable {
        var i1: Int = 1

        @Transient
        var i2: Int = 2

        @StringSerializable.Name("i3")
        var iafgshdf = 3

        var other = OtherClass()

        companion object {

            var i5 = 4
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MyClass

            if (i1 != other.i1) return false
            if (i2 != other.i2) return false
            if (iafgshdf != other.iafgshdf) return false

            return true
        }

        override fun hashCode(): Int {
            var result = i1
            result = 31 * result + i2
            result = 31 * result + iafgshdf
            return result
        }

        override fun toString(): String {
            return "MyClass(i1=$i1, i2=$i2, iafgshdf=$iafgshdf)"
        }


    }

    @Test
    fun addition_isCorrect() {
        val myClass = MyClass()
        myClass.i1 = 9
        myClass.other = OtherClass().apply { r = 123L }
        val c = myClass.toSerializeString()
        val myClass2 = StringSerializable.fromSerializeString(c)
        assertEquals(myClass, myClass2)
    }
}
