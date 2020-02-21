package org.chuxian.passpage.module;

import org.chuxian.passpage.message.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class SignupController {
    @Autowired
    SignupService signupService;

    @RequestMapping(value="/signup", method = RequestMethod.POST)
    @ResponseBody
    public String signup(@RequestBody SignupRequest signupRequest) {
        String email = signupRequest.getEmail();
        String username = signupRequest.getUsername();
        String passwordHash = signupRequest.getPasswordHash();
        String domain = signupRequest.getDomain();
        if (signupService.checkEmail(domain, email) == 0) {
            if (signupService.checkUsername(domain, username) == 0) {
                if (signupService.signup(domain, username, email, passwordHash) == 0) {
                    return "0";
                } else {
                    return "3";
                }
            } else {
                return "2";
            }
        } else {
            return "1";
        }
    }
}
