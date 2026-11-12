package com.cineman.dao;

import com.cineman.model.entity.MembershipCard9;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembershipCardDAO9 {

    public boolean issueCard(int customerId, String cardCode, int validityMonths) {
        String sql = "INSERT INTO MembershipCard9 (customerId, cardCode, issuedDate, expiryDate, cardStatus) " +
                     "VALUES (?, ?, CURRENT_DATE, DATE('now', '+' || ? || ' month'), 'ACTIVE')";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, cardCode);
            ps.setInt(3, validityMonths);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Date parseDate(String val) {
        if (val == null || val.trim().isEmpty()) return new Date(System.currentTimeMillis());
        try {
            if (val.contains(" ")) val = val.substring(0, val.indexOf(" "));
            return Date.valueOf(val.trim());
        } catch (Exception e) {
            return new Date(System.currentTimeMillis());
        }
    }

    public List<MembershipCard9> getCardsByCustomerId(int customerId) {
        List<MembershipCard9> list = new ArrayList<>();
        String sql = "SELECT * FROM MembershipCard9 WHERE customerId = ? ORDER BY cardId DESC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new MembershipCard9(
                            rs.getInt("cardId"),
                            rs.getInt("customerId"),
                            rs.getString("cardCode"),
                            parseDate(rs.getString("issuedDate")),
                            parseDate(rs.getString("expiryDate")),
                            rs.getString("cardStatus")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MembershipCard9> getAllCards() {
        List<MembershipCard9> list = new ArrayList<>();
        String sql = "SELECT * FROM MembershipCard9 ORDER BY cardId DESC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new MembershipCard9(
                        rs.getInt("cardId"),
                        rs.getInt("customerId"),
                        rs.getString("cardCode"),
                        parseDate(rs.getString("issuedDate")),
                        parseDate(rs.getString("expiryDate")),
                        rs.getString("cardStatus")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
