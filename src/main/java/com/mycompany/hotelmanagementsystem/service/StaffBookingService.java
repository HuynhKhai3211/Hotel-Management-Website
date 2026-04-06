package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.constant.RoomStatus;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Occupant;
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.dao.BookingRepository;
import com.mycompany.hotelmanagementsystem.dao.OccupantRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class StaffBookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final OccupantRepository occupantRepository;

    public StaffBookingService() {
        this.bookingRepository = new BookingRepository();
        this.roomRepository = new RoomRepository();
        this.occupantRepository = new OccupantRepository();
    }

    public List<Booking> getActiveBookings() {
        return bookingRepository.findByStatuses(
            Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN));
    }

    public List<Booking> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingDetail(int bookingId) {
        return bookingRepository.findByIdWithDetails(bookingId);
    }

    public int countByStatus(String status) {
        return bookingRepository.countByStatus(status);
    }

    // UC-19.1: Assign Room
    public boolean assignRoom(int bookingId, int roomId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) return false;

        // Update booking with new room
        int updated = bookingRepository.updateRoomId(bookingId, roomId);
        if (updated <= 0) return false;

        // Update booking status to CheckedIn
        bookingRepository.updateStatus(bookingId, BookingStatus.CHECKED_IN);

        // Set check-in actual time
        bookingRepository.updateCheckInActual(bookingId, LocalDateTime.now());

        // Update room status to Occupied
        roomRepository.updateStatus(roomId, RoomStatus.OCCUPIED);

        return true;
    }

    // UC-19.4: Manage Occupants
    public List<Occupant> getOccupants(int bookingId) {
        return occupantRepository.findByBookingId(bookingId);
    }

    public boolean saveOccupants(int bookingId, List<Occupant> occupants) {
        // Delete existing occupants
        occupantRepository.deleteByBookingId(bookingId);

        // Insert new occupants
        for (Occupant occupant : occupants) {
            occupant.setBookingId(bookingId);
            if (occupant.getFullName() != null && !occupant.getFullName().trim().isEmpty()) {
                occupantRepository.insert(occupant);
            }
        }
        return true;
    }

    // UC-19.5: Check-out
    public boolean processCheckout(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) return false;

        // Update booking status
        bookingRepository.updateStatus(bookingId, BookingStatus.CHECKED_OUT);

        // Set check-out actual time
        bookingRepository.updateCheckOutActual(bookingId, LocalDateTime.now());

        // Update room status to Cleaning
        roomRepository.updateStatus(booking.getRoomId(), RoomStatus.CLEANING);

        return true;
    }

    // Get available rooms for assignment
    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE);
    }

    public List<Room> getAvailableRoomsForBooking(int bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId);
        if (booking == null) return List.of();

        // Get rooms available for the booking dates and room type
        return roomRepository.findAvailableForDates(
            booking.getRoom().getRoomType().getTypeId(),
            booking.getCheckInExpected(),
            booking.getCheckOutExpected());
    }
}
