package com.mycompany.hotelmanagementsystem.dao;

import com.mycompany.hotelmanagementsystem.model.BookingExtension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class BookingExtensionRepository extends BaseRepository<BookingExtension> {

    @Override
    protected BookingExtension mapRow(ResultSet rs) throws SQLException {
        BookingExtension ext = new BookingExtension();
        ext.setExtensionId(rs.getInt("extension_id"));
        ext.setBookingId(rs.getInt("booking_id"));
        Timestamp ts = rs.getTimestamp("original_check_out");
        if (ts != null) ext.setOriginalCheckOut(ts.toLocalDateTime());
        ts = rs.getTimestamp("new_check_out");
        if (ts != null) ext.setNewCheckOut(ts.toLocalDateTime());
        ext.setExtensionHours(rs.getInt("extension_hours"));
        ext.setExtensionPrice(rs.getBigDecimal("extension_price"));
        ext.setStatus(rs.getString("status"));
        ts = rs.getTimestamp("created_at");
        if (ts != null) ext.setCreatedAt(ts.toLocalDateTime());
        return ext;
    }

    public int insert(BookingExtension ext) {
        String sql = """
            INSERT INTO BookingExtension (booking_id, original_check_out, new_check_out,
                extension_hours, extension_price, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        return executeInsert(sql,
            ext.getBookingId(),
            Timestamp.valueOf(ext.getOriginalCheckOut()),
            Timestamp.valueOf(ext.getNewCheckOut()),
            ext.getExtensionHours(),
            ext.getExtensionPrice(),
            ext.getStatus());
    }

    public BookingExtension findById(int extensionId) {
        return queryOne("SELECT * FROM BookingExtension WHERE extension_id = ?", extensionId);
    }

    public List<BookingExtension> findByBookingId(int bookingId) {
        return queryList(
            "SELECT * FROM BookingExtension WHERE booking_id = ? ORDER BY created_at",
            bookingId);
    }

    public int updateStatus(int extensionId, String status) {
        return executeUpdate(
            "UPDATE BookingExtension SET status = ? WHERE extension_id = ?",
            status, extensionId);
    }

    // Find the latest pending extension for a booking (for payment confirmation)
    public BookingExtension findPendingByBookingId(int bookingId) {
        return queryOne(
            "SELECT TOP 1 * FROM BookingExtension WHERE booking_id = ? AND status = 'Pending' ORDER BY extension_id DESC",
            bookingId);
    }
}
