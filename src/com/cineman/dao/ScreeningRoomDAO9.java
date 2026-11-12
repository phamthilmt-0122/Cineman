package com.cineman.dao;

import com.cineman.model.entity.ScreeningRoom9;
import com.cineman.model.entity.Seat9;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScreeningRoomDAO9 {

    public List<ScreeningRoom9> getAllRooms() {
        List<ScreeningRoom9> list = new ArrayList<>();
        String sql = "SELECT * FROM ScreeningRoom9 ORDER BY roomId ASC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ScreeningRoom9(
                        rs.getInt("roomId"),
                        rs.getInt("cinemaId"),
                        rs.getString("roomName"),
                        rs.getInt("capacity"),
                        rs.getString("roomStatus")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ScreeningRoom9 getRoomById(int roomId) {
        String sql = "SELECT * FROM ScreeningRoom9 WHERE roomId = ?";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ScreeningRoom9(
                            rs.getInt("roomId"),
                            rs.getInt("cinemaId"),
                            rs.getString("roomName"),
                            rs.getInt("capacity"),
                            rs.getString("roomStatus")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Seat9> getSeatsByRoomId(int roomId) {
        List<Seat9> list = new ArrayList<>();
        String sql = "SELECT * FROM Seat9 WHERE roomId = ? ORDER BY rowLabel ASC, seatNumber ASC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Seat9(
                            rs.getInt("seatId"),
                            rs.getInt("roomId"),
                            rs.getString("rowLabel"),
                            rs.getInt("seatNumber"),
                            rs.getString("seatType"),
                            rs.getString("seatStatus")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertRoom(ScreeningRoom9 room) {
        String sql = "INSERT INTO ScreeningRoom9 (cinemaId, roomName, capacity, roomStatus) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection9.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, room.getCinemaId() <= 0 ? 1 : room.getCinemaId());
                ps.setString(2, room.getRoomName());
                ps.setInt(3, room.getCapacity());
                ps.setString(4, room.getRoomStatus() == null ? "AVAILABLE" : room.getRoomStatus());
                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            int newRoomId = rs.getInt(1);
                            room.setRoomId(newRoomId);

                            // Tạo ghế tự động cho phòng chiếu (ví dụ 4 hàng A, B, C, D)
                            String seatSql = "INSERT INTO Seat9 (roomId, rowLabel, seatNumber, seatType, seatStatus) VALUES (?, ?, ?, ?, 'AVAILABLE')";
                            try (PreparedStatement seatPs = conn.prepareStatement(seatSql)) {
                                char[] rows = {'A', 'B', 'C', 'D'};
                                int seatsPerRow = Math.max(5, room.getCapacity() / 4);
                                for (char row : rows) {
                                    for (int s = 1; s <= seatsPerRow; s++) {
                                        seatPs.setInt(1, newRoomId);
                                        seatPs.setString(2, String.valueOf(row));
                                        seatPs.setInt(3, s);
                                        seatPs.setString(4, (row == 'C' || row == 'D') ? "VIP" : "STANDARD");
                                        seatPs.addBatch();
                                    }
                                }
                                seatPs.executeBatch();
                            }
                        }
                    }
                    conn.commit();
                    return true;
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRoom(ScreeningRoom9 room) {
        String sql = "UPDATE ScreeningRoom9 SET roomName = ?, capacity = ?, roomStatus = ? WHERE roomId = ?";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getRoomStatus());
            ps.setInt(4, room.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM ScreeningRoom9 WHERE roomId = ?";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
