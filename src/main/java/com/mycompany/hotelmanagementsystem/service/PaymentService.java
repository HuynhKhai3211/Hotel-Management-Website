package com.mycompany.hotelmanagementsystem.service;
import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.constant.InvoiceType;
import com.mycompany.hotelmanagementsystem.constant.PaymentStatus;
import com.mycompany.hotelmanagementsystem.constant.PaymentType;
import com.mycompany.hotelmanagementsystem.service.VNPayService;
import com.mycompany.hotelmanagementsystem.dao.BookingExtensionRepository;
import com.mycompany.hotelmanagementsystem.dao.BookingRepository;
import com.mycompany.hotelmanagementsystem.dao.InvoiceRepository;
import com.mycompany.hotelmanagementsystem.dao.PaymentRepository;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.BookingExtension;
import com.mycompany.hotelmanagementsystem.model.Invoice;
import com.mycompany.hotelmanagementsystem.model.Payment;
import com.mycompany.hotelmanagementsystem.utils.PaymentResult;

// Import BigDecimal để tính toán tiền chính xác
import java.math.BigDecimal;

// Import RoundingMode để quy định cách làm tròn số
import java.math.RoundingMode;

// Class service chứa toàn bộ business logic liên quan đến thanh toán
public class PaymentService {

    // Hằng số thuế 10%
    // Dùng BigDecimal thay vì double để tránh sai số khi tính tiền
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    // Repository thao tác với bảng Invoice
    private final InvoiceRepository invoiceRepository;

    // Repository thao tác với bảng Payment
    private final PaymentRepository paymentRepository;

    // Repository thao tác với bảng Booking
    private final BookingRepository bookingRepository;

    // Repository thao tác với bảng BookingExtension
    private final BookingExtensionRepository extensionRepository;

    // Constructor dùng để khởi tạo các repository
    public PaymentService() {

        // Khởi tạo repository hóa đơn
        this.invoiceRepository = new InvoiceRepository();

        // Khởi tạo repository thanh toán
        this.paymentRepository = new PaymentRepository();

        // Khởi tạo repository booking
        this.bookingRepository = new BookingRepository();

        // Khởi tạo repository extension
        this.extensionRepository = new BookingExtensionRepository();
    }

    // Hàm tiện ích: khi chỉ truyền bookingId
    // thì mặc định sẽ tạo/lấy invoice loại BOOKING
    public Invoice getOrCreateInvoice(int bookingId) {

        // Gọi sang hàm overload bên dưới với loại invoice mặc định là BOOKING
        return getOrCreateInvoice(bookingId, InvoiceType.BOOKING);
    }

    // Hàm tạo hoặc lấy invoice theo bookingId và loại invoice
    public Invoice getOrCreateInvoice(int bookingId, String invoiceType) {

        // Tìm xem đã có hóa đơn của booking này với loại invoice này chưa
        // Ví dụ booking 5 đã có invoice BOOKING rồi thì không tạo lại nữa
        Invoice existing = invoiceRepository.findByBookingIdAndType(bookingId, invoiceType);

        // Nếu đã tồn tại thì trả về invoice cũ luôn
        if (existing != null) return existing;

        // Lấy booking từ database theo bookingId
        Booking booking = bookingRepository.findById(bookingId);

        // Nếu không tìm thấy booking thì không thể tạo invoice
        if (booking == null) return null;

        // Biến subtotal dùng để lưu số tiền trước thuế
        BigDecimal subtotal;

        // Xác định số tiền cần thanh toán dựa vào loại hóa đơn và kiểu thanh toán
        if (InvoiceType.REMAINING.equals(invoiceType)) {

            // Nếu là hóa đơn REMAINING thì số tiền còn lại = tổng tiền - tiền cọc

            // Lấy tiền cọc, nếu booking chưa có depositAmount thì cho = 0
            BigDecimal deposit = booking.getDepositAmount() != null ? booking.getDepositAmount() : BigDecimal.ZERO;

            // Tính phần tiền còn lại cần thanh toán
            subtotal = booking.getTotalPrice().subtract(deposit);

            // Nếu phần còn lại <= 0 nghĩa là không còn gì để thanh toán nữa
            if (subtotal.compareTo(BigDecimal.ZERO) <= 0) return null;

        } else if (PaymentType.DEPOSIT.equals(booking.getPaymentType())
                   && InvoiceType.BOOKING.equals(invoiceType)) {

            // Nếu booking chọn kiểu thanh toán DEPOSIT
            // và đang tạo invoice loại BOOKING
            // thì invoice ban đầu chỉ tính tiền cọc, không tính toàn bộ

            // Nếu có depositAmount thì lấy tiền cọc
            // nếu không có thì fallback sang totalPrice
            subtotal = booking.getDepositAmount() != null ? booking.getDepositAmount() : booking.getTotalPrice();

        } else {

            // Các trường hợp còn lại là thanh toán full
            // tức là subtotal = toàn bộ tổng tiền booking
            subtotal = booking.getTotalPrice();
        }

        // Tính số tiền thuế = subtotal * 10%
        // setScale(0, RoundingMode.HALF_UP) nghĩa là làm tròn tới số nguyên gần nhất
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(0, RoundingMode.HALF_UP);

        // Tính tổng tiền thanh toán = tiền trước thuế + tiền thuế
        BigDecimal totalAmount = subtotal.add(taxAmount);

        // Tạo đối tượng Invoice mới
        Invoice invoice = new Invoice();

        // Gán bookingId cho invoice để biết hóa đơn này thuộc booking nào
        invoice.setBookingId(bookingId);

        // Gán tổng tiền phải thanh toán cho invoice
        invoice.setTotalAmount(totalAmount);

        // Gán tiền thuế cho invoice
        invoice.setTaxAmount(taxAmount);

        // Gán loại hóa đơn: BOOKING / EXTENSION / REMAINING
        invoice.setInvoiceType(invoiceType);

        // Insert invoice vào database
        // Hàm insert sẽ trả về id vừa tạo
        int invoiceId = invoiceRepository.insert(invoice);

        // Nếu insert lỗi hoặc không thành công thì trả null
        if (invoiceId <= 0) return null;

        // Gán lại invoiceId vào object invoice trong memory
        invoice.setInvoiceId(invoiceId);

        // Trả về invoice vừa tạo
        return invoice;
    }

    // Tạo hóa đơn cho phần gia hạn thời gian ở
    public Invoice createExtensionInvoice(int bookingId, BigDecimal extensionPrice) {

        // Tính tiền thuế cho phần gia hạn
        BigDecimal taxAmount = extensionPrice.multiply(TAX_RATE).setScale(0, RoundingMode.HALF_UP);

        // Tổng tiền = tiền gia hạn + thuế
        BigDecimal totalAmount = extensionPrice.add(taxAmount);

        // Tạo object Invoice mới
        Invoice invoice = new Invoice();

        // Gán bookingId
        invoice.setBookingId(bookingId);

        // Gán tổng tiền
        invoice.setTotalAmount(totalAmount);

        // Gán tiền thuế
        invoice.setTaxAmount(taxAmount);

        // Gán loại hóa đơn là EXTENSION
        invoice.setInvoiceType(InvoiceType.EXTENSION);

        // Lưu invoice vào database
        int invoiceId = invoiceRepository.insert(invoice);

        // Nếu lưu thất bại thì trả null
        if (invoiceId <= 0) return null;

        // Gán id vừa tạo vào object invoice
        invoice.setInvoiceId(invoiceId);

        // Trả về invoice vừa tạo
        return invoice;
    }

    // Tạo hóa đơn cho phần tiền còn lại lúc checkout
    public Invoice createRemainingInvoice(int bookingId) {

        // Gọi lại hàm getOrCreateInvoice với loại REMAINING
        return getOrCreateInvoice(bookingId, InvoiceType.REMAINING);
    }

    // Lấy invoice theo invoiceId
    public Invoice getInvoice(int invoiceId) {

        // Truy vấn invoice từ database theo id
        return invoiceRepository.findById(invoiceId);
    }

    // Lấy invoice theo bookingId
    public Invoice getInvoiceByBooking(int bookingId) {

        // Truy vấn invoice theo booking
        return invoiceRepository.findByBookingId(bookingId);
    }

    // Khởi tạo giao dịch thanh toán VNPay
    public PaymentResult initiateVNPayPayment(int invoiceId, int customerId, String baseUrl, String ipAddress) {

        // Lấy invoice theo invoiceId
        Invoice invoice = invoiceRepository.findById(invoiceId);

        // Nếu không tìm thấy invoice thì trả failure
        if (invoice == null) {
            return PaymentResult.failure("Khong tim thay hoa don");
        }

        // Kiểm tra hóa đơn này đã có payment thành công chưa
        // Nếu rồi thì không cho thanh toán lại
        if (paymentRepository.hasSuccessfulPayment(invoiceId)) {
            return PaymentResult.failure("Hoa don da duoc thanh toan");
        }

        // Sinh ra mã giao dịch duy nhất để gửi sang VNPay
        String txnRef = VNPayService.generateTxnRef();

        // Lấy số tiền từ invoice
        // longValue() vì VNPay thường dùng amount kiểu số nguyên
        long amount = invoice.getTotalAmount().longValue();

        // Nội dung đơn hàng gửi sang VNPay
        String orderInfo = "Thanh toan dat phong - Invoice " + invoiceId;

        // Tạo object Payment mới
        Payment payment = new Payment();

        // Gắn invoiceId cho payment
        payment.setInvoiceId(invoiceId);

        // Gắn customerId của người thanh toán
        payment.setCustomerId(customerId);

        // Gắn phương thức thanh toán là VNPay
        payment.setPaymentMethod("VNPay");

        // Gắn mã giao dịch VNPay
        payment.setTransactionCode(txnRef);

        // Gắn số tiền phải thanh toán
        payment.setAmount(invoice.getTotalAmount());

        // Ban đầu payment có trạng thái PENDING
        // vì khách mới chỉ bắt đầu giao dịch, chưa biết thành công hay thất bại
        payment.setStatus(PaymentStatus.PENDING);

        // Lưu payment vào database
        int paymentId = paymentRepository.insert(payment);

        // Nếu tạo payment thất bại thì trả lỗi
        if (paymentId <= 0) {
            return PaymentResult.failure("Khong the tao thanh toan");
        }

        // Gán paymentId vừa tạo vào object payment
        payment.setPaymentId(paymentId);

        // Tạo URL thanh toán VNPay
        // URL này sẽ dùng để redirect user sang cổng thanh toán
        String paymentUrl = VNPayService.createPaymentUrl(baseUrl, txnRef, amount, orderInfo, ipAddress);

        // Trả về kết quả thành công kèm payment và URL
        return PaymentResult.successWithUrl(payment, paymentUrl);
    }

    // Xử lý callback từ VNPay khi user thanh toán xong và quay lại hệ thống
    public PaymentResult processVNPayCallback(String txnRef, String responseCode) {

        // Tìm payment theo transactionCode
        Payment payment = paymentRepository.findByTransactionCode(txnRef);

        // Nếu không tìm thấy payment thì trả failure
        if (payment == null) {
            return PaymentResult.failure("Khong tim thay thanh toan");
        }

        // Nếu payment không còn ở trạng thái PENDING
        // nghĩa là giao dịch này đã được xử lý rồi
        if (!PaymentStatus.PENDING.equals(payment.getStatus())) {
            return PaymentResult.failure("Thanh toan da duoc xu ly");
        }

        // Kiểm tra mã phản hồi từ VNPay có phải thành công không
        boolean success = VNPayService.isPaymentSuccess(responseCode);

        // Nếu thành công -> SUCCESS, thất bại -> FAILED
        String newStatus = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        // Cập nhật trạng thái payment trong database
        paymentRepository.updateStatus(payment.getPaymentId(), newStatus);

        // Cập nhật luôn object payment trong bộ nhớ
        payment.setStatus(newStatus);

        // Nếu thanh toán thành công thì cập nhật thêm nghiệp vụ liên quan
        if (success) {

            // Lấy invoice tương ứng với payment này
            Invoice invoice = invoiceRepository.findById(payment.getInvoiceId());

            // Nếu có invoice
            if (invoice != null) {

                // Nếu hóa đơn là BOOKING
                if (InvoiceType.BOOKING.equals(invoice.getInvoiceType())) {

                    // Sau khi thanh toán booking thành công
                    // cập nhật trạng thái booking thành CONFIRMED
                    bookingRepository.updateStatus(invoice.getBookingId(), BookingStatus.CONFIRMED);

                } else if (InvoiceType.EXTENSION.equals(invoice.getInvoiceType())) {

                    // Nếu là hóa đơn gia hạn
                    // tìm extension đang chờ xác nhận của booking đó
                    BookingExtension ext = extensionRepository.findPendingByBookingId(invoice.getBookingId());

                    // Nếu tìm thấy extension pending
                    if (ext != null) {

                        // Cập nhật trạng thái extension thành Confirmed
                        extensionRepository.updateStatus(ext.getExtensionId(), "Confirmed");

                        // Cập nhật thời gian checkout dự kiến mới cho booking
                        bookingRepository.updateCheckOutExpected(ext.getBookingId(), ext.getNewCheckOut());
                    }
                }
            }
        }

        // Trả kết quả xử lý callback
        // Nếu success thì message là thành công
        // nếu không thì message là thất bại
        return PaymentResult.success(success ? "Thanh toan thanh cong" : "Thanh toan that bai", payment);
    }

    /**
     * Xử lý IPN từ VNPay.
     * IPN là callback server-to-server, nghĩa là VNPay gọi trực tiếp tới server của hệ thống.
     *
     * Các mã trả về:
     * "00" = success
     * "01" = order not found
     * "02" = already confirmed
     * "04" = invalid amount
     * "97" = invalid signature
     * "99" = error
     */
    public String[] processVNPayIPN(String txnRef, String responseCode, long vnpAmount) {
        try {

            // Tìm payment theo transactionCode
            Payment payment = paymentRepository.findByTransactionCode(txnRef);

            // Nếu không tìm thấy payment thì trả mã 01
            if (payment == null) {
                return new String[]{"01", "Order not found"};
            }

            // Kiểm tra số tiền có khớp không
            // VNPay thường gửi amount * 100 nên expectedAmount cũng phải nhân 100
            long expectedAmount = payment.getAmount().longValue() * 100;

            // Nếu số tiền gửi từ VNPay không khớp số tiền hệ thống lưu
            if (vnpAmount != expectedAmount) {
                return new String[]{"04", "Invalid amount"};
            }

            // Nếu payment không còn PENDING thì nghĩa là đã xử lý rồi
            if (!PaymentStatus.PENDING.equals(payment.getStatus())) {
                return new String[]{"02", "Order already confirmed"};
            }

            // Kiểm tra giao dịch thành công hay không
            boolean success = VNPayService.isPaymentSuccess(responseCode);

            // Xác định trạng thái mới
            String newStatus = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

            // Cập nhật trạng thái payment trong DB
            paymentRepository.updateStatus(payment.getPaymentId(), newStatus);

            // Nếu thanh toán thành công thì cập nhật nghiệp vụ liên quan
            if (success) {

                // Tìm invoice tương ứng với payment
                Invoice invoice = invoiceRepository.findById(payment.getInvoiceId());

                // Nếu invoice tồn tại
                if (invoice != null) {

                    // Nếu đây là hóa đơn BOOKING
                    if (InvoiceType.BOOKING.equals(invoice.getInvoiceType())) {

                        // Cập nhật booking thành CONFIRMED
                        bookingRepository.updateStatus(invoice.getBookingId(), BookingStatus.CONFIRMED);

                    } else if (InvoiceType.EXTENSION.equals(invoice.getInvoiceType())) {

                        // Nếu là hóa đơn gia hạn thì tìm extension pending
                        BookingExtension ext = extensionRepository.findPendingByBookingId(invoice.getBookingId());

                        // Nếu có extension pending
                        if (ext != null) {

                            // Xác nhận extension
                            extensionRepository.updateStatus(ext.getExtensionId(), "Confirmed");

                            // Cập nhật checkout dự kiến mới
                            bookingRepository.updateCheckOutExpected(ext.getBookingId(), ext.getNewCheckOut());
                        }
                    }
                }
            }

            // Nếu xử lý thành công thì trả mã 00
            return new String[]{"00", "Confirm Success"};

        } catch (Exception e) {

            // Nếu có lỗi ngoài ý muốn thì trả mã 99
            return new String[]{"99", "Unknown error"};
        }
    }

    // Lấy payment theo transactionCode
    public Payment getPaymentByTransaction(String transactionCode) {

        // Truy vấn payment trong database
        return paymentRepository.findByTransactionCode(transactionCode);
    }

    // Từ payment suy ra booking tương ứng
    public Booking getBookingFromPayment(Payment payment) {

        // Tìm invoice dựa trên invoiceId của payment
        Invoice invoice = invoiceRepository.findById(payment.getInvoiceId());

        // Nếu invoice tồn tại
        if (invoice != null) {

            // Tìm booking theo bookingId, kèm thêm details nếu repository hỗ trợ
            return bookingRepository.findByIdWithDetails(invoice.getBookingId());
        }

        // Nếu không có invoice thì trả null
        return null;
    }

    // Kiểm tra một invoice đã có payment thành công hay chưa
    public boolean hasSuccessfulPayment(int invoiceId) {

        // Trả về true nếu đã có thanh toán thành công
        return paymentRepository.hasSuccessfulPayment(invoiceId);
    }

    // Tìm invoice theo bookingId và invoiceType
    // Dùng cho các flow như Extension hoặc Remaining
    public Invoice findLatestInvoiceByType(int bookingId, String invoiceType) {

        // Trả về invoice tìm được
        return invoiceRepository.findByBookingIdAndType(bookingId, invoiceType);
    }
}