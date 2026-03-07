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
            case "/payment/vnpay-return" -> handleVNPayReturn(request, response);
           
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/payment/vnpay".equals(request.getServletPath())) {
            handleVNPayPost(request, response);
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

    private void handleVNPayPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Integer invoiceId = parseIntParam(request, "invoiceId");
            if (invoiceId == null) {
                response.sendRedirect(request.getContextPath() + "/customer/bookings");
                return;
            }

            Account account = SessionHelper.getLoggedInAccount(request);
            if (account == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":"
                           + request.getServerPort() + request.getContextPath();
            String ipAddress = VNPayService.getIpAddress(request);

            var result = paymentService.initiateVNPayPayment(invoiceId, account.getAccountId(), baseUrl, ipAddress);

            if (!result.isSuccess()) {
                Invoice invoice = paymentService.getInvoice(invoiceId);
                request.getSession().setAttribute("paymentError", result.getMessage());
                response.sendRedirect(request.getContextPath() + "/payment/process?bookingId=" +
                    (invoice != null ? invoice.getBookingId() : ""));
                return;
            }

            // Store txnRef in session for verification
            request.getSession().setAttribute("pendingPaymentTxn", result.getPayment().getTransactionCode());

            // Redirect to VNPay
            response.sendRedirect(result.getPaymentUrl());
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("paymentError", "Lỗi hệ thống: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
        }
    }

    private void handleVNPayReturn(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Extract all VNPay parameters
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });

        // Verify signature
        if (!VNPayService.verifySignature(params)) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=invalid_signature");
            return;
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        // Verify session
        String sessionTxn = (String) request.getSession().getAttribute("pendingPaymentTxn");
        if (sessionTxn == null || !sessionTxn.equals(txnRef)) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=session_mismatch");
            return;
        }

        request.getSession().removeAttribute("pendingPaymentTxn");

        // Process callback
        paymentService.processVNPayCallback(txnRef, responseCode);

        response.sendRedirect(request.getContextPath() + "/payment/result?txnCode=" + txnRef);
    }

    

    private Integer parseIntParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null && !value.isEmpty()) {
            try { return Integer.parseInt(value); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}
