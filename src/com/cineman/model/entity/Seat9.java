package com.cineman.model.entity;

public class Seat9 {
    private int seatId;
    private int roomId;
    private String rowLabel;
    private int seatNumber;
    private String seatType;   // 'STANDARD', 'VIP', 'COUPLE'
    private String seatStatus; // 'AVAILABLE', 'RESERVED', 'BROKEN'

    public Seat9() {}

    public Seat9(int seatId, int roomId, String rowLabel, int seatNumber, String seatType, String seatStatus) {
        this.seatId = seatId;
        this.roomId = roomId;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.seatStatus = seatStatus;
    }

    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }

    public String getSeatStatus() { return seatStatus; }
    public void setSeatStatus(String seatStatus) { this.seatStatus = seatStatus; }

    public String getSeatCode() {
        return rowLabel + seatNumber;
    }
}
