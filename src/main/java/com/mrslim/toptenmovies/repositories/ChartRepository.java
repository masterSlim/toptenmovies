package com.mrslim.toptenmovies.repositories;

import com.mrslim.toptenmovies.entities.ChartEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

@Repository
public interface ChartRepository extends CrudRepository<ChartEntity, Integer> {
    public boolean existsByYear(int...years);
    public LinkedList<ChartEntity> findByYear(int...years);
}
