package com.mrslim.toptenmovies.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application-properties")
public class GeneralConfig {
    private Mode mode;
    private int size;


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
