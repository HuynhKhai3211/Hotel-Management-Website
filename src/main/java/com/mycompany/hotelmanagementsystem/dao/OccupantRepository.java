package com.mycompany.hotelmanagementsystem.dao;

import com.mycompany.hotelmanagementsystem.model.Occupant;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OccupantRepository extends BaseRepository<Occupant> {

    @Override
    protected Occupant mapRow(ResultSet rs) throws SQLException {
        Occupant o = new Occupant();
        o.setOccupantId(rs.getInt("occupant_id"));
        o.setBookingId(rs.getInt("booking_id"));
        o.setFullName(rs.getString("full_name"));
        o.setIdCardNumber(rs.getString("id_card_number"));
        o.setPhoneNumber(rs.getString("phone_number"));
        return o;
    }

    public int insert(Occupant occupant) {
        String sql = "INSERT INTO Occupant (booking_id, full_name, id_card_number, phone_number) VALUES (?, ?, ?, ?)";
        return executeInsert(sql, occupant.getBookingId(), occupant.getFullName(),
            occupant.getIdCardNumber(), occupant.getPhoneNumber());
    }

    public List<Occupant> findByBookingId(int bookingId) {
        return queryList("SELECT * FROM Occupant WHERE booking_id = ?", bookingId);
    }

    public int update(Occupant occupant) {
        String sql = "UPDATE Occupant SET full_name = ?, id_card_number = ?, phone_number = ? WHERE occupant_id = ?";
        return executeUpdate(sql, occupant.getFullName(), occupant.getIdCardNumber(),
            occupant.getPhoneNumber(), occupant.getOccupantId());
    }

    public int deleteByBookingId(int bookingId) {
        return executeUpdate("DELETE FROM Occupant WHERE booking_id = ?", bookingId);
    }
}
