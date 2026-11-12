package com.cineman.controller;

import com.cineman.dao.ScreeningDAO9;
import com.cineman.dao.ScreeningRoomDAO9;
import com.cineman.dao.TicketDAO9;
import com.cineman.dao.TransactionDAO9;
import com.cineman.model.entity.Screening9;
import com.cineman.model.entity.Seat9;
import com.cineman.model.entity.Ticket9;
import com.cineman.model.entity.Transaction9;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TicketBookingController9 {
    private final TransactionDAO9 transactionDAO;
    private final TicketDAO9 ticketDAO;
    private final ScreeningDAO9 screeningDAO;
    private final ScreeningRoomDAO9 roomDAO;

    public TicketBookingController9() {
        this.transactionDAO = new TransactionDAO9();
        this.ticketDAO = new TicketDAO9();
        this.screeningDAO = new ScreeningDAO9();
        this.roomDAO = new ScreeningRoomDAO9();
    }

    public List<Seat9> getAvailableSeatsForScreening(int screeningId) {
        Screening9 screening = screeningDAO.getById(screeningId);
        if (screening == null) return new ArrayList<>();

        List<Seat9> allSeats = roomDAO.getSeatsByRoomId(screening.getRoomId());
        Set<Integer> occupiedSeatIds = ticketDAO.getOccupiedSeatIds(screeningId);

        for (Seat9 seat : allSeats) {
            if (occupiedSeatIds.contains(seat.getSeatId())) {
                seat.setSeatStatus("BOOKED");
            }
        }
        return allSeats;
    }

    /**
     * Mua vé: Áp dụng cho cả Bán vé tại quầy (salespersonId != null) và Mua trực tuyến (salespersonId == null)
     */
    public boolean bookTickets(int customerId, Integer salespersonId, int screeningId, List<Integer> seatIds, String paymentMethod, double pricePerTicket) {
        if (seatIds == null || seatIds.isEmpty() || screeningId <= 0 || customerId <= 0) {
            return false;
        }

        Set<Integer> occupied = ticketDAO.getOccupiedSeatIds(screeningId);
        for (int seatId : seatIds) {
            if (occupied.contains(seatId)) {
                return false; // Ghế đã được đặt
            }
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        double totalAmount = seatIds.size() * pricePerTicket;

        Transaction9 trans = new Transaction9(0, customerId, salespersonId, now, totalAmount, paymentMethod, "COMPLETED");
        List<Ticket9> tickets = new ArrayList<>();
        for (int seatId : seatIds) {
            tickets.add(new Ticket9(0, 0, screeningId, seatId, now, pricePerTicket, "BOOKED"));
        }

        return transactionDAO.createBookingTransaction(trans, tickets);
    }

    public List<Ticket9> getTicketsByTransaction(int transactionId) {
        return ticketDAO.getTicketsByTransactionId(transactionId);
    }
}
