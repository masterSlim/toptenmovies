package com.mrslim.toptenmovies.services;

import com.mrslim.toptenmovies.entities.MovieEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

public interface MovieService {
    LinkedList<MovieEntity> getMovies(int forDate, int amount) throws URISyntaxException, IOException, InterruptedException;
}
