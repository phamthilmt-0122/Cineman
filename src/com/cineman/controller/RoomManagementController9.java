package com.cineman.controller;

import com.cineman.dao.ScreeningRoomDAO9;
import com.cineman.model.entity.ScreeningRoom9;
import com.cineman.model.entity.Seat9;

import java.util.List;

public class RoomManagementController9 {
    private final ScreeningRoomDAO9 roomDAO;

    public RoomManagementController9() {
        this.roomDAO = new ScreeningRoomDAO9();
    }

    public List<ScreeningRoom9> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    public ScreeningRoom9 getRoomById(int roomId) {
        return roomDAO.getRoomById(roomId);
    }

    public List<Seat9> getSeatsByRoomId(int roomId) {
        return roomDAO.getSeatsByRoomId(roomId);
    }

    public boolean addRoom(String roomName, int capacity, String roomStatus) {
        if (roomName == null || roomName.trim().isEmpty() || capacity <= 0) {
            return false;
        }
        ScreeningRoom9 room = new ScreeningRoom9(0, 1, roomName.trim(), capacity, roomStatus);
        return roomDAO.insertRoom(room);
    }

    public boolean updateRoom(int roomId, String roomName, int capacity, String roomStatus) {
        if (roomId <= 0 || roomName == null || roomName.trim().isEmpty() || capacity <= 0) {
            return false;
        }
        ScreeningRoom9 room = new ScreeningRoom9(roomId, 1, roomName.trim(), capacity, roomStatus);
        return roomDAO.updateRoom(room);
    }

    public boolean deleteRoom(int roomId) {
        return roomDAO.deleteRoom(roomId);
    }
}
