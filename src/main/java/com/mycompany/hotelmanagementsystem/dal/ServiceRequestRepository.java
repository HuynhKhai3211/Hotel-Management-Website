package com.mycompany.hotelmanagementsystem.dal;

import com.mycompany.hotelmanagementsystem.entity.ServiceRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ServiceRequestRepository extends BaseRepository<ServiceRequest> {

    @Override
    protected ServiceRequest mapRow(ResultSet rs) throws SQLException {
        ServiceRequest sr = new ServiceRequest();
        sr.setRequestId(rs.getInt("request_id"));
        sr.setBookingId(rs.getInt("booking_id"));
        int staffId = rs.getInt("staff_id");
        sr.setStaffId(rs.wasNull() ? null : staffId);
        sr.setServiceType(rs.getString("service_type"));
        Timestamp ts = rs.getTimestamp("request_time");
        if (ts != null) sr.setRequestTime(ts.toLocalDateTime());
        sr.setStatus(rs.getString("status"));
        return sr;
    }

    public int insert(ServiceRequest request) {
        String sql = "INSERT INTO ServiceRequest (booking_id, service_type, status) VALUES (?, ?, ?)";
        return executeInsert(sql, request.getBookingId(), request.getServiceType(), request.getStatus());
    }

    public List<ServiceRequest> findByBookingId(int bookingId) {
        return queryList("SELECT * FROM ServiceRequest WHERE booking_id = ? ORDER BY request_time DESC", bookingId);
    }

    public ServiceRequest findById(int requestId) {
        return queryOne("SELECT * FROM ServiceRequest WHERE request_id = ?", requestId);
    }

    public boolean hasPendingRequest(int bookingId, String serviceType) {
        String sql = "SELECT COUNT(*) FROM ServiceRequest WHERE booking_id = ? AND service_type = ? AND status = 'Pending'";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setString(2, serviceType);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Check pending request failed", e);
        }
        return false;
    }

    public int updateStatus(int requestId, String status) {
        return executeUpdate("UPDATE ServiceRequest SET status = ? WHERE request_id = ?", status, requestId);
    }
}
