package com.cineman.dao;

import com.cineman.model.entity.Screening9;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScreeningDAO9 {

    /**
     * Module 1: Lấy danh sách suất chiếu của phim (kèm thông tin tên phòng và tên phim)
     */
    public List<Screening9> getByMovieId(int movieId) {
        List<Screening9> list = new ArrayList<>();
        String sql = "SELECT s.*, m.title as movieTitle, r.roomName " +
                     "FROM Screening9 s " +
                     "JOIN Movie9 m ON s.movieId = m.movieId " +
                     "JOIN ScreeningRoom9 r ON s.roomId = r.roomId " +
                     "WHERE s.movieId = ? AND s.screeningStatus != 'CANCELLED' " +
                     "ORDER BY s.startTime ASC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Screening9 screening = new Screening9(
                            rs.getInt("screeningId"),
                            rs.getInt("movieId"),
                            rs.getInt("roomId"),
                            rs.getTimestamp("startTime"),
                            rs.getTimestamp("endTime"),
                            rs.getString("screeningStatus")
                    );
                    screening.setMovieTitle(rs.getString("movieTitle"));
                    screening.setRoomName(rs.getString("roomName"));
                    list.add(screening);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Screening9> getAll() {
        List<Screening9> list = new ArrayList<>();
        String sql = "SELECT s.*, m.title as movieTitle, r.roomName " +
                     "FROM Screening9 s " +
                     "JOIN Movie9 m ON s.movieId = m.movieId " +
                     "JOIN ScreeningRoom9 r ON s.roomId = r.roomId " +
                     "ORDER BY s.startTime DESC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Screening9 screening = new Screening9(
                        rs.getInt("screeningId"),
                        rs.getInt("movieId"),
                        rs.getInt("roomId"),
                        rs.getTimestamp("startTime"),
                        rs.getTimestamp("endTime"),
                        rs.getString("screeningStatus")
                );
                screening.setMovieTitle(rs.getString("movieTitle"));
                screening.setRoomName(rs.getString("roomName"));
                list.add(screening);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Screening9 getById(int screeningId) {
        String sql = "SELECT s.*, m.title as movieTitle, r.roomName " +
                     "FROM Screening9 s " +
                     "JOIN Movie9 m ON s.movieId = m.movieId " +
                     "JOIN ScreeningRoom9 r ON s.roomId = r.roomId " +
                     "WHERE s.screeningId = ?";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, screeningId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Screening9 screening = new Screening9(
                            rs.getInt("screeningId"),
                            rs.getInt("movieId"),
                            rs.getInt("roomId"),
                            rs.getTimestamp("startTime"),
                            rs.getTimestamp("endTime"),
                            rs.getString("screeningStatus")
                    );
                    screening.setMovieTitle(rs.getString("movieTitle"));
                    screening.setRoomName(rs.getString("roomName"));
                    return screening;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Screening9 s) {
        String sql = "INSERT INTO Screening9 (movieId, roomId, startTime, endTime, screeningStatus) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getMovieId());
            ps.setInt(2, s.getRoomId());
            ps.setTimestamp(3, s.getStartTime());
            ps.setTimestamp(4, s.getEndTime());
            ps.setString(5, s.getScreeningStatus() == null ? "ACTIVE" : s.getScreeningStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        s.setScreeningId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int screeningId) {
        String sql = "DELETE FROM Screening9 WHERE screeningId = ?";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, screeningId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
