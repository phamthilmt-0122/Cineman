package com.cineman.model.entity;

import java.sql.Date;

public class Customer9 extends UserAccount9 {
    private int customerId;
    private Date registeredDate;
    private int loyaltyPoints;

    public Customer9() {
        this.role = "CUSTOMER";
    }

    public Customer9(int accountId, String username, String password, String fullName, String email, String phone, String status,
                     int customerId, Date registeredDate, int loyaltyPoints) {
        super(accountId, username, password, fullName, email, phone, status, "CUSTOMER");
        this.customerId = customerId;
        this.registeredDate = registeredDate;
        this.loyaltyPoints = loyaltyPoints;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Date getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(Date registeredDate) { this.registeredDate = registeredDate; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
}
