package dao;

import model.Movie;
import model.Schedule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    /**
     * MODULE 1 - Khách hàng tìm kiếm thông tin phim
     * Tìm kiếm các phim có tên (title) chứa từ khóa vừa nhập (hoặc tìm theo thể loại/đạo diễn)
     */
    public List<Movie> searchByTitle(String keyword) {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_movie WHERE LOWER(title) LIKE ? OR LOWER(genre) LIKE ? ORDER BY status ASC, release_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractMovieFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("MovieDAO.searchByTitle error: " + e.getMessage());
        }
        return list;
    }

    /**
     * MODULE 1 - Click vào một phim xem chi tiết
     * Hệ thống hiển thị thông tin chi tiết về phim kèm danh sách lịch chiếu hiện tại
     */
    public Movie getMovieDetailWithSchedules(int movieId) {
        Movie movie = null;
        String sqlMovie = "SELECT * FROM tbl_movie WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlMovie)) {

            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    movie = extractMovieFromResultSet(rs);
                }
            }

            if (movie != null) {
                // Lấy danh sách lịch chiếu kèm thông tin phòng chiếu
                String sqlSchedules = "SELECT s.*, r.room_name, r.room_type, m.title as movie_title " +
                                     "FROM tbl_schedule s " +
                                     "JOIN tbl_room r ON s.room_id = r.id " +
                                     "JOIN tbl_movie m ON s.movie_id = m.id " +
                                     "WHERE s.movie_id = ? " +
                                     "ORDER BY s.show_date ASC, s.start_time ASC";

                try (PreparedStatement psSched = conn.prepareStatement(sqlSchedules)) {
                    psSched.setInt(1, movieId);
                    try (ResultSet rsSched = psSched.executeQuery()) {
                        List<Schedule> schedules = new ArrayList<>();
                        while (rsSched.next()) {
                            Schedule sc = new Schedule();
                            sc.setId(rsSched.getInt("id"));
                            sc.setMovieId(rsSched.getInt("movie_id"));
                            sc.setRoomId(rsSched.getInt("room_id"));
                            sc.setShowDate(rsSched.getString("show_date"));
                            sc.setStartTime(rsSched.getString("start_time"));
                            sc.setEndTime(rsSched.getString("end_time"));
                            sc.setPrice(rsSched.getDouble("price"));
                            sc.setMovieTitle(rsSched.getString("movie_title"));
                            sc.setRoomName(rsSched.getString("room_name"));
                            sc.setRoomType(rsSched.getString("room_type"));
                            schedules.add(sc);
                        }
                        movie.setSchedules(schedules);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("MovieDAO.getMovieDetailWithSchedules error: " + e.getMessage());
        }
        return movie;
    }

    public List<Movie> getAllMovies() {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_movie ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(extractMovieFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("MovieDAO.getAllMovies error: " + e.getMessage());
        }
        return list;
    }

    public boolean addMovie(Movie m) {
        String sql = "INSERT INTO tbl_movie (title, genre, duration_minutes, director, actors, release_date, age_rating, language, synopsis, poster_url, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setInt(3, m.getDurationMinutes());
            ps.setString(4, m.getDirector());
            ps.setString(5, m.getActors());
            ps.setString(6, m.getReleaseDate());
            ps.setString(7, m.getAgeRating());
            ps.setString(8, m.getLanguage());
            ps.setString(9, m.getSynopsis());
            ps.setString(10, m.getPosterUrl());
            ps.setString(11, m.getStatus() == null ? "NOW_SHOWING" : m.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("MovieDAO.addMovie error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMovie(Movie m) {
        String sql = "UPDATE tbl_movie SET title = ?, genre = ?, duration_minutes = ?, director = ?, actors = ?, " +
                     "release_date = ?, age_rating = ?, language = ?, synopsis = ?, poster_url = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setInt(3, m.getDurationMinutes());
            ps.setString(4, m.getDirector());
            ps.setString(5, m.getActors());
            ps.setString(6, m.getReleaseDate());
            ps.setString(7, m.getAgeRating());
            ps.setString(8, m.getLanguage());
            ps.setString(9, m.getSynopsis());
            ps.setString(10, m.getPosterUrl());
            ps.setString(11, m.getStatus());
            ps.setInt(12, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("MovieDAO.updateMovie error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMovie(int id) {
        String sql = "DELETE FROM tbl_movie WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("MovieDAO.deleteMovie error: " + e.getMessage());
            return false;
        }
    }

    private Movie extractMovieFromResultSet(ResultSet rs) throws SQLException {
        Movie m = new Movie();
        m.setId(rs.getInt("id"));
        m.setTitle(rs.getString("title"));
        m.setGenre(rs.getString("genre"));
        m.setDurationMinutes(rs.getInt("duration_minutes"));
        m.setDirector(rs.getString("director"));
        m.setActors(rs.getString("actors"));
        m.setReleaseDate(rs.getString("release_date"));
        m.setAgeRating(rs.getString("age_rating"));
        m.setLanguage(rs.getString("language"));
        m.setSynopsis(rs.getString("synopsis"));
        m.setPosterUrl(rs.getString("poster_url"));
        m.setStatus(rs.getString("status"));
        return m;
    }
}
