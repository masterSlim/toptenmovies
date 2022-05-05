package com.mrslim.toptenmovies.repositories;

import com.mrslim.toptenmovies.entities.MovieEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends CrudRepository<MovieEntity, Long> {
    public MovieEntity findByHash(long hash);

    public boolean existsByHash(long hash);
}
