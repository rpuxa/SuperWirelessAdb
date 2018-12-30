package ru.rpuxa.internalserver

fun checkName(name: CharSequence): Boolean {
    if (name.length < 4 || name.length > 16)
        return false
    for (char in name) {
        if (char !in 'a'..'z' && char !in 'A'..'Z' && char !in 'а'..'я' &&
                char !in 'А'..'Я' && char !in '0'..'9' && char != '-' && char != '_')
            return false
    }
    return true
}