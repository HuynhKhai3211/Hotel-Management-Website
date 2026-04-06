package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.RoomStatus;
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StaffRoomService {
    private final RoomRepository roomRepository;

    public StaffRoomService() {
        this.roomRepository = new RoomRepository();
    }

    public List<Room> getAllRoomsWithType() {
        return roomRepository.findAllWithRoomType();
    }

    public Map<String, List<Room>> getRoomsGroupedByFloor() {
        List<Room> rooms = roomRepository.findAllWithRoomType();
        return rooms.stream()
            .collect(Collectors.groupingBy(room -> {
                String roomNumber = room.getRoomNumber();
                if (roomNumber != null && roomNumber.length() >= 1) {
                    return "Tầng " + roomNumber.charAt(0);
                }
                return "Khác";
            }));
    }

    public Room getRoomDetail(int roomId) {
        return roomRepository.findWithRoomType(roomId);
    }

    public int countByStatus(String status) {
        return roomRepository.countByStatus(status);
    }

    public boolean updateRoomStatus(int roomId, String status) {
        return roomRepository.updateStatus(roomId, status) > 0;
    }

    public boolean markAsAvailable(int roomId) {
        return updateRoomStatus(roomId, RoomStatus.AVAILABLE);
    }

    public boolean markAsOccupied(int roomId) {
        return updateRoomStatus(roomId, RoomStatus.OCCUPIED);
    }

    public boolean markAsCleaning(int roomId) {
        return updateRoomStatus(roomId, RoomStatus.CLEANING);
    }
}
