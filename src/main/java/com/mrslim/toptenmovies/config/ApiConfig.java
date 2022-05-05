package com.mrslim.toptenmovies.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kinopoisk-api")
public class ApiConfig {
    private String key;
    private String api;
    private String getTopMethod;

    public String getTopMethod() {
        return getTopMethod;
    }

    public void setGetTopMethod(String getTopMethod) {
        this.getTopMethod = getTopMethod;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}


