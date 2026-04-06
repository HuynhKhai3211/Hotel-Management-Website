package com.mycompany.hotelmanagementsystem.service;

<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.dao.AmenityRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomTypeRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomImageRepository;
import com.mycompany.hotelmanagementsystem.model.RoomType;
import com.mycompany.hotelmanagementsystem.model.Room;
=======
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.model.RoomType;
import com.mycompany.hotelmanagementsystem.dao.RoomTypeRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
import com.mycompany.hotelmanagementsystem.dao.AmenityRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomImageRepository;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import java.time.LocalDateTime;
import java.util.List;

public class RoomService {
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final AmenityRepository amenityRepository;

    public RoomService() {
        this.roomTypeRepository = new RoomTypeRepository();
        this.roomRepository = new RoomRepository();
        this.roomImageRepository = new RoomImageRepository();
        this.amenityRepository = new AmenityRepository();
    }

    public List<RoomType> getAllRoomTypes() {
        List<RoomType> types = roomTypeRepository.findAll();
        types.forEach(this::loadRoomTypeDetails);
        return types;
    }

    // Dùng inline params thay cho RoomSearchRequest
    public List<RoomType> searchRoomTypes(Integer minPrice, Integer maxPrice, Integer capacity, Integer typeId) {
        List<RoomType> types = roomTypeRepository.findByFilters(minPrice, maxPrice, capacity, typeId);
        types.forEach(this::loadRoomTypeDetails);
        return types;
    }

    public RoomType getRoomTypeById(int typeId) {
        RoomType type = roomTypeRepository.findById(typeId);
        if (type != null) {
            loadRoomTypeDetails(type);
        }
        return type;
    }

    private void loadRoomTypeDetails(RoomType type) {
        type.setImages(roomImageRepository.findByTypeId(type.getTypeId()));
        type.setAmenities(amenityRepository.findByTypeId(type.getTypeId()));
    }

    public int getAvailableRoomCount(int typeId) {
        return roomRepository.countAvailableByTypeId(typeId);
    }

    public List<Room> getAvailableRooms(int typeId, LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkIn == null || checkOut == null) {
            return roomRepository.findAvailableByTypeId(typeId);
        }
        return roomRepository.findAvailableForDates(typeId, checkIn, checkOut);
    }

    public Room getRoomById(int roomId) {
        return roomRepository.findWithRoomType(roomId);
    }

    public List<RoomType> getFeaturedRoomTypes(int limit) {
        List<RoomType> types = roomTypeRepository.findAll();
        types.forEach(this::loadRoomTypeDetails);
        return types.size() > limit ? types.subList(0, limit) : types;
    }
}
