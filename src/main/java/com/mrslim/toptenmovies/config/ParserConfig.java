package com.mrslim.toptenmovies.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("parser")
public class ParserConfig {
    private String testFile;
    private String headersFile;
    private String getChartTemplate;
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

    public String getChartTemplate() {
        return getChartTemplate;
    }

    public void setGetChartTemplate(String getChartTemplate) {
        this.getChartTemplate = getChartTemplate;
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
