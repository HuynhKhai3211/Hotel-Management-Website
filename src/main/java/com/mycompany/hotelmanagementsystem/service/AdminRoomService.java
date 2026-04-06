package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.model.RoomType;
import com.mycompany.hotelmanagementsystem.dao.BookingRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomImageRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomTypeRepository;
import java.util.List;

public class AdminRoomService {
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingRepository bookingRepository;
    private final RoomImageRepository roomImageRepository;

    public AdminRoomService() {
        this.roomRepository = new RoomRepository();
        this.roomTypeRepository = new RoomTypeRepository();
        this.bookingRepository = new BookingRepository();
        this.roomImageRepository = new RoomImageRepository();
    }

    // Room methods
    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAllWithRoomType();
        for (Room room : rooms) {
            if (room.getRoomType() != null) {
                room.getRoomType().setImages(
                    roomImageRepository.findByTypeId(room.getRoomType().getTypeId())
                );
            }
        }
        return rooms;
    }

    public Room getRoomById(int roomId) {
        Room room = roomRepository.findWithRoomType(roomId);
        if (room != null) {
            room.setImages(roomImageRepository.findByRoomId(roomId));
            if (room.getRoomType() != null) {
                room.getRoomType().setImages(roomImageRepository.findByTypeId(room.getRoomType().getTypeId()));
            }
        }
        return room;
    }

    public boolean createRoom(Room room) {
        return roomRepository.insert(room) > 0;
    }

    public Room findRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber);
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

    public int createRoomTypeGetId(RoomType roomType) {
        return roomTypeRepository.insert(roomType);
    }

    public boolean updateRoomType(RoomType roomType) {
        return roomTypeRepository.update(roomType) > 0;
    }

    public boolean deleteRoomType(int typeId) {
        return roomTypeRepository.delete(typeId) > 0;
    }

    public List<Booking> getRoomHistory(int roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    public Booking getCurrentBookingForRoom(int roomId) {
        return bookingRepository.findCurrentBookingForRoom(roomId);
    }

    public boolean addRoomImage(int roomId, String imageUrl) {
        return roomImageRepository.insertForRoom(roomId, imageUrl) > 0;
    }

    public boolean deleteRoomImage(int imageId) {
        return roomImageRepository.deleteById(imageId) > 0;
    }
}
