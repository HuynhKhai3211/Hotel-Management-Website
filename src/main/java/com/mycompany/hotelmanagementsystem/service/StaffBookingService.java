package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.constant.PaymentType;
import com.mycompany.hotelmanagementsystem.constant.RoleConstant;
import com.mycompany.hotelmanagementsystem.constant.RoomStatus;
import com.mycompany.hotelmanagementsystem.constant.ServiceRequestStatusConstant;
import com.mycompany.hotelmanagementsystem.constant.ServiceTypeConstant;
import com.mycompany.hotelmanagementsystem.entity.Account;
import com.mycompany.hotelmanagementsystem.entity.Booking;
import com.mycompany.hotelmanagementsystem.entity.BookingExtension;
import com.mycompany.hotelmanagementsystem.entity.Invoice;
import com.mycompany.hotelmanagementsystem.entity.Occupant;
import com.mycompany.hotelmanagementsystem.entity.Room;
import com.mycompany.hotelmanagementsystem.entity.RoomType;
import com.mycompany.hotelmanagementsystem.entity.ServiceRequest;
import com.mycompany.hotelmanagementsystem.dal.AccountRepository;
import com.mycompany.hotelmanagementsystem.dal.BookingRepository;
import com.mycompany.hotelmanagementsystem.dal.BookingExtensionRepository;
import com.mycompany.hotelmanagementsystem.dal.CustomerRepository;
import com.mycompany.hotelmanagementsystem.dal.InvoiceRepository;
import com.mycompany.hotelmanagementsystem.dal.OccupantRepository;
import com.mycompany.hotelmanagementsystem.dal.PaymentRepository;
import com.mycompany.hotelmanagementsystem.dal.RoomRepository;
import com.mycompany.hotelmanagementsystem.dal.RoomTypeRepository;
import com.mycompany.hotelmanagementsystem.dal.ServiceRequestRepository;
import com.mycompany.hotelmanagementsystem.util.BookingResult;
import com.mycompany.hotelmanagementsystem.util.WalkInCustomerResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class StaffBookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final OccupantRepository occupantRepository;
    private final BookingExtensionRepository extensionRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public StaffBookingService() {
        this.bookingRepository = new BookingRepository();
        this.roomRepository = new RoomRepository();
        this.occupantRepository = new OccupantRepository();
        this.extensionRepository = new BookingExtensionRepository();
        this.invoiceRepository = new InvoiceRepository();
        this.paymentRepository = new PaymentRepository();
        this.accountRepository = new AccountRepository();
        this.customerRepository = new CustomerRepository();
        this.roomTypeRepository = new RoomTypeRepository();
        this.serviceRequestRepository = new ServiceRequestRepository();
    }

    public List<Booking> getActiveBookings() {
        // Bao gồm Pending để staff thấy booking mới từ customer (chưa assign phòng)
        return bookingRepository.findByStatuses(
            Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN));
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

    // UC-19.1: Assign Room (= staff verify / check-in)
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

        // Update room status to Cleaning (only if room assigned)
        if (booking.getRoomId() != null) {
            roomRepository.updateStatus(booking.getRoomId(), RoomStatus.CLEANING);

            // Auto-create cleaning service request
            try {
                Room room = roomRepository.findById(booking.getRoomId());
                String roomNumber = (room != null) ? room.getRoomNumber() : String.valueOf(booking.getRoomId());

                ServiceRequest cleaningRequest = new ServiceRequest();
                cleaningRequest.setBookingId(bookingId);
                cleaningRequest.setServiceType(ServiceTypeConstant.CLEANING);
                cleaningRequest.setStatus(ServiceRequestStatusConstant.PENDING);
                cleaningRequest.setDescription("Don phong sau khi khach tra phong (tu dong tao khi checkout)");
                cleaningRequest.setPriority("Normal");
                cleaningRequest.setRoomNumber(roomNumber);
                serviceRequestRepository.insert(cleaningRequest);
            } catch (Exception e) {
                // Log but don't fail checkout if service request creation fails
                System.err.println("Warning: Failed to create cleaning service request for booking " + bookingId + ": " + e.getMessage());
            }
        }

        return true;
    }

    // Check if booking needs payment at checkout
    // Deposit: remaining = total - deposit > 0
    // Full/Standard: check if any successful payment exists for Booking invoice
    public boolean needsCheckoutPayment(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) return false;

        if (PaymentType.DEPOSIT.equals(booking.getPaymentType())) {
            BigDecimal deposit = booking.getDepositAmount() != null ? booking.getDepositAmount() : BigDecimal.ZERO;
            return booking.getTotalPrice().compareTo(deposit) > 0;
        }

        // Full/Standard: check if Booking invoice has been paid
        Invoice bookingInvoice = invoiceRepository.findByBookingId(bookingId);
        if (bookingInvoice == null) {
            // No invoice exists -> never paid (Standard room)
            return true;
        }
        return !paymentRepository.hasSuccessfulPayment(bookingInvoice.getInvoiceId());
    }

    // Get the amount to collect at checkout
    public BigDecimal getCheckoutPaymentAmount(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) return BigDecimal.ZERO;

        if (PaymentType.DEPOSIT.equals(booking.getPaymentType())) {
            BigDecimal deposit = booking.getDepositAmount() != null ? booking.getDepositAmount() : BigDecimal.ZERO;
            BigDecimal remaining = booking.getTotalPrice().subtract(deposit);
            return remaining.compareTo(BigDecimal.ZERO) > 0 ? remaining : BigDecimal.ZERO;
        }

        // Full/Standard: collect full totalPrice
        return booking.getTotalPrice();
    }

    // Get extensions for a booking
    public List<BookingExtension> getExtensions(int bookingId) {
        return extensionRepository.findByBookingId(bookingId);
    }

    // Get available rooms for assignment
    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE);
    }

    public List<Room> getAvailableRoomsForBooking(int bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId);
        if (booking == null) return List.of();

        // Exclude this booking from conflict check (its own room should still be available)
        return roomRepository.findAvailableForDates(
            booking.getTypeId(),
            booking.getCheckInExpected(),
            booking.getCheckOutExpected(),
            bookingId);
    }

    /**
     * Walk-in: find or create customer account.
     * Priority: phone > email > create new.
     * @param skipEmailCheck if true, ignore email conflict and create account without email
     * @return WalkInCustomerResult with accountId and status
     */
    public WalkInCustomerResult findOrCreateWalkInCustomer(String fullName, String phone,
            String email, boolean skipEmailCheck) {
        // 1. Find by phone (primary identifier)
        Account existing = accountRepository.findByPhone(phone.trim());
        if (existing != null) {
            return new WalkInCustomerResult(existing.getAccountId(), "FOUND_BY_PHONE",
                    existing.getFullName(), existing.getPhone());
        }

        // 2. Find by email if provided
        if (!skipEmailCheck && email != null && !email.trim().isEmpty()) {
            existing = accountRepository.findByEmail(email.trim());
            if (existing != null) {
                // Email exists but phone is different - need staff confirmation
                return new WalkInCustomerResult(existing.getAccountId(), "FOUND_BY_EMAIL",
                        existing.getFullName(), existing.getPhone());
            }
        }

        // 3. Create new account
        Account account = new Account();
        account.setFullName(fullName.trim());
        account.setPhone(phone.trim());

        // Generate random password for walk-in customer
        String rawPassword = UUID.randomUUID().toString().substring(0, 8);
        account.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
        account.setAddress("");
        account.setRoleId(RoleConstant.CUSTOMER);
        account.setActive(true);

        // Email: use provided or generate placeholder (DB NOT NULL + UNIQUE)
        if (skipEmailCheck || email == null || email.trim().isEmpty()) {
            account.setEmail("walkin_" + phone.trim() + "_" + System.currentTimeMillis() + "@walkin.local");
        } else {
            account.setEmail(email.trim());
        }

        int accountId = accountRepository.insert(account);
        if (accountId <= 0) {
            throw new RuntimeException("Failed to create walk-in customer account");
        }
        customerRepository.insert(accountId);

        // Return with generated password so staff can inform customer
        WalkInCustomerResult result = new WalkInCustomerResult(accountId, "CREATED", fullName.trim(), phone.trim());
        result.setGeneratedPassword(rawPassword);
        result.setEmail(account.getEmail());
        return result;
    }

    // Walk-in booking: create booking with CheckedIn status
    public BookingResult createWalkInBooking(int customerId, int typeId,
            LocalDateTime checkIn, LocalDateTime checkOut,
            BigDecimal totalPrice, String note, List<Occupant> occupants) {
        try {
            // Find available room for the dates
            List<Room> availableRooms = roomRepository.findAvailableForDates(typeId, checkIn, checkOut);
            if (availableRooms.isEmpty()) {
                return BookingResult.failure("Khong con phong trong cho loai phong nay");
            }

            Room room = availableRooms.get(0);

            // Create booking with CheckedIn status
            Booking booking = new Booking();
            booking.setCustomerId(customerId);
            booking.setRoomId(room.getRoomId());
            booking.setTypeId(typeId);
            booking.setCheckInExpected(checkIn);
            booking.setCheckOutExpected(checkOut);
            booking.setTotalPrice(totalPrice);
            booking.setStatus(BookingStatus.CHECKED_IN);
            booking.setPaymentType(PaymentType.FULL);
            booking.setDepositAmount(totalPrice);
            booking.setNote(note);

            int bookingId = bookingRepository.insert(booking);
            if (bookingId <= 0) {
                return BookingResult.failure("Khong the tao don dat phong");
            }
            booking.setBookingId(bookingId);

            // Set check-in actual time
            bookingRepository.updateCheckInActual(bookingId, LocalDateTime.now());

            // Set room to Occupied
            roomRepository.updateStatus(room.getRoomId(), RoomStatus.OCCUPIED);

            // Save occupants
            if (occupants != null) {
                for (Occupant occ : occupants) {
                    if (occ.getFullName() != null && !occ.getFullName().trim().isEmpty()) {
                        occ.setBookingId(bookingId);
                        occupantRepository.insert(occ);
                    }
                }
            }

            return BookingResult.success("Dat phong tai quay thanh cong", booking);
        } catch (Exception e) {
            return BookingResult.failure("Loi khi tao booking: " + e.getMessage());
        }
    }

    // Get all room types for walk-in selection
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    // Get room type by ID
    public RoomType getRoomTypeById(int typeId) {
        return roomTypeRepository.findById(typeId);
    }

    // Find available rooms for dates (for walk-in step 2)
    public List<Room> findAvailableRoomsForDates(int typeId, LocalDateTime checkIn, LocalDateTime checkOut) {
        return roomRepository.findAvailableForDates(typeId, checkIn, checkOut);
    }
}
