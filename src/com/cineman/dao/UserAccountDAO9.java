package com.cineman.dao;

import com.cineman.model.entity.Customer9;
import com.cineman.model.entity.Employee9;
import com.cineman.model.entity.UserAccount9;

import java.sql.*;

public class UserAccountDAO9 {

    public UserAccount9 login(String username, String password) {
        String sql = "SELECT * FROM UserAccount9 WHERE username = ? AND password = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int accountId = rs.getInt("accountId");
                    String fullName = rs.getString("fullName");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String status = rs.getString("status");
                    String role = rs.getString("role");

                    if ("CUSTOMER".equalsIgnoreCase(role)) {
                        return getCustomerProfile(conn, accountId, username, fullName, email, phone, status);
                    } else if ("MANAGER".equalsIgnoreCase(role) || "SALESPERSON".equalsIgnoreCase(role)) {
                        return getEmployeeProfile(conn, accountId, username, fullName, email, phone, status, role);
                    } else {
                        return new UserAccount9(accountId, username, "", fullName, email, phone, status, role);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    private Customer9 getCustomerProfile(Connection conn, int accountId, String username, String fullName, String email, String phone, String status) throws SQLException {
        String sql = "SELECT * FROM Customer9 WHERE accountId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer9(accountId, username, "", fullName, email, phone, status,
                            rs.getInt("customerId"), parseDate(rs.getString("registeredDate")), rs.getInt("loyaltyPoints"));
                }
            }
        }
        return new Customer9(accountId, username, "", fullName, email, phone, status, 0, new Date(System.currentTimeMillis()), 0);
    }

    private Employee9 getEmployeeProfile(Connection conn, int accountId, String username, String fullName, String email, String phone, String status, String role) throws SQLException {
        String sql = "SELECT * FROM Employee9 WHERE accountId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Employee9(accountId, username, "", fullName, email, phone, status, role,
                            rs.getInt("employeeId"), parseDate(rs.getString("hiredDate")), rs.getString("employeeRole"));
                }
            }
        }
        return new Employee9(accountId, username, "", fullName, email, phone, status, role, 0, new Date(System.currentTimeMillis()), role);
    }

    public boolean registerCustomer(String username, String password, String fullName, String email, String phone) {
        String insertAccountSql = "INSERT INTO UserAccount9 (username, password, fullName, email, phone, status, role) VALUES (?, ?, ?, ?, ?, 'ACTIVE', 'CUSTOMER')";
        String insertCustomerSql = "INSERT INTO Customer9 (accountId, registeredDate, loyaltyPoints) VALUES (?, CURRENT_DATE, 0)";

        try (Connection conn = DBConnection9.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psAcc = conn.prepareStatement(insertAccountSql, Statement.RETURN_GENERATED_KEYS)) {
                psAcc.setString(1, username);
                psAcc.setString(2, password);
                psAcc.setString(3, fullName);
                psAcc.setString(4, email);
                psAcc.setString(5, phone);
                psAcc.executeUpdate();

                int accountId = 0;
                try (ResultSet rs = psAcc.getGeneratedKeys()) {
                    if (rs.next()) {
                        accountId = rs.getInt(1);
                    }
                }

                if (accountId > 0) {
                    try (PreparedStatement psCust = conn.prepareStatement(insertCustomerSql)) {
                        psCust.setInt(1, accountId);
                        psCust.executeUpdate();
                    }
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
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
}
