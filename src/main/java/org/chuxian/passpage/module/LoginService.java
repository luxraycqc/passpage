package org.chuxian.passpage.module;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.chuxian.passpage.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.util.*;

@Service
public class LoginService {
    @Autowired
    Config config;
    @Autowired
    DataSource dataSource;
    @Autowired
    SignupService signupService;

    Log log = LogFactory.get();

    public static HashMap<String, Integer> userLoginPasswordStates = new HashMap<>();

    public String loginPassword(String username, String passwordHash, String domain) {
        try {
            Connection connection = dataSource.getConnection();
            Entity result = SqlUtil.queryOne(connection, "SELECT 1 FROM user_account WHERE username = ? AND password_hash = ? AND domain = ?", username, passwordHash, domain);
            DbUtil.close(connection);
            if (result != null) {
                log.info("domain为"+ domain + ",username为" + username + "密码正确");
                userLoginPasswordStates.put(domain + username, 1);
                return "0";
            } else {
                log.info("domain为"+ domain + ",username为" + username + "密码错误");
                return "2";
            }
        } catch (Exception e) {
            log.error(e);
            return "1";
        }
    }

    public static HashMap<String, UserLoginState> userLoginStates = new HashMap<>();

    public String[] getMixedPageUris(String username, String domain, String loginMode) {
        try {
            if (userLoginPasswordStates.get(domain + username) != 1) return new String[]{"1"};
            TreeSet<String> mixedFileNames = new TreeSet<>();
            UserLoginState userLoginState = userLoginStates.get(domain + username);
            if (userLoginState == null) {
                userLoginStates.put(domain + username, new UserLoginState());
                userLoginState = userLoginStates.get(domain + username);
            } else {
                if (userLoginState.getQuitFlag() == 1) {
                    userLoginState.setQuitFlag(0);
                    mixedFileNames.addAll(userLoginState.realFileNames);
                    mixedFileNames.addAll(userLoginState.decoyFileNames);
                    return mixedFileNames.toArray(new String[0]);
                }
                if (userLoginState.getAvailableChangeCount() < 1) return new String[]{"2"};
                userLoginState.realFileNames.clear();
                userLoginState.decoyFileNames.clear();
            }
            userLoginState.addAvailableChangeCount(-1);
            Connection connection = dataSource.getConnection();
            if (signupService.checkUsername(domain, username) == 1) {
                List<Entity> allRealPageEntities = SqlUtil.query(connection, "SELECT * FROM user_page WHERE username=? AND domain=? and deleted=0 ORDER BY RAND()", username, domain);
                if (allRealPageEntities.size() == 0) {
                    log.warn("没有真实网页！");
                    DbUtil.close(connection);
                    userLoginState.realFileNames.add("default.html");
                    return new String[]{"default.html"};
                }
                int limit = 3;
                if (allRealPageEntities.size() < 6) limit = 2;
                if (allRealPageEntities.size() < 3) limit = 1;
                log.info("username为" + username + "请求网站" + domain + "的网页，给" + limit + "个真的");
                HashSet<String> allRealTitles = new HashSet<>();
                int i = 0;
                Date earliestTime = new Date();
                Date latestTime = new Date(0);
                for (Entity realPageEntity : allRealPageEntities) {
                    allRealTitles.add(realPageEntity.getStr("title"));
                    if (i < limit) {
                        Date addedTime = realPageEntity.getDate("added_time");
                        if (addedTime != null) {
                            if (addedTime.getTime() < earliestTime.getTime()) earliestTime = addedTime;
                            if (addedTime.getTime() > latestTime.getTime()) latestTime = addedTime;
                        }
                        userLoginState.realFileNames.add("page/" + domain + "/" + loginMode + "/" + realPageEntity.getStr("file_name"));
                        i++;
                    }
                }
                if (earliestTime.getTime() > latestTime.getTime()) {
                    earliestTime = new Date(0);
                    latestTime = new Date();
                }
                log.info("real:" + userLoginState.realFileNames);
                int decoyLimit = 8 + allRealTitles.size();
                Entity decoyPageCountEntity = SqlUtil.queryOne(connection, "SELECT count(1) FROM decoy_page WHERE domain = ? and unix_timestamp(added_time) <= ? and unix_timestamp(added_time) >= ?", domain, latestTime.getTime() / 1000 + 86400, earliestTime.getTime() / 1000 - 86400);
                long decoyPageCount = decoyPageCountEntity.getLong("count(1)");
                double randThreshold = 1;
                if (decoyPageCount > 0) randThreshold = (double)decoyLimit / decoyPageCount * 3;
                List<Entity> decoyPageEntities = SqlUtil.query(connection, "SELECT file_name, title FROM decoy_page WHERE domain = ? and rand() < ? and unix_timestamp(added_time) <= ? and unix_timestamp(added_time) >= ? limit " + decoyLimit, domain, randThreshold, latestTime.getTime() / 1000 + 86400, earliestTime.getTime() / 1000 - 86400);
                for (Entity decoyPageEntity : decoyPageEntities) {
                    if (i < 9 && !allRealTitles.contains(decoyPageEntity.getStr("title"))) {
                        userLoginState.decoyFileNames.add("page/" + domain + "/" + loginMode + "/" + decoyPageEntity.getStr("file_name"));
                        i++;
                    }
                }
                log.info("decoy:" + userLoginState.decoyFileNames);
                mixedFileNames.addAll(userLoginState.realFileNames);
                mixedFileNames.addAll(userLoginState.decoyFileNames);
            } else {
                log.info("username为" + username + "请求网站" + domain + "的网页，但是不存在此用户");
                userLoginStates.remove(domain + username);
                return new String[]{"1"};
            }
            if (mixedFileNames.size() > 0) {
                log.info(mixedFileNames.toString());
                DbUtil.close(connection);
                return mixedFileNames.toArray(new String[0]);
            } else {
                log.warn("没有网页！");
                DbUtil.close(connection);
                userLoginState.realFileNames.add("default.html");
                return new String[]{"default.html"};
            }
        } catch (Exception e) {
            log.error(e);
            return new String[]{"error.html"};
        }
    }

    public String login(String username, String domain, String loginMode, String[] chosenPageUris) {
        try {
            log.info("username=" + username + "请求登录，模式为" + loginMode + "，已选页面uri为" + Arrays.asList(chosenPageUris));
            UserLoginState userLoginState = userLoginStates.get(domain + username);
            if (chosenPageUris.length == 0) {
                userLoginState.setScore(0);
                return "2";
            }
            if (userLoginState == null) {
                return "3";
            }
            if (userLoginState.getLoginCount() >= 3) {
                userLoginState.setScore(0);
                log.info("username=" + username + "登录次数超限，已经登录" + userLoginState.loginCount + "次");
                return "4";
            }
            userLoginState.addAvailableChangeCount(1);
            userLoginState.addLoginCount(1);
            HashSet<String> realFileNames = userLoginState.getRealFileNames();
            log.info("正确的页面uri为" + realFileNames);
            HashSet<String> copiedRealFileNames = new HashSet<>(realFileNames);
            for (String chosenPageUri : chosenPageUris) {
                if (!copiedRealFileNames.remove(chosenPageUri)) {
                    userLoginState.addScore(-1);
                    break;
                }
            }
            if (copiedRealFileNames.size() == 0) userLoginState.addScore(1);
//            double precision = 0, recall = 0, fMeasure = 0;
//            precision = (double) chosenImagesCorrectCount / chosenImagesCount;
//            if (realImagesCount > 0) recall = (double) chosenImagesCorrectCount / realImagesCount;
//            if (precision + recall > 0) fMeasure = 2 * precision * recall / (precision + recall);
            if (userLoginState.getScore() >= 1) {
                String sessionId = RandomUtil.randomString(20);
                CommonController.sessionMap.put(domain + username, sessionId);
                log.info("domain=" + domain + ";username=" + username + ";sessionId=" + sessionId);
                userLoginPasswordStates.remove(domain + username);
                return "https://" + domain + "/?PassPageUser=" + username + "-" + sessionId;
            } else {
                userLoginState.setScore(0);
                return "5";
            }
        } catch (Exception e) {
            log.error(e);
            return "1";
        }
    }

    public void quit(String domain, String username) {
        UserLoginState userLoginState = userLoginStates.get(domain + username);
        if (userLoginState != null) {
            log.info("username=" + username + "请求了图片后没有登录便关闭了" + domain + "的登录页面");
            userLoginState.setQuitFlag(1);
        }
    }

    public void deleteOldPages(String username, String domain) {
        try {
            long thresholdTime = System.currentTimeMillis() / 1000 - 864000;
            Connection connection = dataSource.getConnection();
            int count = SqlUtil.execute(connection, "update user_page set deleted = 1 where domain = ? and username = ? and unix_timestamp(added_time) < ?", domain, thresholdTime);
            log.info("删除username=" + username + "的10天前的日志" + count + "个");
            DbUtil.close(connection);
        } catch (Exception e) {
            log.error(e);
        }
    }

}
