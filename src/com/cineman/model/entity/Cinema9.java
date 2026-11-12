package com.cineman.model.entity;

public class Cinema9 {
    private int cinemaId;
    private String cinemaName;
    private String address;

    public Cinema9() {}

    public Cinema9(int cinemaId, String cinemaName, String address) {
        this.cinemaId = cinemaId;
        this.cinemaName = cinemaName;
        this.address = address;
    }

    public int getCinemaId() { return cinemaId; }
    public void setCinemaId(int cinemaId) { this.cinemaId = cinemaId; }

    public String getCinemaName() { return cinemaName; }
    public void setCinemaName(String cinemaName) { this.cinemaName = cinemaName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
