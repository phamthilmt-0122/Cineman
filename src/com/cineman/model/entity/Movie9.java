package com.cineman.model.entity;

public class Movie9 {
    private int movieId;
    private String title;
    private String genre;
    private int durationMinutes;
    private String description;
    private String movieStatus; // 'SHOWING', 'COMING_SOON', 'STOPPED'

    public Movie9() {}

    public Movie9(int movieId, String title, String genre, int durationMinutes, String description, String movieStatus) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.description = description;
        this.movieStatus = movieStatus;
    }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMovieStatus() { return movieStatus; }
    public void setMovieStatus(String movieStatus) { this.movieStatus = movieStatus; }
}
