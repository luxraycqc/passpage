package org.chuxian.passpage.module;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.chuxian.passpage.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Date;

@Service
public class SignupService {
    @Autowired
    DataSource dataSource;

    Log log = LogFactory.get();

    public int checkEmail(String domain, String email) {
        try {
            log.info("检测" + domain + "的email=" + email + "是否存在");
            Connection connection = dataSource.getConnection();
            Entity re = SqlUtil.queryOne(connection, "SELECT 1 FROM user_account WHERE email=? and domain=?", email, domain);
            DbUtil.close(connection);
            if (re == null) return 0;
            else return 1;
        } catch (Exception e) {
            log.error(e);
            return 0;
        }
    }

    public int checkUsername(String domain, String username) {
        try {
            log.info("检测" + domain + "的username=" + username + "是否存在");
            Connection connection = dataSource.getConnection();
            Entity re = SqlUtil.queryOne(connection, "SELECT 1 FROM user_account WHERE username=? and domain=?", username, domain);
            DbUtil.close(connection);
            if (re == null) return 0;
            else return 1;
        } catch (Exception e) {
            log.error(e);
            return 0;
        }
    }

    public int signup(String domain, String username, String email, String passwordHash) {
        try {
            log.info("来自" + domain + "新用户注册：" + username + "，邮箱：" + email);
            Connection connection = dataSource.getConnection();
            SqlUtil.execute(connection, "INSERT INTO user_account VALUES(?,?,?,?,?)", username, email, passwordHash, domain, new Date());
            String sessionId = RandomUtil.randomString(20);
            CommonController.sessionMap.put(domain + username, sessionId);
            log.info("domain=" + domain + ";username=" + username + ";sessionId=" + sessionId);
            String url = "https://" + domain + "/?PassPageUser=" + username + "-" + sessionId;
            MailUtil.send(email, domain + "账户注册成功", "请点击此链接以登录：" + url, false);
            DbUtil.close(connection);
            return 0;
        } catch (Exception e) {
            log.error(e);
            return 1;
        }
    }

}
