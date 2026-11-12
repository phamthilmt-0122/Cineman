package com.cineman.controller;

import com.cineman.dao.MovieDAO9;
import com.cineman.model.entity.Movie9;

import java.util.List;

public class MovieManagementController9 {
    private final MovieDAO9 movieDAO;

    public MovieManagementController9() {
        this.movieDAO = new MovieDAO9();
    }

    public List<Movie9> getAllMovies() {
        return movieDAO.getAll();
    }

    public Movie9 getMovieById(int movieId) {
        return movieDAO.getById(movieId);
    }

    public boolean addMovie(String title, String genre, int durationMinutes, String description, String movieStatus) {
        if (title == null || title.trim().isEmpty() || durationMinutes <= 0) {
            return false;
        }
        Movie9 movie = new Movie9(0, title.trim(), genre.trim(), durationMinutes, description.trim(), movieStatus);
        return movieDAO.insert(movie);
    }

    public boolean updateMovie(int movieId, String title, String genre, int durationMinutes, String description, String movieStatus) {
        if (movieId <= 0 || title == null || title.trim().isEmpty() || durationMinutes <= 0) {
            return false;
        }
        Movie9 movie = new Movie9(movieId, title.trim(), genre.trim(), durationMinutes, description.trim(), movieStatus);
        return movieDAO.update(movie);
    }

    public boolean deleteMovie(int movieId) {
        return movieDAO.delete(movieId);
    }
}
