package com.cineman.model.dto;

import java.text.NumberFormat;
import java.util.Locale;

public class CustomerRevenueStatistic9 {
    private int customerId;
    private String customerCode; // e.g. "KH001"
    private String fullName;
    private int transactionCount;
    private double totalRevenue;
    private int rank;

    public CustomerRevenueStatistic9() {}

    public CustomerRevenueStatistic9(int customerId, String fullName, int transactionCount, double totalRevenue) {
        this.customerId = customerId;
        this.customerCode = String.format("KH%03d", customerId);
        this.fullName = fullName;
        this.transactionCount = transactionCount;
        this.totalRevenue = totalRevenue;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
        this.customerCode = String.format("KH%03d", customerId);
    }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getFormattedRevenue() {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format((long) totalRevenue) + " VNĐ";
    }
}
