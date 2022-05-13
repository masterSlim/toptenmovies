package com.mrslim.toptenmovies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ToptenmoviesApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ToptenmoviesApplication.class, args);
    }

}
