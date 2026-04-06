package com.mycompany.hotelmanagementsystem.service;

<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.entity.Booking;
import com.mycompany.hotelmanagementsystem.entity.Invoice;
import com.mycompany.hotelmanagementsystem.entity.Payment;
import com.mycompany.hotelmanagementsystem.dal.BookingRepository;
import com.mycompany.hotelmanagementsystem.dal.InvoiceRepository;
import com.mycompany.hotelmanagementsystem.dal.PaymentRepository;
import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.constant.PaymentStatus;
import com.mycompany.hotelmanagementsystem.util.PaymentResult;
=======
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Payment;
import com.mycompany.hotelmanagementsystem.model.BookingExtension;
import com.mycompany.hotelmanagementsystem.model.Invoice;
import com.mycompany.hotelmanagementsystem.dao.InvoiceRepository;
import com.mycompany.hotelmanagementsystem.dao.PaymentRepository;
import com.mycompany.hotelmanagementsystem.dao.BookingExtensionRepository;
import com.mycompany.hotelmanagementsystem.dao.BookingRepository;
import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.constant.InvoiceType;
import com.mycompany.hotelmanagementsystem.constant.PaymentStatus;
import com.mycompany.hotelmanagementsystem.constant.PaymentType;
import com.mycompany.hotelmanagementsystem.utils.PaymentResult;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import com.mycompany.hotelmanagementsystem.service.VNPayService;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PaymentService {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
<<<<<<< HEAD
=======
    private final BookingExtensionRepository extensionRepository;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b

    public PaymentService() {
        this.invoiceRepository = new InvoiceRepository();
        this.paymentRepository = new PaymentRepository();
        this.bookingRepository = new BookingRepository();
<<<<<<< HEAD
    }

    public Invoice getOrCreateInvoice(int bookingId) {
        Invoice existing = invoiceRepository.findByBookingId(bookingId);
=======
        this.extensionRepository = new BookingExtensionRepository();
    }

    public Invoice getOrCreateInvoice(int bookingId) {
        return getOrCreateInvoice(bookingId, InvoiceType.BOOKING);
    }

    public Invoice getOrCreateInvoice(int bookingId, String invoiceType) {
        // Check existing invoice of this type
        Invoice existing = invoiceRepository.findByBookingIdAndType(bookingId, invoiceType);
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        if (existing != null) return existing;

        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) return null;

<<<<<<< HEAD
        BigDecimal subtotal = booking.getTotalPrice();
=======
        BigDecimal subtotal;
        // Determine amount based on invoice type and payment type
        if (InvoiceType.REMAINING.equals(invoiceType)) {
            // Remaining balance = total - deposit
            BigDecimal deposit = booking.getDepositAmount() != null ? booking.getDepositAmount() : BigDecimal.ZERO;
            subtotal = booking.getTotalPrice().subtract(deposit);
            if (subtotal.compareTo(BigDecimal.ZERO) <= 0) return null; // Nothing remaining
        } else if (PaymentType.DEPOSIT.equals(booking.getPaymentType())
                   && InvoiceType.BOOKING.equals(invoiceType)) {
            // Deposit payment: invoice for deposit amount only
            subtotal = booking.getDepositAmount() != null ? booking.getDepositAmount() : booking.getTotalPrice();
        } else {
            // Full payment
            subtotal = booking.getTotalPrice();
        }

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        Invoice invoice = new Invoice();
        invoice.setBookingId(bookingId);
        invoice.setTotalAmount(totalAmount);
        invoice.setTaxAmount(taxAmount);
<<<<<<< HEAD
=======
        invoice.setInvoiceType(invoiceType);
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b

        int invoiceId = invoiceRepository.insert(invoice);
        if (invoiceId <= 0) return null;

        invoice.setInvoiceId(invoiceId);
        return invoice;
    }

<<<<<<< HEAD
=======
    // Create invoice for extension payment
    public Invoice createExtensionInvoice(int bookingId, BigDecimal extensionPrice) {
        BigDecimal taxAmount = extensionPrice.multiply(TAX_RATE).setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalAmount = extensionPrice.add(taxAmount);

        Invoice invoice = new Invoice();
        invoice.setBookingId(bookingId);
        invoice.setTotalAmount(totalAmount);
        invoice.setTaxAmount(taxAmount);
        invoice.setInvoiceType(InvoiceType.EXTENSION);

        int invoiceId = invoiceRepository.insert(invoice);
        if (invoiceId <= 0) return null;

        invoice.setInvoiceId(invoiceId);
        return invoice;
    }

    // Create remaining balance invoice for checkout
    public Invoice createRemainingInvoice(int bookingId) {
        return getOrCreateInvoice(bookingId, InvoiceType.REMAINING);
    }

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    public Invoice getInvoice(int invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }

    public Invoice getInvoiceByBooking(int bookingId) {
        return invoiceRepository.findByBookingId(bookingId);
    }

    public PaymentResult initiateVNPayPayment(int invoiceId, int customerId, String baseUrl, String ipAddress) {
        Invoice invoice = invoiceRepository.findById(invoiceId);
        if (invoice == null) {
<<<<<<< HEAD
            return PaymentResult.failure("Không tìm thấy hóa đơn");
        }

        if (paymentRepository.hasSuccessfulPayment(invoiceId)) {
            return PaymentResult.failure("Hóa đơn đã được thanh toán");
=======
            return PaymentResult.failure("Khong tim thay hoa don");
        }

        if (paymentRepository.hasSuccessfulPayment(invoiceId)) {
            return PaymentResult.failure("Hoa don da duoc thanh toan");
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        }

        String txnRef = VNPayService.generateTxnRef();
        long amount = invoice.getTotalAmount().longValue();
        String orderInfo = "Thanh toan dat phong - Invoice " + invoiceId;

        Payment payment = new Payment();
        payment.setInvoiceId(invoiceId);
        payment.setCustomerId(customerId);
        payment.setPaymentMethod("VNPay");
        payment.setTransactionCode(txnRef);
        payment.setAmount(invoice.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);

        int paymentId = paymentRepository.insert(payment);
        if (paymentId <= 0) {
<<<<<<< HEAD
            return PaymentResult.failure("Không thể tạo thanh toán");
=======
            return PaymentResult.failure("Khong the tao thanh toan");
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        }

        payment.setPaymentId(paymentId);

        String paymentUrl = VNPayService.createPaymentUrl(baseUrl, txnRef, amount, orderInfo, ipAddress);
        return PaymentResult.successWithUrl(payment, paymentUrl);
    }

    public PaymentResult processVNPayCallback(String txnRef, String responseCode) {
        Payment payment = paymentRepository.findByTransactionCode(txnRef);
        if (payment == null) {
<<<<<<< HEAD
            return PaymentResult.failure("Không tìm thấy thanh toán");
        }

        if (!PaymentStatus.PENDING.equals(payment.getStatus())) {
            return PaymentResult.failure("Thanh toán đã được xử lý");
=======
            return PaymentResult.failure("Khong tim thay thanh toan");
        }

        if (!PaymentStatus.PENDING.equals(payment.getStatus())) {
            return PaymentResult.failure("Thanh toan da duoc xu ly");
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        }

        boolean success = VNPayService.isPaymentSuccess(responseCode);
        String newStatus = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        paymentRepository.updateStatus(payment.getPaymentId(), newStatus);
        payment.setStatus(newStatus);

        if (success) {
            Invoice invoice = invoiceRepository.findById(payment.getInvoiceId());
            if (invoice != null) {
<<<<<<< HEAD
                bookingRepository.updateStatus(invoice.getBookingId(), BookingStatus.CONFIRMED);
            }
        }

        return PaymentResult.success(success ? "Thanh toán thành công" : "Thanh toán thất bại", payment);
=======
                if (InvoiceType.BOOKING.equals(invoice.getInvoiceType())) {
                    // Confirm booking after successful booking payment
                    bookingRepository.updateStatus(invoice.getBookingId(), BookingStatus.CONFIRMED);
                } else if (InvoiceType.EXTENSION.equals(invoice.getInvoiceType())) {
                    // Confirm extension after successful extension payment
                    BookingExtension ext = extensionRepository.findPendingByBookingId(invoice.getBookingId());
                    if (ext != null) {
                        extensionRepository.updateStatus(ext.getExtensionId(), "Confirmed");
                        bookingRepository.updateCheckOutExpected(ext.getBookingId(), ext.getNewCheckOut());
                    }
                }
            }
        }

        return PaymentResult.success(success ? "Thanh toan thanh cong" : "Thanh toan that bai", payment);
    }

    /**
     * Process VNPay IPN (server-to-server callback).
     * Returns IPN response code: "00" = success, "01" = order not found,
     * "02" = already confirmed, "04" = invalid amount, "97" = invalid signature, "99" = error.
     */
    public String[] processVNPayIPN(String txnRef, String responseCode, long vnpAmount) {
        try {
            Payment payment = paymentRepository.findByTransactionCode(txnRef);
            if (payment == null) {
                return new String[]{"01", "Order not found"};
            }

            // Verify amount matches (VNPay sends amount * 100)
            long expectedAmount = payment.getAmount().longValue() * 100;
            if (vnpAmount != expectedAmount) {
                return new String[]{"04", "Invalid amount"};
            }

            if (!PaymentStatus.PENDING.equals(payment.getStatus())) {
                return new String[]{"02", "Order already confirmed"};
            }

            boolean success = VNPayService.isPaymentSuccess(responseCode);
            String newStatus = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
            paymentRepository.updateStatus(payment.getPaymentId(), newStatus);

            if (success) {
                Invoice invoice = invoiceRepository.findById(payment.getInvoiceId());
                if (invoice != null) {
                    if (InvoiceType.BOOKING.equals(invoice.getInvoiceType())) {
                        bookingRepository.updateStatus(invoice.getBookingId(), BookingStatus.CONFIRMED);
                    } else if (InvoiceType.EXTENSION.equals(invoice.getInvoiceType())) {
                        BookingExtension ext = extensionRepository.findPendingByBookingId(invoice.getBookingId());
                        if (ext != null) {
                            extensionRepository.updateStatus(ext.getExtensionId(), "Confirmed");
                            bookingRepository.updateCheckOutExpected(ext.getBookingId(), ext.getNewCheckOut());
                        }
                    }
                }
            }

            return new String[]{"00", "Confirm Success"};
        } catch (Exception e) {
            return new String[]{"99", "Unknown error"};
        }
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    }

    public Payment getPaymentByTransaction(String transactionCode) {
        return paymentRepository.findByTransactionCode(transactionCode);
    }

    public Booking getBookingFromPayment(Payment payment) {
        Invoice invoice = invoiceRepository.findById(payment.getInvoiceId());
        if (invoice != null) {
            return bookingRepository.findByIdWithDetails(invoice.getBookingId());
        }
        return null;
    }
<<<<<<< HEAD
=======

    public boolean hasSuccessfulPayment(int invoiceId) {
        return paymentRepository.hasSuccessfulPayment(invoiceId);
    }

    // Find latest invoice by booking and type (for Extension/Remaining flows)
    public Invoice findLatestInvoiceByType(int bookingId, String invoiceType) {
        return invoiceRepository.findByBookingIdAndType(bookingId, invoiceType);
    }
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
}
