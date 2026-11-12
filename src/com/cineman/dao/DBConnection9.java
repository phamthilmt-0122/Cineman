package com.cineman.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection9 {
    private static final String SQLITE_DB_FILE = "cineman.db";
    private static final String SQLITE_URL = "jdbc:sqlite:" + SQLITE_DB_FILE;
    
    // MySQL configuration (nếu người dùng muốn chạy trên MySQL)
    private static final String MYSQL_HOST = System.getProperty("cineman.mysql.host", "localhost");
    private static final String MYSQL_PORT = System.getProperty("cineman.mysql.port", "3306");
    private static final String MYSQL_DB = System.getProperty("cineman.mysql.db", "cineman9");
    private static final String MYSQL_USER = System.getProperty("cineman.mysql.user", "root");
    private static final String MYSQL_PASS = System.getProperty("cineman.mysql.pass", "");
    private static final boolean USE_MYSQL = Boolean.getBoolean("cineman.use.mysql");

    private static boolean initialized = false;

    static {
        try {
            if (USE_MYSQL) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else {
                Class.forName("org.sqlite.JDBC");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Package-private: Chỉ các lớp DAO trong gói com.cineman.dao mới được phép lấy kết nối CSDL.
     * Tầng Giao diện (Boundary) và Điều khiển (Controller) tuyệt đối không thể truy cập trực tiếp CSDL.
     */
    static synchronized Connection getConnection() throws SQLException {
        Connection conn;
        if (USE_MYSQL) {
            String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    MYSQL_HOST, MYSQL_PORT, MYSQL_DB);
            conn = DriverManager.getConnection(url, MYSQL_USER, MYSQL_PASS);
        } else {
            boolean isNewDb = !new File(SQLITE_DB_FILE).exists() || new File(SQLITE_DB_FILE).length() == 0;
            conn = DriverManager.getConnection(SQLITE_URL);
            if (isNewDb && !initialized) {
                initializeDatabase(conn);
                initialized = true;
            }
        }
        return conn;
    }

    public static synchronized void initializeDatabase(Connection conn) {
        System.out.println("[DBConnection9] Khởi tạo cơ sở dữ liệu và nạp dữ liệu mẫu...");
        executeSqlScript(conn, "sql/schema.sql");
        executeSqlScript(conn, "sql/seed_data.sql");
        System.out.println("[DBConnection9] Khởi tạo cơ sở dữ liệu thành công!");
    }

    public static void executeSqlScript(Connection conn, String scriptPath) {
        File file = new File(scriptPath);
        if (!file.exists()) {
            System.err.println("[DBConnection9] Script file không tồn tại: " + scriptPath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
             Statement stmt = conn.createStatement()) {
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Loại bỏ comment trên dòng
                int commentIdx = line.indexOf("--");
                if (commentIdx >= 0) {
                    line = line.substring(0, commentIdx);
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                sb.append(line).append(" ");
                if (line.endsWith(";")) {
                    String sql = sb.toString().trim();
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                        } catch (SQLException e) {
                            System.err.println("[DBConnection9] Lỗi thực thi SQL: " + sql + " -> " + e.getMessage());
                        }
                    }
                    sb.setLength(0);
                }
            }
        } catch (Exception e) {
            System.err.println("[DBConnection9] Lỗi khi đọc " + scriptPath + ": " + e.getMessage());
        }
    }

    public static void ensureInitialized() {
        try (Connection conn = getConnection()) {
            // Check if tables exist
            try (Statement stmt = conn.createStatement()) {
                stmt.executeQuery("SELECT count(*) FROM Movie9");
            } catch (SQLException e) {
                initializeDatabase(conn);
            }
        } catch (SQLException e) {
            System.err.println("[DBConnection9] Lỗi kiểm tra database: " + e.getMessage());
        }
    }
}
