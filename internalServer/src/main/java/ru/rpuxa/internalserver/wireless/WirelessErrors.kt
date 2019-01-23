package ru.rpuxa.internalserver.wireless

const val UNKNOWN_ERROR = -1
const val ADB_NOT_FOUND = -2

enum class WirelessErrors {
    TIMEOUT,
    ADB_NOT_FOUND
}