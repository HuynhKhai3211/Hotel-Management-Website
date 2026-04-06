package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.PaymentStatus;
import com.mycompany.hotelmanagementsystem.entity.Booking;
import com.mycompany.hotelmanagementsystem.entity.Invoice;
import com.mycompany.hotelmanagementsystem.entity.Payment;
import com.mycompany.hotelmanagementsystem.dal.BookingRepository;
import com.mycompany.hotelmanagementsystem.dal.InvoiceRepository;
import com.mycompany.hotelmanagementsystem.dal.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class StaffPaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;

    public StaffPaymentService() {
        this.paymentRepository = new PaymentRepository();
        this.invoiceRepository = new InvoiceRepository();
        this.bookingRepository = new BookingRepository();
    }

    public Invoice getOrCreateInvoice(int bookingId) {
        Invoice invoice = invoiceRepository.findByBookingId(bookingId);
        if (invoice != null) {
            return invoice;
        }

        // Create new invoice
        Booking booking = bookingRepository.findByIdWithDetails(bookingId);
        if (booking == null) return null;

        invoice = new Invoice();
        invoice.setBookingId(bookingId);
        invoice.setIssuedDate(LocalDateTime.now());
        invoice.setTotalAmount(booking.getTotalPrice());
        invoice.setTaxAmount(BigDecimal.ZERO); // No tax for now

        int invoiceId = invoiceRepository.insert(invoice);
        if (invoiceId > 0) {
            invoice.setInvoiceId(invoiceId);
            invoice.setBooking(booking);
            return invoice;
        }
        return null;
    }

    public Invoice getInvoiceByBookingId(int bookingId) {
        return invoiceRepository.findByBookingId(bookingId);
    }

    public Invoice getInvoiceById(int invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }

    public Booking getBookingDetail(int bookingId) {
        return bookingRepository.findByIdWithDetails(bookingId);
    }

    // UC-19.7: Record Cash Payment
    public boolean recordCashPayment(int invoiceId, int customerId, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setInvoiceId(invoiceId);
        payment.setCustomerId(customerId);
        payment.setPaymentMethod("Cash");
        payment.setTransactionCode(generateTransactionCode("CASH"));
        payment.setAmount(amount);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setStatus(PaymentStatus.SUCCESS);

        int paymentId = paymentRepository.insert(payment);
        return paymentId > 0;
    }

    // UC-19.8: Record Momo Payment
    public boolean recordMomoPayment(int invoiceId, int customerId, BigDecimal amount, String transactionCode) {
        Payment payment = new Payment();
        payment.setInvoiceId(invoiceId);
        payment.setCustomerId(customerId);
        payment.setPaymentMethod("Momo");
        payment.setTransactionCode(transactionCode != null ? transactionCode : generateTransactionCode("MOMO"));
        payment.setAmount(amount);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setStatus(PaymentStatus.SUCCESS);

        int paymentId = paymentRepository.insert(payment);
        return paymentId > 0;
    }

    public boolean hasSuccessfulPayment(int invoiceId) {
        return paymentRepository.hasSuccessfulPayment(invoiceId);
    }

    public Payment getPaymentByInvoiceId(int invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }

    private String generateTransactionCode(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    // Generate Momo QR data (simplified - in production would use Momo API)
    public String generateMomoQRData(Invoice invoice) {
        // In real implementation, this would call Momo API to generate QR
        // For demo, return a placeholder string
        return "MOMO-QR-" + invoice.getInvoiceId() + "-" + invoice.getTotalAmount();
    }
}
