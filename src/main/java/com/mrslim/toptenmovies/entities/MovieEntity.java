package com.mrslim.toptenmovies.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "movies")
public class MovieEntity {
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double rating;
    private String origName;
    private int year;
    @Column(unique = true)
    @Id
    private long hash;
    private long votes;


    public MovieEntity() {
    }

    public MovieEntity(double rating, String origName, int year, long votes) {
        this.rating = rating;
        this.origName = origName;
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

    public String getOrigName() {
        return origName;
    }

    public void setOrigName(String origName) {
        this.origName = origName;
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
        boolean equals = (this.origName.equals(((MovieEntity) o).origName))
                && (this.year == ((MovieEntity) o).year);
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(origName, year);
    }

    @Override
    public String toString() {
        return String.format("%s %.1f (%d votes)", origName, rating, votes);
    }
}
