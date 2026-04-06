package com.mycompany.hotelmanagementsystem.controller.staff;

import com.mycompany.hotelmanagementsystem.service.StaffPaymentService;
import com.mycompany.hotelmanagementsystem.entity.Booking;
import com.mycompany.hotelmanagementsystem.entity.Invoice;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(urlPatterns = {
    "/staff/payments/process",
    "/staff/payments/cash",
    "/staff/payments/momo",
    "/staff/payments/success"
})
public class StaffPaymentController extends HttpServlet {
    private StaffPaymentService staffPaymentService;

    @Override
    public void init() {
        staffPaymentService = new StaffPaymentService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/staff/payments/process" -> handleProcessGet(request, response);
            case "/staff/payments/cash" -> handleCashGet(request, response);
            case "/staff/payments/momo" -> handleMomoGet(request, response);
            case "/staff/payments/success" -> handleSuccessGet(request, response);
            default -> response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/staff/payments/cash" -> handleCashPost(request, response);
            case "/staff/payments/momo" -> handleMomoPost(request, response);
            default -> response.sendError(404);
        }
    }

    // UC-19.6: Process Payment - Select Method
    private void handleProcessGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int bookingId = parseIntParam(request, "bookingId");
        if (bookingId <= 0) {
            response.sendError(400, "Invalid booking ID");
            return;
        }

        // Get or create invoice
        Invoice invoice = staffPaymentService.getOrCreateInvoice(bookingId);
        if (invoice == null) {
            response.sendError(404, "Booking not found");
            return;
        }

        Booking booking = staffPaymentService.getBookingDetail(bookingId);

        // Check if already paid
        boolean isPaid = staffPaymentService.hasSuccessfulPayment(invoice.getInvoiceId());

        request.setAttribute("booking", booking);
        request.setAttribute("invoice", invoice);
        request.setAttribute("isPaid", isPaid);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Thanh toán - Booking #" + bookingId);
        request.getRequestDispatcher("/WEB-INF/views/staff/payments/process.jsp").forward(request, response);
    }

    // UC-19.7: Cash Payment
    private void handleCashGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int invoiceId = parseIntParam(request, "invoiceId");
        if (invoiceId <= 0) {
            response.sendError(400, "Invalid invoice ID");
            return;
        }

        Invoice invoice = staffPaymentService.getInvoiceById(invoiceId);
        if (invoice == null) {
            response.sendError(404, "Invoice not found");
            return;
        }

        Booking booking = staffPaymentService.getBookingDetail(invoice.getBookingId());

        request.setAttribute("booking", booking);
        request.setAttribute("invoice", invoice);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Thanh toán tiền mặt");
        request.getRequestDispatcher("/WEB-INF/views/staff/payments/cash.jsp").forward(request, response);
    }

    private void handleCashPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int invoiceId = parseIntParam(request, "invoiceId");
        int customerId = parseIntParam(request, "customerId");
        String amountStr = request.getParameter("amount");

        if (invoiceId <= 0 || amountStr == null) {
            response.sendError(400, "Invalid parameters");
            return;
        }

        BigDecimal amount = new BigDecimal(amountStr);
        boolean success = staffPaymentService.recordCashPayment(invoiceId, customerId, amount);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/staff/payments/success?invoiceId=" + invoiceId);
        } else {
            request.setAttribute("error", "Không thể ghi nhận thanh toán. Vui lòng thử lại.");
            handleCashGet(request, response);
        }
    }

    // UC-19.8: Momo QR Payment
    private void handleMomoGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int invoiceId = parseIntParam(request, "invoiceId");
        if (invoiceId <= 0) {
            response.sendError(400, "Invalid invoice ID");
            return;
        }

        Invoice invoice = staffPaymentService.getInvoiceById(invoiceId);
        if (invoice == null) {
            response.sendError(404, "Invoice not found");
            return;
        }

        Booking booking = staffPaymentService.getBookingDetail(invoice.getBookingId());
        String qrData = staffPaymentService.generateMomoQRData(invoice);

        request.setAttribute("booking", booking);
        request.setAttribute("invoice", invoice);
        request.setAttribute("qrData", qrData);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Thanh toán Momo");
        request.getRequestDispatcher("/WEB-INF/views/staff/payments/momo.jsp").forward(request, response);
    }

    private void handleMomoPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int invoiceId = parseIntParam(request, "invoiceId");
        int customerId = parseIntParam(request, "customerId");
        String amountStr = request.getParameter("amount");
        String transactionCode = request.getParameter("transactionCode");

        if (invoiceId <= 0 || amountStr == null) {
            response.sendError(400, "Invalid parameters");
            return;
        }

        BigDecimal amount = new BigDecimal(amountStr);
        boolean success = staffPaymentService.recordMomoPayment(invoiceId, customerId, amount, transactionCode);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/staff/payments/success?invoiceId=" + invoiceId);
        } else {
            request.setAttribute("error", "Không thể ghi nhận thanh toán. Vui lòng thử lại.");
            handleMomoGet(request, response);
        }
    }

    private void handleSuccessGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int invoiceId = parseIntParam(request, "invoiceId");
        if (invoiceId <= 0) {
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        Invoice invoice = staffPaymentService.getInvoiceById(invoiceId);
        if (invoice == null) {
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        Booking booking = staffPaymentService.getBookingDetail(invoice.getBookingId());
        var payment = staffPaymentService.getPaymentByInvoiceId(invoiceId);

        request.setAttribute("booking", booking);
        request.setAttribute("invoice", invoice);
        request.setAttribute("payment", payment);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Thanh toán thành công");
        request.getRequestDispatcher("/WEB-INF/views/staff/payments/success.jsp").forward(request, response);
    }

    private int parseIntParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isEmpty()) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
