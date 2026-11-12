package dao;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name, u.phone, u.email " +
                     "FROM tbl_customer c " +
                     "LEFT JOIN tbl_user u ON c.user_id = u.id " +
                     "ORDER BY c.id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("CustomerDAO.getAllCustomers error: " + e.getMessage());
        }
        return list;
    }

    public Customer getById(int id) {
        String sql = "SELECT c.*, u.full_name, u.phone, u.email " +
                     "FROM tbl_customer c " +
                     "LEFT JOIN tbl_user u ON c.user_id = u.id " +
                     "WHERE c.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("CustomerDAO.getById error: " + e.getMessage());
        }
        return null;
    }

    public Customer getByUserId(int userId) {
        String sql = "SELECT c.*, u.full_name, u.phone, u.email " +
                     "FROM tbl_customer c " +
                     "LEFT JOIN tbl_user u ON c.user_id = u.id " +
                     "WHERE c.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("CustomerDAO.getByUserId error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Khách hàng: đăng ký thành viên
     */
    public boolean registerCustomer(String fullName, String username, String password, String phone, String email, String address) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Tạo user
            String sqlUser = "INSERT INTO tbl_user (username, password, full_name, role, phone, email) VALUES (?, ?, ?, 'CUSTOMER', ?, ?)";
            PreparedStatement psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, username);
            psUser.setString(2, password);
            psUser.setString(3, fullName);
            psUser.setString(4, phone);
            psUser.setString(5, email);
            psUser.executeUpdate();

            int userId = 0;
            ResultSet rsUser = psUser.getGeneratedKeys();
            if (rsUser.next()) {
                userId = rsUser.getInt(1);
            }

            // 2. Tạo hồ sơ customer và cấp mã thành viên
            String custCode = "KH" + System.currentTimeMillis() % 100000;
            String cardNum = "CARD-STD-" + (int)(Math.random() * 9000 + 1000);
            String sqlCust = "INSERT INTO tbl_customer (user_id, customer_code, member_card_number, card_tier, reward_points, address) " +
                            "VALUES (?, ?, ?, 'STANDARD', 0, ?)";
            PreparedStatement psCust = conn.prepareStatement(sqlCust);
            psCust.setInt(1, userId);
            psCust.setString(2, custCode);
            psCust.setString(3, cardNum);
            psCust.setString(4, address);
            psCust.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("CustomerDAO.registerCustomer error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {}
            }
        }
    }

    /**
     * Nhân viên bán hàng: xuất thẻ thành viên cho khách hàng
     */
    public boolean issueMemberCard(int customerId, String cardTier) {
        String cardPrefix = "CARD-STD-";
        if ("VIP_GOLD".equals(cardTier)) cardPrefix = "CARD-GLD-";
        else if ("VIP_DIAMOND".equals(cardTier)) cardPrefix = "CARD-VIP-";

        String newCardNumber = cardPrefix + (int)(Math.random() * 9000 + 1000);
        String sql = "UPDATE tbl_customer SET member_card_number = ?, card_tier = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newCardNumber);
            ps.setString(2, cardTier);
            ps.setInt(3, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CustomerDAO.issueMemberCard error: " + e.getMessage());
            return false;
        }
    }

    private Customer extractCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setCustomerCode(rs.getString("customer_code"));
        c.setMemberCardNumber(rs.getString("member_card_number"));
        c.setCardTier(rs.getString("card_tier"));
        c.setRewardPoints(rs.getInt("reward_points"));
        c.setAddress(rs.getString("address"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setFullName(rs.getString("full_name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        return c;
    }
}
