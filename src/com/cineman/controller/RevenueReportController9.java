package com.cineman.controller;

import com.cineman.dao.CustomerDAO9;
import com.cineman.dao.TransactionDAO9;
import com.cineman.model.dto.CustomerRevenueStatistic9;
import com.cineman.model.dto.ReportFilter9;
import com.cineman.model.entity.Customer9;
import com.cineman.model.entity.Transaction9;
import com.cineman.model.entity.UserAccount9;
import com.cineman.service.DataProtectionService9;

import java.util.ArrayList;
import java.util.List;

public class RevenueReportController9 {
    private final TransactionDAO9 transactionDAO;
    private final CustomerDAO9 customerDAO;

    public RevenueReportController9() {
        this.transactionDAO = new TransactionDAO9();
        this.customerDAO = new CustomerDAO9();
    }

    public RevenueReportController9(TransactionDAO9 transactionDAO, CustomerDAO9 customerDAO) {
        this.transactionDAO = transactionDAO;
        this.customerDAO = customerDAO;
    }

    /**
     * Module 2: Kiểm tra tính hợp lệ của khoảng ngày (BR1)
     */
    public boolean validateFilter(ReportFilter9 filter) {
        return filter != null && filter.isValid();
    }

    /**
     * Module 2 (Có lớp trung gian bảo vệ): Kiểm tra quyền MANAGER trước khi truy xuất thống kê doanh thu
     */
    public List<CustomerRevenueStatistic9> getCustomerRevenue(UserAccount9 currentUser, ReportFilter9 filter) {
        DataProtectionService9.verifyRole(currentUser, "MANAGER", "View Customer Revenue Statistics");
        return getCustomerRevenue(filter);
    }

    public List<CustomerRevenueStatistic9> getCustomerRevenue(ReportFilter9 filter) {
        if (!validateFilter(filter)) {
            return new ArrayList<>();
        }
        return transactionDAO.sumRevenueByCustomer(filter.getStartDate(), filter.getEndDate());
    }

    /**
     * Module 2 (Có lớp trung gian bảo vệ): Lấy thông tin chi tiết khách hàng và che giấu dữ liệu nhạy cảm
     */
    public Customer9 getCustomerProfile(UserAccount9 currentUser, int customerId) {
        DataProtectionService9.verifyRole(currentUser, "MANAGER", "View Customer Profile (ID=" + customerId + ")");
        return DataProtectionService9.maskSensitiveAccountData(customerDAO.getById(customerId));
    }

    public Customer9 getCustomerProfile(int customerId) {
        return DataProtectionService9.maskSensitiveAccountData(customerDAO.getById(customerId));
    }

    public List<Transaction9> getCustomerTransactions(UserAccount9 currentUser, int customerId, ReportFilter9 filter) {
        DataProtectionService9.verifyRole(currentUser, "MANAGER", "View Customer Transactions (ID=" + customerId + ")");
        return getCustomerTransactions(customerId, filter);
    }

    public List<Transaction9> getCustomerTransactions(int customerId, ReportFilter9 filter) {
        if (!validateFilter(filter)) {
            return new ArrayList<>();
        }
        return transactionDAO.getByCustomerAndDate(customerId, filter.getStartDate(), filter.getEndDate());
    }
}
