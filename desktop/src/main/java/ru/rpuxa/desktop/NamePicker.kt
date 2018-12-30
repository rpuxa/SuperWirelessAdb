package ru.rpuxa.desktop

import ru.rpuxa.internalserver.checkName
import javax.swing.JOptionPane

class NamePicker : FieldPicker("Change name", "Name") {

    private val deviceName: String = "123123"

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
                fieldText = deviceName
                //TODO save
            }

            break
        }
    }
}