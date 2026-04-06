package com.mycompany.hotelmanagementsystem.controller.common;
<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.service.BookingService;
import com.mycompany.hotelmanagementsystem.service.PaymentService;
import com.mycompany.hotelmanagementsystem.model.Account;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Invoice;
import com.mycompany.hotelmanagementsystem.model.Payment;
import com.mycompany.hotelmanagementsystem.service.VNPayService;
import com.mycompany.hotelmanagementsystem.utils.SessionHelper;
=======

import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Payment;
import com.mycompany.hotelmanagementsystem.model.Account;
import com.mycompany.hotelmanagementsystem.model.Invoice;
import com.mycompany.hotelmanagementsystem.utils.SessionHelper;
import com.mycompany.hotelmanagementsystem.utils.EmailHelper;
import com.mycompany.hotelmanagementsystem.service.BookingService;
import com.mycompany.hotelmanagementsystem.service.PaymentService;
import com.mycompany.hotelmanagementsystem.service.VNPayService;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
<<<<<<< HEAD
import java.util.HashMap;
import java.util.Map;

// Đăng ký servlet với nhiều URL khác nhau liên quan đến payment
@WebServlet(urlPatterns = {
    "/payment/process",       // Trang xử lý thông tin thanh toán
    "/payment/vnpay",         //Khi người dùng bấm nút thanh toán VNPay.
    "/payment/vnpay-return",  // Sau khi user thanh toán bên VNPay xong, VNPay redirect về đây.
    "/payment/result"         // Trang hiển thị kết quả thanh toán
})
public class PaymentController extends HttpServlet {

    // Service xử lý nghiệp vụ thanh toán
    private PaymentService paymentService;

    // Service xử lý nghiệp vụ booking
    private BookingService bookingService;

    // Hàm init() được servlet container gọi 1 lần khi servlet khởi tạo
    @Override
    public void init() {
        // Khởi tạo đối tượng PaymentService
        paymentService = new PaymentService();

        // Khởi tạo đối tượng BookingService
        bookingService = new BookingService();
    }

    // Xử lý các request GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy phần đường dẫn servlet hiện tại, ví dụ: /payment/process
        String path = request.getServletPath();

        // Sau đó dùng switch để biết phải gọi hàm nào.
        switch (path) {

            // Nếu vào /payment/process thì gọi hàm hiển thị trang xử lý payment
            case "/payment/process" -> handleProcessGet(request, response);

            // Nếu VNPay redirect về thì xử lý callback return
            case "/payment/vnpay-return" -> handleVNPayReturn(request, response);

            // Nếu vào trang kết quả payment
=======
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            case "/payment/result" -> handleResult(request, response);
        }
    }

<<<<<<< HEAD
    // Xử lý các request POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy servlet path hiện tại
        String path = request.getServletPath();

        // Switch để điều hướng theo URL POST
        switch (path) {

            // Nếu submit tới /payment/vnpay thì tạo giao dịch VNPay
=======
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            case "/payment/vnpay" -> handleVNPayPost(request, response);
        }
    }

<<<<<<< HEAD
    // Xử lý GET cho trang /payment/process
    //kiểm tra booking hợp lệ không
    //kiểm tra user có quyền không
    //tìm đúng invoice
    //đẩy dữ liệu sang JSP để hiển thị
    private void handleProcessGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy bookingId từ request parameter và parse sang Integer
        Integer bookingId = parseIntParam(request, "bookingId");

        // Nếu bookingId không hợp lệ hoặc không tồn tại
        if (bookingId == null) {
            // Quay về trang danh sách booking của customer
=======
    private void handleProcessGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer bookingId = parseIntParam(request, "bookingId");
        if (bookingId == null) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
            return;
        }

<<<<<<< HEAD
        // Lấy account đang đăng nhập từ session -> Tức là xem ai đang login.
        Account account = SessionHelper.getLoggedInAccount(request);

        // Lấy booking theo bookingId ->Gọi service để lấy booking từ DB.
        Booking booking = bookingService.getBookingById(bookingId);

        // Nếu không tìm thấy booking
        // hoặc booking không thuộc về customer đang đăng nhập
        if (booking == null || booking.getCustomerId() != account.getAccountId()) {
            // Trả về lỗi 403 Forbidden
=======
        Account account = SessionHelper.getLoggedInAccount(request);
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking == null || booking.getCustomerId() != account.getAccountId()) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            response.sendError(403);
            return;
        }

<<<<<<< HEAD
        // Lấy loại invoice từ parameter, có thể là Booking / Extension / Remaining
        String invoiceType = request.getParameter("invoiceType");

        // Lấy invoiceId nếu có
        Integer invoiceId = parseIntParam(request, "invoiceId");

        // Khai báo biến invoice sẽ dùng ở dưới
        Invoice invoice;

        // Nếu request truyền trực tiếp invoiceId
        if (invoiceId != null) {

            // Lấy invoice theo đúng ID đó
            // Thường dùng trong flow staff hoặc extension flow
            invoice = paymentService.getInvoice(invoiceId);

        // Nếu invoiceType là Extension -> Là hóa đơn phát sinh thêm khi khách dùng dịch vụ, ở thêm đêm
        } else if ("Extension".equals(invoiceType)) {

            // Tìm invoice mới nhất chưa thanh toán thuộc loại Extension của booking này
            invoice = paymentService.findLatestInvoiceByType(bookingId, "Extension");

        // Nếu invoiceType là Remaining -> Là hóa đơn trả phần còn lại.(trường hợp cọc trước 50%)
        } else if ("Remaining".equals(invoiceType)) {

            // Tìm invoice mới nhất chưa thanh toán thuộc loại Remaining
            // Thường dùng khi checkout trả nốt phần còn lại
            invoice = paymentService.findLatestInvoiceByType(bookingId, "Remaining");

        } else {
            // Mặc định là invoice của booking ban đầu

            // Nếu booking đã Confirmed nhưng paymentType không phải Deposit
            if ("Confirmed".equals(booking.getStatus()) && !"Deposit".equals(booking.getPaymentType())) {

                // Chuyển về trang status booking, không cho thanh toán tiếp ở đây
                response.sendRedirect(request.getContextPath() + "/booking/status?bookingId=" + bookingId);
                return;
            }

            // Lấy hoặc tạo invoice mặc định cho booking
            invoice = paymentService.getOrCreateInvoice(bookingId);
        }

        // Nếu vẫn không tìm được invoice phù hợp
        if (invoice == null) {
            // Quay lại trang booking status kèm lỗi invoice
            response.sendRedirect(request.getContextPath()
                    + "/booking/status?bookingId=" + bookingId + "&error=invoice");
            return;
        }

        // Đẩy booking sang JSP để hiển thị
        request.setAttribute("booking", booking);

        // Đẩy invoice sang JSP để hiển thị
        request.setAttribute("invoice", invoice);

        // Forward sang trang process.jsp
        request.getRequestDispatcher("/WEB-INF/views/payment/process.jsp").forward(request, response);
    }

    // Xử lý POST khi user bấm thanh toán qua VNPay
    private void handleVNPayPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy invoiceId từ form submit lên
            Integer invoiceId = parseIntParam(request, "invoiceId");

            // Nếu không có invoiceId
            if (invoiceId == null) {
                // Quay về trang bookings
=======
        // Check invoice type: Booking (default), Extension, Remaining
        String invoiceType = request.getParameter("invoiceType");
        Integer invoiceId = parseIntParam(request, "invoiceId");
        Invoice invoice;

        if (invoiceId != null) {
            // Direct invoice ID provided (e.g., from staff or extension flow)
            invoice = paymentService.getInvoice(invoiceId);
        } else if ("Extension".equals(invoiceType)) {
            // Find the latest unpaid extension invoice for this booking
            invoice = paymentService.findLatestInvoiceByType(bookingId, "Extension");
        } else if ("Remaining".equals(invoiceType)) {
            // Find the remaining balance invoice for checkout
            invoice = paymentService.findLatestInvoiceByType(bookingId, "Remaining");
        } else {
            // Default booking invoice
            if ("Confirmed".equals(booking.getStatus()) && !"Deposit".equals(booking.getPaymentType())) {
                response.sendRedirect(request.getContextPath() + "/booking/status?bookingId=" + bookingId);
                return;
            }
            invoice = paymentService.getOrCreateInvoice(bookingId);
        }

        if (invoice == null) {
            response.sendRedirect(request.getContextPath() + "/booking/status?bookingId=" + bookingId + "&error=invoice");
            return;
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
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                response.sendRedirect(request.getContextPath() + "/customer/bookings");
                return;
            }

<<<<<<< HEAD
            // Lấy account đang đăng nhập
            Account account = SessionHelper.getLoggedInAccount(request);

            // Nếu chưa đăng nhập
            if (account == null) {
                // Chuyển tới trang login
=======
            Account account = SessionHelper.getLoggedInAccount(request);
            if (account == null) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

<<<<<<< HEAD
            // Tạo baseUrl của hệ thống
            // Ví dụ: http://localhost:8080/HotelManagementSystem
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":"
                           + request.getServerPort() + request.getContextPath();

            // Lấy địa chỉ IP client để gửi sang VNPay
            String ipAddress = VNPayService.getIpAddress(request);

            // Gọi service khởi tạo thanh toán VNPay
            // Truyền invoiceId, accountId, baseUrl, ipAddress
            var result = paymentService.initiateVNPayPayment(
                    invoiceId,
                    account.getAccountId(),
                    baseUrl,
                    ipAddress
            );

            // Nếu khởi tạo payment thất bại
            if (!result.isSuccess()) {

                // Lấy lại invoice để biết bookingId tương ứng
                Invoice invoice = paymentService.getInvoice(invoiceId);

                // Lưu thông báo lỗi vào session
                request.getSession().setAttribute("paymentError", result.getMessage());

                // Redirect quay lại trang process của booking tương ứng
=======
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":"
                           + request.getServerPort() + request.getContextPath();
            String ipAddress = VNPayService.getIpAddress(request);

            var result = paymentService.initiateVNPayPayment(invoiceId, account.getAccountId(), baseUrl, ipAddress);

            if (!result.isSuccess()) {
                Invoice invoice = paymentService.getInvoice(invoiceId);
                request.getSession().setAttribute("paymentError", result.getMessage());
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                response.sendRedirect(request.getContextPath() + "/payment/process?bookingId=" +
                    (invoice != null ? invoice.getBookingId() : ""));
                return;
            }

<<<<<<< HEAD
            // Lưu mã transaction reference vào session
            // Dùng để đối chiếu khi VNPay redirect về
            request.getSession().setAttribute(
                    "pendingPaymentTxn",
                    result.getPayment().getTransactionCode()
            );

            // Chuyển hướng người dùng sang cổng thanh toán VNPay
            response.sendRedirect(result.getPaymentUrl());

        } catch (Exception e) {
            // In lỗi ra console server để debug
            e.printStackTrace();

            // Lưu lỗi hệ thống vào session
            request.getSession().setAttribute("paymentError", "Lỗi hệ thống: " + e.getMessage());

            // Redirect về trang bookings
=======
            // Store txnRef in session for verification
            request.getSession().setAttribute("pendingPaymentTxn", result.getPayment().getTransactionCode());

            // Redirect to VNPay
            response.sendRedirect(result.getPaymentUrl());
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("paymentError", "Lỗi hệ thống: " + e.getMessage());
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
        }
    }

<<<<<<< HEAD
    // Xử lý khi VNPay redirect người dùng quay lại hệ thống
    private void handleVNPayReturn(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Tạo map chứa toàn bộ parameter từ VNPay trả về
        Map<String, String> params = new HashMap<>();

        // Duyệt tất cả parameter trong request
        request.getParameterMap().forEach((key, values) -> {
            // Nếu parameter có dữ liệu
            if (values != null && values.length > 0) {
                // Lấy giá trị đầu tiên và lưu vào map
=======
    private void handleVNPayReturn(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Extract all VNPay parameters
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                params.put(key, values[0]);
            }
        });

<<<<<<< HEAD
        // Kiểm tra chữ ký bảo mật của VNPay
        if (!VNPayService.verifySignature(params)) {
            // Nếu chữ ký sai => có thể request bị giả mạo
=======
        // Verify signature
        if (!VNPayService.verifySignature(params)) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=invalid_signature");
            return;
        }

<<<<<<< HEAD
        // Lấy mã giao dịch VNPay
        String txnRef = params.get("vnp_TxnRef");

        // Lấy mã phản hồi từ VNPay
        // Ví dụ "00" thường là thành công
        String responseCode = params.get("vnp_ResponseCode");

        // Lấy transaction đang chờ xử lý trong session
        String sessionTxn = (String) request.getSession().getAttribute("pendingPaymentTxn");

        // Nếu không có session hoặc transaction trả về không khớp transaction đang lưu
        if (sessionTxn == null || !sessionTxn.equals(txnRef)) {
            // Báo lỗi session mismatch
=======
        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        // Verify session
        String sessionTxn = (String) request.getSession().getAttribute("pendingPaymentTxn");
        if (sessionTxn == null || !sessionTxn.equals(txnRef)) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=session_mismatch");
            return;
        }

<<<<<<< HEAD
        // Xóa transaction pending khỏi session vì đã return xong
        request.getSession().removeAttribute("pendingPaymentTxn");

        // Gọi service xử lý callback từ VNPay
        // Thường cập nhật trạng thái payment / invoice / booking
        paymentService.processVNPayCallback(txnRef, responseCode);

        // Sau khi xử lý xong, chuyển tới trang kết quả payment
        response.sendRedirect(request.getContextPath() + "/payment/result?txnCode=" + txnRef);
    }

    // Xử lý trang hiển thị kết quả cuối cùng sau thanh toán
    private void handleResult(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy mã transaction code từ URL
        String txnCode = request.getParameter("txnCode");

        // Nếu không có txnCode : mã giao dịch
        if (txnCode == null) {
            // Quay về trang bookings
=======
        request.getSession().removeAttribute("pendingPaymentTxn");

        // Process callback
        paymentService.processVNPayCallback(txnRef, responseCode);

        response.sendRedirect(request.getContextPath() + "/payment/result?txnCode=" + txnRef);
    }

    private void handleResult(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String txnCode = request.getParameter("txnCode");
        if (txnCode == null) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
            return;
        }

<<<<<<< HEAD
        // Lấy payment theo transaction code
        Payment payment = paymentService.getPaymentByTransaction(txnCode);

        // Nếu không tìm thấy payment
        if (payment == null) {
            // Quay về trang bookings
=======
        Payment payment = paymentService.getPaymentByTransaction(txnCode);
        if (payment == null) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
            return;
        }

<<<<<<< HEAD
        // Từ payment suy ra booking liên quan
        Booking booking = paymentService.getBookingFromPayment(payment);

        // Đẩy payment sang JSP
        request.setAttribute("payment", payment);

        // Đẩy booking sang JSP
        request.setAttribute("booking", booking);

        // Nếu payment thành công thì dùng success.jsp
        // ngược lại dùng failed.jsp
=======
        Booking booking = paymentService.getBookingFromPayment(payment);

        request.setAttribute("payment", payment);
        request.setAttribute("booking", booking);

        // Send confirmation email after successful payment
        if ("Success".equals(payment.getStatus()) && booking != null) {
            sendBookingConfirmationEmail(booking);
        }

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        String viewPath = "Success".equals(payment.getStatus())
            ? "/WEB-INF/views/payment/success.jsp"
            : "/WEB-INF/views/payment/failed.jsp";

<<<<<<< HEAD
        // Forward sang JSP tương ứng
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    // Hàm tiện ích để parse parameter sang Integer an toàn
    private Integer parseIntParam(HttpServletRequest request, String name) {

        // Lấy giá trị parameter theo tên
        String value = request.getParameter(name);

        // Nếu parameter tồn tại và không rỗng
        if (value != null && !value.isEmpty()) {
            try {
                // Thử parse sang Integer
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // Nếu parse lỗi thì trả về null
                return null;
            }
        }

        // Nếu không có parameter thì trả về null
        return null;
    }
}
=======
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    private void sendBookingConfirmationEmail(Booking booking) {
        try {
            // Get customer account for email
            Account account = null;
            com.mycompany.hotelmanagementsystem.dao.AccountRepository accountRepo = new com.mycompany.hotelmanagementsystem.dao.AccountRepository();
            if (booking.getCustomerId() > 0) {
                account = accountRepo.findById(booking.getCustomerId());
            }

            if (account == null || account.getEmail() == null || account.getEmail().isEmpty()) {
                System.out.println("Cannot send booking email: no account or email");
                return;
            }

            // Get booking rooms for multi-room info
            Booking bookingWithRooms = bookingService.getBookingWithRooms(booking.getBookingId());
            List<String> roomDetails = new ArrayList<>();
            BigDecimal totalSurcharge = BigDecimal.ZERO;
            BigDecimal totalPromotion = BigDecimal.ZERO;

            if (bookingWithRooms != null && bookingWithRooms.getBookingRooms() != null && !bookingWithRooms.getBookingRooms().isEmpty()) {
                // Multi-room booking
                for (var br : bookingWithRooms.getBookingRooms()) {
                    String detail = br.getRoomType() != null
                        ? br.getRoomType().getTypeName()
                        : "Phong #" + br.getBookingRoomId();
                    if (br.getUnitPrice() != null) {
                        detail += " - " + formatCurrency(br.getUnitPrice());
                    }
                    roomDetails.add(detail);

                    if (br.getEarlySurcharge() != null) totalSurcharge = totalSurcharge.add(br.getEarlySurcharge());
                    if (br.getLateSurcharge() != null) totalSurcharge = totalSurcharge.add(br.getLateSurcharge());
                    if (br.getPromotionDiscount() != null) totalPromotion = totalPromotion.add(br.getPromotionDiscount());
                }
            } else {
                // Single-room booking
                String detail = booking.getRoom() != null && booking.getRoom().getRoomType() != null
                    ? booking.getRoom().getRoomType().getTypeName()
                    : "Phong";
                if (booking.getTotalPrice() != null) {
                    detail += " - " + formatCurrency(booking.getTotalPrice());
                }
                roomDetails.add(detail);
            }

            String checkInFormatted = booking.getCheckInExpected() != null
                ? booking.getCheckInExpected().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";
            String checkOutFormatted = booking.getCheckOutExpected() != null
                ? booking.getCheckOutExpected().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";

            EmailHelper.sendBookingConfirmation(
                account.getEmail(),
                booking.getBookingId(),
                account.getFullName(),
                checkInFormatted,
                checkOutFormatted,
                roomDetails,
                booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO,
                booking.getDepositAmount(),
                "Da thanh toan",
                booking.getEarlySurcharge() != null ? booking.getEarlySurcharge() : BigDecimal.ZERO,
                booking.getLateSurcharge() != null ? booking.getLateSurcharge() : BigDecimal.ZERO,
                totalPromotion,
                null // voucher discount not stored on booking
            );
        } catch (Exception e) {
            System.out.println("Failed to send booking confirmation email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0d";
        return String.format("%,.0fd", amount);
    }

    private Integer parseIntParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null && !value.isEmpty()) {
            try { return Integer.parseInt(value); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
