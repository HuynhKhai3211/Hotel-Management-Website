// Khai báo package chứa repository thao tác với bảng Invoice
package com.mycompany.hotelmanagementsystem.dao;

// Import entity Invoice
import com.mycompany.hotelmanagementsystem.model.Invoice;

// Import các class JDBC để đọc dữ liệu từ database
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

// Import List để xử lý danh sách nhiều invoice
import java.util.List;

// Repository kế thừa BaseRepository để dùng các hàm chung (query, insert...)
public class InvoiceRepository extends BaseRepository<Invoice> {

    // Hàm map dữ liệu từ ResultSet (DB) sang object Invoice
    @Override
    protected Invoice mapRow(ResultSet rs) throws SQLException {

        // Tạo object Invoice mới
        Invoice inv = new Invoice();

        // Map từng cột từ DB sang field trong object
        inv.setInvoiceId(rs.getInt("invoice_id"));   // id của invoice
        inv.setBookingId(rs.getInt("booking_id"));   // id booking liên quan

        // Lấy thời gian phát hành hóa đơn từ DB (kiểu Timestamp)
        Timestamp ts = rs.getTimestamp("issued_date");

        // Nếu có dữ liệu thì convert sang LocalDateTime
        if (ts != null) inv.setIssuedDate(ts.toLocalDateTime());

        // Tổng tiền đã bao gồm thuế
        inv.setTotalAmount(rs.getBigDecimal("total_amount"));

        // Tiền thuế
        inv.setTaxAmount(rs.getBigDecimal("tax_amount"));

        // Loại invoice: BOOKING / EXTENSION / REMAINING
        inv.setInvoiceType(rs.getString("invoice_type"));

        // Trả về object Invoice đã map xong
        return inv;
    }

    // Hàm insert invoice mới vào database
    public int insert(Invoice invoice) {

        // Câu SQL insert invoice
        String sql = "INSERT INTO Invoice (booking_id, total_amount, tax_amount, invoice_type) VALUES (?, ?, ?, ?)";

        // Gọi executeInsert từ BaseRepository
        // Truyền các giá trị tương ứng với dấu ?
        return executeInsert(
                sql,
                invoice.getBookingId(),   // booking_id
                invoice.getTotalAmount(), // total_amount
                invoice.getTaxAmount(),   // tax_amount
                invoice.getInvoiceType()  // invoice_type
        );
    }

    // Tìm invoice theo invoiceId
    public Invoice findById(int invoiceId) {

        // queryOne: lấy duy nhất 1 record
        return queryOne("SELECT * FROM Invoice WHERE invoice_id = ?", invoiceId);
    }

    // Tìm invoice BOOKING theo bookingId
    public Invoice findByBookingId(int bookingId) {

        // Chỉ lấy invoice loại 'Booking'
        // dùng khi cần hóa đơn ban đầu của booking
        return queryOne(
                "SELECT * FROM Invoice WHERE booking_id = ? AND invoice_type = 'Booking'",
                bookingId
        );
    }

    // Tìm invoice theo bookingId và loại invoice (BOOKING / EXTENSION / REMAINING)
    public Invoice findByBookingIdAndType(int bookingId, String invoiceType) {

        // SELECT TOP 1 để lấy invoice mới nhất
        // ORDER BY invoice_id DESC để đảm bảo lấy bản ghi gần nhất
        return queryOne(
            "SELECT TOP 1 * FROM Invoice WHERE booking_id = ? AND invoice_type = ? ORDER BY invoice_id DESC",
            bookingId, invoiceType
        );
    }

    // Lấy tất cả invoice của một booking
    public List<Invoice> findAllByBookingId(int bookingId) {

        // Lấy danh sách invoice theo bookingId
        // ORDER BY issued_date để sắp xếp theo thời gian tạo
        return queryList(
                "SELECT * FROM Invoice WHERE booking_id = ? ORDER BY issued_date",
                bookingId
        );
    }
}