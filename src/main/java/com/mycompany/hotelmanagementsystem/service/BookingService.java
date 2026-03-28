package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.constant.PaymentType;
import com.mycompany.hotelmanagementsystem.util.BookingCalcResponse;
import com.mycompany.hotelmanagementsystem.util.BookingResult;
import com.mycompany.hotelmanagementsystem.util.ServiceResult;
import com.mycompany.hotelmanagementsystem.util.DateHelper;
import com.mycompany.hotelmanagementsystem.entity.*;
import com.mycompany.hotelmanagementsystem.dal.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final VoucherRepository voucherRepository;
    private final OccupantRepository occupantRepository;
    private final PromotionRepository promotionRepository;

    public BookingService() {
        this.bookingRepository = new BookingRepository();
        this.roomRepository = new RoomRepository();
        this.roomTypeRepository = new RoomTypeRepository();
        this.voucherRepository = new VoucherRepository();
        this.occupantRepository = new OccupantRepository();
        this.promotionRepository = new PromotionRepository();
    }

    public BookingCalcResponse calculateBooking(int typeId, int roomId,
            LocalDateTime checkIn, LocalDateTime checkOut, String voucherCode) {

        RoomType roomType = roomTypeRepository.findById(typeId);
        if (roomType == null) return null;

        Room room = roomRepository.findById(roomId);
        if (room == null || room.getTypeId() != typeId) return null;

        long nights = DateHelper.calculateNights(checkIn, checkOut);
        BigDecimal subtotal = roomType.getBasePrice().multiply(BigDecimal.valueOf(nights));

        // Promotion discount (percentage-based, auto-applied from active promotion)
        BigDecimal promotionDiscount = BigDecimal.ZERO;
        Promotion promotion = promotionRepository.findActiveByTypeId(typeId);
        if (promotion != null) {
            promotionDiscount = roomType.getBasePrice()
                .multiply(promotion.getDiscountPercent())
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(nights));
            if (promotionDiscount.compareTo(subtotal) > 0) {
                promotionDiscount = subtotal;
            }
        }

        // Voucher discount (fixed amount, capped at remaining after promotion)
        BigDecimal voucherDiscount = BigDecimal.ZERO;
        Voucher voucher = null;

        if (voucherCode != null && !voucherCode.isEmpty()) {
            voucher = voucherRepository.findByCode(voucherCode);
            if (voucher != null && voucher.isActive()) {
                if (voucher.getMinOrderValue() == null || subtotal.compareTo(voucher.getMinOrderValue()) >= 0) {
                    voucherDiscount = voucher.getDiscountAmount();
                    BigDecimal remaining = subtotal.subtract(promotionDiscount);
                    if (voucherDiscount.compareTo(remaining) > 0) {
                        voucherDiscount = remaining;
                    }
                }
            }
        }

        BigDecimal total = subtotal.subtract(promotionDiscount).subtract(voucherDiscount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        // Calculate deposit info
        BigDecimal depositPercent = roomType.getDepositPercent() != null
            ? roomType.getDepositPercent() : BigDecimal.ZERO;
        BigDecimal depositAmount = total.multiply(depositPercent)
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        boolean isStandard = roomType.isStandardRoom();

        BookingCalcResponse response = new BookingCalcResponse();
        response.setRoomType(roomType);
        response.setRoom(room);
        response.setCheckIn(checkIn);
        response.setCheckOut(checkOut);
        response.setNights(nights);
        response.setSubtotal(subtotal);
        response.setPromotion(promotion);
        response.setPromotionDiscount(promotionDiscount);
        response.setDiscount(voucherDiscount);
        response.setTotal(total);
        response.setVoucher(voucher);
        response.setDepositPercent(depositPercent);
        response.setDepositAmount(depositAmount);
        response.setStandardRoom(isStandard);
        response.setPricePerHour(roomType.getPricePerHour());
        return response;
    }

    public List<Room> getAvailableRooms(int typeId, LocalDateTime checkIn, LocalDateTime checkOut) {
        return roomRepository.findAvailableForDates(typeId, checkIn, checkOut);
    }

    /**
     * Lấy danh sách khoảng ngày đã bị đặt (active) cho loại phòng,
     * để hiển thị lịch "ngày bận" cho customer khi đặt phòng.
     */
    public List<LocalDateTime[]> getOccupiedDateRanges(int typeId) {
        return bookingRepository.findOccupiedDateRangesByTypeId(typeId);
    }

    public BookingResult createBooking(int customerId, int roomId, LocalDateTime checkIn,
            LocalDateTime checkOut, BigDecimal totalPrice, Integer voucherId,
            String note, List<Occupant> occupants, String paymentType, BigDecimal depositAmount) {

        if (!DateHelper.isFutureDate(checkIn)) {
            return BookingResult.failure("Ngày nhận phòng phải là ngày trong tương lai");
        }
        if (!checkOut.isAfter(checkIn)) {
            return BookingResult.failure("Ngày trả phòng phải sau ngày nhận phòng");
        }
        if (ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate()) > 30) {
            return BookingResult.failure("Đặt phòng tối đa 30 ngày");
        }
        if (!bookingRepository.isRoomAvailable(roomId, checkIn, checkOut)) {
            return BookingResult.failure("Phòng không còn trống trong thời gian này");
        }

        // Get room to determine typeId
        Room room = roomRepository.findById(roomId);
        if (room == null) {
            return BookingResult.failure("Phòng không tồn tại");
        }

        Booking booking = new Booking();
        booking.setCustomerId(customerId);
        booking.setRoomId(roomId);
        booking.setTypeId(room.getTypeId());
        booking.setCheckInExpected(checkIn);
        booking.setCheckOutExpected(checkOut);
        booking.setTotalPrice(totalPrice);
        booking.setVoucherId(voucherId);
        booking.setNote(note);
        booking.setStatus(BookingStatus.PENDING);

        // Set payment type and deposit amount
        if (paymentType != null) {
            booking.setPaymentType(paymentType);
        } else {
            booking.setPaymentType(PaymentType.FULL);
        }
        if (depositAmount != null) {
            booking.setDepositAmount(depositAmount);
        } else {
            booking.setDepositAmount(totalPrice);
        }

        int bookingId = bookingRepository.insert(booking);
        if (bookingId <= 0) return BookingResult.failure("Không thể tạo đơn đặt phòng");

        booking.setBookingId(bookingId);

        if (occupants != null) {
            for (Occupant occ : occupants) {
                if (occ.getFullName() != null && !occ.getFullName().trim().isEmpty()) {
                    occ.setBookingId(bookingId);
                    occ.setFullName(occ.getFullName().trim());
                    occupantRepository.insert(occ);
                }
            }
        }

        return BookingResult.success("Đặt phòng thành công", booking);
    }

    // Backward-compatible overload for existing callers
    public BookingResult createBooking(int customerId, int roomId, LocalDateTime checkIn,
            LocalDateTime checkOut, BigDecimal totalPrice, Integer voucherId,
            String note, List<Occupant> occupants) {
        return createBooking(customerId, roomId, checkIn, checkOut, totalPrice,
            voucherId, note, occupants, PaymentType.FULL, totalPrice);
    }

    public Booking getBookingById(int bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId);
        // Lazy check: auto-cancel overdue bookings (Pending or Confirmed, all room types)
        if (booking != null && (BookingStatus.PENDING.equals(booking.getStatus())
                || BookingStatus.CONFIRMED.equals(booking.getStatus()))) {
            try {
                if (isOverdueBooking(booking)) {
                    bookingRepository.updateStatus(bookingId, BookingStatus.CANCELLED);
                    booking.setStatus(BookingStatus.CANCELLED);
                }
            } catch (Exception e) {
                // Don't break the flow if lazy check fails
            }
        }
        return booking;
    }

    public List<Booking> getCustomerBookings(int customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        return bookingRepository.updateStatus(bookingId, status) > 0;
    }

    public List<Occupant> getBookingOccupants(int bookingId) {
        return occupantRepository.findByBookingId(bookingId);
    }

    public ServiceResult cancelBooking(int bookingId, int customerId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null || booking.getCustomerId() != customerId) {
            return ServiceResult.failure("Khong tim thay dat phong");
        }
        if (!BookingStatus.PENDING.equals(booking.getStatus()) &&
            !BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            return ServiceResult.failure("Chi co the huy don o trang thai cho thanh toan hoac da xac nhan");
        }
        if (bookingRepository.updateStatus(bookingId, BookingStatus.CANCELLED) > 0) {
            return ServiceResult.success("Dat phong da duoc huy thanh cong");
        }
        return ServiceResult.failure("Khong the huy dat phong, vui long thu lai");
    }

    // Check if booking is overdue (1 minute past check-in expected, all room types)
    private boolean isOverdueBooking(Booking booking) {
        if (booking.getCheckInExpected() == null) return false;
        LocalDateTime deadline = booking.getCheckInExpected().plusMinutes(1);
        return LocalDateTime.now().isAfter(deadline);
    }
}
