package ru.rpuxa.desktop.wireless

import ru.rpuxa.internalserver.wireless.Passport

object DesktopPassport {
    var passport = Passport(
            172312321312,//Random().nextLong()
            System.getProperty("user.name")
    )
}