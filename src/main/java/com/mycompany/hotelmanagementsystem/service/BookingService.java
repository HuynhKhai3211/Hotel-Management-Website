package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.model.Voucher;
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.model.RoomType;
import com.mycompany.hotelmanagementsystem.dao.RoomTypeRepository;
import com.mycompany.hotelmanagementsystem.dao.OccupantRepository;
import com.mycompany.hotelmanagementsystem.dao.VoucherRepository;
import com.mycompany.hotelmanagementsystem.dao.BookingRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
import com.mycompany.hotelmanagementsystem.utils.BookingCalcResponse;
import com.mycompany.hotelmanagementsystem.utils.DateHelper;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingService {
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final VoucherRepository voucherRepository;

    public BookingService() {
        new BookingRepository();
        this.roomRepository = new RoomRepository();
        this.roomTypeRepository = new RoomTypeRepository();
        this.voucherRepository = new VoucherRepository();
        new OccupantRepository();
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

}