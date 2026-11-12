package model;

public class CustomerRevenueDTO {
    private int customerId;
    private String customerCode;
    private String memberCardNumber;
    private String fullName;
    private String phone;
    private String email;
    private String cardTier;
    private int totalOrders; // Số lượt giao dịch
    private int totalTickets; // Tổng số vé đã mua
    private double totalRevenue; // Tổng tiền đã chi tiêu
    private double revenuePercentage; // Tỷ lệ % đóng góp trên tổng doanh thu
    private int rank; // Thứ hạng xếp hạng

    public CustomerRevenueDTO() {}

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getMemberCardNumber() { return memberCardNumber; }
    public void setMemberCardNumber(String memberCardNumber) { this.memberCardNumber = memberCardNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCardTier() { return cardTier; }
    public void setCardTier(String cardTier) { this.cardTier = cardTier; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public int getTotalTickets() { return totalTickets; }
    public void setTotalTickets(int totalTickets) { this.totalTickets = totalTickets; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getRevenuePercentage() { return revenuePercentage; }
    public void setRevenuePercentage(double revenuePercentage) { this.revenuePercentage = revenuePercentage; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
}
