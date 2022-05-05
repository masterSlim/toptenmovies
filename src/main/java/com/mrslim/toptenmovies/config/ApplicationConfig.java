package com.mrslim.toptenmovies.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ApiConfig.class, ParserConfig.class})
public class ApplicationConfig {
    @Autowired
    private ApiConfig apiConfig;
    @Autowired
    private ParserConfig parserConfig;
}
