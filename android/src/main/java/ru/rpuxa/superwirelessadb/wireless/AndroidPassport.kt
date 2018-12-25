package ru.rpuxa.superwirelessadb.wireless

import android.os.Build
import ru.rpuxa.internalserver.wireless.Passport

object AndroidPassport {

    var passport = Passport(
            17825471235,//Random().nextLong()
            Build.MANUFACTURER + Build.PRODUCT
    )

}
