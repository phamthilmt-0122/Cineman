package com.cineman.model.entity;

public class ScreeningRoom9 {
    private int roomId;
    private int cinemaId;
    private String roomName;
    private int capacity;
    private String roomStatus; // 'AVAILABLE', 'MAINTENANCE'

    public ScreeningRoom9() {}

    public ScreeningRoom9(int roomId, int cinemaId, String roomName, int capacity, String roomStatus) {
        this.roomId = roomId;
        this.cinemaId = cinemaId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.roomStatus = roomStatus;
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getCinemaId() { return cinemaId; }
    public void setCinemaId(int cinemaId) { this.cinemaId = cinemaId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getRoomStatus() { return roomStatus; }
    public void setRoomStatus(String roomStatus) { this.roomStatus = roomStatus; }
}
