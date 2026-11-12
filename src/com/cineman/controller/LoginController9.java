package com.cineman.controller;

import com.cineman.dao.UserAccountDAO9;
import com.cineman.model.entity.UserAccount9;
import com.cineman.service.DataProtectionService9;

public class LoginController9 {
    private final UserAccountDAO9 userDAO;

    public LoginController9() {
        this.userDAO = new UserAccountDAO9();
    }

    public LoginController9(UserAccountDAO9 userDAO) {
        this.userDAO = userDAO;
    }

    public UserAccount9 authenticate(String username, String password) {
        String cleanUser = DataProtectionService9.sanitizeInput(username);
        if (cleanUser.isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        UserAccount9 account = userDAO.login(cleanUser, password.trim());
        if (account != null) {
            DataProtectionService9.logAudit(account.getUsername(), account.getRole(), "User Login", true, "Đăng nhập thành công");
            return DataProtectionService9.maskSensitiveAccountData(account);
        } else {
            DataProtectionService9.logAudit(cleanUser, "UNKNOWN", "User Login", false, "Sai thông tin đăng nhập");
            return null;
        }
    }
}
