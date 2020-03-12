package org.chuxian.passpage.module;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.chuxian.passpage.message.LoginRequest;
import org.chuxian.passpage.message.LoginPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.File;

@Controller
public class LoginController {
    @Autowired
    LoginService loginService;
    @Autowired
    DataSource dataSource;
    @Autowired
    Config config;

    Log log = LogFactory.get();

    @RequestMapping(value="/loginPassword", method = RequestMethod.POST)
    @ResponseBody
    public String loginPassword(@RequestBody LoginPasswordRequest loginPasswordRequest) {
        String username = loginPasswordRequest.getUsername();
        String passwordHash = loginPasswordRequest.getPasswordHash();
        String domain = loginPasswordRequest.getDomain();
        return loginService.loginPassword(username, passwordHash, domain);
    }

    @RequestMapping(value="/pageUris/{getUrisRequest}", method = RequestMethod.GET)
    @ResponseBody
    public String[] getMixedPageUris(@PathVariable String getUrisRequest) {
        try {
            String[] requests = getUrisRequest.split("-");
            if (requests.length < 3) return new String[]{"error.html"};
            String username = requests[0];
            String domain = requests[1];
            String loginMode = requests[2];
            return loginService.getMixedPageUris(username, domain, loginMode);
        } catch (Exception e) {
            log.error(e);
            return new String[]{"error.html"};
        }
    }

    @RequestMapping(value="/page/{domain}/{loginMode}/{fileName}", method = RequestMethod.GET)
    @ResponseBody
    public String getPage(@PathVariable String domain, @PathVariable String loginMode, @PathVariable String fileName) {
        try {
            String completePath = config.getPageLibraryPath() + domain + "/" + loginMode + "/" + fileName;
            File file = new File(completePath);
            if (!file.exists()) completePath = config.getPageLibraryPath() + domain + "/displayHead/" + fileName;
            FileReader fileReader = new FileReader(completePath);
            return fileReader.readString();
        } catch (Exception e) {
            log.error(e);
            return "";
        }
    }

    @RequestMapping(value="/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String domain = loginRequest.getDomain();
        String loginMode = loginRequest.getLoginMode();
        String[] chosenPageUris = loginRequest.getChosenPageUris();
        int passwordUsedTime = loginRequest.getPasswordUsedTime();
        int usedTime = loginRequest.getUsedTime();
        return loginService.login(username, domain, loginMode, chosenPageUris, passwordUsedTime, usedTime);
    }

    @RequestMapping(value="/quit/{domain}/{username}", method = RequestMethod.GET)
    @ResponseBody
    public void quit(@PathVariable String domain, @PathVariable String username) {
        loginService.quit(domain, username);
    }

}
