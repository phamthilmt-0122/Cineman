package com.cineman.controller;

import com.cineman.dao.CustomerDAO9;
import com.cineman.dao.MembershipCardDAO9;
import com.cineman.dao.UserAccountDAO9;
import com.cineman.model.entity.Customer9;
import com.cineman.model.entity.MembershipCard9;

import java.util.List;

public class MembershipController9 {
    private final UserAccountDAO9 userDAO;
    private final CustomerDAO9 customerDAO;
    private final MembershipCardDAO9 cardDAO;

    public MembershipController9() {
        this.userDAO = new UserAccountDAO9();
        this.customerDAO = new CustomerDAO9();
        this.cardDAO = new MembershipCardDAO9();
    }

    public boolean registerMembership(String username, String password, String fullName, String email, String phone) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        return userDAO.registerCustomer(username.trim(), password.trim(), fullName.trim(), email.trim(), phone.trim());
    }

    public boolean issueMemberCard(int customerId, String customCardCode, int validityMonths) {
        if (customerId <= 0) return false;
        if (customCardCode == null || customCardCode.trim().isEmpty()) {
            customCardCode = "CARD-KH" + String.format("%03d", customerId) + "-" + System.currentTimeMillis() % 10000;
        }
        if (validityMonths <= 0) validityMonths = 24; // Mặc định 24 tháng (2 năm)
        return cardDAO.issueCard(customerId, customCardCode.trim(), validityMonths);
    }

    public List<Customer9> getAllCustomers() {
        return customerDAO.getAll();
    }

    public List<MembershipCard9> getCardsByCustomer(int customerId) {
        return cardDAO.getCardsByCustomerId(customerId);
    }

    public List<MembershipCard9> getAllCards() {
        return cardDAO.getAllCards();
    }
}
