package com.mycompany.hotelmanagementsystem.controller.common;

import com.mycompany.hotelmanagementsystem.model.Invoice;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Account;
import com.mycompany.hotelmanagementsystem.model.Payment;
import com.mycompany.hotelmanagementsystem.utils.SessionHelper;
import com.mycompany.hotelmanagementsystem.service.BookingService;
import com.mycompany.hotelmanagementsystem.service.PaymentService;
import com.mycompany.hotelmanagementsystem.service.VNPayService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/payment/process", "/payment/vnpay", "/payment/vnpay-return", "/payment/result"})
public class PaymentController extends HttpServlet {
    private PaymentService paymentService;
    private BookingService bookingService;

    @Override
    public void init() {
        paymentService = new PaymentService();
        bookingService = new BookingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/payment/process" -> handleProcessGet(request, response);
            
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/payment/vnpay".equals(request.getServletPath())) {
           
        }
    }

    private void handleProcessGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer bookingId = parseIntParam(request, "bookingId");
        if (bookingId == null) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
            return;
        }

        Account account = SessionHelper.getLoggedInAccount(request);
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking == null || booking.getCustomerId() != account.getAccountId()) {
            response.sendError(403);
            return;
        }

        if ("Confirmed".equals(booking.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/booking/status?bookingId=" + bookingId);
            return;
        }

        Invoice invoice = paymentService.getOrCreateInvoice(bookingId);
        if (invoice == null) {
            request.setAttribute("error", "Không thể tạo hóa đơn");
        }

        request.setAttribute("booking", booking);
        request.setAttribute("invoice", invoice);
        request.getRequestDispatcher("/WEB-INF/views/payment/process.jsp").forward(request, response);
    }
    private Integer parseIntParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null && !value.isEmpty()) {
            try { return Integer.parseInt(value); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }
    
}
