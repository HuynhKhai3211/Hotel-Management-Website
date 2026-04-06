package com.mycompany.hotelmanagementsystem.dao;
<<<<<<< HEAD
=======

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import com.mycompany.hotelmanagementsystem.model.Payment;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

<<<<<<< HEAD
// Repository kế thừa BaseRepository để tái sử dụng các hàm chung (query, insert...)

public class PaymentRepository extends BaseRepository<Payment> {

    // Hàm map dữ liệu từ ResultSet (DB) -> object Payment
    @Override
    protected Payment mapRow(ResultSet rs) throws SQLException {

        // Tạo object Payment mới
        Payment p = new Payment();

        // Map từng cột trong DB sang field của object
        p.setPaymentId(rs.getInt("payment_id"));           // id của payment
        p.setInvoiceId(rs.getInt("invoice_id"));           // id hóa đơn
        p.setCustomerId(rs.getInt("customer_id"));         // id khách hàng

        // Phương thức thanh toán (VNPay, Cash...)
        p.setPaymentMethod(rs.getString("payment_method"));

        // Mã giao dịch VNPay hoặc hệ thống
        p.setTransactionCode(rs.getString("transaction_code"));

        // Số tiền thanh toán
        p.setAmount(rs.getBigDecimal("amount"));

        // Lấy thời gian thanh toán dạng Timestamp từ DB
        Timestamp ts = rs.getTimestamp("payment_time");

        // Nếu có thời gian thì convert sang LocalDateTime
        if (ts != null) p.setPaymentTime(ts.toLocalDateTime());

        // Trạng thái thanh toán: PENDING / SUCCESS / FAILED
        p.setStatus(rs.getString("status"));

        // Trả về object Payment đã map
        return p;
    }

    // Hàm insert payment mới vào database
    public int insert(Payment payment) {

        // Câu SQL insert payment
=======
public class PaymentRepository extends BaseRepository<Payment> {

    @Override
    protected Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setInvoiceId(rs.getInt("invoice_id"));
        p.setCustomerId(rs.getInt("customer_id"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setTransactionCode(rs.getString("transaction_code"));
        p.setAmount(rs.getBigDecimal("amount"));
        Timestamp ts = rs.getTimestamp("payment_time");
        if (ts != null) p.setPaymentTime(ts.toLocalDateTime());
        p.setStatus(rs.getString("status"));
        return p;
    }

    public int insert(Payment payment) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        String sql = """
            INSERT INTO Payment (invoice_id, customer_id, payment_method, transaction_code, amount, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
<<<<<<< HEAD

        // Gọi hàm executeInsert (được định nghĩa trong BaseRepository)
        // Truyền vào các giá trị tương ứng với dấu ?
        return executeInsert(sql,
                payment.getInvoiceId(),        // invoice_id
                payment.getCustomerId(),       // customer_id
                payment.getPaymentMethod(),    // payment_method
                payment.getTransactionCode(),  // transaction_code
                payment.getAmount(),           // amount
                payment.getStatus()            // status
        );
    }

    // Tìm payment theo paymentId
    public Payment findById(int paymentId) {

        // queryOne: lấy 1 record duy nhất
        return queryOne("SELECT * FROM Payment WHERE payment_id = ?", paymentId);
    }

    // Tìm payment theo transactionCode (dùng cho VNPay callback)
    public Payment findByTransactionCode(String transactionCode) {

        // Tìm payment theo mã giao dịch
        return queryOne("SELECT * FROM Payment WHERE transaction_code = ?", transactionCode);
    }

    // Tìm payment theo invoiceId
    public Payment findByInvoiceId(int invoiceId) {

        // Lấy danh sách payment theo invoiceId
        // ORDER BY DESC để lấy payment mới nhất
        List<Payment> payments = queryList(
                "SELECT * FROM Payment WHERE invoice_id = ? ORDER BY payment_id DESC",
                invoiceId
        );

        // Nếu danh sách rỗng thì trả null
        // nếu có thì trả payment mới nhất (index 0)
        return payments.isEmpty() ? null : payments.get(0);
    }

    // Cập nhật trạng thái payment
    public int updateStatus(int paymentId, String status) {

        // Update status theo paymentId
        return executeUpdate(
                "UPDATE Payment SET status = ? WHERE payment_id = ?",
                status, paymentId
        );
    }

    // Kiểm tra invoice đã có payment thành công chưa
    public boolean hasSuccessfulPayment(int invoiceId) {

        // Câu SQL đếm số payment SUCCESS theo invoiceId
        String sql = "SELECT COUNT(*) FROM Payment WHERE invoice_id = ? AND status = 'Success'";

        try (
            // Lấy connection từ DB
            var conn = getConnection();

            // Tạo PreparedStatement
            var ps = conn.prepareStatement(sql)
        ) {

            // Set giá trị invoiceId vào dấu ?
            ps.setInt(1, invoiceId);

            // Execute query
            try (var rs = ps.executeQuery()) {

                // Nếu có kết quả
                if (rs.next()) {

                    // Nếu count > 0 nghĩa là đã có thanh toán thành công
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {

            // Nếu lỗi DB thì throw runtime exception
            throw new RuntimeException("Check payment failed", e);
        }

        // Mặc định trả false nếu không có payment success
        return false;
    }
}
=======
        return executeInsert(sql, payment.getInvoiceId(), payment.getCustomerId(),
            payment.getPaymentMethod(), payment.getTransactionCode(),
            payment.getAmount(), payment.getStatus());
    }

    public Payment findById(int paymentId) {
        return queryOne("SELECT * FROM Payment WHERE payment_id = ?", paymentId);
    }

    public Payment findByTransactionCode(String transactionCode) {
        return queryOne("SELECT * FROM Payment WHERE transaction_code = ?", transactionCode);
    }

    public Payment findByInvoiceId(int invoiceId) {
        List<Payment> payments = queryList("SELECT * FROM Payment WHERE invoice_id = ? ORDER BY payment_id DESC", invoiceId);
        return payments.isEmpty() ? null : payments.get(0);
    }

    public int updateStatus(int paymentId, String status) {
        return executeUpdate("UPDATE Payment SET status = ? WHERE payment_id = ?", status, paymentId);
    }

    public boolean hasSuccessfulPayment(int invoiceId) {
        String sql = "SELECT COUNT(*) FROM Payment WHERE invoice_id = ? AND status = 'Success'";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Check payment failed", e);
        }
        return false;
    }

    public java.math.BigDecimal sumByDateRange(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM Payment WHERE status = 'Success' AND payment_time BETWEEN ? AND ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Sum payment failed", e);
        }
        return java.math.BigDecimal.ZERO;
    }
}
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
