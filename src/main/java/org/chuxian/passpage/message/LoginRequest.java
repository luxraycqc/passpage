package org.chuxian.passpage.message;

public class LoginRequest {
    String username;

    String domain;

    String loginMode;

    String[] chosenPageUris;

    public String getUsername() {
        return username;
    }

    public void setUsername(String usernameHash) {
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

}
