package org.chuxian.passpage.message;

public class UploadPageLogRequest {
    String domain;
    String fileName;
    Integer[] timeArray;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer[] getTimeArray() {
        return timeArray;
    }

    public void setTimeArray(Integer[] timeArray) {
        this.timeArray = timeArray;
    }
}
