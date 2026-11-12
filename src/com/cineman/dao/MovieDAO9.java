package com.cineman.dao;

import com.cineman.model.entity.Movie9;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO9 {

    /**
     * Module 1: Tìm kiếm phim theo tên chứa từ khóa (case-insensitive)
     */
    public List<Movie9> searchByTitle(String keyword) {
        List<Movie9> list = new ArrayList<>();
        String sql = "SELECT * FROM Movie9 WHERE LOWER(title) LIKE ? ORDER BY movieId ASC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Movie9(
                            rs.getInt("movieId"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getInt("durationMinutes"),
                            rs.getString("description"),
                            rs.getString("movieStatus")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Movie9 getById(int movieId) {
        String sql = "SELECT * FROM Movie9 WHERE movieId = ?";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Movie9(
                            rs.getInt("movieId"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getInt("durationMinutes"),
                            rs.getString("description"),
                            rs.getString("movieStatus")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Movie9> getAll() {
        List<Movie9> list = new ArrayList<>();
        String sql = "SELECT * FROM Movie9 ORDER BY movieId ASC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Movie9(
                        rs.getInt("movieId"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("durationMinutes"),
                        rs.getString("description"),
                        rs.getString("movieStatus")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Movie9 movie) {
        String sql = "INSERT INTO Movie9 (title, genre, durationMinutes, description, movieStatus) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getGenre());
            ps.setInt(3, movie.getDurationMinutes());
            ps.setString(4, movie.getDescription());
            ps.setString(5, movie.getMovieStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        movie.setMovieId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Movie9 movie) {
        String sql = "UPDATE Movie9 SET title = ?, genre = ?, durationMinutes = ?, description = ?, movieStatus = ? WHERE movieId = ?";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getGenre());
            ps.setInt(3, movie.getDurationMinutes());
            ps.setString(4, movie.getDescription());
            ps.setString(5, movie.getMovieStatus());
            ps.setInt(6, movie.getMovieId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int movieId) {
        String sql = "DELETE FROM Movie9 WHERE movieId = ?";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
