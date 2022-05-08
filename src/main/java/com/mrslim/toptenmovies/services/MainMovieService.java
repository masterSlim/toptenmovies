package com.mrslim.toptenmovies.services;

import com.mrslim.toptenmovies.config.ApplicationConfig;
import com.mrslim.toptenmovies.entities.ChartEntity;
import com.mrslim.toptenmovies.entities.MovieEntity;
import com.mrslim.toptenmovies.repositories.ChartRepository;
import com.mrslim.toptenmovies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;

@Service
public class MainMovieService implements MovieService {
    @Autowired
    ApplicationConfig appConfig;
    @Autowired
    ChartRepository chartRepo;
    @Autowired
    MovieRepository movieRepo;
    @Autowired
    ParserService parserService;
    @Autowired
    ApiService apiService;
    @Autowired
    ServiceFactory serviceFactory;

    //TODO Наличие в кеше или в базе данных будет проверяться здесь
    @Override
    public LinkedList<MovieEntity> getMovies(int... years) throws URISyntaxException, IOException, InterruptedException {
        LinkedList<MovieEntity> movies = new LinkedList<>();

        if (years.length == 0) return movies;

        movies = getFromCache();
        if (movies != null && !movies.isEmpty()) {
            System.out.println("Взято с кэша:");
            movies.forEach(System.out::println);
            return movies;
        }

        movies = getFromDatabase(years);
        if (movies != null && !movies.isEmpty()) {
            System.out.println("Взято с базы данных:");
            movies.forEach(System.out::println);
            return movies;
        }

        String successMessage = "В базу данных были добавлены следующие фильмы и рейтинг";
        MovieService movieService = serviceFactory.getMovieService();
        movies = movieService.getMovies(years);
        updateMovieRepo(movies);
        updateChartRepo(movies, years);
        System.out.printf(successMessage + " за период %s \n", Arrays.toString(years));
        movies.forEach(System.out::println);
        return movies;
    }

    public LinkedList<MovieEntity> getMovies(String years) throws URISyntaxException, IOException, InterruptedException {
        int[] parsed;
        String[] split = years.split("-");
        parsed = new int[split.length];
        for (int i = 0; i < split.length; i++) parsed[i] = Integer.parseInt(split[i]);
        return getMovies(parsed);
    }

    private LinkedList<MovieEntity> getFromDatabase(int... years) {
        LinkedList<ChartEntity> chartEntities = chartRepo.findByYear(years);
        if (chartEntities.isEmpty()) return null;
        LinkedList<MovieEntity> chart = new LinkedList<>();
        for (ChartEntity ce : chartEntities) {
            chart.add(movieRepo.findByHash(ce.getMovieHash()));
        }
        return chart;
    }

    private LinkedList<MovieEntity> getFromCache() {
        return null;
    }


    private boolean updateMovieRepo(LinkedList<MovieEntity> movies) {
        movieRepo.saveAll(movies);
        return true;
    }

    private boolean updateChartRepo(LinkedList<MovieEntity> movies, int... years) {
        LinkedList<ChartEntity> chart = new LinkedList<>();
        for (MovieEntity movie : movies) {
            ChartEntity chartEntity = new ChartEntity(
                    movies.indexOf(movie),
                    movie.getHash(),
                    years
            );
            chart.add(chartEntity);
        }
        chartRepo.saveAll(chart);
        return true;
    }

}
