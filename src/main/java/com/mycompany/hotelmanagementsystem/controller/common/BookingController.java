package com.mycompany.hotelmanagementsystem.controller.common;

import com.mycompany.hotelmanagementsystem.util.BookingCalcResponse;
import com.mycompany.hotelmanagementsystem.util.DateHelper;
import com.mycompany.hotelmanagementsystem.util.SessionHelper;
import com.mycompany.hotelmanagementsystem.constant.PaymentType;
import com.mycompany.hotelmanagementsystem.service.BookingService;
import com.mycompany.hotelmanagementsystem.service.RoomService;
import com.mycompany.hotelmanagementsystem.entity.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/booking/create", "/booking/confirm", "/booking/status", "/booking/availability"})
public class BookingController extends HttpServlet {
    private RoomService roomService;
    private BookingService bookingService;

    @Override
    public void init() {
        roomService = new RoomService();
        bookingService = new BookingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/booking/create" -> handleCreateGet(request, response);
            case "/booking/confirm" -> handleConfirmGet(request, response);
            case "/booking/status" -> handleStatusGet(request, response);
            case "/booking/availability" -> handleAvailabilityApi(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/booking/create" -> handleCreatePost(request, response);
            case "/booking/confirm" -> handleConfirmPost(request, response);
        }
    }

    private void handleCreateGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer typeId = parseIntParam(request, "typeId");
        if (typeId == null) {
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }
        RoomType roomType = roomService.getRoomTypeById(typeId);
        if (roomType == null) {
            response.sendError(404);
            return;
        }
        request.setAttribute("roomType", roomType);
        request.setAttribute("minDate", LocalDate.now().plusDays(1));
        request.setAttribute("maxDate", LocalDate.now().plusMonths(6));
        request.getRequestDispatcher("/WEB-INF/views/booking/create.jsp").forward(request, response);
    }

    private void handleCreatePost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account account = SessionHelper.getLoggedInAccount(request);
        int typeId = Integer.parseInt(request.getParameter("typeId"));
        LocalDateTime checkIn = DateHelper.toCheckInTime(DateHelper.parseDate(request.getParameter("checkIn")));
        LocalDateTime checkOut = DateHelper.toCheckOutTime(DateHelper.parseDate(request.getParameter("checkOut")));
        String voucherCode = request.getParameter("voucherCode");

        List<Room> availableRooms = bookingService.getAvailableRooms(typeId, checkIn, checkOut);
        if (availableRooms.isEmpty()) {
            request.setAttribute("error", "Không có phòng trống trong thời gian này");
            handleCreateGet(request, response);
            return;
        }

        // Auto-assign: hệ thống tự chọn phòng trống đầu tiên, customer không được chọn phòng
        Room autoRoom = availableRooms.get(0);
        var calc = bookingService.calculateBooking(typeId, autoRoom.getRoomId(), checkIn, checkOut, voucherCode);

        request.getSession().setAttribute("pendingBooking", calc);
        request.getSession().setAttribute("bookingCustomerId", account.getAccountId());
        response.sendRedirect(request.getContextPath() + "/booking/confirm");
    }

    private void handleConfirmGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BookingCalcResponse calc = (BookingCalcResponse) request.getSession().getAttribute("pendingBooking");
        if (calc == null) {
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }
        request.setAttribute("booking", calc);
        request.setAttribute("account", SessionHelper.getLoggedInAccount(request));
        request.getRequestDispatcher("/WEB-INF/views/booking/confirm.jsp").forward(request, response);
    }

    private void handleConfirmPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        BookingCalcResponse calc = (BookingCalcResponse) session.getAttribute("pendingBooking");
        Integer customerId = (Integer) session.getAttribute("bookingCustomerId");

        if (calc == null || customerId == null) {
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }

        String[] names = request.getParameterValues("occupantName");
        String[] ids = request.getParameterValues("occupantIdCard");
        String[] phones = request.getParameterValues("occupantPhone");

        // Dùng Occupant entity thay cho OccupantRequest
        List<Occupant> occupants = new ArrayList<>();
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                Occupant occ = new Occupant();
                occ.setFullName(names[i]);
                if (ids != null && i < ids.length) occ.setIdCardNumber(ids[i]);
                if (phones != null && i < phones.length) occ.setPhoneNumber(phones[i]);
                occupants.add(occ);
            }
        }

        Integer voucherId = calc.getVoucher() != null ? calc.getVoucher().getVoucherId() : null;

        // Get payment type choice from form
        String paymentType = request.getParameter("paymentType");
        BigDecimal depositAmount;
        if (PaymentType.DEPOSIT.equals(paymentType) && !calc.isStandardRoom()) {
            depositAmount = calc.getDepositAmount();
        } else {
            paymentType = PaymentType.FULL;
            depositAmount = calc.getTotal();
        }

        var result = bookingService.createBooking(customerId, calc.getRoom().getRoomId(),
            calc.getCheckIn(), calc.getCheckOut(), calc.getTotal(), voucherId,
            request.getParameter("note"), occupants, paymentType, depositAmount);

        session.removeAttribute("pendingBooking");
        session.removeAttribute("bookingCustomerId");

        if (!result.isSuccess()) {
            request.setAttribute("error", result.getMessage());
            request.setAttribute("booking", calc);
            request.getRequestDispatcher("/WEB-INF/views/booking/confirm.jsp").forward(request, response);
            return;
        }

        int newBookingId = result.getBooking().getBookingId();

        // Standard room with no deposit: skip payment, go to booking status
        if (calc.isStandardRoom()) {
            // Auto-confirm since no payment needed (will auto-cancel if not verified in 6h)
            bookingService.updateBookingStatus(newBookingId, "Confirmed");
            response.sendRedirect(request.getContextPath() + "/booking/status?bookingId=" + newBookingId);
        } else {
            response.sendRedirect(request.getContextPath() + "/payment/process?bookingId=" + newBookingId);
        }
    }

    private void handleStatusGet(HttpServletRequest request, HttpServletResponse response)
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
        var occupants = bookingService.getBookingOccupants(bookingId);
        request.setAttribute("booking", booking);
        request.setAttribute("occupants", occupants);
        request.getRequestDispatcher("/WEB-INF/views/booking/status.jsp").forward(request, response);
    }

    /**
     * API endpoint trả về JSON danh sách ngày bận theo loại phòng.
     * Dùng cho calendar trên trang đặt phòng của customer.
     * GET /booking/availability?typeId=1
     * Response: [{"start":"2026-03-20","end":"2026-03-22"}, ...]
     */
    private void handleAvailabilityApi(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Integer typeId = parseIntParam(request, "typeId");
        if (typeId == null) {
            out.print("[]");
            return;
        }

        List<LocalDateTime[]> ranges = bookingService.getOccupiedDateRanges(typeId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < ranges.size(); i++) {
            LocalDateTime[] range = ranges.get(i);
            if (range[0] == null || range[1] == null) continue;
            if (i > 0) json.append(",");
            json.append("{\"start\":\"").append(range[0].toLocalDate().format(fmt)).append("\"")
                .append(",\"end\":\"").append(range[1].toLocalDate().format(fmt)).append("\"}");
        }
        json.append("]");
        out.print(json.toString());
    }

    private Integer parseIntParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null && !value.isEmpty()) {
            try { return Integer.parseInt(value); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}
