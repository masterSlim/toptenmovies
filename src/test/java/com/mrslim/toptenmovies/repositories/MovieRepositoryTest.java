package com.mrslim.toptenmovies.repositories;

import com.mrslim.toptenmovies.entities.MovieEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    public void whenCalledSave_thenCorrectNumberOfMovies() {
        movieRepository.save(new MovieEntity(666, "Original name", 1992, 3123));
        List<MovieEntity> movieEntities = (List<MovieEntity>) movieRepository.findAll();

        assertEquals(movieEntities.size(), 1);
    }
}
