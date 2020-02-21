package org.chuxian.passpage.module;

import java.util.HashSet;

public class UserLoginState {
    HashSet<String> realFileNames;
    HashSet<String> decoyFileNames;
    int availableChangeCount;
    int loginCount;
    int quitFlag;
    double score;

    public UserLoginState() {
        realFileNames = new HashSet<>();
        decoyFileNames = new HashSet<>();
        availableChangeCount = 1;
        loginCount = 0;
        quitFlag = 0;
        score = 0;
    }

    public HashSet<String> getRealFileNames() {
        return realFileNames;
    }

    public void setRealFileNames(HashSet<String> realFileNames) {
        this.realFileNames = realFileNames;
    }

    public HashSet<String> getDecoyFileNames() {
        return decoyFileNames;
    }

    public void setDecoyFileNames(HashSet<String> decoyFileNames) {
        this.decoyFileNames = decoyFileNames;
    }

    public int getAvailableChangeCount() {
        return availableChangeCount;
    }

    public void setAvailableChangeCount(int availableChangeCount) {
        this.availableChangeCount = availableChangeCount;
    }

    public void addAvailableChangeCount(int availableChangeCount) {
        this.availableChangeCount += availableChangeCount;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public void addLoginCount(int loginCount) {
        this.loginCount += loginCount;
    }

    public int getQuitFlag() {
        return quitFlag;
    }

    public void setQuitFlag(int quitFlag) {
        this.quitFlag = quitFlag;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void addScore(double score) {
        this.score += score;
    }
}