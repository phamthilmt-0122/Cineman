package com.cineman.model.dto;

import java.sql.Date;
import java.time.LocalDate;

public class ReportFilter9 {
    private Date startDate;
    private Date endDate;

    public ReportFilter9() {}

    public ReportFilter9(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ReportFilter9(String startStr, String endStr) {
        try {
            if (startStr != null && !startStr.trim().isEmpty()) {
                this.startDate = Date.valueOf(startStr.trim());
            }
            if (endStr != null && !endStr.trim().isEmpty()) {
                this.endDate = Date.valueOf(endStr.trim());
            }
        } catch (Exception e) {
            // invalid format
        }
    }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public boolean isValid() {
        if (startDate == null || endDate == null) {
            return false;
        }
        return !startDate.after(endDate);
    }
}
