package ru.rpuxa.superwirelessadb

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.jetbrains.android.sdk.AndroidSdkUtils
import ru.rpuxa.desktop.Actions
import ru.rpuxa.desktop.setActions
import ru.rpuxa.desktop.view.MainPanel
import ru.rpuxa.desktop.wireless.Adb
import ru.rpuxa.desktop.wireless.InternalServerController

class MainView : ToolWindowFactory {

    init {
        setActions(PluginActions)
        Actions.log("===============Android Studio started===============")
        if (InternalServerController.autoLoading)
            InternalServerController.runServer()
    }

    override fun shouldBeAvailable(project: Project): Boolean {
        Adb.adbPathGetter = { AndroidSdkUtils.getAdb(project)!!.parent }
        return true
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(
                MainPanel(),
                "",
                false
        )

        toolWindow.contentManager.addContent(content)
    }
}