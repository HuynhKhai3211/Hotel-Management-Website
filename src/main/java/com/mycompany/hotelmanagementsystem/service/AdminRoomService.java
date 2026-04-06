package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.model.RoomType;
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomTypeRepository;
import java.util.List;

public class AdminRoomService {
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public AdminRoomService() {
        this.roomRepository = new RoomRepository();
        this.roomTypeRepository = new RoomTypeRepository();
    }

    // Room methods
    public List<Room> getAllRooms() {
        return roomRepository.findAllWithRoomType();
    }

    public Room getRoomById(int roomId) {
        return roomRepository.findWithRoomType(roomId);
    }

    public boolean createRoom(Room room) {
        return roomRepository.insert(room) > 0;
    }

    public boolean updateRoom(Room room) {
        return roomRepository.update(room) > 0;
    }

    public boolean deleteRoom(int roomId) {
        return roomRepository.delete(roomId) > 0;
    }

    // RoomType methods
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    public RoomType getRoomTypeById(int typeId) {
        return roomTypeRepository.findById(typeId);
    }

    public boolean createRoomType(RoomType roomType) {
        return roomTypeRepository.insert(roomType) > 0;
    }

    public boolean updateRoomType(RoomType roomType) {
        return roomTypeRepository.update(roomType) > 0;
    }

    public boolean deleteRoomType(int typeId) {
        return roomTypeRepository.delete(typeId) > 0;
    }
}
