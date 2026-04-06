package com.mycompany.hotelmanagementsystem.controller.staff;

import com.mycompany.hotelmanagementsystem.service.StaffPaymentService;
<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.entity.Booking;
import com.mycompany.hotelmanagementsystem.entity.Invoice;
=======
import com.mycompany.hotelmanagementsystem.service.VNPayService;
import com.mycompany.hotelmanagementsystem.utils.PaymentResult;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Invoice;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
<<<<<<< HEAD
=======
import java.util.HashMap;
import java.util.Map;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b

@WebServlet(urlPatterns = {
    "/staff/payments/process",
    "/staff/payments/cash",
<<<<<<< HEAD
    "/staff/payments/momo",
=======
    "/staff/payments/vnpay",
    "/staff/payments/vnpay-return",
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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
<<<<<<< HEAD
            case "/staff/payments/momo" -> handleMomoGet(request, response);
=======
            case "/staff/payments/vnpay" -> handleVNPayGet(request, response);
            case "/staff/payments/vnpay-return" -> handleVNPayReturn(request, response);
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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
<<<<<<< HEAD
            case "/staff/payments/momo" -> handleMomoPost(request, response);
=======
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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

<<<<<<< HEAD
        // Get or create invoice
        Invoice invoice = staffPaymentService.getOrCreateInvoice(bookingId);
        if (invoice == null) {
            response.sendError(404, "Booking not found");
=======
        // Support different invoice types (Booking, Remaining, Extension)
        String invoiceType = request.getParameter("invoiceType");
        Invoice invoice;
        if ("Remaining".equals(invoiceType)) {
            invoice = staffPaymentService.createRemainingInvoice(bookingId);
        } else if (invoiceType != null && !invoiceType.isEmpty()) {
            invoice = staffPaymentService.getOrCreateInvoice(bookingId, invoiceType);
        } else {
            invoice = staffPaymentService.getOrCreateInvoice(bookingId);
        }

        if (invoice == null) {
            response.sendError(404, "Booking not found or no balance");
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            return;
        }

        Booking booking = staffPaymentService.getBookingDetail(bookingId);

        // Check if already paid
        boolean isPaid = staffPaymentService.hasSuccessfulPayment(invoice.getInvoiceId());

        request.setAttribute("booking", booking);
        request.setAttribute("invoice", invoice);
        request.setAttribute("isPaid", isPaid);
        request.setAttribute("activePage", "bookings");
<<<<<<< HEAD
        request.setAttribute("pageTitle", "Thanh toán - Booking #" + bookingId);
=======
        request.setAttribute("pageTitle", "Thanh toan - Booking #" + bookingId);
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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
<<<<<<< HEAD
        request.setAttribute("pageTitle", "Thanh toán tiền mặt");
=======
        request.setAttribute("pageTitle", "Thanh toan tien mat");
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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

<<<<<<< HEAD
        BigDecimal amount = new BigDecimal(amountStr);
=======
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid amount format");
            return;
        }
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        boolean success = staffPaymentService.recordCashPayment(invoiceId, customerId, amount);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/staff/payments/success?invoiceId=" + invoiceId);
        } else {
<<<<<<< HEAD
            request.setAttribute("error", "Không thể ghi nhận thanh toán. Vui lòng thử lại.");
=======
            request.setAttribute("error", "Khong the ghi nhan thanh toan. Vui long thu lai.");
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            handleCashGet(request, response);
        }
    }

<<<<<<< HEAD
    // UC-19.8: Momo QR Payment
    private void handleMomoGet(HttpServletRequest request, HttpServletResponse response)
=======
    // UC-19.8: VNPay Payment - Initiate redirect to VNPay
    private void handleVNPayGet(HttpServletRequest request, HttpServletResponse response)
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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
<<<<<<< HEAD
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
=======
        int customerId = booking != null ? booking.getCustomerId() : 0;

        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + request.getContextPath();
        String ipAddress = VNPayService.getIpAddress(request);

        PaymentResult result = staffPaymentService.initiateVNPayPayment(
                invoiceId, customerId, baseUrl, ipAddress);

        if (result.isSuccess() && result.getPaymentUrl() != null) {
            // Store txnRef in session for verification on return
            request.getSession().setAttribute("staffPendingPaymentTxn",
                    result.getPayment().getTransactionCode());
            response.sendRedirect(result.getPaymentUrl());
        } else {
            request.setAttribute("error", result.getMessage());
            request.setAttribute("booking", booking);
            request.setAttribute("invoice", invoice);
            request.setAttribute("activePage", "bookings");
            request.getRequestDispatcher("/WEB-INF/views/staff/payments/process.jsp").forward(request, response);
        }
    }

    // VNPay return handler (after customer pays on VNPay page)
    private void handleVNPayReturn(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Extract params
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        // Verify signature
        if (!VNPayService.verifySignature(params)) {
            request.getSession().setAttribute("errorMessage", "Chu ky khong hop le");
            response.sendRedirect(request.getContextPath() + "/staff/bookings");
            return;
        }

        // Verify session txnRef matches
        String sessionTxn = (String) request.getSession().getAttribute("staffPendingPaymentTxn");
        if (sessionTxn != null && !sessionTxn.equals(txnRef)) {
            request.getSession().setAttribute("errorMessage", "Giao dich khong hop le");
            response.sendRedirect(request.getContextPath() + "/staff/bookings");
            return;
        }
        request.getSession().removeAttribute("staffPendingPaymentTxn");

        // Process callback
        PaymentResult result = staffPaymentService.processVNPayCallback(txnRef, responseCode);

        if (result.isSuccess() && result.getPayment() != null) {
            Invoice invoice = staffPaymentService.getInvoiceById(result.getPayment().getInvoiceId());
            if (invoice != null) {
                if ("Success".equals(result.getPayment().getStatus())) {
                    response.sendRedirect(request.getContextPath()
                            + "/staff/payments/success?invoiceId=" + invoice.getInvoiceId());
                } else {
                    request.getSession().setAttribute("errorMessage", "Thanh toan VNPay that bai");
                    response.sendRedirect(request.getContextPath()
                            + "/staff/payments/process?bookingId=" + invoice.getBookingId());
                }
                return;
            }
        }

        request.getSession().setAttribute("errorMessage", "Loi xu ly thanh toan");
        response.sendRedirect(request.getContextPath() + "/staff/bookings");
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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

<<<<<<< HEAD
        request.setAttribute("booking", booking);
        request.setAttribute("invoice", invoice);
        request.setAttribute("payment", payment);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Thanh toán thành công");
=======
        // Check if this is a post-payment checkout situation (single room)
        Integer pendingCheckoutBookingRoomId = (Integer) request.getSession().getAttribute("pendingCheckoutBookingRoomId");
        boolean isPostPaymentCheckout = pendingCheckoutBookingRoomId != null && pendingCheckoutBookingRoomId > 0;

        // Check if this is a post-payment checkout for multi-room booking
        Integer pendingCheckoutForPayment = (Integer) request.getSession().getAttribute("pendingCheckoutForPayment");
        boolean isPostPaymentMultiRoomCheckout = pendingCheckoutForPayment != null && pendingCheckoutForPayment > 0;

        request.setAttribute("booking", booking);
        request.setAttribute("invoice", invoice);
        request.setAttribute("payment", payment);
        request.setAttribute("isPostPaymentCheckout", isPostPaymentCheckout || isPostPaymentMultiRoomCheckout);
        request.setAttribute("pendingCheckoutForPayment", pendingCheckoutForPayment);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Thanh toan thanh cong");
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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
