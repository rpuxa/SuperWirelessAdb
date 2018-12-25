package ru.rpuxa.superwirelessadb.wireless

import android.os.Build
import ru.rpuxa.internalserver.wireless.Passport

object AndroidPassport : Passport {

    override var id: Long = 17825471235//Random().nextLong()

    override var name: String = Build.MANUFACTURER + Build.PRODUCT


}
