package com.mrslim.toptenmovies.repositories;

import com.mrslim.toptenmovies.entities.ChartEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartRepository extends CrudRepository<ChartEntity, Integer> {
    public boolean existsByHash(int hash);
}
