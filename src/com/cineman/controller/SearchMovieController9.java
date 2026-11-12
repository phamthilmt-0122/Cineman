package com.cineman.controller;

import com.cineman.dao.MovieDAO9;
import com.cineman.dao.ScreeningDAO9;
import com.cineman.model.dto.SearchCriteria9;
import com.cineman.model.entity.Movie9;
import com.cineman.model.entity.Screening9;
import com.cineman.service.DataProtectionService9;

import java.util.ArrayList;
import java.util.List;

public class SearchMovieController9 {
    private final MovieDAO9 movieDAO;
    private final ScreeningDAO9 screeningDAO;

    public SearchMovieController9() {
        this.movieDAO = new MovieDAO9();
        this.screeningDAO = new ScreeningDAO9();
    }

    public SearchMovieController9(MovieDAO9 movieDAO, ScreeningDAO9 screeningDAO) {
        this.movieDAO = movieDAO;
        this.screeningDAO = screeningDAO;
    }

    /**
     * Module 1: Tìm kiếm phim theo từ khóa
     * Kiểm tra BR2 (từ khóa rỗng), làm sạch từ khóa qua DataProtectionService9 và gọi MovieDAO9
     */
    public List<Movie9> searchMovies(SearchCriteria9 criteria) {
        if (criteria == null || !criteria.isValid()) {
            return new ArrayList<>();
        }
        String safeKeyword = DataProtectionService9.sanitizeInput(criteria.normalize());
        return movieDAO.searchByTitle(safeKeyword);
    }

    /**
     * Module 1: Xem chi tiết phim
     */
    public Movie9 getMovieDetail(int movieId) {
        return movieDAO.getById(movieId);
    }

    /**
     * Module 1: Lấy danh sách lịch chiếu của phim
     */
    public List<Screening9> getScreenings(int movieId) {
        return screeningDAO.getByMovieId(movieId);
    }
}
