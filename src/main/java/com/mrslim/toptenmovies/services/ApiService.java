package com.mrslim.toptenmovies.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrslim.toptenmovies.config.ApiConfig;
import com.mrslim.toptenmovies.entities.MovieEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedList;

@Service
public class ApiService implements MovieService {
    /*
    Используется неофициальный Api Кинопоиска https://kinopoiskapiunofficial.tech
     */

    @Autowired
    private ApiConfig apiConfig;
    private String api;
    private String getTopMethod;
    private URI getTopURI;
    private CacheManager cacheManager;

    @Override
    @Cacheable()
    public LinkedList<MovieEntity> getMovies(int...years) throws URISyntaxException, IOException, InterruptedException {

        LinkedList<MovieEntity> result = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(requestToApi());
        JsonNode films = rootNode.get("films");
        for (int i = 0; i < films.size(); i++) {
            JsonNode movieNode = films.get(i);
            MovieEntity movie = new MovieEntity(
                    movieNode.get("rating").asDouble(),
                    movieNode.get("nameRu").asText(),
                    movieNode.get("year").asInt(),
                    movieNode.get("ratingVoteCount").asLong()
            );
            result.add(movie);
        }
        System.out.println(result);
        return result;
    }

    private String requestToApi() throws URISyntaxException, IOException, InterruptedException {
        api = apiConfig.getApi();
        getTopMethod = apiConfig.getTopMethod();
        getTopURI = new URI(api + getTopMethod);
        HttpRequest topTen = HttpRequest.newBuilder()
                .uri(getTopURI)
                .GET()
                .header("X-API-KEY", apiConfig.getKey())
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(topTen, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
