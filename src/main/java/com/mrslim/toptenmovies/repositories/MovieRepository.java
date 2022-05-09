package com.mrslim.toptenmovies.repositories;

import com.mrslim.toptenmovies.entities.MovieEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends CrudRepository<MovieEntity, Long> {
    public MovieEntity findByOrigNameAndYear(String origName, int... year);
}
