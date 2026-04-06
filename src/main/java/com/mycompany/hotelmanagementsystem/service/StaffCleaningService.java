package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.RoomStatus;
import com.mycompany.hotelmanagementsystem.entity.Room;
import com.mycompany.hotelmanagementsystem.dal.RoomRepository;
import java.util.List;

public class StaffCleaningService {
    private final RoomRepository roomRepository;

    public StaffCleaningService() {
        this.roomRepository = new RoomRepository();
    }

    // UC-20.1: Get rooms that need cleaning
    public List<Room> getRoomsNeedingCleaning() {
        return roomRepository.findByStatus(RoomStatus.CLEANING);
    }

    public int countRoomsNeedingCleaning() {
        return roomRepository.countByStatus(RoomStatus.CLEANING);
    }

    // UC-20.3: Mark room as cleaned
    public boolean markRoomAsClean(int roomId) {
        return roomRepository.updateStatus(roomId, RoomStatus.AVAILABLE) > 0;
    }

    public Room getRoomDetail(int roomId) {
        return roomRepository.findWithRoomType(roomId);
    }
}
