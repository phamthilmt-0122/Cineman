package com.cineman.model.entity;

import java.sql.Timestamp;

public class Screening9 {
    private int screeningId;
    private int movieId;
    private int roomId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String screeningStatus; // 'ACTIVE', 'CANCELLED', 'FINISHED'

    // Transient fields for easy presentation
    private String movieTitle;
    private String roomName;

    public Screening9() {}

    public Screening9(int screeningId, int movieId, int roomId, Timestamp startTime, Timestamp endTime, String screeningStatus) {
        this.screeningId = screeningId;
        this.movieId = movieId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.screeningStatus = screeningStatus;
    }

    public int getScreeningId() { return screeningId; }
    public void setScreeningId(int screeningId) { this.screeningId = screeningId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }

    public String getScreeningStatus() { return screeningStatus; }
    public void setScreeningStatus(String screeningStatus) { this.screeningStatus = screeningStatus; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
}
