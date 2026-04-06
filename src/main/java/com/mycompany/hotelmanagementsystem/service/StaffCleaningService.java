package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.RoomStatus;
import com.mycompany.hotelmanagementsystem.model.Room;
<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
=======
import com.mycompany.hotelmanagementsystem.model.RoomCleaningInfo;
import com.mycompany.hotelmanagementsystem.model.ServiceRequest;
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
import com.mycompany.hotelmanagementsystem.dao.ServiceRequestRepository;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import java.util.List;

public class StaffCleaningService {
    private final RoomRepository roomRepository;
<<<<<<< HEAD

    public StaffCleaningService() {
        this.roomRepository = new RoomRepository();
    }

    // UC-20.1: Get rooms that need cleaning
    public List<Room> getRoomsNeedingCleaning() {
        return roomRepository.findByStatus(RoomStatus.CLEANING);
=======
    private final ServiceRequestRepository serviceRequestRepository;

    public StaffCleaningService() {
        this.roomRepository = new RoomRepository();
        this.serviceRequestRepository = new ServiceRequestRepository();
    }

    // UC-20.1: Get rooms that need cleaning with their cleaning request info
    public List<RoomCleaningInfo> getRoomsNeedingCleaning() {
        List<Room> rooms = roomRepository.findByStatus(RoomStatus.CLEANING);
        System.out.println("[DEBUG] getRoomsNeedingCleaning: found " + rooms.size() + " rooms with CLEANING status");
        for (Room r : rooms) {
            System.out.println("[DEBUG]   Room: " + r.getRoomNumber() + ", status=" + r.getStatus());
        }
        return rooms.stream()
                .map(room -> {
                    ServiceRequest cleaningRequest = serviceRequestRepository.findPendingCleaningByRoomNumber(room.getRoomNumber());
                    return new RoomCleaningInfo(room, cleaningRequest);
                })
                .toList();
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    }

    public int countRoomsNeedingCleaning() {
        return roomRepository.countByStatus(RoomStatus.CLEANING);
    }

<<<<<<< HEAD
    // UC-20.3: Mark room as cleaned
    public boolean markRoomAsClean(int roomId) {
        return roomRepository.updateStatus(roomId, RoomStatus.AVAILABLE) > 0;
=======
    // UC-20.2: Staff accepts cleaning request
    public boolean acceptCleaningRequest(int roomId, int staffId) {
        Room room = roomRepository.findById(roomId);
        if (room == null) return false;

        ServiceRequest cleaningRequest = serviceRequestRepository.findPendingCleaningByRoomNumber(room.getRoomNumber());
        if (cleaningRequest == null) return false;

        return serviceRequestRepository.assignStaff(cleaningRequest.getRequestId(), staffId) > 0;
    }

    // UC-20.3: Mark room as cleaned and complete the associated cleaning request
    public boolean markRoomAsClean(int roomId) {
        // First update room status to Available
        boolean roomUpdated = roomRepository.updateStatus(roomId, RoomStatus.AVAILABLE) > 0;

        // Find the room to get room number
        Room room = roomRepository.findById(roomId);
        if (room != null) {
            // Find and complete the pending/in-progress cleaning request for this room
            ServiceRequest cleaningRequest = serviceRequestRepository.findPendingCleaningByRoomNumber(room.getRoomNumber());
            if (cleaningRequest != null) {
                // If request is Pending, assign to current staff before completing
                if (cleaningRequest.getStaffId() == null) {
                    serviceRequestRepository.assignStaff(cleaningRequest.getRequestId(), 0); // system
                }
                serviceRequestRepository.complete(cleaningRequest.getRequestId(), "Da duoc don phong");
            }
        }

        return roomUpdated;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    }

    public Room getRoomDetail(int roomId) {
        return roomRepository.findWithRoomType(roomId);
    }
}
