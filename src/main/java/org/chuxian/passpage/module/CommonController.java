package org.chuxian.passpage.module;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.chuxian.passpage.message.UploadPageLogRequest;
import org.chuxian.passpage.message.UploadPageUrlRequest;
import org.chuxian.passpage.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class CommonController {
    @Autowired
    Config config;
    @Autowired
    DataSource dataSource;
    @Autowired
    SignupService signupService;
    @Autowired
    LogService logService;
    @Autowired
    DecoyPageService decoyPageService;
    @Autowired
    HtmlTransformService htmlTransformService;
    @Autowired
    AsyncUploadService asyncUploadService;
    @Autowired
    ResetService resetService;

    Log log = LogFactory.get();

    public static HashMap<String, String> sessionMap = new HashMap<>();

    @GetMapping("/")
    public String home() {
        return "/index.html";
    }

    @RequestMapping(value="/checkEmail/{domain}/{email}", method = RequestMethod.GET)
    @ResponseBody
    public int checkEmail(@PathVariable String domain, @PathVariable String email) {
        try {
            return signupService.checkEmail(domain, email);
        } catch (Exception e) {
            log.error(e);
            return 1;
        }
    }

    @RequestMapping(value="/checkUsername/{domain}/{username}", method = RequestMethod.GET)
    @ResponseBody
    public int checkUsername(@PathVariable String domain, @PathVariable String username) {
        try {
            return signupService.checkUsername(domain, username);
        } catch (Exception e) {
            log.error(e);
            return 1;
        }
    }

    @RequestMapping(value="/checkUserSession/{domain}/{session}", method = RequestMethod.GET)
    @ResponseBody
    public int checkUserSession(@PathVariable String domain, @PathVariable String session) {
        try {
            log.info("检测来自domain:" + domain + "的session:" + session);
            String username = session.split("-")[0];
            String sessionId = session.split("-")[1];
            if (username == null || sessionId == null) return 1;
            if (sessionMap.get(domain + username) == null) {
                log.warn(username + "没有登录");
                return 1;
            }
            if (sessionMap.get(domain + username).equals(sessionId)) {
                return 0;
            } else {
                log.warn(username + "的sessionId应该是" + sessionMap.get(domain + username));
                return 1;
            }
        } catch (Exception e) {
            log.error(e);
            return 1;
        }
    }

    @RequestMapping(value="/uploadPageUrl", method = RequestMethod.POST)
    @ResponseBody
    public String uploadPageUrl(@RequestBody UploadPageUrlRequest uploadPageUrlRequest) {
        log.info("收到上传页面URL的信号");
        try {
            String domain = uploadPageUrlRequest.getDomain();
            String username = uploadPageUrlRequest.getUsername();
            String url = uploadPageUrlRequest.getUrl();
            String html = HttpRequest.get(url)
                    .cookie(DecoyPageService.cookie)
                    .setReadTimeout(10000)
                    .setConnectionTimeout(10000)
                    .setMaxRedirectCount(2)
                    .execute().body();
            String title = ReUtil.get("<h1.*?>(.*?)</h1>", html, 1);
            if (title != null) {
                title = title.replaceAll("<.*?>|\\s{2,}", "");
                if (title.length() > 0) {
                    log.info(title);
                    String fileName = IdUtil.simpleUUID() + ".html";
                    String[] loginModes;
                    if ("www.sohu.com".equals(domain)) loginModes = new String[]{"/displayRaw", "/displayHead"};
                    else if ("www.zhihu.com".equals(domain)) loginModes = new String[]{"/displayRaw", "/displayHead"};
                    else loginModes = new String[]{"/displayRaw"};
                    for (String loginMode : loginModes) {
                        File file = new File(config.getPageLibraryPath() + domain + loginMode, fileName);
                        FileWriter writer = new FileWriter(file);
                        if ("/displayRaw".equals(loginMode)) {
                            writer.write(html);
                        } else if ("/displayHead".equals(loginMode)) {
                            writer.write(htmlTransformService.displayHead(html));
                        }
                    }
                    logService.uploadHtml(username, domain, fileName, title, new Date());
                    AsyncUploadService.tempTitleMap.put(fileName, title);
                    return fileName;
                } else {
                    return "1";
                }
            } else {
                return "2";
            }
        } catch (Exception e) {
            log.error(e);
            return "3";
        }
    }

    @RequestMapping(value="/uploadPageLog", method = RequestMethod.POST)
    @ResponseBody
    public String uploadPageLog(@RequestBody UploadPageLogRequest uploadPageLogRequest) {
        log.info("收到上传浏览日志的信号");
        asyncUploadService.uploadPageLogAsync(uploadPageLogRequest);
        return "OK";
    }

}
