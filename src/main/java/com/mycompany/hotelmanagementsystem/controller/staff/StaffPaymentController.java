package com.mycompany.hotelmanagementsystem.controller.staff;
import com.mycompany.hotelmanagementsystem.service.StaffPaymentService;
import com.mycompany.hotelmanagementsystem.service.VNPayService;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Invoice;
import com.mycompany.hotelmanagementsystem.utils.PaymentResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// Đăng ký servlet với các URL liên quan đến payment của staff
@WebServlet(urlPatterns = {
    "/staff/payments/process",      // Trang chọn phương thức thanh toán
    "/staff/payments/cash",         // Thanh toán tiền mặt
    "/staff/payments/vnpay",        // Thanh toán bằng VNPay
    "/staff/payments/vnpay-return", // URL VNPay redirect về
    "/staff/payments/success"       // Trang thành công
})
public class StaffPaymentController extends HttpServlet {

    // Service xử lý nghiệp vụ payment cho staff
    private StaffPaymentService staffPaymentService;

    // Hàm init được gọi 1 lần khi servlet khởi tạo
    @Override
    public void init() {

        // Khởi tạo StaffPaymentService
        staffPaymentService = new StaffPaymentService();
    }

    // Xử lý request GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy đường dẫn servlet hiện tại
        String path = request.getServletPath();

        // Điều hướng theo từng URL
        switch (path) {

            // Trang chọn phương thức thanh toán
            case "/staff/payments/process" -> handleProcessGet(request, response);

            // Trang nhập thanh toán tiền mặt
            case "/staff/payments/cash" -> handleCashGet(request, response);

            // Redirect sang VNPay
            case "/staff/payments/vnpay" -> handleVNPayGet(request, response);

            // VNPay redirect về sau khi thanh toán
            case "/staff/payments/vnpay-return" -> handleVNPayReturn(request, response);

            // Trang thanh toán thành công
            case "/staff/payments/success" -> handleSuccessGet(request, response);

            // Nếu URL không hợp lệ thì báo 404
            default -> response.sendError(404);
        }
    }

    // Xử lý request POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy đường dẫn servlet hiện tại
        String path = request.getServletPath();

        // Điều hướng theo URL POST
        switch (path) {

            // Submit form thanh toán tiền mặt
            case "/staff/payments/cash" -> handleCashPost(request, response);

            // URL không hợp lệ
            default -> response.sendError(404);
        }
    }

    // UC-19.6: Process Payment - Select Method
    // Hiển thị trang chọn phương thức thanh toán
    private void handleProcessGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy bookingId từ request parameter
        int bookingId = parseIntParam(request, "bookingId");

        // Nếu bookingId không hợp lệ
        if (bookingId <= 0) {
            response.sendError(400, "Invalid booking ID");
            return;
        }

        // Lấy loại hóa đơn từ request
        // Có thể là Booking, Remaining, Extension
        String invoiceType = request.getParameter("invoiceType");

        // Khai báo biến invoice
        Invoice invoice;

        // Nếu là loại Remaining
        if ("Remaining".equals(invoiceType)) {

            // Tạo hoặc lấy invoice phần tiền còn lại
            invoice = staffPaymentService.createRemainingInvoice(bookingId);

        // Nếu có invoiceType khác và không rỗng
        } else if (invoiceType != null && !invoiceType.isEmpty()) {

            // Tạo hoặc lấy invoice theo type tương ứng
            invoice = staffPaymentService.getOrCreateInvoice(bookingId, invoiceType);

        } else {

            // Mặc định lấy/tạo invoice BOOKING
            invoice = staffPaymentService.getOrCreateInvoice(bookingId);
        }

        // Nếu không có invoice
        // Có thể booking không tồn tại hoặc không còn số dư cần thanh toán
        if (invoice == null) {
            response.sendError(404, "Booking not found or no balance");
            return;
        }

        // Lấy chi tiết booking để hiển thị
        Booking booking = staffPaymentService.getBookingDetail(bookingId);

        // Kiểm tra invoice này đã thanh toán thành công chưa
        boolean isPaid = staffPaymentService.hasSuccessfulPayment(invoice.getInvoiceId());

        // Đẩy booking sang JSP
        request.setAttribute("booking", booking);

        // Đẩy invoice sang JSP
        request.setAttribute("invoice", invoice);

        // Đẩy trạng thái đã thanh toán hay chưa
        request.setAttribute("isPaid", isPaid);

        // Đánh dấu active menu
        request.setAttribute("activePage", "bookings");

        // Tiêu đề trang
        request.setAttribute("pageTitle", "Thanh toan - Booking #" + bookingId);

        // Forward sang trang process.jsp
        request.getRequestDispatcher("/WEB-INF/views/staff/payments/process.jsp").forward(request, response);
    }

    // UC-19.7: Cash Payment
    // Hiển thị trang thanh toán tiền mặt
    private void handleCashGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy invoiceId từ request
        int invoiceId = parseIntParam(request, "invoiceId");

        // Nếu invoiceId không hợp lệ
        if (invoiceId <= 0) {
            response.sendError(400, "Invalid invoice ID");
            return;
        }

        // Lấy invoice theo id
        Invoice invoice = staffPaymentService.getInvoiceById(invoiceId);

        // Nếu không tìm thấy invoice
        if (invoice == null) {
            response.sendError(404, "Invoice not found");
            return;
        }

        // Lấy booking tương ứng với invoice
        Booking booking = staffPaymentService.getBookingDetail(invoice.getBookingId());

        // Đẩy booking sang JSP
        request.setAttribute("booking", booking);

        // Đẩy invoice sang JSP
        request.setAttribute("invoice", invoice);

        // Active menu
        request.setAttribute("activePage", "bookings");

        // Tiêu đề trang
        request.setAttribute("pageTitle", "Thanh toan tien mat");

        // Forward sang cash.jsp
        request.getRequestDispatcher("/WEB-INF/views/staff/payments/cash.jsp").forward(request, response);
    }

    // Xử lý submit thanh toán tiền mặt
    private void handleCashPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy invoiceId từ form
        int invoiceId = parseIntParam(request, "invoiceId");

        // Lấy customerId từ form
        int customerId = parseIntParam(request, "customerId");

        // Lấy amount từ form dưới dạng String
        String amountStr = request.getParameter("amount");

        // Nếu invoiceId không hợp lệ hoặc amount rỗng
        if (invoiceId <= 0 || amountStr == null) {
            response.sendError(400, "Invalid parameters");
            return;
        }

        // Khai báo amount dạng BigDecimal
        BigDecimal amount;

        try {
            // Chuyển amount từ String sang BigDecimal
            amount = new BigDecimal(amountStr);

        } catch (NumberFormatException e) {

            // Nếu format tiền sai
            response.sendError(400, "Invalid amount format");
            return;
        }

        // Gọi service để ghi nhận thanh toán tiền mặt
        boolean success = staffPaymentService.recordCashPayment(invoiceId, customerId, amount);

        // Nếu ghi nhận thành công
        if (success) {

            // Chuyển sang trang success
            response.sendRedirect(request.getContextPath() + "/staff/payments/success?invoiceId=" + invoiceId);

        } else {

            // Nếu thất bại thì trả lỗi về lại form
            request.setAttribute("error", "Khong the ghi nhan thanh toan. Vui long thu lai.");

            // Gọi lại trang cash
            handleCashGet(request, response);
        }
    }

    // UC-19.8: VNPay Payment - Initiate redirect to VNPay
    // Khởi tạo thanh toán VNPay và redirect user sang cổng VNPay
    private void handleVNPayGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy invoiceId từ request
        int invoiceId = parseIntParam(request, "invoiceId");

        // Nếu invoiceId không hợp lệ
        if (invoiceId <= 0) {
            response.sendError(400, "Invalid invoice ID");
            return;
        }

        // Lấy invoice theo id
        Invoice invoice = staffPaymentService.getInvoiceById(invoiceId);

        // Nếu không tìm thấy invoice
        if (invoice == null) {
            response.sendError(404, "Invoice not found");
            return;
        }

        // Lấy booking của invoice
        Booking booking = staffPaymentService.getBookingDetail(invoice.getBookingId());

        // Lấy customerId từ booking
        // nếu booking null thì cho = 0
        int customerId = booking != null ? booking.getCustomerId() : 0;

        // Tạo baseUrl của hệ thống
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + request.getContextPath();

        // Lấy IP client
        String ipAddress = VNPayService.getIpAddress(request);

        // Gọi service để khởi tạo thanh toán VNPay
        PaymentResult result = staffPaymentService.initiateVNPayPayment(
                invoiceId, customerId, baseUrl, ipAddress);

        // Nếu thành công và có URL thanh toán
        if (result.isSuccess() && result.getPaymentUrl() != null) {

            // Lưu txnRef vào session để kiểm tra khi VNPay redirect về
            request.getSession().setAttribute("staffPendingPaymentTxn",
                    result.getPayment().getTransactionCode());

            // Redirect sang trang VNPay
            response.sendRedirect(result.getPaymentUrl());

        } else {

            // Nếu thất bại thì trả lỗi lên trang process
            request.setAttribute("error", result.getMessage());
            request.setAttribute("booking", booking);
            request.setAttribute("invoice", invoice);
            request.setAttribute("activePage", "bookings");

            // Forward lại process.jsp
            request.getRequestDispatcher("/WEB-INF/views/staff/payments/process.jsp").forward(request, response);
        }
    }

    // VNPay return handler
    // Xử lý khi VNPay redirect về sau khi thanh toán
    private void handleVNPayReturn(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Tạo map chứa toàn bộ tham số trả về từ VNPay
        Map<String, String> params = new HashMap<>();

        // Duyệt toàn bộ parameter trong request
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                // Lấy giá trị đầu tiên của từng parameter
                params.put(key, values[0]);
            }
        });

        // Lấy mã giao dịch VNPay
        String txnRef = params.get("vnp_TxnRef");

        // Lấy mã phản hồi
        String responseCode = params.get("vnp_ResponseCode");

        // Kiểm tra chữ ký callback có hợp lệ không
        if (!VNPayService.verifySignature(params)) {
            request.getSession().setAttribute("errorMessage", "Chu ky khong hop le");
            response.sendRedirect(request.getContextPath() + "/staff/bookings");
            return;
        }

        // Lấy txnRef đang chờ xử lý trong session
        String sessionTxn = (String) request.getSession().getAttribute("staffPendingPaymentTxn");

        // Nếu session có txn nhưng không khớp txn trả về
        if (sessionTxn != null && !sessionTxn.equals(txnRef)) {
            request.getSession().setAttribute("errorMessage", "Giao dich khong hop le");
            response.sendRedirect(request.getContextPath() + "/staff/bookings");
            return;
        }

        // Xóa txn pending khỏi session sau khi đã verify xong
        request.getSession().removeAttribute("staffPendingPaymentTxn");

        // Gọi service xử lý callback VNPay
        PaymentResult result = staffPaymentService.processVNPayCallback(txnRef, responseCode);

        // Nếu xử lý thành công và có payment
        if (result.isSuccess() && result.getPayment() != null) {

            // Lấy invoice tương ứng với payment
            Invoice invoice = staffPaymentService.getInvoiceById(result.getPayment().getInvoiceId());

            if (invoice != null) {

                // Nếu payment status là Success
                if ("Success".equals(result.getPayment().getStatus())) {

                    // Redirect sang trang success
                    response.sendRedirect(request.getContextPath()
                            + "/staff/payments/success?invoiceId=" + invoice.getInvoiceId());

                } else {

                    // Nếu VNPay thất bại thì quay lại trang process
                    request.getSession().setAttribute("errorMessage", "Thanh toan VNPay that bai");
                    response.sendRedirect(request.getContextPath()
                            + "/staff/payments/process?bookingId=" + invoice.getBookingId());
                }
                return;
            }
        }

        // Nếu có lỗi trong quá trình xử lý callback
        request.getSession().setAttribute("errorMessage", "Loi xu ly thanh toan");
        response.sendRedirect(request.getContextPath() + "/staff/bookings");
    }

    // Hiển thị trang thanh toán thành công
    private void handleSuccessGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy invoiceId từ request
        int invoiceId = parseIntParam(request, "invoiceId");

        // Nếu invoiceId không hợp lệ thì quay về dashboard
        if (invoiceId <= 0) {
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        // Lấy invoice theo id
        Invoice invoice = staffPaymentService.getInvoiceById(invoiceId);

        // Nếu không có invoice thì quay về dashboard
        if (invoice == null) {
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        // Lấy booking tương ứng
        Booking booking = staffPaymentService.getBookingDetail(invoice.getBookingId());

        // Lấy payment theo invoiceId
        var payment = staffPaymentService.getPaymentByInvoiceId(invoiceId);

        // Đẩy dữ liệu sang JSP
        request.setAttribute("booking", booking);
        request.setAttribute("invoice", invoice);
        request.setAttribute("payment", payment);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Thanh toan thanh cong");

        // Forward sang success.jsp
        request.getRequestDispatcher("/WEB-INF/views/staff/payments/success.jsp").forward(request, response);
    }

    // Hàm tiện ích parse parameter sang int an toàn
    private int parseIntParam(HttpServletRequest request, String name) {

        // Lấy giá trị parameter theo tên
        String value = request.getParameter(name);

        // Nếu null hoặc rỗng thì trả 0
        if (value == null || value.isEmpty()) return 0;

        try {
            // Parse sang int
            return Integer.parseInt(value);

        } catch (NumberFormatException e) {

            // Nếu lỗi format thì trả 0
            return 0;
        }
    }
}