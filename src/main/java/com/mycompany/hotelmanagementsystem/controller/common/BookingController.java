package com.mycompany.hotelmanagementsystem.controller.common;

import com.mycompany.hotelmanagementsystem.model.Occupant;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Account;
import com.mycompany.hotelmanagementsystem.model.Room;
import com.mycompany.hotelmanagementsystem.model.RoomType;
import com.mycompany.hotelmanagementsystem.utils.BookingCalcResponse;
import com.mycompany.hotelmanagementsystem.utils.DateHelper;
import com.mycompany.hotelmanagementsystem.utils.SessionHelper;
import com.mycompany.hotelmanagementsystem.service.BookingService;
import com.mycompany.hotelmanagementsystem.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/booking/create", "/booking/confirm", "/booking/status"})
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
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/booking/create" -> handleCreatePost(request, response);
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
        String roomIdParam = request.getParameter("roomId");

        List<Room> availableRooms = bookingService.getAvailableRooms(typeId, checkIn, checkOut);
        if (availableRooms.isEmpty()) {
            request.setAttribute("error", "Không có phòng trống trong thời gian này");
            handleCreateGet(request, response);
            return;
        }

        // Step 1: Chưa chọn phòng → Hiển thị danh sách phòng
        if (roomIdParam == null || roomIdParam.isEmpty()) {
            RoomType roomType = roomService.getRoomTypeById(typeId);
            request.setAttribute("roomType", roomType);
            request.setAttribute("availableRooms", availableRooms);
            request.setAttribute("selectedCheckIn", request.getParameter("checkIn"));
            request.setAttribute("selectedCheckOut", request.getParameter("checkOut"));
            request.setAttribute("voucherCode", voucherCode);
            request.setAttribute("minDate", LocalDate.now().plusDays(1));
            request.setAttribute("maxDate", LocalDate.now().plusMonths(6));
            request.getRequestDispatcher("/WEB-INF/views/booking/create.jsp").forward(request, response);
            return;
        }

        // Step 2: Đã chọn phòng → Tính giá và redirect confirm
        int roomId = Integer.parseInt(roomIdParam);
        var calc = bookingService.calculateBooking(typeId, roomId, checkIn, checkOut, voucherCode);

        request.getSession().setAttribute("pendingBooking", calc);
        request.getSession().setAttribute("bookingCustomerId", account.getAccountId());
        response.sendRedirect(request.getContextPath() + "/booking/confirm");
    }

    private Integer parseIntParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null && !value.isEmpty()) {
            try { return Integer.parseInt(value); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}
