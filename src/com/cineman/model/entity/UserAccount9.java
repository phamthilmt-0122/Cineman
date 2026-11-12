package com.cineman.model.entity;

public class UserAccount9 {
    protected int accountId;
    protected String username;
    protected String password;
    protected String fullName;
    protected String email;
    protected String phone;
    protected String status; // 'ACTIVE', 'LOCKED'
    protected String role;   // 'MANAGER', 'SALESPERSON', 'CUSTOMER'

    public UserAccount9() {}

    public UserAccount9(int accountId, String username, String password, String fullName, String email, String phone, String status, String role) {
        this.accountId = accountId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.role = role;
    }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
