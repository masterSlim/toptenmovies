package com.mrslim.toptenmovies.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "movies")
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double rating;
    private String originalName;
    private int year;
    private long votes;
    @Column(unique = true)
    private long hash;


    public MovieEntity() {
    }

    public MovieEntity(double rating, String originalName, int year, long votes) {
        this.rating = rating;
        this.originalName = originalName;
        this.year = year;
        this.votes = votes;
        hash = hashCode();
    }

    public long getHash() {
        return hash;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getVotes() {
        return votes;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        boolean equals = (this.originalName.equals(((MovieEntity) o).originalName))
                && (this.year == ((MovieEntity) o).year);
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalName, year);
    }

    @Override
    public String toString() {
        return String.format("%s %.1f (%d votes)", originalName, rating, votes);
    }
}
