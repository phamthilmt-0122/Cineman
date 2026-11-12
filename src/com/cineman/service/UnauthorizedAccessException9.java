package com.cineman.service;

/**
 * Ngoại lệ bảo mật ném ra khi người dùng hoặc tiến trình cố tình truy xuất dữ liệu
 * trái phép hoặc vượt quá quyền hạn cho phép.
 */
public class UnauthorizedAccessException9 extends SecurityException {
    public UnauthorizedAccessException9(String message) {
        super(message);
    }
}
