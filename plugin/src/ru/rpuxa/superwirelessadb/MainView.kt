package ru.rpuxa.superwirelessadb

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.jetbrains.android.sdk.AndroidSdkUtils
import ru.rpuxa.desktop.view.MainPanel
import ru.rpuxa.desktop.wireless.Adb

class MainView : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        Adb.adbPathGetter = { AndroidSdkUtils.getAdb(project)!!.path }
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(
                MainPanel(),
                "",
                false
        )

        toolWindow.contentManager.addContent(content)
    }
}