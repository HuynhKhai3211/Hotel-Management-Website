package com.mycompany.hotelmanagementsystem.dao;
import com.mycompany.hotelmanagementsystem.model.Payment;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

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
        String sql = """
            INSERT INTO Payment (invoice_id, customer_id, payment_method, transaction_code, amount, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

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