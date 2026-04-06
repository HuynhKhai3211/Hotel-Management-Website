package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.entity.Booking;
import com.mycompany.hotelmanagementsystem.entity.Invoice;
import com.mycompany.hotelmanagementsystem.entity.Payment;
import com.mycompany.hotelmanagementsystem.dal.BookingRepository;
import com.mycompany.hotelmanagementsystem.dal.InvoiceRepository;
import com.mycompany.hotelmanagementsystem.dal.PaymentRepository;
import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.constant.PaymentStatus;
import com.mycompany.hotelmanagementsystem.util.PaymentResult;
import com.mycompany.hotelmanagementsystem.service.VNPayService;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PaymentService {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService() {
        this.invoiceRepository = new InvoiceRepository();
        this.paymentRepository = new PaymentRepository();
        this.bookingRepository = new BookingRepository();
    }

    public Invoice getOrCreateInvoice(int bookingId) {
        Invoice existing = invoiceRepository.findByBookingId(bookingId);
        if (existing != null) return existing;

        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) return null;

        BigDecimal subtotal = booking.getTotalPrice();
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        Invoice invoice = new Invoice();
        invoice.setBookingId(bookingId);
        invoice.setTotalAmount(totalAmount);
        invoice.setTaxAmount(taxAmount);

        int invoiceId = invoiceRepository.insert(invoice);
        if (invoiceId <= 0) return null;

        invoice.setInvoiceId(invoiceId);
        return invoice;
    }

    public Invoice getInvoice(int invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }

    public Invoice getInvoiceByBooking(int bookingId) {
        return invoiceRepository.findByBookingId(bookingId);
    }

    public PaymentResult initiateVNPayPayment(int invoiceId, int customerId, String baseUrl, String ipAddress) {
        Invoice invoice = invoiceRepository.findById(invoiceId);
        if (invoice == null) {
            return PaymentResult.failure("Không tìm thấy hóa đơn");
        }

        if (paymentRepository.hasSuccessfulPayment(invoiceId)) {
            return PaymentResult.failure("Hóa đơn đã được thanh toán");
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
            return PaymentResult.failure("Không thể tạo thanh toán");
        }

        payment.setPaymentId(paymentId);

        String paymentUrl = VNPayService.createPaymentUrl(baseUrl, txnRef, amount, orderInfo, ipAddress);
        return PaymentResult.successWithUrl(payment, paymentUrl);
    }

    public PaymentResult processVNPayCallback(String txnRef, String responseCode) {
        Payment payment = paymentRepository.findByTransactionCode(txnRef);
        if (payment == null) {
            return PaymentResult.failure("Không tìm thấy thanh toán");
        }

        if (!PaymentStatus.PENDING.equals(payment.getStatus())) {
            return PaymentResult.failure("Thanh toán đã được xử lý");
        }

        boolean success = VNPayService.isPaymentSuccess(responseCode);
        String newStatus = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        paymentRepository.updateStatus(payment.getPaymentId(), newStatus);
        payment.setStatus(newStatus);

        if (success) {
            Invoice invoice = invoiceRepository.findById(payment.getInvoiceId());
            if (invoice != null) {
                bookingRepository.updateStatus(invoice.getBookingId(), BookingStatus.CONFIRMED);
            }
        }

        return PaymentResult.success(success ? "Thanh toán thành công" : "Thanh toán thất bại", payment);
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
}
