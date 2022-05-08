package com.mrslim.toptenmovies.services;

import com.mrslim.toptenmovies.config.ApplicationConfig;
import com.mrslim.toptenmovies.config.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class ServiceFactory {
    @Autowired
    ApplicationConfig appConfig;
    @Autowired
    ParserService parserService;
    @Autowired
    ApiService apiService;

    @Bean("MovieService")
    public MovieService getMovieService() {
        if (appConfig.getMode() == Mode.PARSER) return parserService;
        if (appConfig.getMode() == Mode.API) return apiService;
        return parserService;
    }
}
