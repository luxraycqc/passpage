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
import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

@Service
public class ResetService {
    @Autowired
    Config config;
    @Autowired
    DataSource dataSource;
    Log log = LogFactory.get();
    HashMap<String, String> userTokens = new HashMap<>();

    public String getResetToken(String domain, String username) {
        try {
            String userToken = userTokens.get(domain + username);
            Connection connection = dataSource.getConnection();
            String email = SqlUtil.queryOne(connection, "SELECT email FROM user_account WHERE username = ? AND domain = ?", username, domain).getStr("email");
            if (userToken == null) {
                String token = RandomUtil.randomString(6);
                long timestamp = System.currentTimeMillis();
                log.info("domain=" + domain + ",username=" + username + "申请重置密码，验证码为" + token);
                userTokens.put(domain + username, token + timestamp);
                MailUtil.send(email, domain + "重置密码验证码", "您的验证码为" + token, false);
            } else {
                long lastTimestamp = Long.parseLong(userToken.substring(6));
                long timeToWait = (60000 - System.currentTimeMillis() + lastTimestamp) / 1000;
                if (timeToWait > 0) {
                    log.info("domain=" + domain + ",username=" + username + "申请重置密码，需要再等" + timeToWait + "秒");
                    return String.valueOf(timeToWait);
                } else {
                    String token = RandomUtil.randomString(6);
                    long timestamp = System.currentTimeMillis();
                    userTokens.put(domain + username, token + timestamp);
                    MailUtil.send(email, domain + "重置密码验证码", "您的验证码为" + token, false);
                    log.info("domain=" + domain + ",username=" + username + "申请重置密码，验证码为" + token);
                }
            }
            DbUtil.close(connection);
            return "0";
        } catch (Exception e) {
            log.error(e);
            return "1";
        }
    }

    public String resetPassword(String username, String token, String passwordHash, String domain) {
        try {
            String userToken = userTokens.get(domain + username);
            if (userToken == null) {
                return "2";
            } else {
                if (token.equals(userToken.substring(0, 6))) {
                    Connection connection = dataSource.getConnection();
                    SqlUtil.execute(connection, "update user_account set password_hash = ? where username = ? and domain = ?", passwordHash, username, domain);
                    DbUtil.close(connection);
                    userTokens.remove(domain + username);
                    return "0";
                } else {
                    return "3";
                }
            }
        } catch (Exception e) {
            log.error(e);
            return "1";
        }
    }

    public String resetUserPage(String username, String passwordHash, String domain) {
        try {
            Connection connection = dataSource.getConnection();
            Entity userAccountEntity = SqlUtil.queryOne(connection, "SELECT email,password_hash FROM user_account WHERE username = ?", username);
            String email = userAccountEntity.getStr("email");
            String correctPasswordHash = userAccountEntity.getStr("password_hash");
            if (passwordHash.equals(correctPasswordHash)) {
//                List<Entity> userPageEntities = SqlUtil.query(connection, "select * from user_page where username = ? and domain = ? and deleted = 0", username, domain);
//                String[] loginModes = new String[]{"/displayRaw", "/displayHead"};
//                for (Entity userPageEntity : userPageEntities) {
//                    String fileName = userPageEntity.getStr("file_name");
//                    for (String loginMode : loginModes) {
//                        File filePath = new File(config.getPageLibraryPath() + domain + loginMode + "/" + fileName);
//                        filePath.delete();
//                    }
//                }
                SqlUtil.execute(connection, "update user_page set deleted = 1 where username = ? and domain = ?", username, domain);
                log.info("删除username=" + username + "的所有浏览日志");
                String sessionId = RandomUtil.randomString(20);
                CommonController.sessionMap.put(domain + username, sessionId);
                log.info("domain=" + domain + ";username=" + username + ";sessionId=" + sessionId);
                String url = "https://" + domain + "/?PassPageUser=" + username + "-" + sessionId;
                MailUtil.send(email, domain + "图形口令重置成功", "请点击此链接以登录：" + url, false);
                DbUtil.close(connection);
                return "0";
            } else {
                return "2";
            }
        } catch (Exception e) {
            log.error(e);
            return "1";
        }
    }

}
