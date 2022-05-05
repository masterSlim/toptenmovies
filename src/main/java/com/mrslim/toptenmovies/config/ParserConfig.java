package com.mrslim.toptenmovies.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("parser")
public class ParserConfig {
    private boolean online;
    private String testFile;
    private String headersFile;
    private String getTopTemplate;
    private String titleCssClass;
    private String ratingCssClass;
    private String votesCssClass;
    private String secondaryTextCssClass;

    public String getRatingCssClass() {
        return ratingCssClass;
    }

    public void setRatingCssClass(String ratingCssClass) {
        this.ratingCssClass = ratingCssClass;
    }

    public String getVotesCssClass() {
        return votesCssClass;
    }

    public void setVotesCssClass(String votesCssClass) {
        this.votesCssClass = votesCssClass;
    }

    public String getSecondaryTextCssClass() {
        return secondaryTextCssClass;
    }

    public void setSecondaryTextCssClass(String secondaryTextCssClass) {
        this.secondaryTextCssClass = secondaryTextCssClass;
    }

    public String getTestFile() {
        return testFile;
    }

    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getGetTopTemplate() {
        return getTopTemplate;
    }

    public void setGetTopTemplate(String getTopTemplate) {
        this.getTopTemplate = getTopTemplate;
    }

    public String getTitleCssClass() {
        return titleCssClass;
    }

    public void setTitleCssClass(String titleCssClass) {
        this.titleCssClass = titleCssClass;
    }

    public String getHeadersFile() {
        return headersFile;
    }

    public void setHeadersFile(String headersFile) {
        this.headersFile = headersFile;
    }

}
