package com.cineman.model.entity;

import java.sql.Date;

public class Employee9 extends UserAccount9 {
    protected int employeeId;
    protected Date hiredDate;
    protected String employeeRole; // 'MANAGER', 'SALESPERSON'

    public Employee9() {}

    public Employee9(int accountId, String username, String password, String fullName, String email, String phone, String status, String role,
                     int employeeId, Date hiredDate, String employeeRole) {
        super(accountId, username, password, fullName, email, phone, status, role);
        this.employeeId = employeeId;
        this.hiredDate = hiredDate;
        this.employeeRole = employeeRole;
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public Date getHiredDate() { return hiredDate; }
    public void setHiredDate(Date hiredDate) { this.hiredDate = hiredDate; }

    public String getEmployeeRole() { return employeeRole; }
    public void setEmployeeRole(String employeeRole) { this.employeeRole = employeeRole; }
}
