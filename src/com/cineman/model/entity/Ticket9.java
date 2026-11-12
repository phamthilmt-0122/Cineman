package com.cineman.model.entity;

import java.sql.Timestamp;

public class Ticket9 {
    private int ticketId;
    private int transactionId;
    private int screeningId;
    private int seatId;
    private Timestamp bookingTime;
    private double ticketPrice;
    private String ticketStatus; // 'BOOKED', 'USED', 'CANCELLED'

    // Transient presentation fields
    private String seatCode;
    private String movieTitle;
    private String roomName;

    public Ticket9() {}

    public Ticket9(int ticketId, int transactionId, int screeningId, int seatId,
                   Timestamp bookingTime, double ticketPrice, String ticketStatus) {
        this.ticketId = ticketId;
        this.transactionId = transactionId;
        this.screeningId = screeningId;
        this.seatId = seatId;
        this.bookingTime = bookingTime;
        this.ticketPrice = ticketPrice;
        this.ticketStatus = ticketStatus;
    }

    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getScreeningId() { return screeningId; }
    public void setScreeningId(int screeningId) { this.screeningId = screeningId; }

    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }

    public Timestamp getBookingTime() { return bookingTime; }
    public void setBookingTime(Timestamp bookingTime) { this.bookingTime = bookingTime; }

    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }

    public String getTicketStatus() { return ticketStatus; }
    public void setTicketStatus(String ticketStatus) { this.ticketStatus = ticketStatus; }

    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
}
