package com.khattech.twinsterchallenge.models;

import java.util.ArrayList;

/**
 * Created by Mohamed Achref on 4/29/20.
 */
public class Movie {

    private String id;
    private String title;
    private String poster_path;
    private String overview;
    private ArrayList<String> genre_ids;
    private ArrayList<Genre> genres;
    private String vote_average;
    private String release_date;
    private boolean isFavourite;

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", poster_path='" + poster_path + '\'' +
                ", overview='" + overview + '\'' +
                ", genre_ids=" + genre_ids +
                ", genres=" + genres +
                ", vote_average='" + vote_average + '\'' +
                ", release_date='" + release_date + '\'' +
                ", isFavourite=" + isFavourite +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public ArrayList<String> getGenre_ids() {
        return genre_ids;
    }

    public String getVote_average() {
        return vote_average;
    }

    public boolean isFavourite() {
        return isFavourite;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setGenre_ids(ArrayList<String> genre_ids) {
        this.genre_ids = genre_ids;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}
