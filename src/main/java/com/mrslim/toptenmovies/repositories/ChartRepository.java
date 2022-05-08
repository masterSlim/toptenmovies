package com.mrslim.toptenmovies.repositories;

import com.mrslim.toptenmovies.entities.ChartEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

@Repository
public interface ChartRepository extends CrudRepository<ChartEntity, Integer> {
    public boolean existsByHash(int hash);
    public LinkedList<ChartEntity> findByYear(int...years);
}
