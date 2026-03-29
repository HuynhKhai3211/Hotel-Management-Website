package com.mycompany.hotelmanagementsystem.controller.common;
import com.mycompany.hotelmanagementsystem.service.BookingService;
import com.mycompany.hotelmanagementsystem.service.PaymentService;
import com.mycompany.hotelmanagementsystem.model.Account;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Invoice;
import com.mycompany.hotelmanagementsystem.model.Payment;
import com.mycompany.hotelmanagementsystem.service.VNPayService;
import com.mycompany.hotelmanagementsystem.utils.SessionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Đăng ký servlet với nhiều URL khác nhau liên quan đến payment
@WebServlet(urlPatterns = {
    "/payment/process",       // Trang xử lý thông tin thanh toán
    "/payment/vnpay",         // URL submit để tạo thanh toán VNPay
    "/payment/vnpay-return",  // URL VNPay redirect về sau khi thanh toán
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

        // Dùng switch để điều hướng theo từng URL
        switch (path) {

            // Nếu vào /payment/process thì gọi hàm hiển thị trang xử lý payment
            case "/payment/process" -> handleProcessGet(request, response);

            // Nếu VNPay redirect về thì xử lý callback return
            case "/payment/vnpay-return" -> handleVNPayReturn(request, response);

            // Nếu vào trang kết quả payment
            case "/payment/result" -> handleResult(request, response);
        }
    }

    // Xử lý các request POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy servlet path hiện tại
        String path = request.getServletPath();

        // Switch để điều hướng theo URL POST
        switch (path) {

            // Nếu submit tới /payment/vnpay thì tạo giao dịch VNPay
            case "/payment/vnpay" -> handleVNPayPost(request, response);
        }
    }

    // Xử lý GET cho trang /payment/process
    private void handleProcessGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy bookingId từ request parameter và parse sang Integer
        Integer bookingId = parseIntParam(request, "bookingId");

        // Nếu bookingId không hợp lệ hoặc không tồn tại
        if (bookingId == null) {
            // Quay về trang danh sách booking của customer
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
            return;
        }

        // Lấy account đang đăng nhập từ session
        Account account = SessionHelper.getLoggedInAccount(request);

        // Lấy booking theo bookingId
        Booking booking = bookingService.getBookingById(bookingId);

        // Nếu không tìm thấy booking
        // hoặc booking không thuộc về customer đang đăng nhập
        if (booking == null || booking.getCustomerId() != account.getAccountId()) {
            // Trả về lỗi 403 Forbidden
            response.sendError(403);
            return;
        }

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

        // Nếu invoiceType là Extension
        } else if ("Extension".equals(invoiceType)) {

            // Tìm invoice mới nhất chưa thanh toán thuộc loại Extension của booking này
            invoice = paymentService.findLatestInvoiceByType(bookingId, "Extension");

        // Nếu invoiceType là Remaining
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
                response.sendRedirect(request.getContextPath() + "/customer/bookings");
                return;
            }

            // Lấy account đang đăng nhập
            Account account = SessionHelper.getLoggedInAccount(request);

            // Nếu chưa đăng nhập
            if (account == null) {
                // Chuyển tới trang login
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }

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
                response.sendRedirect(request.getContextPath() + "/payment/process?bookingId=" +
                    (invoice != null ? invoice.getBookingId() : ""));
                return;
            }

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
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
        }
    }

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
                params.put(key, values[0]);
            }
        });

        // Kiểm tra chữ ký bảo mật của VNPay
        if (!VNPayService.verifySignature(params)) {
            // Nếu chữ ký sai => có thể request bị giả mạo
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=invalid_signature");
            return;
        }

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
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=session_mismatch");
            return;
        }

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

        // Nếu không có txnCode
        if (txnCode == null) {
            // Quay về trang bookings
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
            return;
        }

        // Lấy payment theo transaction code
        Payment payment = paymentService.getPaymentByTransaction(txnCode);

        // Nếu không tìm thấy payment
        if (payment == null) {
            // Quay về trang bookings
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
            return;
        }

        // Từ payment suy ra booking liên quan
        Booking booking = paymentService.getBookingFromPayment(payment);

        // Đẩy payment sang JSP
        request.setAttribute("payment", payment);

        // Đẩy booking sang JSP
        request.setAttribute("booking", booking);

        // Nếu payment thành công thì dùng success.jsp
        // ngược lại dùng failed.jsp
        String viewPath = "Success".equals(payment.getStatus())
            ? "/WEB-INF/views/payment/success.jsp"
            : "/WEB-INF/views/payment/failed.jsp";

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