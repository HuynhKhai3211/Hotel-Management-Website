package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.model.Voucher;
import com.mycompany.hotelmanagementsystem.model.Occupant;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.model.RoomType;
import com.mycompany.hotelmanagementsystem.dao.RoomTypeRepository;
import com.mycompany.hotelmanagementsystem.dao.OccupantRepository;
import com.mycompany.hotelmanagementsystem.dao.VoucherRepository;
import com.mycompany.hotelmanagementsystem.dao.BookingRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.utils.BookingCalcResponse;
import com.mycompany.hotelmanagementsystem.utils.BookingResult;
import com.mycompany.hotelmanagementsystem.utils.ServiceResult;
import com.mycompany.hotelmanagementsystem.utils.DateHelper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final VoucherRepository voucherRepository;
    private final OccupantRepository occupantRepository;

    public BookingService() {
        this.bookingRepository = new BookingRepository();
        this.roomRepository = new RoomRepository();
        this.roomTypeRepository = new RoomTypeRepository();
        this.voucherRepository = new VoucherRepository();
        this.occupantRepository = new OccupantRepository();
    }

    public BookingCalcResponse calculateBooking(int typeId, int roomId,
            LocalDateTime checkIn, LocalDateTime checkOut, String voucherCode) {

        RoomType roomType = roomTypeRepository.findById(typeId);
        if (roomType == null) return null;

        Room room = roomRepository.findById(roomId);
        if (room == null || room.getTypeId() != typeId) return null;

        long nights = DateHelper.calculateNights(checkIn, checkOut);
        BigDecimal subtotal = roomType.getBasePrice().multiply(BigDecimal.valueOf(nights));
        BigDecimal discount = BigDecimal.ZERO;
        Voucher voucher = null;

        if (voucherCode != null && !voucherCode.isEmpty()) {
            voucher = voucherRepository.findByCode(voucherCode);
            if (voucher != null && voucher.isActive()) {
                if (voucher.getMinOrderValue() == null || subtotal.compareTo(voucher.getMinOrderValue()) >= 0) {
                    discount = voucher.getDiscountAmount();
                    if (discount.compareTo(subtotal) > 0) discount = subtotal;
                }
            }
        }

        BookingCalcResponse response = new BookingCalcResponse();
        response.setRoomType(roomType);
        response.setRoom(room);
        response.setCheckIn(checkIn);
        response.setCheckOut(checkOut);
        response.setNights(nights);
        response.setSubtotal(subtotal);
        response.setDiscount(discount);
        response.setTotal(subtotal.subtract(discount));
        response.setVoucher(voucher);
        return response;
    }

    public List<Room> getAvailableRooms(int typeId, LocalDateTime checkIn, LocalDateTime checkOut) {
        return roomRepository.findAvailableForDates(typeId, checkIn, checkOut);
    }

    // Dùng List<Occupant> entity thay cho List<OccupantRequest>
    public BookingResult createBooking(int customerId, int roomId, LocalDateTime checkIn,
            LocalDateTime checkOut, BigDecimal totalPrice, Integer voucherId,
            String note, List<Occupant> occupants) {

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

        Booking booking = new Booking();
        booking.setCustomerId(customerId);
        booking.setRoomId(roomId);
        booking.setCheckInExpected(checkIn);
        booking.setCheckOutExpected(checkOut);
        booking.setTotalPrice(totalPrice);
        booking.setVoucherId(voucherId);
        booking.setNote(note);
        booking.setStatus(BookingStatus.PENDING);

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

    public Booking getBookingById(int bookingId) {
        return bookingRepository.findByIdWithDetails(bookingId);
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
            return ServiceResult.failure("Không tìm thấy đặt phòng");
        }
        if (!BookingStatus.PENDING.equals(booking.getStatus()) &&
            !BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            return ServiceResult.failure("Chỉ có thể hủy đơn ở trạng thái chờ thanh toán hoặc đã xác nhận");
        }
        if (bookingRepository.updateStatus(bookingId, BookingStatus.CANCELLED) > 0) {
            return ServiceResult.success("Đặt phòng đã được hủy thành công");
        }
        return ServiceResult.failure("Không thể hủy đặt phòng, vui lòng thử lại");
    }
}
