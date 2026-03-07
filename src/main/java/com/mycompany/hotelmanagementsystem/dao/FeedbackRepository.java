package com.mycompany.hotelmanagementsystem.dao;

import com.mycompany.hotelmanagementsystem.model.Customer;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Feedback;
import com.mycompany.hotelmanagementsystem.model.Account;
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.model.RoomType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FeedbackRepository extends BaseRepository<Feedback> {

    @Override
    protected Feedback mapRow(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        f.setFeedbackId(rs.getInt("feedback_id"));
        f.setBookingId(rs.getInt("booking_id"));
        f.setRating(rs.getInt("rating"));
        f.setComment(rs.getString("comment"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) f.setCreatedAt(ts.toLocalDateTime());
        f.setHidden(rs.getBoolean("is_hidden"));
        try {
            f.setAdminReply(rs.getString("admin_reply"));
        } catch (SQLException ignored) {}
        return f;
    }

    public int insert(Feedback feedback) {
        String sql = "INSERT INTO Feedback (booking_id, rating, comment) VALUES (?, ?, ?)";
        return executeInsert(sql, feedback.getBookingId(), feedback.getRating(), feedback.getComment());
    }

    public Feedback findByBookingId(int bookingId) {
        String sql = """
            SELECT f.*, fr.reply_content AS admin_reply
            FROM Feedback f
            LEFT JOIN FeedbackReply fr ON f.feedback_id = fr.feedback_id
            WHERE f.booking_id = ?
            """;
        return queryOne(sql, bookingId);
    }

   
}
