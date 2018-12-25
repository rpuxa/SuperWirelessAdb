package ru.rpuxa.desktop.wireless

import ru.rpuxa.internalserver.wireless.Passport

object DesktopPassport : Passport {
    override var id: Long = 172312321312

    override var name: String = System.getProperty("user.name")
}