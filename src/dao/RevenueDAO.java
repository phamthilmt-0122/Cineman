package dao;

import model.CustomerRevenueDTO;
import model.TransactionDetailDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RevenueDAO {

    /**
     * MODULE 2 - Nhân viên thống kê khách hàng theo doanh thu
     * Lọc theo ngày bắt đầu (startDate) và ngày kết thúc (endDate)
     * Trả về danh sách khách hàng xếp hạng theo tổng tiền chi tiêu (doanh thu)
     */
    public List<CustomerRevenueDTO> getCustomerRevenueReport(String startDate, String endDate) {
        List<CustomerRevenueDTO> list = new ArrayList<>();

        // Chuẩn hóa ngày
        String startFilter = (startDate != null && !startDate.trim().isEmpty()) ? startDate.trim() + " 00:00:00" : "1970-01-01 00:00:00";
        String endFilter = (endDate != null && !endDate.trim().isEmpty()) ? endDate.trim() + " 23:59:59" : "2099-12-31 23:59:59";

        String sql = "SELECT c.id as customer_id, c.customer_code, c.member_card_number, c.card_tier, " +
                     "       u.full_name, u.phone, u.email, " +
                     "       COUNT(DISTINCT i.id) as total_orders, " +
                     "       COALESCE(SUM(i.final_amount), 0) as total_revenue, " +
                     "       COUNT(t.id) as total_tickets " +
                     "FROM tbl_customer c " +
                     "JOIN tbl_user u ON c.user_id = u.id " +
                     "LEFT JOIN tbl_invoice i ON c.id = i.customer_id " +
                     "     AND i.created_at >= ? AND i.created_at <= ? AND i.payment_status = 'PAID' " +
                     "LEFT JOIN tbl_ticket t ON i.id = t.invoice_id AND t.ticket_status != 'CANCELLED' " +
                     "GROUP BY c.id, c.customer_code, c.member_card_number, c.card_tier, u.full_name, u.phone, u.email " +
                     "ORDER BY total_revenue DESC, total_orders DESC";

        double grandTotal = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, startFilter);
            ps.setString(2, endFilter);

            try (ResultSet rs = ps.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    CustomerRevenueDTO dto = new CustomerRevenueDTO();
                    dto.setCustomerId(rs.getInt("customer_id"));
                    dto.setCustomerCode(rs.getString("customer_code"));
                    dto.setMemberCardNumber(rs.getString("member_card_number"));
                    dto.setCardTier(rs.getString("card_tier"));
                    dto.setFullName(rs.getString("full_name"));
                    dto.setPhone(rs.getString("phone"));
                    dto.setEmail(rs.getString("email"));
                    dto.setTotalOrders(rs.getInt("total_orders"));
                    double rev = rs.getDouble("total_revenue");
                    dto.setTotalRevenue(rev);
                    dto.setTotalTickets(rs.getInt("total_tickets"));
                    dto.setRank(rank++);

                    grandTotal += rev;
                    list.add(dto);
                }
            }

            // Tính tỷ lệ % đóng góp doanh thu
            for (CustomerRevenueDTO dto : list) {
                if (grandTotal > 0) {
                    double pct = (dto.getTotalRevenue() / grandTotal) * 100.0;
                    dto.setRevenuePercentage(Math.round(pct * 10.0) / 10.0);
                } else {
                    dto.setRevenuePercentage(0.0);
                }
            }

        } catch (SQLException e) {
            System.err.println("RevenueDAO.getCustomerRevenueReport error: " + e.getMessage());
        }

        return list;
    }

    /**
     * MODULE 2 - Click vào một khách hàng xem chi tiết
     * Hệ thống hiển thị chi tiết tất cả các lần giao dịch của khách hàng đã chọn
     */
    public List<TransactionDetailDTO> getCustomerTransactions(int customerId, String startDate, String endDate) {
        List<TransactionDetailDTO> transactions = new ArrayList<>();

        String startFilter = (startDate != null && !startDate.trim().isEmpty()) ? startDate.trim() + " 00:00:00" : "1970-01-01 00:00:00";
        String endFilter = (endDate != null && !endDate.trim().isEmpty()) ? endDate.trim() + " 23:59:59" : "2099-12-31 23:59:59";

        String sqlInvoice = "SELECT i.*, uCashier.full_name as cashier_name " +
                           "FROM tbl_invoice i " +
                           "LEFT JOIN tbl_user uCashier ON i.cashier_id = uCashier.id " +
                           "WHERE i.customer_id = ? " +
                           "  AND i.created_at >= ? AND i.created_at <= ? " +
                           "ORDER BY i.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInvoice)) {

            ps.setInt(1, customerId);
            ps.setString(2, startFilter);
            ps.setString(3, endFilter);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int invoiceId = rs.getInt("id");
                    TransactionDetailDTO trans = new TransactionDetailDTO();
                    trans.setInvoiceId(invoiceId);
                    trans.setInvoiceCode(rs.getString("invoice_code"));
                    trans.setCreatedAt(rs.getString("created_at"));
                    trans.setBookingType(rs.getString("booking_type"));
                    trans.setCashierName(rs.getString("cashier_name") != null ? rs.getString("cashier_name") : "Hệ thống Trực tuyến (Online)");
                    trans.setTotalAmount(rs.getDouble("total_amount"));
                    trans.setDiscountAmount(rs.getDouble("discount_amount"));
                    trans.setFinalAmount(rs.getDouble("final_amount"));
                    trans.setPaymentMethod(rs.getString("payment_method"));
                    trans.setPaymentStatus(rs.getString("payment_status"));

                    // Lấy chi tiết vé, phim, phòng của hóa đơn này
                    String sqlTickets = "SELECT t.seat_code, t.ticket_price, " +
                                       "       s.show_date, s.start_time, " +
                                       "       m.title as movie_title, " +
                                       "       r.room_name, r.room_type " +
                                       "FROM tbl_ticket t " +
                                       "JOIN tbl_schedule s ON t.schedule_id = s.id " +
                                       "JOIN tbl_movie m ON s.movie_id = m.id " +
                                       "JOIN tbl_room r ON s.room_id = r.id " +
                                       "WHERE t.invoice_id = ?";

                    try (PreparedStatement psTick = conn.prepareStatement(sqlTickets)) {
                        psTick.setInt(1, invoiceId);
                        try (ResultSet rsTick = psTick.executeQuery()) {
                            List<String> seats = new ArrayList<>();
                            String movieTitle = "";
                            String roomName = "";
                            String roomType = "";
                            String showDate = "";
                            String startTime = "";

                            while (rsTick.next()) {
                                seats.add(rsTick.getString("seat_code"));
                                movieTitle = rsTick.getString("movie_title");
                                roomName = rsTick.getString("room_name");
                                roomType = rsTick.getString("room_type");
                                showDate = rsTick.getString("show_date");
                                startTime = rsTick.getString("start_time");
                            }
                            trans.setSeatCodes(seats);
                            trans.setTicketCount(seats.size());
                            trans.setMovieTitle(movieTitle);
                            trans.setRoomName(roomName);
                            trans.setRoomType(roomType);
                            trans.setShowDate(showDate);
                            trans.setStartTime(startTime);
                        }
                    }

                    transactions.add(trans);
                }
            }

        } catch (SQLException e) {
            System.err.println("RevenueDAO.getCustomerTransactions error: " + e.getMessage());
        }

        return transactions;
    }

    /**
     * Thống kê tổng hợp toàn rạp
     */
    public double getTotalCinemaRevenue() {
        String sql = "SELECT COALESCE(SUM(final_amount), 0) FROM tbl_invoice WHERE payment_status = 'PAID'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("RevenueDAO.getTotalCinemaRevenue error: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalTicketsSold() {
        String sql = "SELECT COUNT(*) FROM tbl_ticket WHERE ticket_status != 'CANCELLED'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("RevenueDAO.getTotalTicketsSold error: " + e.getMessage());
        }
        return 0;
    }
}
