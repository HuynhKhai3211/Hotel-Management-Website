package com.mycompany.hotelmanagementsystem.service;
import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.constant.InvoiceType;
import com.mycompany.hotelmanagementsystem.constant.PaymentStatus;
import com.mycompany.hotelmanagementsystem.constant.PaymentType;
import com.mycompany.hotelmanagementsystem.dao.BookingRepository;
import com.mycompany.hotelmanagementsystem.dao.InvoiceRepository;
import com.mycompany.hotelmanagementsystem.dao.PaymentRepository;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Invoice;
import com.mycompany.hotelmanagementsystem.model.Payment;
import com.mycompany.hotelmanagementsystem.utils.PaymentResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
public class StaffPaymentService {

    // Repository thao tác với bảng Payment
    private final PaymentRepository paymentRepository;

    // Repository thao tác với bảng Invoice
    private final InvoiceRepository invoiceRepository;

    // Repository thao tác với bảng Booking
    private final BookingRepository bookingRepository;

    // Constructor khởi tạo các repository
    public StaffPaymentService() {

        // Khởi tạo PaymentRepository
        this.paymentRepository = new PaymentRepository();

        // Khởi tạo InvoiceRepository
        this.invoiceRepository = new InvoiceRepository();

        // Khởi tạo BookingRepository
        this.bookingRepository = new BookingRepository();
    }

    // Hàm tiện ích:
    // nếu chỉ truyền bookingId thì mặc định lấy/tạo invoice loại BOOKING
    public Invoice getOrCreateInvoice(int bookingId) {

        // Gọi sang hàm overload với loại invoice mặc định là BOOKING
        return getOrCreateInvoice(bookingId, InvoiceType.BOOKING);
    }

    // Hàm lấy hoặc tạo invoice theo bookingId và invoiceType
    public Invoice getOrCreateInvoice(int bookingId, String invoiceType) {

        // Tìm invoice đã tồn tại của booking này theo đúng type
        // để tránh tạo trùng invoice
        Invoice existing = invoiceRepository.findByBookingIdAndType(bookingId, invoiceType);

        // Nếu đã có invoice rồi thì trả luôn
        if (existing != null) return existing;

        // Lấy booking chi tiết từ DB
        Booking booking = bookingRepository.findByIdWithDetails(bookingId);

        // Nếu không có booking thì không thể tạo invoice
        if (booking == null) return null;

        // Biến subtotal chứa số tiền trước khi lưu vào invoice
        BigDecimal subtotal;

        // Nếu invoiceType là REMAINING
        if (InvoiceType.REMAINING.equals(invoiceType)) {

            // Lấy tiền cọc, nếu null thì gán bằng 0
            BigDecimal deposit = booking.getDepositAmount() != null ? booking.getDepositAmount() : BigDecimal.ZERO;

            // Tính phần tiền còn lại = tổng tiền - tiền cọc
            subtotal = booking.getTotalPrice().subtract(deposit);

            // Nếu phần còn lại <= 0 thì không cần tạo invoice nữa
            if (subtotal.compareTo(BigDecimal.ZERO) <= 0) return null;

        // Nếu booking chọn kiểu thanh toán DEPOSIT
        // và đang tạo invoice BOOKING
        } else if (PaymentType.DEPOSIT.equals(booking.getPaymentType())
                   && InvoiceType.BOOKING.equals(invoiceType)) {

            // Invoice ban đầu chỉ lấy phần tiền cọc
            // nếu không có depositAmount thì fallback sang totalPrice
            subtotal = booking.getDepositAmount() != null ? booking.getDepositAmount() : booking.getTotalPrice();

        } else {

            // Các trường hợp còn lại là thanh toán toàn bộ
            subtotal = booking.getTotalPrice();
        }

        // Tạo object Invoice mới
        Invoice invoice = new Invoice();

        // Gắn bookingId cho invoice
        invoice.setBookingId(bookingId);

        // Gắn thời gian phát hành invoice là thời điểm hiện tại
        invoice.setIssuedDate(LocalDateTime.now());

        // Gắn tổng tiền cho invoice
        invoice.setTotalAmount(subtotal);

        // Staff flow hiện tại chưa tính thuế nên set bằng 0
        invoice.setTaxAmount(BigDecimal.ZERO);

        // Gắn loại invoice
        invoice.setInvoiceType(invoiceType);

        // Insert invoice vào database
        int invoiceId = invoiceRepository.insert(invoice);

        // Nếu insert thành công
        if (invoiceId > 0) {

            // Gán invoiceId mới tạo vào object invoice
            invoice.setInvoiceId(invoiceId);

            // Gắn luôn booking object để tiện dùng ở layer trên
            invoice.setBooking(booking);

            // Trả về invoice vừa tạo
            return invoice;
        }

        // Nếu insert thất bại thì trả null
        return null;
    }

    // Tạo invoice phần tiền còn lại khi checkout
    public Invoice createRemainingInvoice(int bookingId) {

        // Gọi lại hàm getOrCreateInvoice với type = REMAINING
        return getOrCreateInvoice(bookingId, InvoiceType.REMAINING);
    }

    // Kiểm tra booking có còn số dư chưa thanh toán hay không
    public boolean hasUnpaidRemainingBalance(int bookingId) {

        // Lấy booking từ DB
        Booking booking = bookingRepository.findById(bookingId);

        // Nếu booking không tồn tại
        // hoặc booking không dùng hình thức DEPOSIT
        // thì không có remaining balance
        if (booking == null || !PaymentType.DEPOSIT.equals(booking.getPaymentType())) {
            return false;
        }

        // Lấy tiền cọc, nếu null thì = 0
        BigDecimal deposit = booking.getDepositAmount() != null ? booking.getDepositAmount() : BigDecimal.ZERO;

        // Tính số tiền còn lại
        BigDecimal remaining = booking.getTotalPrice().subtract(deposit);

        // Nếu số dư <= 0 thì không còn gì để thanh toán
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) return false;

        // Tìm invoice loại REMAINING của booking này
        Invoice remainingInvoice = invoiceRepository.findByBookingIdAndType(bookingId, InvoiceType.REMAINING);

        // Nếu chưa có invoice remaining
        // nghĩa là phần còn lại chưa được thanh toán
        if (remainingInvoice == null) return true;

        // Nếu đã có invoice thì kiểm tra invoice đó đã thanh toán thành công chưa
        return !paymentRepository.hasSuccessfulPayment(remainingInvoice.getInvoiceId());
    }

    // Lấy invoice theo bookingId
    public Invoice getInvoiceByBookingId(int bookingId) {

        // Trả về invoice BOOKING của booking
        return invoiceRepository.findByBookingId(bookingId);
    }

    // Lấy invoice theo invoiceId
    public Invoice getInvoiceById(int invoiceId) {

        // Trả về invoice theo id
        return invoiceRepository.findById(invoiceId);
    }

    // Lấy chi tiết booking theo bookingId
    public Booking getBookingDetail(int bookingId) {

        // Trả về booking có đầy đủ detail
        return bookingRepository.findByIdWithDetails(bookingId);
    }

    // UC-19.7: Ghi nhận thanh toán tiền mặt
    public boolean recordCashPayment(int invoiceId, int customerId, BigDecimal amount) {

        // Tạo object Payment mới
        Payment payment = new Payment();

        // Gắn invoiceId mà payment này thuộc về
        payment.setInvoiceId(invoiceId);

        // Gắn customerId của khách thanh toán
        payment.setCustomerId(customerId);

        // Phương thức thanh toán là tiền mặt
        payment.setPaymentMethod("Cash");

        // Tạo mã giao dịch riêng cho cash
        payment.setTransactionCode(generateTransactionCode("CASH"));

        // Gắn số tiền thanh toán
        payment.setAmount(amount);

        // Gắn thời gian thanh toán là hiện tại
        payment.setPaymentTime(LocalDateTime.now());

        // Tiền mặt được xem là thanh toán thành công ngay
        payment.setStatus(PaymentStatus.SUCCESS);

        // Insert payment vào database
        int paymentId = paymentRepository.insert(payment);

        // Nếu insert thành công
        if (paymentId > 0) {

            // Sau khi thanh toán thành công thì cập nhật trạng thái booking
            try {

                // Lấy invoice tương ứng
                Invoice invoice = invoiceRepository.findById(invoiceId);

                // Chỉ xử lý nếu invoice tồn tại và là invoice loại BOOKING
                if (invoice != null && InvoiceType.BOOKING.equals(invoice.getInvoiceType())) {

                    // Lấy booking chi tiết
                    Booking booking = bookingRepository.findByIdWithDetails(invoice.getBookingId());

                    // Nếu booking tồn tại và đang ở trạng thái PENDING
                    if (booking != null && BookingStatus.PENDING.equals(booking.getStatus())) {

                        // Cập nhật booking sang CONFIRMED
                        bookingRepository.updateStatus(invoice.getBookingId(), BookingStatus.CONFIRMED);
                    }
                }

            } catch (Exception e) {

                // Nếu việc update booking lỗi thì không làm fail toàn bộ thanh toán
                // vì payment đã được ghi nhận thành công rồi
                System.err.println("Warning: Cash payment recorded but booking status update failed: " + e.getMessage());
            }

            // Trả true nếu ghi nhận payment thành công
            return true;
        }

        // Nếu insert payment thất bại thì trả false
        return false;
    }

    // UC-19.8: Khởi tạo thanh toán VNPay cho staff checkout
    public PaymentResult initiateVNPayPayment(int invoiceId, int customerId, String baseUrl, String ipAddress) {

        // Lấy invoice theo id
        Invoice invoice = invoiceRepository.findById(invoiceId);

        // Nếu không tìm thấy invoice thì trả lỗi
        if (invoice == null) {
            return PaymentResult.failure("Khong tim thay hoa don");
        }

        // Nếu invoice đã thanh toán thành công rồi thì không cho thanh toán lại
        if (paymentRepository.hasSuccessfulPayment(invoiceId)) {
            return PaymentResult.failure("Hoa don da duoc thanh toan");
        }

        // Tạo mã giao dịch duy nhất cho VNPay
        String txnRef = VNPayService.generateTxnRef();

        // Lấy số tiền thanh toán
        long amount = invoice.getTotalAmount().longValue();

        // Nội dung đơn hàng hiển thị ở VNPay
        String orderInfo = "Thanh toan tai quay - Invoice " + invoiceId;

        // Tạo object Payment
        Payment payment = new Payment();

        // Gắn invoiceId
        payment.setInvoiceId(invoiceId);

        // Gắn customerId
        payment.setCustomerId(customerId);

        // Phương thức thanh toán là VNPay
        payment.setPaymentMethod("VNPay");

        // Gắn mã giao dịch
        payment.setTransactionCode(txnRef);

        // Gắn số tiền thanh toán
        payment.setAmount(invoice.getTotalAmount());

        // Trạng thái ban đầu là PENDING
        payment.setStatus(PaymentStatus.PENDING);

        // Insert payment vào DB
        int paymentId = paymentRepository.insert(payment);

        // Nếu insert thất bại thì trả lỗi
        if (paymentId <= 0) {
            return PaymentResult.failure("Khong the tao thanh toan");
        }

        // Gắn paymentId vừa tạo vào object payment
        payment.setPaymentId(paymentId);

        // Tạo URL thanh toán VNPay
        // dùng return URL riêng cho staff
        String paymentUrl = VNPayService.createPaymentUrl(baseUrl, txnRef, amount, orderInfo, ipAddress,
                baseUrl + "/staff/payments/vnpay-return");

        // Trả về kết quả thành công kèm payment và URL
        return PaymentResult.successWithUrl(payment, paymentUrl);
    }

    // Xử lý callback VNPay cho payment phía staff
    public PaymentResult processVNPayCallback(String txnRef, String responseCode) {

        // Tìm payment theo transaction code
        Payment payment = paymentRepository.findByTransactionCode(txnRef);

        // Nếu không tìm thấy payment thì trả lỗi
        if (payment == null) {
            return PaymentResult.failure("Khong tim thay thanh toan");
        }

        // Nếu payment không còn ở trạng thái PENDING
        // nghĩa là giao dịch đã được xử lý rồi
        if (!PaymentStatus.PENDING.equals(payment.getStatus())) {
            return PaymentResult.failure("Thanh toan da duoc xu ly");
        }

        // Kiểm tra responseCode từ VNPay có phải thành công không
        boolean success = VNPayService.isPaymentSuccess(responseCode);

        // Xác định trạng thái mới
        String newStatus = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        // Cập nhật trạng thái payment trong DB
        paymentRepository.updateStatus(payment.getPaymentId(), newStatus);

        // Cập nhật luôn object payment trong memory
        payment.setStatus(newStatus);

        // Nếu thanh toán thành công
        if (success) {

            // Lấy invoice tương ứng
            Invoice invoice = invoiceRepository.findById(payment.getInvoiceId());

            // Nếu invoice tồn tại và là BOOKING invoice
            if (invoice != null && InvoiceType.BOOKING.equals(invoice.getInvoiceType())) {

                // Lấy booking chi tiết
                Booking booking = bookingRepository.findByIdWithDetails(invoice.getBookingId());

                // Nếu booking đang PENDING thì chuyển sang CONFIRMED
                if (booking != null && BookingStatus.PENDING.equals(booking.getStatus())) {
                    bookingRepository.updateStatus(invoice.getBookingId(), BookingStatus.CONFIRMED);
                }
            }
        }

        // Trả kết quả xử lý callback
        return PaymentResult.success(success ? "Thanh toan thanh cong" : "Thanh toan that bai", payment);
    }

    // Kiểm tra invoice đã có payment thành công chưa
    public boolean hasSuccessfulPayment(int invoiceId) {

        // Trả về true nếu invoice đã được thanh toán thành công
        return paymentRepository.hasSuccessfulPayment(invoiceId);
    }

    // Lấy payment mới nhất theo invoiceId
    public Payment getPaymentByInvoiceId(int invoiceId) {

        // Trả về payment mới nhất của invoice
        return paymentRepository.findByInvoiceId(invoiceId);
    }

    // Hàm private tạo mã giao dịch
    private String generateTransactionCode(String prefix) {

        // Tạo mã theo format:
        // PREFIX-timestamp-random
        // Ví dụ: CASH-1712345678901-AB12CD
        return prefix + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}