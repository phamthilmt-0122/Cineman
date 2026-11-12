package com.cineman.boundary;

import com.cineman.controller.*;
import com.cineman.model.dto.CustomerRevenueStatistic9;
import com.cineman.model.dto.ReportFilter9;
import com.cineman.model.dto.SearchCriteria9;
import com.cineman.model.entity.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CineManWebServer9 {
    private final int port;
    private HttpServer server;

    private final LoginController9 loginController = new LoginController9();
    private final SearchMovieController9 searchMovieController = new SearchMovieController9();
    private final RevenueReportController9 revenueReportController = new RevenueReportController9();
    private final MovieManagementController9 movieController = new MovieManagementController9();
    private final RoomManagementController9 roomController = new RoomManagementController9();
    private final ScreeningController9 screeningController = new ScreeningController9();
    private final TicketBookingController9 bookingController = new TicketBookingController9();
    private final MembershipController9 membershipController = new MembershipController9();

    // In-memory sessions
    private final Map<String, UserAccount9> sessions = new HashMap<>();

    public CineManWebServer9(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new MainRouterHandler());
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
        System.out.println("==================================================================");
        System.out.println(" [CineMan System] Máy chủ Web CineMan9 đã khởi chạy thành công!");
        System.out.println(" Truy cập tại: http://localhost:" + port);
        System.out.println(" Tài khoản demo:");
        System.out.println("  1. Quản lý (Manager):     manager / 123456");
        System.out.println("  2. Bán hàng (Salesperson): sales   / 123456");
        System.out.println("  3. Khách hàng (Customer): customer / 123456");
        System.out.println("==================================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private class MainRouterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQuery(query);

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                params.putAll(parseQuery(body));
            }

            UserAccount9 currentUser = getSessionUser(exchange);

            try {
                if ("/login".equals(path)) {
                    handleLogin(exchange, params);
                } else if ("/logout".equals(path)) {
                    handleLogout(exchange);
                } else if ("/customer/search".equals(path)) {
                    renderSearchMoviePage(exchange, currentUser, params);
                } else if ("/customer/movie-detail".equals(path)) {
                    renderMovieDetailPage(exchange, currentUser, params);
                } else if ("/customer/booking".equals(path)) {
                    renderCustomerBookingPage(exchange, currentUser, params);
                } else if ("/customer/register".equals(path)) {
                    renderRegisterPage(exchange, params);
                } else if ("/manager/reports".equals(path)) {
                    renderReportMenuPage(exchange, currentUser);
                } else if ("/manager/customer-revenue".equals(path)) {
                    renderCustomerRevenueReportPage(exchange, currentUser, params);
                } else if ("/manager/customer-transactions".equals(path)) {
                    renderCustomerTransactionsDetailPage(exchange, currentUser, params);
                } else if ("/manager/movies".equals(path)) {
                    renderMovieManagementPage(exchange, currentUser, params);
                } else if ("/manager/rooms".equals(path)) {
                    renderRoomManagementPage(exchange, currentUser, params);
                } else if ("/manager/screenings".equals(path)) {
                    renderScreeningManagementPage(exchange, currentUser, params);
                } else if ("/sales/pos".equals(path)) {
                    renderSalesPosPage(exchange, currentUser, params);
                } else if ("/sales/issue-card".equals(path)) {
                    renderIssueCardPage(exchange, currentUser, params);
                } else {
                    // Default redirect
                    if (currentUser == null) {
                        redirect(exchange, "/login");
                    } else if ("MANAGER".equalsIgnoreCase(currentUser.getRole())) {
                        redirect(exchange, "/manager/reports");
                    } else if ("SALESPERSON".equalsIgnoreCase(currentUser.getRole())) {
                        redirect(exchange, "/sales/pos");
                    } else {
                        redirect(exchange, "/customer/search");
                    }
                }
            } catch (com.cineman.service.UnauthorizedAccessException9 secEx) {
                sendHtml(exchange, 403, buildLayout(currentUser, "", "<div class='alert alert-danger'><b>403 - Truy cập bị chặn bởi lớp bảo vệ dữ liệu:</b><br>" + escapeHtml(secEx.getMessage()) + "</div>"));
            } catch (Exception e) {
                e.printStackTrace();
                sendHtml(exchange, 500, "<h2>Đã xảy ra lỗi hệ thống: " + e.getMessage() + "</h2>");
            }
        }
    }

    // -------------------------------------------------------------
    // Authentication handlers
    // -------------------------------------------------------------
    private void handleLogin(HttpExchange exchange, Map<String, String> params) throws IOException {
        String error = "";
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String u = params.get("username");
            String p = params.get("password");
            UserAccount9 user = loginController.authenticate(u, p);
            if (user != null) {
                String token = UUID.randomUUID().toString();
                sessions.put(token, user);
                exchange.getResponseHeaders().add("Set-Cookie", "cineman_session=" + token + "; Path=/; HttpOnly");

                if ("MANAGER".equalsIgnoreCase(user.getRole())) {
                    redirect(exchange, "/manager/reports");
                } else if ("SALESPERSON".equalsIgnoreCase(user.getRole())) {
                    redirect(exchange, "/sales/pos");
                } else {
                    redirect(exchange, "/customer/search");
                }
                return;
            } else {
                error = "Tên đăng nhập hoặc mật khẩu không chính xác!";
            }
        }

        String html = "<!DOCTYPE html><html lang='vi'><head><meta charset='UTF-8'>" +
                "<title>CineMan - Đăng nhập hệ thống</title>" +
                getCommonCss() +
                "</head><body style='display:flex; justify-content:center; align-items:center; min-height:100vh; background:#f0f2f5;'>" +
                "<div style='background:#fff; padding:35px 40px; border-radius:10px; box-shadow:0 6px 18px rgba(0,0,0,0.1); width:420px;'>" +
                "<div style='text-align:center; margin-bottom:25px;'>" +
                "<h1 style='color:#1a2b49; margin:0; font-size:28px;'>CineMan</h1>" +
                "<p style='color:#666; margin:6px 0 0;'>Hệ thống Quản lý Rạp chiếu phim (Đề 17)</p>" +
                "</div>" +
                (error.isEmpty() ? "" : "<div class='alert alert-danger'>" + error + "</div>") +
                "<form method='POST' action='/login'>" +
                "<div class='form-group'><label>Tên đăng nhập:</label><input type='text' name='username' class='form-control' placeholder='Nhập username...' required value='manager'></div>" +
                "<div class='form-group'><label>Mật khẩu:</label><input type='password' name='password' class='form-control' placeholder='Nhập mật khẩu...' required value='123456'></div>" +
                "<button type='submit' class='btn btn-primary' style='width:100%; padding:12px; font-size:16px; margin-top:10px;'>Đăng nhập</button>" +
                "</form>" +
                "<div style='margin-top:20px; font-size:13px; color:#555; background:#eef4fb; padding:12px; border-radius:6px;'>" +
                "<b>Gợi ý tài khoản demo:</b><br>" +
                "• Quản lý: <code>manager</code> / <code>123456</code><br>" +
                "• Bán hàng: <code>sales</code> / <code>123456</code><br>" +
                "• Khách hàng: <code>customer</code> / <code>123456</code>" +
                "</div>" +
                "<div style='text-align:center; margin-top:15px;'><a href='/customer/register' style='color:#1a73e8; text-decoration:none;'>Chưa có tài khoản? Đăng kí thành viên</a></div>" +
                "</div></body></html>";
        sendHtml(exchange, 200, html);
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        String token = getCookie(exchange, "cineman_session");
        if (token != null) {
            sessions.remove(token);
        }
        exchange.getResponseHeaders().add("Set-Cookie", "cineman_session=; Path=/; Max-Age=0");
        redirect(exchange, "/login");
    }

    // -------------------------------------------------------------
    // MODULE 1: Khách hàng tìm kiếm thông tin phim
    // Khớp Hình 3 & Hình 4 trong đề bài
    // -------------------------------------------------------------
    private void renderSearchMoviePage(HttpExchange exchange, UserAccount9 user, Map<String, String> params) throws IOException {
        if (user == null) { redirect(exchange, "/login"); return; }

        String keyword = params.getOrDefault("keyword", "");
        List<Movie9> movies = new ArrayList<>();
        String message = "";

        if (params.containsKey("keyword")) {
            SearchCriteria9 criteria = new SearchCriteria9(keyword);
            if (!criteria.isValid()) {
                message = "<div class='alert alert-warning'>Vui lòng nhập tên phim trước khi tìm kiếm!</div>";
            } else {
                movies = searchMovieController.searchMovies(criteria);
                if (movies.isEmpty()) {
                    message = "<div class='alert alert-warning'>Không tìm thấy phim phù hợp với từ khóa: <b>" + escapeHtml(keyword) + "</b></div>";
                }
            }
        } else {
            // Mặc định nạp toàn bộ phim để khách hàng dễ tham khảo
            movies = movieController.getAllMovies();
        }

        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (Movie9 m : movies) {
            String statusBadge = "SHOWING".equalsIgnoreCase(m.getMovieStatus()) ? "Đang chiếu" : ("COMING_SOON".equalsIgnoreCase(m.getMovieStatus()) ? "Sắp chiếu" : "Ngừng chiếu");
            rows.append("<tr onclick=\"window.location='/customer/movie-detail?id=").append(m.getMovieId()).append("'\" style='cursor:pointer;'>")
                    .append("<td>").append(stt++).append("</td>")
                    .append("<td><b style='color:#1a73e8;'>").append(escapeHtml(m.getTitle())).append("</b></td>")
                    .append("<td>").append(escapeHtml(m.getGenre())).append("</td>")
                    .append("<td>").append(m.getDurationMinutes()).append(" phút</td>")
                    .append("<td>").append(statusBadge).append("</td>")
                    .append("</tr>");
        }

        String content = "<h2>TÌM THÔNG TIN PHIM</h2>" +
                "<form method='GET' action='/customer/search' style='display:flex; gap:10px; margin-bottom:20px;'>" +
                "<input type='text' name='keyword' class='form-control' style='flex:1;' placeholder='Nhập tên phim...' value='" + escapeHtml(keyword) + "'>" +
                "<button type='submit' class='btn btn-primary' style='padding:0 25px;'>Tìm kiếm</button>" +
                "</form>" +
                message +
                (keyword.trim().isEmpty() ? "" : "<p style='color:#666;'>Kết quả tìm kiếm cho từ khóa: <b>\"" + escapeHtml(keyword) + "\"</b></p>") +
                "<table class='data-table'>" +
                "<thead><tr><th style='width:60px;'>STT</th><th>Tên phim</th><th>Thể loại</th><th>Thời lượng</th><th>Trạng thái</th></tr></thead>" +
                "<tbody>" + rows + "</tbody>" +
                "</table>" +
                "<p style='color:#777; font-size:13px; margin-top:10px;'>* Nhấn vào một dòng để xem chi tiết phim.</p>";

        sendHtml(exchange, 200, buildLayout(user, "customer-search", content));
    }

    private void renderMovieDetailPage(HttpExchange exchange, UserAccount9 user, Map<String, String> params) throws IOException {
        if (user == null) { redirect(exchange, "/login"); return; }

        int movieId = Integer.parseInt(params.getOrDefault("id", "0"));
        Movie9 movie = searchMovieController.getMovieDetail(movieId);
        if (movie == null) {
            redirect(exchange, "/customer/search");
            return;
        }

        List<Screening9> screenings = searchMovieController.getScreenings(movieId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        StringBuilder screeningRows = new StringBuilder();
        if (screenings.isEmpty()) {
            screeningRows.append("<tr><td colspan='5' style='text-align:center; color:#888;'>Hiện chưa có lịch chiếu cho bộ phim này.</td></tr>");
        } else {
            for (Screening9 s : screenings) {
                screeningRows.append("<tr>")
                        .append("<td><b>S").append(s.getScreeningId()).append("</b></td>")
                        .append("<td>").append(escapeHtml(s.getRoomName() == null ? "Phòng 1" : s.getRoomName())).append("</td>")
                        .append("<td>").append(s.getStartTime() == null ? "" : dateFormat.format(s.getStartTime())).append("</td>")
                        .append("<td>").append(s.getStartTime() == null ? "" : timeFormat.format(s.getStartTime())).append("</td>")
                        .append("<td>").append(s.getEndTime() == null ? "" : timeFormat.format(s.getEndTime())).append("</td>")
                        .append("</tr>");
            }
        }

        String statusStr = "SHOWING".equalsIgnoreCase(movie.getMovieStatus()) ? "Đang chiếu" : ("COMING_SOON".equalsIgnoreCase(movie.getMovieStatus()) ? "Sắp chiếu" : "Ngừng chiếu");

        String content = "<h2>CHI TIẾT PHIM</h2>" +
                "<div style='display:flex; gap:30px; margin-bottom:30px;'>" +
                "<div style='width:180px; height:240px; background:#dfe6ec; border-radius:8px; display:flex; align-items:center; justify-content:center; color:#555; font-size:18px; font-weight:bold; border:1px solid #c9d6df;'>" +
                "Poster" +
                "</div>" +
                "<div style='flex:1; line-height:1.8; font-size:15px;'>" +
                "<table style='width:100%; border:none;'>" +
                "<tr><td style='width:120px; font-weight:bold;'>Tên phim:</td><td style='font-size:18px; font-weight:bold; color:#1a2b49;'>" + escapeHtml(movie.getTitle()) + "</td></tr>" +
                "<tr><td style='font-weight:bold;'>Thể loại:</td><td>" + escapeHtml(movie.getGenre()) + "</td></tr>" +
                "<tr><td style='font-weight:bold;'>Thời lượng:</td><td>" + movie.getDurationMinutes() + " phút</td></tr>" +
                "<tr><td style='font-weight:bold;'>Trạng thái:</td><td><span class='badge'>" + statusStr + "</span></td></tr>" +
                "<tr><td style='font-weight:bold; vertical-align:top;'>Mô tả:</td><td>" + escapeHtml(movie.getDescription()) + "</td></tr>" +
                "</table>" +
                "</div>" +
                "</div>" +
                "<h3>Lịch chiếu</h3>" +
                "<table class='data-table' style='margin-bottom:25px;'>" +
                "<thead><tr><th>Mã suất</th><th>Phòng chiếu</th><th>Ngày</th><th>Giờ bắt đầu</th><th>Giờ kết thúc</th></tr></thead>" +
                "<tbody>" + screeningRows + "</tbody>" +
                "</table>" +
                "<div style='display:flex; gap:15px;'>" +
                "<a href='/customer/search' class='btn btn-secondary' style='text-decoration:none;'>Quay lại</a>" +
                "<a href='/customer/booking?movieId=" + movie.getMovieId() + "' class='btn btn-primary' style='text-decoration:none;'>Mua vé phim này</a>" +
                "</div>";

        sendHtml(exchange, 200, buildLayout(user, "customer-search", content));
    }

    // -------------------------------------------------------------
    // MODULE 2: Nhân viên thống kê khách hàng theo doanh thu
    // Khớp Hình 6, Hình 7 & Hình 8 trong đề bài
    // -------------------------------------------------------------
    private void renderReportMenuPage(HttpExchange exchange, UserAccount9 user) throws IOException {
        if (user == null || !"MANAGER".equalsIgnoreCase(user.getRole())) { redirect(exchange, "/login"); return; }

        String content = "<h2>XEM BÁO CÁO</h2>" +
                "<p style='color:#666; margin-bottom:25px;'>Chọn loại báo cáo cần xem.</p>" +
                "<div style='display:flex; flex-direction:column; gap:18px; max-width:650px;'>" +
                "<div onclick=\"alert('Báo cáo thống kê phim đang được hoàn thiện.')\" style='padding:22px; border:1px solid #c9d6df; border-radius:6px; cursor:pointer; background:#fff; font-size:16px; font-weight:600; color:#1a2b49; box-shadow:0 2px 4px rgba(0,0,0,0.03);'>" +
                "Thống kê phim" +
                "</div>" +
                "<div onclick=\"window.location='/manager/customer-revenue'\" style='padding:22px; border:2px solid #1a73e8; border-radius:6px; cursor:pointer; background:#fff; font-size:17px; font-weight:bold; color:#1a73e8; box-shadow:0 2px 8px rgba(26,115,232,0.15);'>" +
                "Thống kê khách hàng theo doanh thu" +
                "</div>" +
                "<div onclick=\"alert('Báo cáo tổng doanh thu rạp đang được cập nhật.')\" style='padding:22px; border:1px solid #c9d6df; border-radius:6px; cursor:pointer; background:#fff; font-size:16px; font-weight:600; color:#1a2b49; box-shadow:0 2px 4px rgba(0,0,0,0.03);'>" +
                "Thống kê doanh thu" +
                "</div>" +
                "</div>";

        sendHtml(exchange, 200, buildLayout(user, "manager-reports", content));
    }

    private void renderCustomerRevenueReportPage(HttpExchange exchange, UserAccount9 user, Map<String, String> params) throws IOException {
        if (user == null) { redirect(exchange, "/login"); return; }

        // Giá trị mặc định khớp ví dụ trong đề bài: 01/10/2026 - 15/10/2026
        String startDateStr = params.getOrDefault("startDate", "2026-10-01");
        String endDateStr = params.getOrDefault("endDate", "2026-10-15");

        ReportFilter9 filter = new ReportFilter9(startDateStr, endDateStr);
        String alert = "";
        List<CustomerRevenueStatistic9> stats = new ArrayList<>();

        if (!filter.isValid()) {
            alert = "<div class='alert alert-danger'>Khoảng ngày không hợp lệ! Vui lòng chọn ngày bắt đầu không sau ngày kết thúc.</div>";
        } else {
            stats = revenueReportController.getCustomerRevenue(user, filter);
            if (stats.isEmpty()) {
                alert = "<div class='alert alert-warning'>Không có dữ liệu giao dịch trong khoảng thời gian đã chọn!</div>";
            }
        }

        StringBuilder rows = new StringBuilder();
        int stt = 1;
        for (CustomerRevenueStatistic9 s : stats) {
            rows.append("<tr onclick=\"window.location='/manager/customer-transactions?id=").append(s.getCustomerId())
                    .append("&startDate=").append(startDateStr).append("&endDate=").append(endDateStr)
                    .append("'\" style='cursor:pointer;'>")
                    .append("<td>").append(stt++).append("</td>")
                    .append("<td><b style='color:#1a73e8;'>").append(s.getCustomerCode()).append("</b></td>")
                    .append("<td>").append(escapeHtml(s.getFullName())).append("</td>")
                    .append("<td>").append(s.getTransactionCount()).append("</td>")
                    .append("<td><b>").append(s.getFormattedRevenue()).append("</b></td>")
                    .append("</tr>");
        }

        String content = "<h2>THỐNG KÊ KHÁCH HÀNG THEO DOANH THU</h2>" +
                "<form method='GET' action='/manager/customer-revenue' style='display:flex; align-items:center; gap:15px; margin-bottom:25px; background:#fff; padding:15px; border-radius:6px; border:1px solid #e2e8f0;'>" +
                "<span>Từ ngày:</span>" +
                "<input type='date' name='startDate' class='form-control' style='width:160px;' value='" + escapeHtml(startDateStr) + "' required>" +
                "<span>Đến ngày:</span>" +
                "<input type='date' name='endDate' class='form-control' style='width:160px;' value='" + escapeHtml(endDateStr) + "' required>" +
                "<button type='submit' class='btn btn-primary'>Xem thống kê</button>" +
                "</form>" +
                alert +
                "<table class='data-table'>" +
                "<thead><tr><th style='width:60px;'>STT</th><th>Mã KH</th><th>Họ tên</th><th>Số giao dịch</th><th>Tổng doanh thu (VNĐ)</th></tr></thead>" +
                "<tbody>" + rows + "</tbody>" +
                "</table>" +
                "<p style='color:#777; font-size:13px; margin-top:12px;'>* Nhấn vào một khách hàng để xem chi tiết các giao dịch.</p>";

        sendHtml(exchange, 200, buildLayout(user, "manager-reports", content));
    }

    private void renderCustomerTransactionsDetailPage(HttpExchange exchange, UserAccount9 user, Map<String, String> params) throws IOException {
        if (user == null) { redirect(exchange, "/login"); return; }

        int customerId = Integer.parseInt(params.getOrDefault("id", "0"));
        String startDateStr = params.getOrDefault("startDate", "2026-10-01");
        String endDateStr = params.getOrDefault("endDate", "2026-10-15");

        Customer9 customer = revenueReportController.getCustomerProfile(user, customerId);
        if (customer == null) {
            redirect(exchange, "/manager/customer-revenue");
            return;
        }

        ReportFilter9 filter = new ReportFilter9(startDateStr, endDateStr);
        List<Transaction9> transactions = revenueReportController.getCustomerTransactions(user, customerId, filter);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        double totalRevenue = 0;
        int totalTrans = transactions.size();

        StringBuilder rows = new StringBuilder();
        for (Transaction9 t : transactions) {
            totalRevenue += t.getTotalAmount();
            String statusBadge = "<span class='badge' style='background:#d4edda; color:#155724;'>" + escapeHtml(t.getTransactionStatus().equalsIgnoreCase("COMPLETED") ? "Thành công" : t.getTransactionStatus()) + "</span>";

            rows.append("<tr>")
                    .append("<td><b>GD").append(t.getTransactionId()).append("</b></td>")
                    .append("<td>").append(t.getTransactionTime() == null ? "" : sdf.format(t.getTransactionTime())).append("</td>")
                    .append("<td>").append(t.getTicketCount()).append("</td>")
                    .append("<td>").append(escapeHtml(t.getPaymentMethod())).append("</td>")
                    .append("<td>").append(statusBadge).append("</td>")
                    .append("<td><b>").append(nf.format((long) t.getTotalAmount())).append("</b></td>")
                    .append("</tr>");
        }

        String content = "<h2>CHI TIẾT GIAO DỊCH CỦA KHÁCH HÀNG</h2>" +
                "<div style='display:flex; justify-content:space-between; margin-bottom:20px; font-size:15px; background:#fff; padding:15px; border-radius:6px; border:1px solid #e2e8f0;'>" +
                "<div><b>Khách hàng:</b> KH" + String.format("%03d", customer.getCustomerId()) + " - " + escapeHtml(customer.getFullName()) + "</div>" +
                "<div><b>Khoảng ngày:</b> " + escapeHtml(startDateStr) + " – " + escapeHtml(endDateStr) + "</div>" +
                "</div>" +
                "<table class='data-table' style='margin-bottom:20px;'>" +
                "<thead><tr><th>Mã GD</th><th>Thời điểm</th><th>Số vé</th><th>Thanh toán</th><th>Trạng thái</th><th>Tổng tiền</th></tr></thead>" +
                "<tbody>" + rows + "</tbody>" +
                "</table>" +
                "<div style='display:flex; justify-content:space-between; align-items:center; font-size:16px; font-weight:bold; margin-bottom:25px; padding:10px 0;'>" +
                "<div>Tổng số giao dịch: " + totalTrans + "</div>" +
                "<div style='color:#1a73e8;'>Tổng doanh thu: " + nf.format((long) totalRevenue) + " VNĐ</div>" +
                "</div>" +
                "<a href='/manager/customer-revenue?startDate=" + startDateStr + "&endDate=" + endDateStr + "' class='btn btn-secondary' style='text-decoration:none;'>Quay lại</a>";

        sendHtml(exchange, 200, buildLayout(user, "manager-reports", content));
    }

    // -------------------------------------------------------------
    // Additional Core Features: Booking, Movie/Room/Screening, Cards
    // -------------------------------------------------------------
    private void renderCustomerBookingPage(HttpExchange exchange, UserAccount9 user, Map<String, String> params) throws IOException {
        if (user == null) { redirect(exchange, "/login"); return; }

        String msg = "";
        int customerId = (user instanceof Customer9) ? ((Customer9) user).getCustomerId() : 1;

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            int screeningId = Integer.parseInt(params.getOrDefault("screeningId", "0"));
            String seatIdsStr = params.getOrDefault("seatIds", "");
            String paymentMethod = params.getOrDefault("paymentMethod", "Trực tuyến");

            List<Integer> seatIds = new ArrayList<>();
            for (String s : seatIdsStr.split(",")) {
                if (!s.trim().isEmpty()) {
                    seatIds.add(Integer.parseInt(s.trim()));
                }
            }

            boolean ok = bookingController.bookTickets(customerId, null, screeningId, seatIds, paymentMethod, 180000.0);
            if (ok) {
                msg = "<div class='alert alert-success'>Đặt vé trực tuyến thành công! Hệ thống đã ghi nhận giao dịch và tích điểm thưởng cho bạn.</div>";
            } else {
                msg = "<div class='alert alert-danger'>Đặt vé thất bại! Ghế đã có người đặt trước hoặc thông tin không hợp lệ.</div>";
            }
        }

        List<Screening9> screenings = screeningController.getAllScreenings();
        int selectedScreeningId = Integer.parseInt(params.getOrDefault("screeningId", screenings.isEmpty() ? "0" : String.valueOf(screenings.get(0).getScreeningId())));
        List<Seat9> seats = bookingController.getAvailableSeatsForScreening(selectedScreeningId);

        StringBuilder screeningOptions = new StringBuilder();
        for (Screening9 s : screenings) {
            String sel = (s.getScreeningId() == selectedScreeningId) ? "selected" : "";
            screeningOptions.append("<option value='").append(s.getScreeningId()).append("' ").append(sel).append(">")
                    .append(escapeHtml(s.getMovieTitle())).append(" - ").append(escapeHtml(s.getRoomName()))
                    .append(" (").append(s.getStartTime()).append(")")
                    .append("</option>");
        }

        StringBuilder seatGrid = new StringBuilder("<div style='display:grid; grid-template-columns:repeat(5, 55px); gap:10px; margin:20px 0;'>");
        for (Seat9 seat : seats) {
            boolean booked = "BOOKED".equalsIgnoreCase(seat.getSeatStatus());
            String bg = booked ? "#e53e3e" : ("VIP".equalsIgnoreCase(seat.getSeatType()) ? "#d69e2e" : "#3182ce");
            String cursor = booked ? "not-allowed" : "pointer";
            seatGrid.append("<div class='seat-box' style='background:").append(bg).append("; color:#fff; text-align:center; padding:10px 0; border-radius:6px; cursor:").append(cursor).append(";' ")
                    .append(booked ? "" : "onclick=\"toggleSeat(this, " + seat.getSeatId() + ")\"")
                    .append(">")
                    .append(seat.getSeatCode())
                    .append("</div>");
        }
        seatGrid.append("</div>");

        String content = "<h2>MUA VÉ TRỰC TUYẾN</h2>" +
                msg +
                "<form method='GET' action='/customer/booking' style='margin-bottom:20px;'>" +
                "<label><b>Chọn suất chiếu:</b></label>" +
                "<div style='display:flex; gap:10px; margin-top:5px;'>" +
                "<select name='screeningId' class='form-control' style='flex:1;' onchange='this.form.submit()'>" +
                screeningOptions +
                "</select>" +
                "</div></form>" +
                "<form method='POST' action='/customer/booking'>" +
                "<input type='hidden' name='screeningId' value='" + selectedScreeningId + "'>" +
                "<input type='hidden' name='seatIds' id='selectedSeatIds' value=''>" +
                "<p><b>Chọn ghế trong phòng:</b> (<span style='color:#3182ce;'>■</span> Tiêu chuẩn 180k, <span style='color:#d69e2e;'>■</span> VIP 180k, <span style='color:#e53e3e;'>■</span> Đã đặt, <span style='color:#38a169;'>■</span> Đang chọn)</p>" +
                seatGrid +
                "<div style='margin-top:15px; font-weight:bold; font-size:16px;'>Ghế đã chọn: <span id='selectedSeatText' style='color:#1a73e8;'>Chưa chọn</span></div>" +
                "<div class='form-group' style='margin-top:15px;'><label>Hình thức thanh toán:</label>" +
                "<select name='paymentMethod' class='form-control' style='width:250px;'><option value='Trực tuyến'>Ví điện tử / QR Pay</option><option value='Thẻ ATM'>Thẻ ATM nội địa</option><option value='Visa/Master'>Thẻ tín dụng Quốc tế</option></select>" +
                "</div>" +
                "<button type='submit' class='btn btn-primary' style='padding:10px 25px;'>Xác nhận đặt vé</button>" +
                "</form>" +
                "<script>" +
                "var chosen = [];" +
                "function toggleSeat(elem, id){" +
                "  var idx = chosen.indexOf(id);" +
                "  if(idx > -1){ chosen.splice(idx,1); elem.style.outline='none'; }" +
                "  else { chosen.push(id); elem.style.outline='3px solid #38a169'; }" +
                "  document.getElementById('selectedSeatIds').value = chosen.join(',');" +
                "  document.getElementById('selectedSeatText').innerText = chosen.length > 0 ? (chosen.length + ' ghế (Tổng: ' + (chosen.length*180000).toLocaleString('vi-VN') + ' đ)') : 'Chưa chọn';" +
                "}" +
                "</script>";

        sendHtml(exchange, 200, buildLayout(user, "customer-booking", content));
    }

    private void renderRegisterPage(HttpExchange exchange, Map<String, String> params) throws IOException {
        String msg = "";
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String u = params.get("username");
            String p = params.get("password");
            String fn = params.get("fullName");
            String email = params.get("email");
            String phone = params.get("phone");

            boolean ok = membershipController.registerMembership(u, p, fn, email, phone);
            if (ok) {
                msg = "<div class='alert alert-success'>Đăng ký thành viên thành công! Bạn có thể <a href='/login'>Đăng nhập ngay</a>.</div>";
            } else {
                msg = "<div class='alert alert-danger'>Tên đăng nhập đã tồn tại hoặc dữ liệu không hợp lệ. Vui lòng thử lại!</div>";
            }
        }

        String html = "<!DOCTYPE html><html lang='vi'><head><meta charset='UTF-8'>" +
                "<title>CineMan - Đăng kí thành viên</title>" + getCommonCss() + "</head>" +
                "<body style='display:flex; justify-content:center; align-items:center; min-height:100vh; background:#f0f2f5;'>" +
                "<div style='background:#fff; padding:35px 40px; border-radius:10px; box-shadow:0 6px 18px rgba(0,0,0,0.1); width:450px;'>" +
                "<h2 style='color:#1a2b49; margin-top:0;'>Đăng kí thành viên CineMan</h2>" +
                msg +
                "<form method='POST' action='/customer/register'>" +
                "<div class='form-group'><label>Họ và tên:</label><input type='text' name='fullName' class='form-control' required></div>" +
                "<div class='form-group'><label>Tên đăng nhập:</label><input type='text' name='username' class='form-control' required></div>" +
                "<div class='form-group'><label>Mật khẩu:</label><input type='password' name='password' class='form-control' required></div>" +
                "<div class='form-group'><label>Email:</label><input type='email' name='email' class='form-control' required></div>" +
                "<div class='form-group'><label>Số điện thoại:</label><input type='text' name='phone' class='form-control' required></div>" +
                "<button type='submit' class='btn btn-primary' style='width:100%; padding:10px;'>Hoàn tất đăng kí</button>" +
                "</form>" +
                "<div style='text-align:center; margin-top:15px;'><a href='/login'>Đã có tài khoản? Đăng nhập</a></div>" +
                "</div></body></html>";

        sendHtml(exchange, 200, html);
    }

    private void renderMovieManagementPage(HttpExchange exchange, UserAccount9 user, Map<String, String> params) throws IOException {
        if (user == null || !"MANAGER".equalsIgnoreCase(user.getRole())) { redirect(exchange, "/login"); return; }

        String msg = "";
        String action = params.getOrDefault("action", "");

        if ("add".equalsIgnoreCase(action) && "POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String title = params.get("title");
            String genre = params.get("genre");
            int duration = Integer.parseInt(params.getOrDefault("duration", "120"));
            String desc = params.get("description");
            String status = params.get("status");
            boolean ok = movieController.addMovie(title, genre, duration, desc, status);
            msg = ok ? "<div class='alert alert-success'>Thêm phim mới thành công!</div>" : "<div class='alert alert-danger'>Lỗi khi thêm phim!</div>";
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(params.getOrDefault("id", "0"));
            boolean ok = movieController.deleteMovie(id);
            msg = ok ? "<div class='alert alert-success'>Xóa phim thành công!</div>" : "<div class='alert alert-danger'>Lỗi khi xóa phim!</div>";
        }

        List<Movie9> movies = movieController.getAllMovies();
        StringBuilder rows = new StringBuilder();
        for (Movie9 m : movies) {
            rows.append("<tr>")
                    .append("<td>").append(m.getMovieId()).append("</td>")
                    .append("<td><b>").append(escapeHtml(m.getTitle())).append("</b></td>")
                    .append("<td>").append(escapeHtml(m.getGenre())).append("</td>")
                    .append("<td>").append(m.getDurationMinutes()).append(" phút</td>")
                    .append("<td>").append(escapeHtml(m.getMovieStatus())).append("</td>")
                    .append("<td><a href='/manager/movies?action=delete&id=").append(m.getMovieId()).append("' onclick=\"return confirm('Chắc chắn xóa phim này?')\" style='color:#e53e3e;'>Xóa</a></td>")
                    .append("</tr>");
        }

        String content = "<h2>QUẢN LÍ PHIM</h2>" +
                msg +
                "<div style='background:#fff; padding:15px; border-radius:6px; border:1px solid #e2e8f0; margin-bottom:20px;'>" +
                "<h4>Thêm phim mới</h4>" +
                "<form method='POST' action='/manager/movies?action=add' style='display:grid; grid-template-columns:1fr 1fr; gap:10px;'>" +
                "<input type='text' name='title' class='form-control' placeholder='Tên phim' required>" +
                "<input type='text' name='genre' class='form-control' placeholder='Thể loại' required>" +
                "<input type='number' name='duration' class='form-control' placeholder='Thời lượng (phút)' required>" +
                "<select name='status' class='form-control'><option value='SHOWING'>Đang chiếu</option><option value='COMING_SOON'>Sắp chiếu</option></select>" +
                "<textarea name='description' class='form-control' style='grid-column:span 2;' placeholder='Mô tả tóm tắt...'></textarea>" +
                "<button type='submit' class='btn btn-primary' style='grid-column:span 2;'>Thêm phim</button>" +
                "</form></div>" +
                "<table class='data-table'><thead><tr><th>Mã</th><th>Tên phim</th><th>Thể loại</th><th>Thời lượng</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>" +
                "<tbody>" + rows + "</tbody></table>";

        sendHtml(exchange, 200, buildLayout(user, "manager-movies", content));
    }

    private void renderRoomManagementPage(HttpExchange exchange, UserAccount9 user, Map<String, String> params) throws IOException {
        if (user == null || !"MANAGER".equalsIgnoreCase(user.getRole())) { redirect(exchange, "/login"); return; }

        String msg = "";
        String action = params.getOrDefault("action", "");
        if ("add".equalsIgnoreCase(action) && "POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String name = params.get("roomName");
            int cap = Integer.parseInt(params.getOrDefault("capacity", "30"));
            boolean ok = roomController.addRoom(name, cap, "AVAILABLE");
            msg = ok ? "<div class='alert alert-success'>Thêm phòng chiếu và tự động tạo ghế thành công!</div>" : "<div class='alert alert-danger'>Lỗi thêm phòng!</div>";
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(params.getOrDefault("id", "0"));
            roomController.deleteRoom(id);
            msg = "<div class='alert alert-success'>Đã xóa phòng chiếu!</div>";
        }

        List<ScreeningRoom9> rooms = roomController.getAllRooms();
        StringBuilder rows = new StringBuilder();
        for (ScreeningRoom9 r : rooms) {
            rows.append("<tr>")
                    .append("<td>").append(r.getRoomId()).append("</td>")
                    .append("<td><b>").append(escapeHtml(r.getRoomName())).append("</b></td>")
                    .append("<td>").append(r.getCapacity()).append(" ghế</td>")
                    .append("<td>").append(escapeHtml(r.getRoomStatus())).append("</td>")
                    .append("<td><a href='/manager/rooms?action=delete&id=").append(r.getRoomId()).append("' onclick=\"return confirm('Chắc chắn xóa phòng?')\" style='color:#e53e3e;'>Xóa</a></td>")
                    .append("</tr>");
        }

        String content = "<h2>QUẢN LÍ PHÒNG CHIẾU</h2>" +
                msg +
                "<div style='background:#fff; padding:15px; border-radius:6px; border:1px solid #e2e8f0; margin-bottom:20px;'>" +
                "<h4>Thêm phòng chiếu mới</h4>" +
                "<form method='POST' action='/manager/rooms?action=add' style='display:flex; gap:10px;'>" +
                "<input type='text' name='roomName' class='form-control' placeholder='Tên phòng (ví dụ: Phòng 4 - 3D)' required>" +
                "<input type='number' name='capacity' class='form-control' style='width:150px;' placeholder='Sức chứa' value='30' required>" +
                "<button type='submit' class='btn btn-primary'>Tạo phòng</button>" +
                "</form></div>" +
                "<table class='data-table'><thead><tr><th>Mã phòng</th><th>Tên phòng</th><th>Sức chứa</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>" +
                "<tbody>" + rows + "</tbody></table>";

        sendHtml(exchange, 200, buildLayout(user, "manager-rooms", content));
    }

    private void renderScreeningManagementPage(HttpExchange exchange, UserAccount9 user, Map<String, String> params) throws IOException {
        if (user == null || !"MANAGER".equalsIgnoreCase(user.getRole())) { redirect(exchange, "/login"); return; }

        String msg = "";
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            int movieId = Integer.parseInt(params.getOrDefault("movieId", "0"));
            int roomId = Integer.parseInt(params.getOrDefault("roomId", "0"));
            String startStr = params.getOrDefault("startTime", "").replace("T", " ") + ":00";
            String endStr = params.getOrDefault("endTime", "").replace("T", " ") + ":00";
            try {
                Timestamp st = Timestamp.valueOf(startStr);
                Timestamp et = Timestamp.valueOf(endStr);
                boolean ok = screeningController.addScreening(movieId, roomId, st, et);
                msg = ok ? "<div class='alert alert-success'>Lên lịch chiếu thành công!</div>" : "<div class='alert alert-danger'>Lỗi lên lịch chiếu!</div>";
            } catch (Exception e) {
                msg = "<div class='alert alert-danger'>Định dạng thời gian không hợp lệ!</div>";
            }
        }

        List<Movie9> movies = movieController.getAllMovies();
        List<ScreeningRoom9> rooms = roomController.getAllRooms();
        List<Screening9> screenings = screeningController.getAllScreenings();

        StringBuilder mOpts = new StringBuilder();
        for (Movie9 m : movies) mOpts.append("<option value='").append(m.getMovieId()).append("'>").append(escapeHtml(m.getTitle())).append("</option>");

        StringBuilder rOpts = new StringBuilder();
        for (ScreeningRoom9 r : rooms) rOpts.append("<option value='").append(r.getRoomId()).append("'>").append(escapeHtml(r.getRoomName())).append("</option>");

        StringBuilder rows = new StringBuilder();
        for (Screening9 s : screenings) {
            rows.append("<tr>")
                    .append("<td>S").append(s.getScreeningId()).append("</td>")
                    .append("<td><b>").append(escapeHtml(s.getMovieTitle())).append("</b></td>")
                    .append("<td>").append(escapeHtml(s.getRoomName())).append("</td>")
                    .append("<td>").append(s.getStartTime()).append("</td>")
                    .append("<td>").append(s.getEndTime()).append("</td>")
                    .append("<td>").append(s.getScreeningStatus()).append("</td>")
                    .append("</tr>");
        }

        String content = "<h2>LÊN LỊCH CHIẾU</h2>" +
                msg +
                "<div style='background:#fff; padding:15px; border-radius:6px; border:1px solid #e2e8f0; margin-bottom:20px;'>" +
                "<h4>Thêm suất chiếu mới</h4>" +
                "<form method='POST' action='/manager/screenings' style='display:grid; grid-template-columns:1fr 1fr; gap:10px;'>" +
                "<div><label>Chọn phim:</label><select name='movieId' class='form-control'>" + mOpts + "</select></div>" +
                "<div><label>Chọn phòng chiếu:</label><select name='roomId' class='form-control'>" + rOpts + "</select></div>" +
                "<div><label>Giờ bắt đầu:</label><input type='datetime-local' name='startTime' class='form-control' required></div>" +
                "<div><label>Giờ kết thúc:</label><input type='datetime-local' name='endTime' class='form-control' required></div>" +
                "<button type='submit' class='btn btn-primary' style='grid-column:span 2;'>Tạo suất chiếu</button>" +
                "</form></div>" +
                "<table class='data-table'><thead><tr><th>Mã suất</th><th>Tên phim</th><th>Phòng chiếu</th><th>Bắt đầu</th><th>Kết thúc</th><th>Trạng thái</th></tr></thead>" +
                "<tbody>" + rows + "</tbody></table>";

        sendHtml(exchange, 200, buildLayout(user, "manager-screenings", content));
    }

    private void renderSalesPosPage(HttpExchange exchange, UserAccount9 user, Map<String, String> params) throws IOException {
        if (user == null || (!"SALESPERSON".equalsIgnoreCase(user.getRole()) && !"MANAGER".equalsIgnoreCase(user.getRole()))) {
            redirect(exchange, "/login"); return;
        }

        String msg = "";
        int salespersonId = (user instanceof Employee9) ? ((Employee9) user).getEmployeeId() : 2;

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            int customerId = Integer.parseInt(params.getOrDefault("customerId", "1"));
            int screeningId = Integer.parseInt(params.getOrDefault("screeningId", "0"));
            String seatIdsStr = params.getOrDefault("seatIds", "");
            List<Integer> seatIds = new ArrayList<>();
            for (String s : seatIdsStr.split(",")) {
                if (!s.trim().isEmpty()) seatIds.add(Integer.parseInt(s.trim()));
            }

            boolean ok = bookingController.bookTickets(customerId, salespersonId, screeningId, seatIds, "Tại quầy", 180000.0);
            if (ok) {
                msg = "<div class='alert alert-success'>Bán vé tại quầy thành công! Đã in vé và tích điểm khách hàng.</div>";
            } else {
                msg = "<div class='alert alert-danger'>Bán vé thất bại! Ghế đã được đặt hoặc lỗi thông tin.</div>";
            }
        }

        List<Customer9> customers = membershipController.getAllCustomers();
        List<Screening9> screenings = screeningController.getAllScreenings();
        int selectedScreeningId = Integer.parseInt(params.getOrDefault("screeningId", screenings.isEmpty() ? "0" : String.valueOf(screenings.get(0).getScreeningId())));
        List<Seat9> seats = bookingController.getAvailableSeatsForScreening(selectedScreeningId);

        StringBuilder cOpts = new StringBuilder();
        for (Customer9 c : customers) {
            cOpts.append("<option value='").append(c.getCustomerId()).append("'>KH")
                    .append(String.format("%03d", c.getCustomerId())).append(" - ")
                    .append(escapeHtml(c.getFullName())).append(" (").append(c.getPhone()).append(")")
                    .append("</option>");
        }

        StringBuilder sOpts = new StringBuilder();
        for (Screening9 s : screenings) {
            String sel = (s.getScreeningId() == selectedScreeningId) ? "selected" : "";
            sOpts.append("<option value='").append(s.getScreeningId()).append("' ").append(sel).append(">")
                    .append(escapeHtml(s.getMovieTitle())).append(" - ").append(escapeHtml(s.getRoomName()))
                    .append("</option>");
        }

        StringBuilder seatGrid = new StringBuilder("<div style='display:grid; grid-template-columns:repeat(5, 55px); gap:10px; margin:15px 0;'>");
        for (Seat9 seat : seats) {
            boolean booked = "BOOKED".equalsIgnoreCase(seat.getSeatStatus());
            String bg = booked ? "#e53e3e" : "#3182ce";
            seatGrid.append("<div style='background:").append(bg).append("; color:#fff; text-align:center; padding:10px 0; border-radius:6px; cursor:").append(booked ? "not-allowed" : "pointer").append(";' ")
                    .append(booked ? "" : "onclick=\"togglePosSeat(this, " + seat.getSeatId() + ")\"")
                    .append(">").append(seat.getSeatCode()).append("</div>");
        }
        seatGrid.append("</div>");

        String content = "<h2>BÁN VÉ TẠI QUẦY (POS)</h2>" +
                msg +
                "<form method='POST' action='/sales/pos'>" +
                "<input type='hidden' name='seatIds' id='posSeatIds' value=''>" +
                "<div class='form-group'><label>Khách hàng mua vé:</label><select name='customerId' class='form-control'>" + cOpts + "</select></div>" +
                "<div class='form-group'><label>Suất chiếu:</label><select name='screeningId' class='form-control' onchange=\"window.location='/sales/pos?screeningId=' + this.value\">" + sOpts + "</select></div>" +
                "<label>Chọn ghế cho khách:</label>" +
                seatGrid +
                "<div style='margin-bottom:15px; font-weight:bold;'>Số ghế đã chọn: <span id='posCount' style='color:#1a73e8;'>0</span></div>" +
                "<button type='submit' class='btn btn-primary'>Xuất vé & Thu tiền</button>" +
                "</form>" +
                "<script>" +
                "var posChosen = [];" +
                "function togglePosSeat(el, id){" +
                "  var idx = posChosen.indexOf(id);" +
                "  if(idx > -1){ posChosen.splice(idx,1); el.style.outline='none'; }" +
                "  else { posChosen.push(id); el.style.outline='3px solid #38a169'; }" +
                "  document.getElementById('posSeatIds').value = posChosen.join(',');" +
                "  document.getElementById('posCount').innerText = posChosen.length + ' ghế (Tổng: ' + (posChosen.length*180000).toLocaleString('vi-VN') + ' đ)';" +
                "}" +
                "</script>";

        sendHtml(exchange, 200, buildLayout(user, "sales-pos", content));
    }

    private void renderIssueCardPage(HttpExchange exchange, UserAccount9 user, Map<String, String> params) throws IOException {
        if (user == null || (!"SALESPERSON".equalsIgnoreCase(user.getRole()) && !"MANAGER".equalsIgnoreCase(user.getRole()))) {
            redirect(exchange, "/login"); return;
        }

        String msg = "";
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            int customerId = Integer.parseInt(params.getOrDefault("customerId", "0"));
            String code = params.getOrDefault("cardCode", "");
            int months = Integer.parseInt(params.getOrDefault("months", "24"));
            boolean ok = membershipController.issueMemberCard(customerId, code, months);
            msg = ok ? "<div class='alert alert-success'>Xuất thẻ thành viên thành công! Thẻ đã kích hoạt và sẵn sàng sử dụng.</div>" : "<div class='alert alert-danger'>Lỗi xuất thẻ!</div>";
        }

        List<Customer9> customers = membershipController.getAllCustomers();
        List<MembershipCard9> cards = membershipController.getAllCards();

        StringBuilder cOpts = new StringBuilder();
        for (Customer9 c : customers) {
            cOpts.append("<option value='").append(c.getCustomerId()).append("'>KH")
                    .append(String.format("%03d", c.getCustomerId())).append(" - ")
                    .append(escapeHtml(c.getFullName())).append(" (Điểm: ").append(c.getLoyaltyPoints()).append(")")
                    .append("</option>");
        }

        StringBuilder cardRows = new StringBuilder();
        for (MembershipCard9 card : cards) {
            cardRows.append("<tr>")
                    .append("<td>").append(card.getCardId()).append("</td>")
                    .append("<td><b>").append(escapeHtml(card.getCardCode())).append("</b></td>")
                    .append("<td>KH").append(String.format("%03d", card.getCustomerId())).append("</td>")
                    .append("<td>").append(card.getIssuedDate()).append("</td>")
                    .append("<td>").append(card.getExpiryDate()).append("</td>")
                    .append("<td><span class='badge'>").append(card.getCardStatus()).append("</span></td>")
                    .append("</tr>");
        }

        String content = "<h2>XUẤT THẺ THÀNH VIÊN</h2>" +
                msg +
                "<div style='background:#fff; padding:15px; border-radius:6px; border:1px solid #e2e8f0; margin-bottom:20px;'>" +
                "<h4>Cấp thẻ thành viên mới cho khách hàng</h4>" +
                "<form method='POST' action='/sales/issue-card' style='display:grid; grid-template-columns:1fr 1fr; gap:10px;'>" +
                "<div><label>Chọn khách hàng:</label><select name='customerId' class='form-control'>" + cOpts + "</select></div>" +
                "<div><label>Mã thẻ (để trống sẽ tạo tự động):</label><input type='text' name='cardCode' class='form-control' placeholder='CARD-KH...'></div>" +
                "<div><label>Thời hạn (tháng):</label><input type='number' name='months' class='form-control' value='24'></div>" +
                "<div style='display:flex; align-items:flex-end;'><button type='submit' class='btn btn-primary' style='width:100%;'>Cấp và Xuất thẻ</button></div>" +
                "</form></div>" +
                "<h3>Danh sách thẻ thành viên đã cấp</h3>" +
                "<table class='data-table'><thead><tr><th>Mã thẻ #</th><th>Mã vạch / RFID</th><th>Mã KH</th><th>Ngày cấp</th><th>Ngày hết hạn</th><th>Trạng thái</th></tr></thead>" +
                "<tbody>" + cardRows + "</tbody></table>";

        sendHtml(exchange, 200, buildLayout(user, "sales-card", content));
    }

    // -------------------------------------------------------------
    // UI Templates & Helpers
    // -------------------------------------------------------------
    private String buildLayout(UserAccount9 user, String activeNav, String bodyContent) {
        String role = user != null ? user.getRole() : "CUSTOMER";
        String userName = user != null ? user.getFullName() : "Khách";
        String roleBadge = " (" + ("MANAGER".equalsIgnoreCase(role) ? "Quản lí" : ("SALESPERSON".equalsIgnoreCase(role) ? "Bán hàng" : "Khách hàng")) + ")";

        StringBuilder menu = new StringBuilder();
        if ("MANAGER".equalsIgnoreCase(role)) {
            menu.append(navItem("/manager/reports", "Xem báo cáo", "manager-reports".equals(activeNav)));
            menu.append(navItem("/manager/movies", "Quản lí phim", "manager-movies".equals(activeNav)));
            menu.append(navItem("/manager/rooms", "Quản lí phòng chiếu", "manager-rooms".equals(activeNav)));
            menu.append(navItem("/manager/screenings", "Lên lịch chiếu", "manager-screenings".equals(activeNav)));
            menu.append(navItem("/sales/pos", "Bán vé tại quầy", "sales-pos".equals(activeNav)));
        } else if ("SALESPERSON".equalsIgnoreCase(role)) {
            menu.append(navItem("/sales/pos", "Bán vé tại quầy", "sales-pos".equals(activeNav)));
            menu.append(navItem("/sales/issue-card", "Xuất thẻ thành viên", "sales-card".equals(activeNav)));
            menu.append(navItem("/customer/search", "Tra cứu phim", "customer-search".equals(activeNav)));
        } else {
            menu.append(navItem("/customer/search", "Tìm thông tin phim", "customer-search".equals(activeNav)));
            menu.append(navItem("/customer/booking", "Mua vé trực tuyến", "customer-booking".equals(activeNav)));
            menu.append(navItem("/customer/register", "Đăng kí thành viên", false));
        }

        return "<!DOCTYPE html><html lang='vi'><head><meta charset='UTF-8'>" +
                "<title>CineMan - Hệ thống Quản lý Rạp chiếu phim</title>" +
                getCommonCss() +
                "</head><body>" +
                "<div class='top-bar'>" +
                "<div class='logo'>CineMan</div>" +
                "<div class='user-info'>" + escapeHtml(userName + roleBadge) + " | <a href='/logout' style='color:#e2e8f0; text-decoration:none;'>Đăng xuất</a></div>" +
                "</div>" +
                "<div class='main-container'>" +
                "<div class='sidebar'>" +
                menu +
                "</div>" +
                "<div class='content'>" +
                bodyContent +
                "</div>" +
                "</div>" +
                "</body></html>";
    }

    private String navItem(String url, String text, boolean active) {
        String activeClass = active ? "active" : "";
        return "<a href='" + url + "' class='nav-item " + activeClass + "'>" + text + "</a>";
    }

    private String getCommonCss() {
        return "<style>" +
                "* { box-sizing:border-box; font-family:'Segoe UI', Roboto, Helvetica, Arial, sans-serif; margin:0; padding:0; }" +
                "body { background:#f7fafc; color:#2d3748; }" +
                ".top-bar { height:60px; background:#1a2b49; color:#fff; display:flex; justify-content:space-between; align-items:center; padding:0 30px; box-shadow:0 2px 5px rgba(0,0,0,0.1); }" +
                ".top-bar .logo { font-size:24px; font-weight:bold; letter-spacing:1px; }" +
                ".top-bar .user-info { font-size:14px; }" +
                ".main-container { display:flex; min-height:calc(100vh - 60px); }" +
                ".sidebar { width:240px; background:#f0f4f8; border-right:1px solid #d9e2ec; padding:20px 0; display:flex; flex-direction:column; }" +
                ".nav-item { padding:14px 25px; color:#334e68; text-decoration:none; font-size:15px; font-weight:500; display:block; transition:0.2s; }" +
                ".nav-item:hover { background:#e2e8f0; }" +
                ".nav-item.active { background:#1a2b49; color:#ffffff; font-weight:600; }" +
                ".content { flex:1; padding:35px 45px; background:#f7fafc; overflow-y:auto; }" +
                "h2 { color:#1a2b49; margin-bottom:20px; font-size:22px; font-weight:bold; }" +
                "h3 { color:#1a2b49; margin:25px 0 15px; font-size:18px; }" +
                "h4 { color:#2d3748; margin-bottom:12px; }" +
                ".form-group { margin-bottom:15px; }" +
                ".form-group label { display:block; margin-bottom:6px; font-size:14px; font-weight:600; color:#4a5568; }" +
                ".form-control { width:100%; padding:10px 14px; border:1px solid #cbd5e0; border-radius:5px; font-size:14px; }" +
                ".form-control:focus { outline:none; border-color:#3182ce; box-shadow:0 0 0 2px rgba(49,130,206,0.2); }" +
                ".btn { padding:10px 20px; border:none; border-radius:5px; cursor:pointer; font-weight:600; font-size:14px; display:inline-block; transition:0.2s; }" +
                ".btn-primary { background:#1a73e8; color:#fff; }" +
                ".btn-primary:hover { background:#1557b0; }" +
                ".btn-secondary { background:#4a5568; color:#fff; }" +
                ".btn-secondary:hover { background:#2d3748; }" +
                ".data-table { width:100%; border-collapse:collapse; background:#fff; border-radius:6px; overflow:hidden; box-shadow:0 1px 4px rgba(0,0,0,0.05); margin-top:10px; }" +
                ".data-table th { background:#1a2b49; color:#fff; padding:12px 16px; text-align:left; font-size:14px; font-weight:600; }" +
                ".data-table td { padding:13px 16px; border-bottom:1px solid #edf2f7; font-size:14px; }" +
                ".data-table tbody tr:hover { background:#edf2f7; }" +
                ".alert { padding:12px 18px; border-radius:5px; margin-bottom:20px; font-size:14px; }" +
                ".alert-success { background:#def7ec; color:#03543f; border:1px solid #bcf0da; }" +
                ".alert-danger { background:#fde8e8; color:#9b1c1c; border:1px solid #fbd5d5; }" +
                ".alert-warning { background:#fef08a; color:#713f12; border:1px solid #facc15; }" +
                ".badge { display:inline-block; padding:4px 10px; border-radius:12px; font-size:12px; font-weight:600; background:#ebf8ff; color:#2b6cb0; }" +
                "</style>";
    }

    private UserAccount9 getSessionUser(HttpExchange exchange) {
        String token = getCookie(exchange, "cineman_session");
        if (token != null) {
            return sessions.get(token);
        }
        return null;
    }

    private String getCookie(HttpExchange exchange, String name) {
        List<String> cookies = exchange.getRequestHeaders().get("Cookie");
        if (cookies != null) {
            for (String cookieHeader : cookies) {
                String[] parts = cookieHeader.split(";");
                for (String part : parts) {
                    String[] kv = part.trim().split("=");
                    if (kv.length == 2 && kv[0].equals(name)) {
                        return kv[1];
                    }
                }
            }
        }
        return null;
    }

    private void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
    }

    private void sendHtml(HttpExchange exchange, int status, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                try {
                    map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                } catch (Exception ignored) {}
            } else if (kv.length == 1) {
                try {
                    map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), "");
                } catch (Exception ignored) {}
            }
        }
        return map;
    }

    private String escapeHtml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
