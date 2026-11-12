# HỆ THỐNG QUẢN LÝ RẠP CHIẾU PHIM (CINEMAN) - ĐỀ SỐ 17

Hệ thống được thiết kế và hiện thực hóa bằng **Java** kết hợp **SQL (SQLite & MySQL)**, tuân thủ chặt chẽ kiến trúc phân tầng chuẩn công nghiệp **BCE (Boundary - Controller - DAO - Entity)** và quy tắc đặt tên hậu tố **9** (theo số cuối mã sinh viên đề tài) dựa trên toàn bộ tài liệu đặc tả thiết kế Đề số 17.

---

## 1. Cấu trúc thư mục dự án

```text
C:\Users\Admin\.gemini\antigravity\scratch\cineman/
├── bin/                          # Thư mục chứa các file .class sau khi biên dịch
├── lib/                          # Thư viện JDBC driver và logging
│   ├── sqlite-jdbc-3.45.1.0.jar  # Driver SQLite (chạy độc lập, không cần cài server)
│   ├── mysql-connector-j-8.3.0.jar# Driver MySQL (tùy chọn)
│   ├── slf4j-api-1.7.36.jar
│   └── slf4j-simple-1.7.36.jar
├── sql/
│   ├── schema.sql                # DDL khởi tạo 11 bảng CSDL
│   └── seed_data.sql             # DML dữ liệu mẫu khớp 100% tài liệu
├── src/
│   └── com/cineman/
│       ├── model/
│       │   ├── entity/           # 13 lớp thực thể miền (Entity)
│       │   │   ├── UserAccount9.java
│       │   │   ├── Employee9.java
│       │   │   ├── Manager9.java
│       │   │   ├── Salesperson9.java
│       │   │   ├── Customer9.java
│       │   │   ├── MembershipCard9.java
│       │   │   ├── Cinema9.java
│       │   │   ├── ScreeningRoom9.java
│       │   │   ├── Seat9.java
│       │   │   ├── Movie9.java
│       │   │   ├── Screening9.java
│       │   │   ├── Transaction9.java
│       │   │   └── Ticket9.java
│       │   └── dto/              # DTO và Value Objects
│       │       ├── SearchCriteria9.java
│       │       ├── ReportFilter9.java
│       │       └── CustomerRevenueStatistic9.java
│       ├── dao/                  # Tầng truy xuất dữ liệu (DAO)
│       │   ├── DBConnection9.java
│       │   ├── UserAccountDAO9.java
│       │   ├── CustomerDAO9.java
│       │   ├── MovieDAO9.java
│       │   ├── ScreeningDAO9.java
│       │   ├── ScreeningRoomDAO9.java
│       │   ├── TransactionDAO9.java
│       │   ├── TicketDAO9.java
│       │   └── MembershipCardDAO9.java
│       ├── controller/           # Tầng điều khiển nghiệp vụ (Controller)
│       │   ├── LoginController9.java
│       │   ├── SearchMovieController9.java        # Module 1
│       │   ├── RevenueReportController9.java      # Module 2
│       │   ├── MovieManagementController9.java
│       │   ├── RoomManagementController9.java
│       │   ├── ScreeningController9.java
│       │   ├── TicketBookingController9.java
│       │   └── MembershipController9.java
│       ├── boundary/             # Tầng giao diện người dùng (Boundary/Web)
│       │   └── CineManWebServer9.java
│       └── Main.java             # Điểm khởi chạy hệ thống & kiểm thử xác thực
├── build.bat                     # Script biên dịch tự động
└── run.bat                       # Script chạy ứng dụng
```

---

## 2. Các module chức năng và Tác nhân

| Tác nhân | Các module chức năng |
| :--- | :--- |
| **Nhân viên quản lí (Manager)** | • **Xem báo cáo & thống kê (Module 2)**: Thống kê khách hàng theo doanh thu theo khoảng thời gian (`startDate`, `endDate`), click xem chi tiết các lần giao dịch của khách hàng.<br>• **Quản lí thông tin phim**: Thêm, sửa, xóa, duyệt danh sách phim.<br>• **Quản lí phòng chiếu**: Thêm phòng, thiết lập sức chứa, tự động sinh sơ đồ ghế.<br>• **Lên lịch chiếu**: Phân bổ suất chiếu theo phòng, phim và khung giờ. |
| **Nhân viên bán hàng (Salesperson)** | • **Bán vé tại quầy (POS)**: Chọn khách hàng, chọn suất chiếu, chọn ghế trực quan, xuất vé và thu tiền.<br>• **Xuất thẻ thành viên**: Đăng ký và cấp mã thẻ thành viên cứng/điện tử cho khách hàng. |
| **Khách hàng (Customer)** | • **Đăng kí thành viên**: Tạo tài khoản để nhận ưu đãi và tích điểm thưởng.<br>• **Tìm thông tin phim (Module 1)**: Nhập từ khóa tên phim -> hiển thị danh sách khớp -> xem chi tiết thông tin và lịch chiếu.<br>• **Mua vé trực tuyến**: Chọn phim, lịch chiếu, chọn ghế, chọn phương thức thanh toán. |
| **Dùng chung** | • **Đăng nhập (Login) & Đăng xuất (Logout)**: Xác thực đa vai trò. |

---

## 3. Tài khoản Demo kiểm thử

| Vai trò | Tên đăng nhập | Mật khẩu | Chức năng chính |
| :--- | :--- | :--- | :--- |
| **Quản lý (Manager)** | `manager` | `123456` | Xem báo cáo doanh thu, Quản lí phim, Quản lí phòng, Lên lịch chiếu |
| **Bán hàng (Salesperson)** | `sales` | `123456` | Bán vé tại quầy (POS), Xuất thẻ thành viên |
| **Khách hàng (Customer)** | `customer` | `123456` | Tra cứu thông tin phim, Xem lịch chiếu, Đặt vé trực tuyến |

---

## 4. Hướng dẫn biên dịch và Chạy

### Cách 1: Chạy trực tiếp qua file script
1. Nhấp đúp vào `build.bat` để biên dịch toàn bộ mã nguồn Java.
2. Nhấp đúp vào `run.bat` để khởi chạy máy chủ.
3. Mở trình duyệt web và truy cập địa chỉ: **`http://localhost:8080`**

### Cách 2: Chạy bằng dòng lệnh (Terminal / PowerShell / CMD)
```bash
cd C:\Users\Admin\.gemini\antigravity\scratch\cineman

# 1. Biên dịch
javac -cp "lib/*" -d bin @sources.txt

# 2. Khởi chạy
java -cp "bin;lib/*" com.cineman.Main 8080
```
