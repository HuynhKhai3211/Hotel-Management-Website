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
import com.mycompany.hotelmanagementsystem.dal.BookingRoomRepository;
import com.mycompany.hotelmanagementsystem.dal.CustomerRepository;
import com.mycompany.hotelmanagementsystem.dal.InvoiceRepository;
import com.mycompany.hotelmanagementsystem.dal.OccupantRepository;
import com.mycompany.hotelmanagementsystem.dal.PaymentRepository;
import com.mycompany.hotelmanagementsystem.dal.RoomRepository;
import com.mycompany.hotelmanagementsystem.dal.RoomTypeRepository;
import com.mycompany.hotelmanagementsystem.dal.ServiceRequestRepository;
import com.mycompany.hotelmanagementsystem.entity.BookingRoom;
import com.mycompany.hotelmanagementsystem.entity.RoomSuggestionItem;
import com.mycompany.hotelmanagementsystem.entity.UnassignedRoomInfo;
import com.mycompany.hotelmanagementsystem.util.BookingResult;
import com.mycompany.hotelmanagementsystem.util.WalkInCustomerResult;
import com.mycompany.hotelmanagementsystem.util.RoomSelectionItem;
import com.mycompany.hotelmanagementsystem.util.SurchargeResult;
import com.mycompany.hotelmanagementsystem.util.DateHelper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private final BookingRoomRepository bookingRoomRepository;

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
        this.bookingRoomRepository = new BookingRoomRepository();
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

    /**
     * Get booking detail with all BookingRoom records loaded.
     */
    public Booking getBookingDetailWithRooms(int bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId);
        if (booking != null) {
            booking.setBookingRooms(bookingRoomRepository.findByBookingIdWithDetails(bookingId));
        }
        return booking;
    }

    /**
     * Assign a specific room to a BookingRoom record.
     * Updates BookingRoom status to CheckedIn.
     * If all rooms checked in, updates parent Booking status too.
     */
    public boolean assignRoomToBookingRoom(int bookingRoomId, int roomId) {
        try {
            BookingRoom br = bookingRoomRepository.findById(bookingRoomId);
            if (br == null) return false;

            bookingRoomRepository.updateRoomId(bookingRoomId, roomId);
            bookingRoomRepository.updateStatus(bookingRoomId, BookingStatus.CHECKED_IN);
            bookingRoomRepository.updateCheckInActual(bookingRoomId, LocalDateTime.now());
            roomRepository.updateStatus(roomId, RoomStatus.OCCUPIED);

            // If all rooms in this booking are now CheckedIn, update parent Booking
            if (bookingRoomRepository.allRoomsInStatus(br.getBookingId(), BookingStatus.CHECKED_IN)) {
                bookingRepository.updateStatus(br.getBookingId(), BookingStatus.CHECKED_IN);
                bookingRepository.updateCheckInActual(br.getBookingId(), LocalDateTime.now());
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error assigning room to BookingRoom: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checkout a specific BookingRoom.
     * Creates cleaning service request for the room.
     * If all rooms checked out, updates parent Booking status too.
     */
    public boolean checkoutBookingRoom(int bookingRoomId) {
        try {
            BookingRoom br = bookingRoomRepository.findById(bookingRoomId);
            if (br == null) {
                System.err.println("Checkout failed: BookingRoom not found: " + bookingRoomId);
                return false;
            }

            System.out.println("Checkout BookingRoom: " + bookingRoomId + ", roomId: " + br.getRoomId() + ", bookingId: " + br.getBookingId());

            int updated = bookingRoomRepository.updateStatus(bookingRoomId, BookingStatus.CHECKED_OUT);
            System.out.println("Update status result: " + updated);
            if (updated <= 0) {
                System.err.println("Warning: updateStatus returned 0 for BookingRoom: " + bookingRoomId);
            }

            bookingRoomRepository.updateCheckOutActual(bookingRoomId, LocalDateTime.now());

            // Set room to Cleaning
            if (br.getRoomId() != null) {
                int roomUpdated = roomRepository.updateStatus(br.getRoomId(), RoomStatus.CLEANING);
                System.out.println("Update room status result: " + roomUpdated);

                // Auto-create cleaning service request
                try {
                    Room room = roomRepository.findById(br.getRoomId());
                    String roomNumber = (room != null) ? room.getRoomNumber() : String.valueOf(br.getRoomId());

                    ServiceRequest cleaningRequest = new ServiceRequest();
                    cleaningRequest.setBookingId(br.getBookingId());
                    cleaningRequest.setServiceType(ServiceTypeConstant.CLEANING);
                    cleaningRequest.setStatus(ServiceRequestStatusConstant.PENDING);
                    cleaningRequest.setDescription("Don phong sau khi khach tra phong (tu dong tao khi checkout)");
                    cleaningRequest.setPriority("Normal");
                    cleaningRequest.setRoomNumber(roomNumber);
                    serviceRequestRepository.insert(cleaningRequest);
                } catch (Exception e) {
                    System.err.println("Warning: Failed to create cleaning request: " + e.getMessage());
                }
            } else {
                System.err.println("Warning: br.getRoomId() is null, skipping room status update");
            }

            // If all rooms checked out, update parent Booking
            try {
                boolean allCheckedOut = bookingRoomRepository.allRoomsInStatus(br.getBookingId(), BookingStatus.CHECKED_OUT);
                System.out.println("All rooms checked out: " + allCheckedOut);
                if (allCheckedOut) {
                    bookingRepository.updateStatus(br.getBookingId(), BookingStatus.CHECKED_OUT);
                    bookingRepository.updateCheckOutActual(br.getBookingId(), LocalDateTime.now());
                }
            } catch (Exception e) {
                System.err.println("Warning: Error checking all rooms status: " + e.getMessage());
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error checking out BookingRoom: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Bulk assign rooms: assigns multiple rooms at once for a multi-room booking.
     * @param bookingRoomIdToRoomId map of bookingRoomId -> roomId
     */
    public boolean bulkAssignRooms(int bookingId, Map<Integer, Integer> bookingRoomIdToRoomId) {
        try {
            for (Map.Entry<Integer, Integer> entry : bookingRoomIdToRoomId.entrySet()) {
                assignRoomToBookingRoom(entry.getKey(), entry.getValue());
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error in bulk assign: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bulk checkout: checkout all checked-in rooms in a booking.
     */
    public boolean bulkCheckout(int bookingId) {
        try {
            List<BookingRoom> rooms = bookingRoomRepository.findByBookingId(bookingId);
            for (BookingRoom br : rooms) {
                if (BookingStatus.CHECKED_IN.equals(br.getStatus())) {
                    checkoutBookingRoom(br.getBookingRoomId());
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error in bulk checkout: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get available rooms for a specific BookingRoom assignment (by type + dates).
     */
    public List<Room> getAvailableRoomsForBookingRoom(int bookingRoomId) {
        BookingRoom br = bookingRoomRepository.findById(bookingRoomId);
        if (br == null) return List.of();

        Booking booking = bookingRepository.findByIdWithDetails(br.getBookingId());
        if (booking == null) return List.of();

        return roomRepository.findAvailableForDates(
            br.getTypeId(),
            booking.getCheckInExpected(),
            booking.getCheckOutExpected(),
            booking.getBookingId());
    }

    /**
     * Get all BookingRoom records for a booking.
     */
    public List<BookingRoom> getBookingRooms(int bookingId) {
        return bookingRoomRepository.findByBookingIdWithDetails(bookingId);
    }

    /**
     * Get unassigned rooms with available rooms and suggestions for multi-room assignment.
     */
    public List<UnassignedRoomInfo> getUnassignedRoomsWithSuggestions(int bookingId) {
        List<UnassignedRoomInfo> result = new ArrayList<>();

        Booking booking = bookingRepository.findByIdWithDetails(bookingId);
        if (booking == null) return result;

        List<BookingRoom> bookingRooms = bookingRoomRepository.findByBookingIdWithDetails(bookingId);
        List<BookingRoom> unassigned = new ArrayList<>();
        for (BookingRoom br : bookingRooms) {
            if (br.getRoomId() == null) {
                unassigned.add(br);
            }
        }

        if (unassigned.isEmpty()) return result;

        // Calculate needs by typeId
        Map<Integer, Integer> needs = new LinkedHashMap<>();
        for (BookingRoom br : unassigned) {
            needs.merge(br.getTypeId(), 1, Integer::sum);
        }

        // Get suggestions for all unassigned rooms
        RoomSuggestionService suggestionService = new RoomSuggestionService();
        Map<Integer, List<Room>> rawSuggestions = suggestionService.suggestNearbyRooms(
            needs, booking.getCheckInExpected(), booking.getCheckOutExpected());

        // Build result for each unassigned BookingRoom
        for (BookingRoom br : unassigned) {
            List<Room> available = roomRepository.findAvailableForDates(
                br.getTypeId(),
                booking.getCheckInExpected(),
                booking.getCheckOutExpected(),
                bookingId);

            List<RoomSuggestionItem> suggestions = new ArrayList<>();
            List<Room> suggestedRooms = rawSuggestions.get(br.getTypeId());
            if (suggestedRooms != null) {
                for (Room room : suggestedRooms) {
                    suggestions.add(new RoomSuggestionItem(
                        br.getBookingRoomId(),
                        room.getRoomId(),
                        room.getRoomType() != null ? room.getRoomType().getTypeName() : "",
                        room.getRoomNumber()
                    ));
                }
            }

            result.add(new UnassignedRoomInfo(br, available, suggestions));
        }

        return result;
    }

    /**
     * Check if a specific BookingRoom needs payment at checkout.
     * Returns true if there are unpaid extension invoices for this room,
     * or if the room's portion of the booking total hasn't been fully paid.
     */
    public boolean needsCheckoutPaymentForRoom(int bookingRoomId) {
        BookingRoom br = bookingRoomRepository.findById(bookingRoomId);
        if (br == null) return false;

        // For multi-room bookings, check if the booking has been paid in full
        Booking booking = bookingRepository.findById(br.getBookingId());
        if (booking == null) return false;

        // Check if booking invoice has been paid
        Invoice invoice = invoiceRepository.findByBookingId(booking.getBookingId());
        if (invoice == null) return false;

        return !paymentRepository.hasSuccessfulPayment(invoice.getInvoiceId());
    }

    /**
     * Walk-in multi-room booking: create booking + rooms with CheckedIn status.
     * Auto-assigns first available room for each BookingRoom.
     */
    public BookingResult createWalkInMultiRoom(int customerId, List<RoomSelectionItem> selections,
            LocalDateTime checkIn, LocalDateTime checkOut,
            BigDecimal totalPrice, String note, List<Occupant> occupants) {
        try {
            // Validate inputs
            if (selections == null || selections.isEmpty()) {
                return BookingResult.failure("Danh sach phong trong");
            }
            if (customerId <= 0) {
                return BookingResult.failure("Khach hang khong hop le");
            }
            // Verify customer exists
            Account customer = accountRepository.findById(customerId);
            if (customer == null) {
                return BookingResult.failure("Khach hang khong ton tai: " + customerId);
            }
            // Ensure customer record exists in Customer table (for FK constraint)
            var existingCustomer = customerRepository.findById(customerId);
            if (existingCustomer == null) {
                int inserted = customerRepository.insert(customerId);
                if (inserted <= 0) {
                    return BookingResult.failure("Khong the tao customer record: " + customerId);
                }
            }
            int firstTypeId = selections.get(0).getTypeId();
            if (firstTypeId <= 0) {
                return BookingResult.failure("Loai phong khong hop le");
            }
            RoomType firstRoomType = roomTypeRepository.findById(firstTypeId);
            if (firstRoomType == null) {
                return BookingResult.failure("Loai phong khong ton tai: " + firstTypeId);
            }

            // Create parent Booking
            Booking booking = new Booking();
            booking.setCustomerId(customerId);
            booking.setRoomId(null); // multi-room: actual rooms are in BookingRoom
            booking.setTypeId(firstTypeId);
            booking.setCheckInExpected(checkIn);
            booking.setCheckOutExpected(checkOut);
            booking.setTotalPrice(totalPrice);
            booking.setStatus(BookingStatus.CHECKED_IN);
            booking.setPaymentType(PaymentType.FULL);
            booking.setDepositAmount(totalPrice);
            booking.setNote(note);

            int bookingId;
            try {
                bookingId = bookingRepository.insert(booking);
            } catch (RuntimeException e) {
                String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                return BookingResult.failure("Loi INSERT Booking: " + cause +
                    " | customerId=" + customerId + ", roomId=" + booking.getRoomId() +
                    ", typeId=" + booking.getTypeId());
            }
            if (bookingId <= 0) return BookingResult.failure("Khong the tao don dat phong");
            booking.setBookingId(bookingId);

            bookingRepository.updateCheckInActual(bookingId, LocalDateTime.now());

            // Create BookingRoom records and auto-assign rooms
            for (RoomSelectionItem sel : selections) {
                List<Room> availableRooms = roomRepository.findAvailableForDates(sel.getTypeId(), checkIn, checkOut);
                if (availableRooms.size() < sel.getQuantity()) {
                    bookingRepository.updateStatus(bookingId, BookingStatus.CANCELLED);
                    return BookingResult.failure("Khong du phong trong cho loai phong " + sel.getTypeId());
                }

                RoomType rt = roomTypeRepository.findById(sel.getTypeId());
                BigDecimal pricePerHour = (rt != null && rt.getPricePerHour() != null) ? rt.getPricePerHour() : BigDecimal.ZERO;
                SurchargeResult surcharge = DateHelper.calculateSurcharges(checkIn, checkOut, pricePerHour);
                long nights = DateHelper.calculateNights(checkIn, checkOut);
                BigDecimal roomPrice = surcharge.isSameDayBooking()
                    ? surcharge.getHourlyTotal()
                    : rt.getBasePrice().multiply(BigDecimal.valueOf(nights));

                for (int i = 0; i < sel.getQuantity(); i++) {
                    Room room = availableRooms.get(i);

                    BookingRoom br = new BookingRoom();
                    br.setBookingId(bookingId);
                    if (room != null) {
                        br.setRoomId(room.getRoomId());
                    }
                    br.setTypeId(sel.getTypeId());
                    br.setUnitPrice(roomPrice);
                    br.setEarlySurcharge(surcharge.getEarlySurcharge());
                    br.setLateSurcharge(surcharge.getLateSurcharge());
                    br.setPromotionDiscount(BigDecimal.ZERO);
                    br.setStatus(BookingStatus.CHECKED_IN);
                    bookingRoomRepository.insert(br);

                    roomRepository.updateStatus(room.getRoomId(), RoomStatus.OCCUPIED);
                }
            }

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
}
