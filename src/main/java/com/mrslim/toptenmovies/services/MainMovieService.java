package com.mrslim.toptenmovies.services;

import com.mrslim.toptenmovies.entities.ChartEntity;
import com.mrslim.toptenmovies.entities.MovieEntity;
import com.mrslim.toptenmovies.repositories.ChartRepository;
import com.mrslim.toptenmovies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;

@Service
public class MainMovieService implements MovieService {
    @Autowired
    ChartRepository chartRepo;
    @Autowired
    MovieRepository movieRepo;
    @Autowired
    ParserService parser;

    @Override
    @Cacheable(value = "movies", key = "#years")
    public LinkedList<MovieEntity> getMovies(int... years) throws URISyntaxException, IOException, InterruptedException {
        LinkedList<MovieEntity> movies = new LinkedList<>();

        if (years.length == 0) return movies;

        movies = getFromDatabase(years);
        if (!movies.isEmpty()) return movies;

        movies = getFromSite(years);

        return movies;
    }

    public LinkedList<MovieEntity> getFromDatabase(int... years) {
        LinkedList<MovieEntity> movies = new LinkedList<>();
        LinkedList<ChartEntity> chartEntities = chartRepo.findByYear(years);
        if (chartEntities.isEmpty()) return movies;
        for (ChartEntity ce : chartEntities) {
            movies.add(movieRepo.findByOrigNameAndYear(ce.getOrigName(), ce.getMovieYear()));
        }
        return movies;
    }

    public LinkedList<MovieEntity> getFromSite(int... years) throws IOException {
        LinkedList<MovieEntity> movies = new LinkedList<>();
        try {
            movies = parser.getMovies(years);
        }
        catch (RuntimeException e){
            System.err.println(e.getMessage());
            return movies;
        }

        if (movies.isEmpty()) return movies;

        updateMovieRepo(movies);
        updateChartRepo(movies, years);
        if(!movies.isEmpty()) {
            String successMessage = "В базу данных были добавлены следующие фильмы и рейтинг";
            System.out.printf(successMessage + " за период %s \n", Arrays.toString(years));
            movies.forEach(System.out::println);
        }
        return movies;
    }

    // Перегруженный метод в который можно подавать диапазон лет в виде строки. Удобно вызывать из контроллера.
    // Разбирает аргумент на массив int и вызывает основной метод интерфейса.

    @Cacheable(value = "movies", key = "#years")
    public LinkedList<MovieEntity> getMovies(String years) throws
            URISyntaxException, IOException, InterruptedException {
        int[] parsed;
        String[] split = years.split("-");
        parsed = new int[split.length];
        for (int i = 0; i < split.length; i++) parsed[i] = Integer.parseInt(split[i]);
        return getMovies(parsed);
    }


    private boolean updateMovieRepo(LinkedList<MovieEntity> movies) {
        if (movies.isEmpty()) return false;
        movieRepo.saveAll(movies);
        return true;
    }

    private boolean updateChartRepo(LinkedList<MovieEntity> movies, int... years) {
        if (movies.isEmpty()) return false;
        LinkedList<ChartEntity> chart = new LinkedList<>();
        for (MovieEntity movie : movies) {
            ChartEntity chartEntity = new ChartEntity(
                    movies.indexOf(movie),
                    movie.getOrigName(),
                    years,
                    movie.getYear()
            );
            chart.add(chartEntity);
        }
        chartRepo.saveAll(chart);
        return true;
    }

}
