package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try {
            File dbFile = new File("cineman.db");
            boolean needInit = !dbFile.exists() || dbFile.length() == 0;

            if (needInit) {
                System.out.println("[DatabaseInitializer] Khởi tạo cơ sở dữ liệu CineMan mới...");
                executeSqlScript("sql/cineman_schema.sql");
                executeSqlScript("sql/cineman_data.sql");
                System.out.println("[DatabaseInitializer] Đã nạp thành công Schema và Dữ liệu mẫu phong phú!");
            } else {
                System.out.println("[DatabaseInitializer] Cơ sở dữ liệu cineman.db đã tồn tại và sẵn sàng.");
            }
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Lỗi khởi tạo cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void resetDatabase() {
        try {
            System.out.println("[DatabaseInitializer] Đang nạp lại dữ liệu cơ sở dữ liệu...");
            executeSqlScript("sql/cineman_schema.sql");
            executeSqlScript("sql/cineman_data.sql");
            System.out.println("[DatabaseInitializer] Nạp lại dữ liệu thành công!");
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Lỗi reset DB: " + e.getMessage());
        }
    }

    private static void executeSqlScript(String scriptPath) throws Exception {
        File file = new File(scriptPath);
        if (!file.exists()) {
            System.err.println("[DatabaseInitializer] Không tìm thấy file script: " + scriptPath);
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // Bỏ qua comment
                if (trimmed.startsWith("--") || trimmed.startsWith("//") || trimmed.isEmpty()) {
                    continue;
                }
                sb.append(line).append("\n");
                if (trimmed.endsWith(";")) {
                    String sql = sb.toString().trim();
                    if (!sql.isEmpty()) {
                        stmt.execute(sql);
                    }
                    sb.setLength(0);
                }
            }
            if (sb.length() > 0) {
                String sql = sb.toString().trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
        }
    }
}
