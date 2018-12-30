package ru.rpuxa.superwirelessadb.view

import android.content.Context
import android.os.Build
import ru.rpuxa.internalserver.wireless.Passport
import java.util.*
import kotlin.collections.ArrayList

val Context.dataBase: IDataBase
    get() {
        if (_dataBase == null) {
            val load = StringSerializable.load(DATA_BASE_SAVE_NAME, this) ?: DataBaseImpl()
            _dataBase = load as IDataBase
        }
        return _dataBase!!
    }

interface IDataBase : StringSerializable {

    val passport: Passport

    val myDevices: MutableList<Passport>

    val autoConnectedDevices: MutableSet<Long>

    var runServiceInBackground: Boolean

    fun save(context: Context)
}

private var _dataBase: IDataBase? = null

private class DataBaseImpl : IDataBase {
    override val passport = Passport(
            Random().nextLong(),
            Build.MANUFACTURER + Build.PRODUCT
    )

    override val myDevices = ArrayList<Passport>()

    override val autoConnectedDevices = HashSet<Long>()

    override var runServiceInBackground = true

    override fun save(context: Context) {
        save(context, DATA_BASE_SAVE_NAME)
    }

    companion object {
        private const val serialVersionUID = -5176460647131049324L
    }
}

private const val DATA_BASE_SAVE_NAME = "database"
