package com.cineman;

import com.cineman.boundary.CineManWebServer9;
import com.cineman.controller.RevenueReportController9;
import com.cineman.controller.SearchMovieController9;
import com.cineman.dao.DBConnection9;
import com.cineman.model.dto.CustomerRevenueStatistic9;
import com.cineman.model.dto.ReportFilter9;
import com.cineman.model.dto.SearchCriteria9;
import com.cineman.model.entity.Movie9;
import com.cineman.model.entity.Screening9;
import com.cineman.model.entity.Transaction9;

import java.io.File;
import java.sql.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   HỆ THỐNG QUẢN LÝ RẠP CHIẾU PHIM CINEMAN (ĐỀ SỐ 17 - HẬU TỐ 9)");
        System.out.println("==================================================================");

        // 1. Khởi tạo cơ sở dữ liệu
        DBConnection9.ensureInitialized();

        // 2. Chạy kiểm thử tự động xác thực Module 1 & Module 2
        runVerificationTests();

        // 3. Khởi chạy Web Server phục vụ giao diện người dùng
        int port = 8080;
        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (Exception ignored) {}

        try {
            CineManWebServer9 webServer = new CineManWebServer9(port);
            webServer.start();
        } catch (Exception e) {
            System.err.println("Không thể khởi động Web Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runVerificationTests() {
        System.out.println("\n--- [KIỂM THỬ XÁC THỰC MODULE 1: TÌM THÔNG TIN PHIM] ---");
        SearchMovieController9 searchCtrl = new SearchMovieController9();
        
        // Test 1: Tìm với từ khóa 'avengers'
        SearchCriteria9 crit1 = new SearchCriteria9("avengers");
        List<Movie9> results1 = searchCtrl.searchMovies(crit1);
        System.out.println("• Tìm kiếm từ khóa 'avengers': Tìm thấy " + results1.size() + " bộ phim:");
        for (Movie9 m : results1) {
            System.out.println("  -> ID: " + m.getMovieId() + " | Tên: " + m.getTitle() + " | Thể loại: " + m.getGenre() + " | Thời lượng: " + m.getDurationMinutes() + "p | Trạng thái: " + m.getMovieStatus());
        }

        // Test 2: Xem chi tiết phim ID = 101 và lịch chiếu
        Movie9 detail = searchCtrl.getMovieDetail(101);
        List<Screening9> screenings = searchCtrl.getScreenings(101);
        System.out.println("• Chi tiết phim ID 101: " + detail.getTitle() + " - Lịch chiếu: " + screenings.size() + " suất.");
        for (Screening9 s : screenings) {
            System.out.println("  -> Suất S" + s.getScreeningId() + " tại " + s.getRoomName() + " (" + s.getStartTime() + " -> " + s.getEndTime() + ")");
        }

        System.out.println("\n--- [KIỂM THỬ XÁC THỰC MODULE 2: THỐNG KÊ DOANH THU KHÁCH HÀNG] ---");
        RevenueReportController9 reportCtrl = new RevenueReportController9();
        ReportFilter9 filter = new ReportFilter9("2026-10-01", "2026-10-15");
        List<CustomerRevenueStatistic9> stats = reportCtrl.getCustomerRevenue(filter);
        System.out.println("• Thống kê từ 01/10/2026 đến 15/10/2026: Có " + stats.size() + " khách hàng phát sinh doanh thu:");
        for (CustomerRevenueStatistic9 stat : stats) {
            System.out.println("  -> Hạng " + stat.getRank() + " | " + stat.getCustomerCode() + " - " + stat.getFullName() + " | Số GD: " + stat.getTransactionCount() + " | Tổng: " + stat.getFormattedRevenue());
        }

        // Test chi tiết giao dịch khách hàng KH001 (Customer ID = 1)
        List<Transaction9> transList = reportCtrl.getCustomerTransactions(1, filter);
        System.out.println("• Chi tiết giao dịch của khách hàng KH001 (Nguyễn Văn A): " + transList.size() + " giao dịch:");
        for (Transaction9 t : transList) {
            System.out.println("  -> GD" + t.getTransactionId() + " lúc " + t.getTransactionTime() + " | " + t.getTicketCount() + " vé | " + t.getPaymentMethod() + " | Số tiền: " + (long)t.getTotalAmount() + " VNĐ");
        }
        System.out.println("------------------------------------------------------------------\n");
    }
}
