package com.mrslim.toptenmovies.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "charts")
public class ChartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // для реализации хранения топов за дипазон лет поле year представляет собой строку (просто 2021 или 2021-2022)
    private int[] year;
    private int position;
    private long movieHash;
    @Column(unique = true)
    private int hash;

    public ChartEntity() {}

    public ChartEntity(int position, long movieHash, int... year) {
        this.year = year;
        this.position = position;
        this.movieHash = movieHash;
        hash = hashCode();
    }


    public int getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChartEntity that = (ChartEntity) o;

        if (year != that.year) return false;
        if (position != that.position) return false;
        return movieHash == that.movieHash;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, movieHash);
    }

    public long getMovieHash() {
        return movieHash;
    }

    public void setMovieHash(long movieHash) {
        this.movieHash = movieHash;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int[] getYear() {
        return year;
    }

    public void setYear(int...years) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "ChartEntity{" +
                "id=" + id +
                ", year=" + year +
                ", position=" + position +
                ", movieHash=" + movieHash +
                '}';
    }
}
