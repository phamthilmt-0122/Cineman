package com.cineman.model.entity;

import java.sql.Date;

public class Salesperson9 extends Employee9 {
    private int salespersonId;

    public Salesperson9() {
        this.employeeRole = "SALESPERSON";
        this.role = "SALESPERSON";
    }

    public Salesperson9(int accountId, String username, String password, String fullName, String email, String phone, String status,
                        int employeeId, Date hiredDate, int salespersonId) {
        super(accountId, username, password, fullName, email, phone, status, "SALESPERSON", employeeId, hiredDate, "SALESPERSON");
        this.salespersonId = salespersonId;
    }

    public int getSalespersonId() { return salespersonId; }
    public void setSalespersonId(int salespersonId) { this.salespersonId = salespersonId; }
}
