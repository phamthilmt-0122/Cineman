package com.cineman.dao;

import com.cineman.model.dto.CustomerRevenueStatistic9;
import com.cineman.model.entity.Ticket9;
import com.cineman.model.entity.Transaction9;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO9 {

    /**
     * Module 2: Thống kê khách hàng theo doanh thu trong khoảng ngày
     */
    public List<CustomerRevenueStatistic9> sumRevenueByCustomer(Date startDate, Date endDate) {
        List<CustomerRevenueStatistic9> list = new ArrayList<>();
        String sql = "SELECT c.customerId, u.fullName, COUNT(t.transactionId) AS transCount, SUM(t.totalAmount) AS totalRev " +
                     "FROM Customer9 c " +
                     "JOIN UserAccount9 u ON c.accountId = u.accountId " +
                     "JOIN Transaction9 t ON c.customerId = t.customerId " +
                     "WHERE DATE(t.transactionTime) >= ? AND DATE(t.transactionTime) <= ? " +
                     "  AND t.transactionStatus = 'COMPLETED' " +
                     "GROUP BY c.customerId, u.fullName " +
                     "ORDER BY totalRev DESC";

        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startDate.toString());
            ps.setString(2, endDate.toString());
            try (ResultSet rs = ps.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    CustomerRevenueStatistic9 stat = new CustomerRevenueStatistic9(
                            rs.getInt("customerId"),
                            rs.getString("fullName"),
                            rs.getInt("transCount"),
                            rs.getDouble("totalRev")
                    );
                    stat.setRank(rank++);
                    list.add(stat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Module 2: Xem chi tiết các lần giao dịch của khách hàng đã chọn trong khoảng ngày
     */
    public List<Transaction9> getByCustomerAndDate(int customerId, Date startDate, Date endDate) {
        List<Transaction9> list = new ArrayList<>();
        String sql = "SELECT t.*, u.fullName as customerName, " +
                     "       (SELECT COUNT(*) FROM Ticket9 WHERE transactionId = t.transactionId) as ticketCount " +
                     "FROM Transaction9 t " +
                     "JOIN Customer9 c ON t.customerId = c.customerId " +
                     "JOIN UserAccount9 u ON c.accountId = u.accountId " +
                     "WHERE t.customerId = ? " +
                     "  AND DATE(t.transactionTime) >= ? AND DATE(t.transactionTime) <= ? " +
                     "ORDER BY t.transactionTime DESC";

        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, startDate.toString());
            ps.setString(3, endDate.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction9 trans = new Transaction9(
                            rs.getInt("transactionId"),
                            rs.getInt("customerId"),
                            (Integer) rs.getObject("salespersonId"),
                            rs.getTimestamp("transactionTime"),
                            rs.getDouble("totalAmount"),
                            rs.getString("paymentMethod"),
                            rs.getString("transactionStatus")
                    );
                    trans.setCustomerName(rs.getString("customerName"));
                    int ticketCount = rs.getInt("ticketCount");
                    trans.setTicketCount(ticketCount <= 0 ? 1 : ticketCount);
                    list.add(trans);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tạo giao dịch mua vé mới (Áp dụng cho Khách hàng mua online hoặc Nhân viên bán vé tại quầy)
     */
    public boolean createBookingTransaction(Transaction9 trans, List<Ticket9> tickets) {
        String insertTransSql = "INSERT INTO Transaction9 (customerId, salespersonId, transactionTime, totalAmount, paymentMethod, transactionStatus) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";
        String insertTicketSql = "INSERT INTO Ticket9 (transactionId, screeningId, seatId, bookingTime, ticketPrice, ticketStatus) " +
                                 "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection9.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psTrans = conn.prepareStatement(insertTransSql, Statement.RETURN_GENERATED_KEYS)) {
                psTrans.setInt(1, trans.getCustomerId());
                if (trans.getSalespersonId() != null) {
                    psTrans.setInt(2, trans.getSalespersonId());
                } else {
                    psTrans.setNull(2, Types.INTEGER);
                }
                psTrans.setTimestamp(3, trans.getTransactionTime());
                psTrans.setDouble(4, trans.getTotalAmount());
                psTrans.setString(5, trans.getPaymentMethod());
                psTrans.setString(6, trans.getTransactionStatus() == null ? "COMPLETED" : trans.getTransactionStatus());
                psTrans.executeUpdate();

                int transId = 0;
                try (ResultSet rs = psTrans.getGeneratedKeys()) {
                    if (rs.next()) {
                        transId = rs.getInt(1);
                        trans.setTransactionId(transId);
                    }
                }

                if (transId > 0 && tickets != null && !tickets.isEmpty()) {
                    try (PreparedStatement psTicket = conn.prepareStatement(insertTicketSql)) {
                        for (Ticket9 ticket : tickets) {
                            psTicket.setInt(1, transId);
                            psTicket.setInt(2, ticket.getScreeningId());
                            psTicket.setInt(3, ticket.getSeatId());
                            psTicket.setTimestamp(4, ticket.getBookingTime());
                            psTicket.setDouble(5, ticket.getTicketPrice());
                            psTicket.setString(6, "BOOKED");
                            psTicket.addBatch();
                        }
                        psTicket.executeBatch();
                    }

                    // Tích điểm cho khách hàng (1 điểm mỗi 10.000 VNĐ)
                    int points = (int) (trans.getTotalAmount() / 10000);
                    if (points > 0) {
                        try (PreparedStatement psPoints = conn.prepareStatement("UPDATE Customer9 SET loyaltyPoints = loyaltyPoints + ? WHERE customerId = ?")) {
                            psPoints.setInt(1, points);
                            psPoints.setInt(2, trans.getCustomerId());
                            psPoints.executeUpdate();
                        }
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

    public List<Transaction9> getAll() {
        List<Transaction9> list = new ArrayList<>();
        String sql = "SELECT t.*, u.fullName as customerName, " +
                     "       (SELECT COUNT(*) FROM Ticket9 WHERE transactionId = t.transactionId) as ticketCount " +
                     "FROM Transaction9 t " +
                     "JOIN Customer9 c ON t.customerId = c.customerId " +
                     "JOIN UserAccount9 u ON c.accountId = u.accountId " +
                     "ORDER BY t.transactionTime DESC";
        try (Connection conn = DBConnection9.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Transaction9 trans = new Transaction9(
                        rs.getInt("transactionId"),
                        rs.getInt("customerId"),
                        (Integer) rs.getObject("salespersonId"),
                        rs.getTimestamp("transactionTime"),
                        rs.getDouble("totalAmount"),
                        rs.getString("paymentMethod"),
                        rs.getString("transactionStatus")
                );
                trans.setCustomerName(rs.getString("customerName"));
                trans.setTicketCount(rs.getInt("ticketCount"));
                list.add(trans);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
