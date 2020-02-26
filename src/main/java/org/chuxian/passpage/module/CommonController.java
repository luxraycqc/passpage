package org.chuxian.passpage.module;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.HashMap;

@Controller
public class CommonController {
    @Autowired
    Config config;
    @Autowired
    DataSource dataSource;
    @Autowired
    SignupService signupService;

    Log log = LogFactory.get();

    public static HashMap<String, String> sessionMap = new HashMap<>();

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

}
