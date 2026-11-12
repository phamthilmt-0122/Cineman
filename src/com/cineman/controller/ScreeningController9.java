package com.cineman.controller;

import com.cineman.dao.ScreeningDAO9;
import com.cineman.model.entity.Screening9;

import java.sql.Timestamp;
import java.util.List;

public class ScreeningController9 {
    private final ScreeningDAO9 screeningDAO;

    public ScreeningController9() {
        this.screeningDAO = new ScreeningDAO9();
    }

    public List<Screening9> getAllScreenings() {
        return screeningDAO.getAll();
    }

    public Screening9 getScreeningById(int screeningId) {
        return screeningDAO.getById(screeningId);
    }

    public boolean addScreening(int movieId, int roomId, Timestamp startTime, Timestamp endTime) {
        if (movieId <= 0 || roomId <= 0 || startTime == null || endTime == null || !startTime.before(endTime)) {
            return false;
        }
        Screening9 screening = new Screening9(0, movieId, roomId, startTime, endTime, "ACTIVE");
        return screeningDAO.insert(screening);
    }

    public boolean deleteScreening(int screeningId) {
        return screeningDAO.delete(screeningId);
    }
}
