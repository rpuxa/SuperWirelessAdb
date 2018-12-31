package ru.rpuxa.desktop.view

import ru.rpuxa.desktop.wireless.InternalServerController
import ru.rpuxa.desktop.wireless.InternalServerController.passport
import javax.swing.JOptionPane

class NamePicker : FieldPicker("Change name", "Name") {

    private val deviceName: String get() = passport.name

    init {
        fieldText = deviceName
    }

    override fun onButtonClick() {
        while (true) {
            val res = JOptionPane.showInputDialog(this, "Enter name")

            if (res != null) {
                if (!checkName(res)) {
                    JOptionPane.showMessageDialog(this, "Name must contains only [A-Z, a-z, А-Я, а-я, 0-9, _, -] characters,\n" +
                            " and length must be from 4 to 16")
                    continue
                }
                fieldText = res
                InternalServerController.renameServer(res)
            }

            break
        }
    }

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
}