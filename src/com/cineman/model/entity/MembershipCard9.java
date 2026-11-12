package com.cineman.model.entity;

import java.sql.Date;

public class MembershipCard9 {
    private int cardId;
    private int customerId;
    private String cardCode;
    private Date issuedDate;
    private Date expiryDate;
    private String cardStatus; // 'ACTIVE', 'EXPIRED', 'LOCKED'

    public MembershipCard9() {}

    public MembershipCard9(int cardId, int customerId, String cardCode, Date issuedDate, Date expiryDate, String cardStatus) {
        this.cardId = cardId;
        this.customerId = customerId;
        this.cardCode = cardCode;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
        this.cardStatus = cardStatus;
    }

    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCardCode() { return cardCode; }
    public void setCardCode(String cardCode) { this.cardCode = cardCode; }

    public Date getIssuedDate() { return issuedDate; }
    public void setIssuedDate(Date issuedDate) { this.issuedDate = issuedDate; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public String getCardStatus() { return cardStatus; }
    public void setCardStatus(String cardStatus) { this.cardStatus = cardStatus; }
}
