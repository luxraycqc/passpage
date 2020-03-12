package org.chuxian.passpage.message;

public class LoginRequest {
    String username;

    String domain;

    String loginMode;

    String[] chosenPageUris;

    int passwordUsedTime;

    int usedTime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }

    public String[] getChosenPageUris() {
        return chosenPageUris;
    }

    public void setChosenPageUris(String[] chosenPageUris) {
        this.chosenPageUris = chosenPageUris;
    }

    public int getPasswordUsedTime() {
        return passwordUsedTime;
    }

    public void setPasswordUsedTime(int passwordUsedTime) {
        this.passwordUsedTime = passwordUsedTime;
    }

    public int getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(int usedTime) {
        this.usedTime = usedTime;
    }
}
