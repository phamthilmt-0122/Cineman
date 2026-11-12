package com.cineman.model.entity;

import java.sql.Date;

public class Manager9 extends Employee9 {
    private int managerId;

    public Manager9() {
        this.employeeRole = "MANAGER";
        this.role = "MANAGER";
    }

    public Manager9(int accountId, String username, String password, String fullName, String email, String phone, String status,
                    int employeeId, Date hiredDate, int managerId) {
        super(accountId, username, password, fullName, email, phone, status, "MANAGER", employeeId, hiredDate, "MANAGER");
        this.managerId = managerId;
    }

    public int getManagerId() { return managerId; }
    public void setManagerId(int managerId) { this.managerId = managerId; }
}
