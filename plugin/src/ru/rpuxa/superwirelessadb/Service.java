package ru.rpuxa.superwirelessadb;

import com.intellij.openapi.components.ServiceManager;
import ru.rpuxa.desktop.wireless.InternalServerController;

public interface Service {
    static Service getInstance() {
        if (InternalServerController.INSTANCE.getAutoLoading())
            InternalServerController.INSTANCE.runServer();
        return ServiceManager.getService(Service.class);
    }
}
