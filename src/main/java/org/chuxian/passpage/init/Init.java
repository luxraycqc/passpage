package org.chuxian.passpage.init;

import cn.hutool.core.util.ZipUtil;
import org.chuxian.passpage.module.Config;
import org.chuxian.passpage.module.DecoyPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class Init implements ApplicationRunner {
    @Autowired
    Config config;
    @Autowired
    DecoyPageService decoyPageService;
    @Override
    public void run(ApplicationArguments args) {
        decoyPageService.createDirectories("www.sohu.com", new String[]{"/displayRaw", "/displayHead"});
        decoyPageService.createDirectories("www.zhihu.com", new String[]{"/displayRaw", "/displayHead"});
        ZipUtil.zip(config.getResourcesPath() + "extension", config.getResourcesPath() + "static/extension.zip", true);
    }
}
