package org.chuxian.passpage.module;

import cn.hutool.db.Db;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.dialect.Props;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class Config {
    private static final Props appProps = new Props("config/application.properties");

    private static DruidDataSource destDs;

    private static Db destDb;

    private static final String pageLibraryPath = appProps.getStr("pageLibraryPath");

    private static final String resourcesPath = appProps.getStr("resourcesPath");

    public Db createDestDb() {
        String destUrl = appProps.getStr("jdbc.mysql.jdbcUrl");
        String destUsername = appProps.getStr("jdbc.mysql.username");
        String destPassword = appProps.getStr("jdbc.mysql.password");
        destDs = new DruidDataSource();
        destDs.setUrl(destUrl);
        destDs.setUsername(destUsername);
        destDs.setPassword(destPassword);
        destDb = Db.use(destDs);
        return destDb;
    }

    public DruidDataSource getDestDs() {
        return destDs;
    }

    public Db getDestDb() {
        return destDb;
    }

    public Connection getConnection() {
        try {
            return destDs.getConnection();
        } catch (Exception e) {
            LogFactory.get().error(e);
            return null;
        }
    }

    public String getPageLibraryPath() {
        return pageLibraryPath;
    }

    public String getResourcesPath() {
        return resourcesPath;
    }
}
