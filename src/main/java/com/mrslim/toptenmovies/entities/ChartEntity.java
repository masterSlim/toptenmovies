package com.mrslim.toptenmovies.entities;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "charts")
public class ChartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int[] year;
    private int position;
    private String origName;
    private int[] movieYear;

    public ChartEntity() {}

    public ChartEntity(int position, String origName, int[] year, int[] movieYear) {
        this.position = position;
        this.origName=origName;
        this.year = year;
        this.movieYear = movieYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChartEntity that = (ChartEntity) o;

        if (year != that.year) return false;
        if (position != that.position) return false;
        return hashCode() == that.hashCode();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getOrigName() {
        return origName;
    }

    public void setOrigName(String origName) {
        this.origName = origName;
    }

    public int[] getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(int[] movieYear) {
        this.movieYear = movieYear;
    }

    public int[] getYear() {
        return year;
    }

    public void setYear(int[] years) {
        this.year = years;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, origName, movieYear);
    }

    @Override
    public String toString() {
        return String.format("Чарт за %s: %d место %s (%s)", Arrays.toString(year), position, origName, Arrays.toString(movieYear));
    }
}
