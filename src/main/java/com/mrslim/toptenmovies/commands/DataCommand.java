package com.mrslim.toptenmovies.commands;

import com.mrslim.toptenmovies.repositories.ChartRepository;
import com.mrslim.toptenmovies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class DataCommand implements Command {
    @Autowired
    ChartRepository chartRepo;
    @Autowired
    MovieRepository movieRepo;

    @Override
    public boolean execute(String input) throws URISyntaxException, IOException, InterruptedException {
        long chartSize = chartRepo.count();
        long movieSize = movieRepo.count();
        System.out.printf("В базе данных %d записей о рейтингах и %d фильмов\n", chartSize, movieSize);
        return true;
    }
}
