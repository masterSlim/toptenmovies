package com.mrslim.toptenmovies;

import com.mrslim.toptenmovies.entities.ChartEntity;
import com.mrslim.toptenmovies.entities.MovieEntity;
import com.mrslim.toptenmovies.repositories.ChartRepository;
import com.mrslim.toptenmovies.repositories.MovieRepository;
import com.mrslim.toptenmovies.services.ParseMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;

@SpringBootApplication
@EnableCaching
public class ToptenmoviesApplication {
    @Autowired
    ParseMovieService pms;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    ChartRepository chartRepository;

    public static void main(String[] args) {
        SpringApplication.run(ToptenmoviesApplication.class, args);
    }

    @PostConstruct
    public void test() {
        try {
            int forDate = 2011;
            LinkedList<MovieEntity> movies = pms.getMovies(forDate);
            for (MovieEntity me :movies) {
                if(!movieRepository.existsByHash(me.getHash())) movieRepository.save(me);
            }
            for (int i = 0; i < movies.size(); i++) {
                ChartEntity chartEntity = new ChartEntity(forDate, i+1, movies.get(i).getHash());
                if(!chartRepository.existsByHash(chartEntity.getHash())) chartRepository.save(chartEntity);
            }
            Iterable<ChartEntity> ce = chartRepository.findAll();

            System.out.println(movieRepository.count());
            System.out.println(chartRepository.count());
            for (Iterator<ChartEntity> it = ce.iterator(); it.hasNext(); ) {
                ChartEntity e = it.next();
                System.out.println(e);
                System.out.println("movie: " + movieRepository.findByHash(e.getMovieHash())+ "\n");
            }
        } catch (URISyntaxException e) {
            System.err.println("Ошибка в ссылке API");
        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка при обращении к серверу API");
            e.printStackTrace();
        }
    }

}
