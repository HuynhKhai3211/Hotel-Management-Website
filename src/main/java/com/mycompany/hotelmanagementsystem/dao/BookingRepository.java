package com.mycompany.hotelmanagementsystem.dao;

import com.mycompany.hotelmanagementsystem.model.Customer;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Account;
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.model.RoomType;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository extends BaseRepository<Booking> {

    @Override
    protected Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setCustomerId(rs.getInt("customer_id"));
        b.setRoomId(rs.getInt("room_id"));
        int voucherId = rs.getInt("voucher_id");
        b.setVoucherId(rs.wasNull() ? null : voucherId);
        Timestamp ts = rs.getTimestamp("booking_date");
        if (ts != null) b.setBookingDate(ts.toLocalDateTime());
        ts = rs.getTimestamp("check_in_expected");
        if (ts != null) b.setCheckInExpected(ts.toLocalDateTime());
        ts = rs.getTimestamp("check_out_expected");
        if (ts != null) b.setCheckOutExpected(ts.toLocalDateTime());
        b.setTotalPrice(rs.getBigDecimal("total_price"));
        b.setStatus(rs.getString("status"));
        b.setNote(rs.getString("note"));
        return b;
    }

    public int insert(Booking booking) {
        String sql = """
            INSERT INTO Booking (customer_id, room_id, voucher_id,
                check_in_expected, check_out_expected, total_price, status, note)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        return executeInsert(sql,
            booking.getCustomerId(), booking.getRoomId(), booking.getVoucherId(),
            Timestamp.valueOf(booking.getCheckInExpected()),
            Timestamp.valueOf(booking.getCheckOutExpected()),
            booking.getTotalPrice(), booking.getStatus(), booking.getNote());
    }

    public Booking findById(int bookingId) {
        return queryOne("SELECT * FROM Booking WHERE booking_id = ?", bookingId);
    }

    public Booking findByIdWithDetails(int bookingId) {
        String sql = """
            SELECT b.*, r.room_number, r.type_id, rt.type_name, rt.base_price
            FROM Booking b
            JOIN Room r ON b.room_id = r.room_id
            JOIN RoomType rt ON r.type_id = rt.type_id
            WHERE b.booking_id = ?
            """;
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking b = mapRow(rs);
                    Room room = new Room();
                    room.setRoomId(b.getRoomId());
                    room.setRoomNumber(rs.getString("room_number"));
                    RoomType rt = new RoomType();
                    rt.setTypeId(rs.getInt("type_id"));
                    rt.setTypeName(rs.getString("type_name"));
                    rt.setBasePrice(rs.getBigDecimal("base_price"));
                    room.setRoomType(rt);
                    b.setRoom(room);
                    return b;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find booking failed", e);
        }
        return null;
    }

    public List<Booking> findByCustomerId(int customerId) {
        String sql = """
            SELECT b.*, r.room_number, rt.type_name
            FROM Booking b
            JOIN Room r ON b.room_id = r.room_id
            JOIN RoomType rt ON r.type_id = rt.type_id
            WHERE b.customer_id = ?
            ORDER BY b.booking_date DESC
            """;
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (var rs = ps.executeQuery()) {
                List<Booking> list = new ArrayList<>();
                while (rs.next()) {
                    Booking b = mapRow(rs);
                    Room room = new Room();
                    room.setRoomNumber(rs.getString("room_number"));
                    RoomType rt = new RoomType();
                    rt.setTypeName(rs.getString("type_name"));
                    room.setRoomType(rt);
                    b.setRoom(room);
                    list.add(b);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find bookings failed", e);
        }
    }

    public int updateStatus(int bookingId, String status) {
        return executeUpdate("UPDATE Booking SET status = ? WHERE booking_id = ?", status, bookingId);
    }

    public boolean isRoomAvailable(int roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        String sql = """
            SELECT COUNT(*) FROM Booking
            WHERE room_id = ? AND status IN ('Pending', 'Confirmed', 'CheckedIn')
            AND NOT (check_out_expected <= ? OR check_in_expected >= ?)
            """;
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setTimestamp(2, Timestamp.valueOf(checkIn));
            ps.setTimestamp(3, Timestamp.valueOf(checkOut));
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Check availability failed", e);
        }
        return false;
    }

    public List<Booking> findByStatus(String status) {
        String sql = """
            SELECT b.*, r.room_number, rt.type_name, a.full_name as customer_name
            FROM Booking b
            JOIN Room r ON b.room_id = r.room_id
            JOIN RoomType rt ON r.type_id = rt.type_id
            JOIN Account a ON b.customer_id = a.account_id
            WHERE b.status = ?
            ORDER BY b.check_in_expected ASC
            """;
        return findBookingsWithDetails(sql, status);
    }

    public List<Booking> findByStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) return new ArrayList<>();
        String placeholders = String.join(",", statuses.stream().map(s -> "?").toList());
        String sql = """
            SELECT b.*, r.room_number, rt.type_name, a.full_name as customer_name
            FROM Booking b
            JOIN Room r ON b.room_id = r.room_id
            JOIN RoomType rt ON r.type_id = rt.type_id
            JOIN Account a ON b.customer_id = a.account_id
            WHERE b.status IN (%s)
            ORDER BY b.check_in_expected ASC
            """.formatted(placeholders);
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < statuses.size(); i++) {
                ps.setString(i + 1, statuses.get(i));
            }
            try (var rs = ps.executeQuery()) {
                return mapBookingsWithDetails(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Find bookings failed", e);
        }
    }

    public List<Booking> findAll() {
        String sql = """
            SELECT b.*, r.room_number, rt.type_name, a.full_name as customer_name
            FROM Booking b
            JOIN Room r ON b.room_id = r.room_id
            JOIN RoomType rt ON r.type_id = rt.type_id
            JOIN Account a ON b.customer_id = a.account_id
            ORDER BY b.booking_date DESC
            """;
        return findBookingsWithDetails(sql);
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Booking WHERE status = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Count bookings failed", e);
        }
        return 0;
    }

    

    

    private List<Booking> mapBookingsWithDetails(ResultSet rs) throws SQLException {
        List<Booking> list = new ArrayList<>();
        while (rs.next()) {
            Booking b = mapRow(rs);
            Room room = new Room();
            room.setRoomNumber(rs.getString("room_number"));
            RoomType rt = new RoomType();
            rt.setTypeName(rs.getString("type_name"));
            room.setRoomType(rt);
            b.setRoom(room);
            // Store customer name in note temporarily for display (or create a DTO)
            try {
                String customerName = rs.getString("customer_name");
                if (customerName != null) {
                    Customer c = new Customer();
                    Account a = new Account();
                    a.setFullName(customerName);
                    c.setAccount(a);
                    b.setCustomer(c);
                }
            } catch (SQLException ignored) {}
            list.add(b);
        }
        return list;
    }

   
}
