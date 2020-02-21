package org.chuxian.passpage.module;

import org.chuxian.passpage.message.GetResetTokenRequest;
import org.chuxian.passpage.message.ResetPasswordRequest;
import org.chuxian.passpage.message.ResetUserPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ResetController {
    @Autowired
    ResetService resetService;

    @RequestMapping(value="/getResetToken", method = RequestMethod.POST)
    @ResponseBody
    public String getResetToken(@RequestBody GetResetTokenRequest getResetTokenRequest) {
        String username = getResetTokenRequest.getUsername();
        String domain = getResetTokenRequest.getDomain();
        return resetService.getResetToken(domain, username);
    }

    @RequestMapping(value="/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public String resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String username = resetPasswordRequest.getUsername();
        String token = resetPasswordRequest.getToken();
        String passwordHash = resetPasswordRequest.getPasswordHash();
        String domain = resetPasswordRequest.getDomain();
        return resetService.resetPassword(username, token, passwordHash, domain);
    }

    @RequestMapping(value="/resetUserPage", method = RequestMethod.POST)
    @ResponseBody
    public String resetUserPage(@RequestBody ResetUserPageRequest resetUserPageRequest) {
        String username = resetUserPageRequest.getUsername();
        String passwordHash = resetUserPageRequest.getPasswordHash();
        String domain = resetUserPageRequest.getDomain();
        return resetService.resetUserPage(username, passwordHash, domain);
    }
}
