package ru.rpuxa.desktop.view

import java.awt.Component
import java.awt.Dimension
import javax.swing.*

abstract class FieldPicker(buttonText: String, private val fieldName: String) : JPanel() {
    private val fieldLabel = JLabel()
    private val fieldButton = JButton(buttonText)

    private var fieldValue = ""

    var fieldText: String
        get() = fieldValue
        set(value) {
            fieldLabel.text = "$fieldName:   $value"
            fieldValue = value
        }

    init {
        fieldText = ""

        layout = BoxLayout(this, BoxLayout.X_AXIS)
        maximumSize = Dimension(1000, 35)

        fieldButton.addActionListener {
            onButtonClick()
        }

        add(fieldLabel)
        add(Box.createHorizontalStrut(50))
        add(fieldButton)
    }

    protected abstract fun onButtonClick()


    final override fun add(p0: Component?): Component {
        return super.add(p0)
    }
}