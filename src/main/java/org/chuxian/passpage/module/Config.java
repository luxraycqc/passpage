package org.chuxian.passpage.module;

import cn.hutool.setting.dialect.Props;
import org.springframework.stereotype.Service;

@Service
public class Config {
    private static final Props appProps = new Props("config/application.properties");

    private static final String pageLibraryPath = appProps.getStr("pageLibraryPath");

    private static final String resourcesPath = appProps.getStr("resourcesPath");

    public String getPageLibraryPath() {
        return pageLibraryPath;
    }

    public String getResourcesPath() {
        return resourcesPath;
    }
}
