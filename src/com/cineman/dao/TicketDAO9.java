package com.cineman.dao;

import com.cineman.model.entity.Ticket9;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TicketDAO9 {

    public Set<Integer> getOccupiedSeatIds(int screeningId) {
        Set<Integer> occupied = new HashSet<>();
        String sql = "SELECT seatId FROM Ticket9 WHERE screeningId = ? AND ticketStatus = 'BOOKED'";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, screeningId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    occupied.add(rs.getInt("seatId"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return occupied;
    }

    public List<Ticket9> getTicketsByTransactionId(int transactionId) {
        List<Ticket9> list = new ArrayList<>();
        String sql = "SELECT t.*, s.rowLabel, s.seatNumber, m.title, r.roomName " +
                     "FROM Ticket9 t " +
                     "JOIN Seat9 s ON t.seatId = s.seatId " +
                     "JOIN Screening9 sc ON t.screeningId = sc.screeningId " +
                     "JOIN Movie9 m ON sc.movieId = m.movieId " +
                     "JOIN ScreeningRoom9 r ON sc.roomId = r.roomId " +
                     "WHERE t.transactionId = ? " +
                     "ORDER BY t.ticketId ASC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ticket9 ticket = new Ticket9(
                            rs.getInt("ticketId"),
                            rs.getInt("transactionId"),
                            rs.getInt("screeningId"),
                            rs.getInt("seatId"),
                            rs.getTimestamp("bookingTime"),
                            rs.getDouble("ticketPrice"),
                            rs.getString("ticketStatus")
                    );
                    ticket.setSeatCode(rs.getString("rowLabel") + rs.getInt("seatNumber"));
                    ticket.setMovieTitle(rs.getString("title"));
                    ticket.setRoomName(rs.getString("roomName"));
                    list.add(ticket);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
