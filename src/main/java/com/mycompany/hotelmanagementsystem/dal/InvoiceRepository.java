package com.mycompany.hotelmanagementsystem.dal;

import com.mycompany.hotelmanagementsystem.entity.Invoice;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class InvoiceRepository extends BaseRepository<Invoice> {

    @Override
    protected Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setInvoiceId(rs.getInt("invoice_id"));
        inv.setBookingId(rs.getInt("booking_id"));
        Timestamp ts = rs.getTimestamp("issued_date");
        if (ts != null) inv.setIssuedDate(ts.toLocalDateTime());
        inv.setTotalAmount(rs.getBigDecimal("total_amount"));
        inv.setTaxAmount(rs.getBigDecimal("tax_amount"));
        return inv;
    }

    public int insert(Invoice invoice) {
        String sql = "INSERT INTO Invoice (booking_id, total_amount, tax_amount) VALUES (?, ?, ?)";
        return executeInsert(sql, invoice.getBookingId(), invoice.getTotalAmount(), invoice.getTaxAmount());
    }

    public Invoice findById(int invoiceId) {
        return queryOne("SELECT * FROM Invoice WHERE invoice_id = ?", invoiceId);
    }

    public Invoice findByBookingId(int bookingId) {
        return queryOne("SELECT * FROM Invoice WHERE booking_id = ?", bookingId);
    }
}
