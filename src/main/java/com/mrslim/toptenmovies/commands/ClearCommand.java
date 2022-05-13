package com.mrslim.toptenmovies.commands;

import com.mrslim.toptenmovies.repositories.ChartRepository;
import com.mrslim.toptenmovies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class ClearCommand implements Command {
    @Autowired
    ChartRepository chartRepo;
    @Autowired
    MovieRepository movieRepo;

    @Override
    public boolean execute(String input) throws URISyntaxException, IOException, InterruptedException {
        chartRepo.deleteAll();
        movieRepo.deleteAll();
        System.out.println("Локальные базы данных успешно очищены");
        return true;
    }
}
