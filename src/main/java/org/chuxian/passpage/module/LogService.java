package org.chuxian.passpage.module;

import cn.hutool.db.DbUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.chuxian.passpage.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Date;

@Service
public class LogService {
    @Autowired
    DataSource dataSource;

    Log log = LogFactory.get();

    public int uploadHtml(String username, String domain, String fileName, String title, Date addedTime) {
        try {
            Connection connection = dataSource.getConnection();
            SqlUtil.execute(connection, "INSERT INTO user_page(username,domain,file_name,title,added_time,deleted) VALUES(?,?,?,?,?,?)", username, domain, fileName, title, addedTime, 0);
            DbUtil.close(connection);
            log.info("用户" + username + "增加来自" + domain + "的网页：" + title);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int uploadPageLog(String fileName, int totalTime, int scrollingCount, String timeArray) {
        try {
            Connection connection = dataSource.getConnection();
            int result = SqlUtil.execute(connection, "UPDATE user_page SET total_time=?,scrolling_count=?,time_array=? WHERE file_name=?", totalTime, scrollingCount, timeArray, fileName);
            DbUtil.close(connection);
            if (result == 1) return 0;
            else return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
