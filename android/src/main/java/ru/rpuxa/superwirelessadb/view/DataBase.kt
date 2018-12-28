package ru.rpuxa.superwirelessadb.view

import android.content.Context
import android.os.Build
import android.util.ArraySet
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
}

private var _dataBase: IDataBase? = null

private class DataBaseImpl : IDataBase {
    override var passport = Passport(
            Random().nextLong(),
            Build.MANUFACTURER + Build.PRODUCT
    )

    override val myDevices = ArrayList<Passport>()

    override val autoConnectedDevices = HashSet<Long>()

    override fun getSaveName() = DATA_BASE_SAVE_NAME
}

private const val DATA_BASE_SAVE_NAME = "database"
