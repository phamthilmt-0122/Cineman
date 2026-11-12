package com.cineman.model.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Transaction9 {
    private int transactionId;
    private int customerId;
    private Integer salespersonId; // null if purchased online
    private Timestamp transactionTime;
    private double totalAmount;
    private String paymentMethod;    // 'Trực tuyến', 'Tại quầy', 'ONLINE_BANKING', 'CASH'
    private String transactionStatus; // 'COMPLETED', 'PENDING', 'CANCELLED'

    // Transient fields for reporting and presentation
    private String customerName;
    private int ticketCount;
    private List<Ticket9> tickets = new ArrayList<>();

    public Transaction9() {}

    public Transaction9(int transactionId, int customerId, Integer salespersonId, Timestamp transactionTime,
                        double totalAmount, String paymentMethod, String transactionStatus) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.salespersonId = salespersonId;
        this.transactionTime = transactionTime;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionStatus = transactionStatus;
    }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Integer getSalespersonId() { return salespersonId; }
    public void setSalespersonId(Integer salespersonId) { this.salespersonId = salespersonId; }

    public Timestamp getTransactionTime() { return transactionTime; }
    public void setTransactionTime(Timestamp transactionTime) { this.transactionTime = transactionTime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getTicketCount() { return ticketCount; }
    public void setTicketCount(int ticketCount) { this.ticketCount = ticketCount; }

    public List<Ticket9> getTickets() { return tickets; }
    public void setTickets(List<Ticket9> tickets) { this.tickets = tickets; }
}
