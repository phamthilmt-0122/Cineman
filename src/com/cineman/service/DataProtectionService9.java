package com.cineman.service;

import com.cineman.model.entity.Customer9;
import com.cineman.model.entity.UserAccount9;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LỚP TRUNG GIAN BẢO VỆ DỮ LIỆU (DATA PROTECTION & SECURITY INTERMEDIARY LAYER)
 * 
 * Đảm bảo:
 * 1. Phân quyền và Kiểm soát truy cập (RBAC - Role-Based Access Control):
 *    Ngăn chặn việc lấy trực tiếp dữ liệu nhạy cảm khi chưa được xác thực hoặc không đủ quyền.
 * 2. Che giấu dữ liệu nhạy cảm (Data Masking):
 *    Ẩn mật khẩu, mặt nạ hóa số điện thoại, email trước khi gửi tới giao diện.
 * 3. Chuẩn hóa & Làm sạch dữ liệu nhập (Data Sanitization):
 *    Ngăn chặn mã độc, chuỗi rác trước khi truyền xuống tầng DAO/Database.
 * 4. Ghi nhận vết truy cập dữ liệu (Audit Logging):
 *    Lưu lịch sử mọi thao tác truy xuất dữ liệu nghiệp vụ quan trọng.
 */
public class DataProtectionService9 {
    private static final DateTimeFormatter LOG_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Xác thực quyền hạn truy cập nghiệp vụ
     */
    public static void verifyRole(UserAccount9 user, String requiredRole, String businessAction) throws UnauthorizedAccessException9 {
        if (user == null) {
            logAudit("ANONYMOUS", "UNAUTHENTICATED", businessAction, false, "Chưa đăng nhập hệ thống");
            throw new UnauthorizedAccessException9("Yêu cầu bị từ chối: Bạn chưa đăng nhập vào hệ thống.");
        }

        if (requiredRole != null && !requiredRole.trim().isEmpty()) {
            if (!requiredRole.equalsIgnoreCase(user.getRole())) {
                logAudit(user.getUsername(), user.getRole(), businessAction, false, "Không đủ quyền (Yêu cầu vai trò: " + requiredRole + ")");
                throw new UnauthorizedAccessException9("Yêu cầu bị từ chối: Tài khoản '" + user.getUsername() + "' không có quyền thực hiện: " + businessAction);
            }
        }

        logAudit(user.getUsername(), user.getRole(), businessAction, true, "Truy cập thành công qua lớp trung gian bảo vệ");
    }

    /**
     * Làm sạch và chuẩn hóa chuỗi dữ liệu (Data Sanitization)
     */
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[';\"\\\\]", "");
    }

    /**
     * Che giấu dữ liệu người dùng (Data Masking)
     * Xóa sạch mật khẩu khỏi bộ nhớ trước khi truyền ra Boundary/UI
     */
    public static <T extends UserAccount9> T maskSensitiveAccountData(T account) {
        if (account != null) {
            account.setPassword("[PROTECTED]");
        }
        return account;
    }

    /**
     * Mặt nạ hóa số điện thoại (VD: 0987654321 -> 098****321)
     */
    public static String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 7) return "***";
        int len = phone.length();
        return phone.substring(0, 3) + "****" + phone.substring(len - 3);
    }

    /**
     * Ghi nhật ký bảo vệ dữ liệu (Security Audit Log)
     */
    public static void logAudit(String username, String role, String action, boolean granted, String detail) {
        String timestamp = LocalDateTime.now().format(LOG_FORMAT);
        String status = granted ? "[GRANTED]" : "[BLOCKED]";
        System.out.printf("[%s] %s | User: %-12s | Role: %-12s | Action: %-35s | Detail: %s%n",
                timestamp, status, username, role, action, detail);
    }
}
