package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Invoice;
import com.mycompany.hotelmanagementsystem.model.Payment;
import com.mycompany.hotelmanagementsystem.dao.BookingRepository;
import com.mycompany.hotelmanagementsystem.dao.InvoiceRepository;
import com.mycompany.hotelmanagementsystem.dao.PaymentRepository;
import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.constant.PaymentStatus;
import com.mycompany.hotelmanagementsystem.utils.PaymentResult;
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
