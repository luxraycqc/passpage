package org.chuxian.passpage.module;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.chuxian.passpage.message.UploadPageLogRequest;
import org.chuxian.passpage.message.UploadPageUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.File;
import java.util.Date;

@Controller
public class LogController {
    @Autowired
    Config config;
    @Autowired
    DataSource dataSource;
    @Autowired
    LogService logService;
    @Autowired
    HtmlTransformService htmlTransformService;
    @Autowired
    AsyncUploadService asyncUploadService;

    Log log = LogFactory.get();

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
