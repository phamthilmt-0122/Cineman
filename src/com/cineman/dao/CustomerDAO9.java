package com.cineman.dao;

import com.cineman.model.entity.Customer9;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO9 {

    private static java.sql.Date parseDate(String val) {
        if (val == null || val.trim().isEmpty()) return new java.sql.Date(System.currentTimeMillis());
        try {
            if (val.contains(" ")) val = val.substring(0, val.indexOf(" "));
            return java.sql.Date.valueOf(val.trim());
        } catch (Exception e) {
            return new java.sql.Date(System.currentTimeMillis());
        }
    }

    public Customer9 getById(int customerId) {
        String sql = "SELECT c.customerId, c.accountId, c.registeredDate, c.loyaltyPoints, " +
                     "u.username, u.fullName, u.email, u.phone, u.status " +
                     "FROM Customer9 c " +
                     "JOIN UserAccount9 u ON c.accountId = u.accountId " +
                     "WHERE c.customerId = ?";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer9(
                            rs.getInt("accountId"),
                            rs.getString("username"),
                            "",
                            rs.getString("fullName"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("status"),
                            rs.getInt("customerId"),
                            parseDate(rs.getString("registeredDate")),
                            rs.getInt("loyaltyPoints")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Customer9> getAll() {
        List<Customer9> list = new ArrayList<>();
        String sql = "SELECT c.customerId, c.accountId, c.registeredDate, c.loyaltyPoints, " +
                     "u.username, u.fullName, u.email, u.phone, u.status " +
                     "FROM Customer9 c " +
                     "JOIN UserAccount9 u ON c.accountId = u.accountId " +
                     "ORDER BY c.customerId ASC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Customer9(
                        rs.getInt("accountId"),
                        rs.getString("username"),
                        "",
                        rs.getString("fullName"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("status"),
                        rs.getInt("customerId"),
                        parseDate(rs.getString("registeredDate")),
                        rs.getInt("loyaltyPoints")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addLoyaltyPoints(int customerId, int points) {
        String sql = "UPDATE Customer9 SET loyaltyPoints = loyaltyPoints + ? WHERE customerId = ?";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, points);
            ps.setInt(2, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
