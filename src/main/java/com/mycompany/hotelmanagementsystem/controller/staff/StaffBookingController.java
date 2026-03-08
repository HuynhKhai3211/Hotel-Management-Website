package com.mycompany.hotelmanagementsystem.controller.staff;

import com.mycompany.hotelmanagementsystem.service.StaffBookingService;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.Occupant;
import com.mycompany.hotelmanagementsystem.model.Room;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {
    "/staff/bookings",
    "/staff/bookings/detail",
    "/staff/bookings/assign",
    "/staff/bookings/occupants",
    "/staff/bookings/checkout"
})
public class StaffBookingController extends HttpServlet {
    private StaffBookingService staffBookingService;

    @Override
    public void init() {
        staffBookingService = new StaffBookingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/staff/bookings" -> handleBookingList(request, response);
            case "/staff/bookings/detail" -> handleBookingDetail(request, response);
            case "/staff/bookings/assign" -> handleAssignRoomGet(request, response);
            case "/staff/bookings/occupants" -> handleOccupantsGet(request, response);
            case "/staff/bookings/checkout" -> handleCheckoutGet(request, response);
            default -> response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/staff/bookings/assign" -> handleAssignRoomPost(request, response);
            case "/staff/bookings/occupants" -> handleOccupantsPost(request, response);
            case "/staff/bookings/checkout" -> handleCheckoutPost(request, response);
            default -> response.sendError(404);
        }
    }

    private void handleBookingList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String status = request.getParameter("status");
        List<Booking> bookings;

        if (status != null && !status.isEmpty()) {
            bookings = staffBookingService.getBookingsByStatus(status);
            request.setAttribute("filterStatus", status);
        } else {
            bookings = staffBookingService.getActiveBookings();
        }

        request.setAttribute("bookings", bookings);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Danh sách đặt phòng");
        request.getRequestDispatcher("/WEB-INF/views/staff/bookings/list.jsp").forward(request, response);
    }

    private void handleBookingDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int bookingId = parseIntParam(request, "id");
        if (bookingId <= 0) {
            response.sendError(400, "Invalid booking ID");
            return;
        }

        Booking booking = staffBookingService.getBookingDetail(bookingId);
        if (booking == null) {
            response.sendError(404, "Booking not found");
            return;
        }

        List<Occupant> occupants = staffBookingService.getOccupants(bookingId);

        request.setAttribute("booking", booking);
        request.setAttribute("occupants", occupants);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Chi tiết booking #" + bookingId);
        request.getRequestDispatcher("/WEB-INF/views/staff/bookings/detail.jsp").forward(request, response);
    }

    // UC-19.1: Assign Room
    private void handleAssignRoomGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int bookingId = parseIntParam(request, "bookingId");
        if (bookingId <= 0) {
            response.sendError(400, "Invalid booking ID");
            return;
        }

        Booking booking = staffBookingService.getBookingDetail(bookingId);
        if (booking == null) {
            response.sendError(404, "Booking not found");
            return;
        }

        List<Room> availableRooms = staffBookingService.getAvailableRoomsForBooking(bookingId);

        request.setAttribute("booking", booking);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Gán phòng cho booking #" + bookingId);
        request.getRequestDispatcher("/WEB-INF/views/staff/bookings/assign-room.jsp").forward(request, response);
    }

    private void handleAssignRoomPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int bookingId = parseIntParam(request, "bookingId");
        int roomId = parseIntParam(request, "roomId");

        if (bookingId <= 0 || roomId <= 0) {
            request.setAttribute("error", "Thông tin không hợp lệ");
            handleAssignRoomGet(request, response);
            return;
        }

        boolean success = staffBookingService.assignRoom(bookingId, roomId);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings/occupants?bookingId=" + bookingId + "&success=assigned");
        } else {
            request.setAttribute("error", "Không thể gán phòng. Vui lòng thử lại.");
            handleAssignRoomGet(request, response);
        }
    }

    // UC-19.4: Manage Occupants
    private void handleOccupantsGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int bookingId = parseIntParam(request, "bookingId");
        if (bookingId <= 0) {
            response.sendError(400, "Invalid booking ID");
            return;
        }

        Booking booking = staffBookingService.getBookingDetail(bookingId);
        if (booking == null) {
            response.sendError(404, "Booking not found");
            return;
        }

        List<Occupant> occupants = staffBookingService.getOccupants(bookingId);

        if ("assigned".equals(request.getParameter("success"))) {
            request.setAttribute("success", "Đã gán phòng và check-in thành công!");
        }
        if ("saved".equals(request.getParameter("success"))) {
            request.setAttribute("success", "Đã lưu thông tin khách thành công!");
        }

        request.setAttribute("booking", booking);
        request.setAttribute("occupants", occupants);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Quản lý khách - Booking #" + bookingId);
        request.getRequestDispatcher("/WEB-INF/views/staff/bookings/occupants.jsp").forward(request, response);
    }

    private void handleOccupantsPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int bookingId = parseIntParam(request, "bookingId");
        if (bookingId <= 0) {
            response.sendError(400, "Invalid booking ID");
            return;
        }

        // Parse occupants from form
        List<Occupant> occupants = new ArrayList<>();
        String[] names = request.getParameterValues("fullName");
        String[] idCards = request.getParameterValues("idCardNumber");
        String[] phones = request.getParameterValues("phoneNumber");

        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                if (names[i] != null && !names[i].trim().isEmpty()) {
                    Occupant o = new Occupant();
                    o.setFullName(names[i].trim());
                    o.setIdCardNumber(idCards != null && i < idCards.length ? idCards[i] : "");
                    o.setPhoneNumber(phones != null && i < phones.length ? phones[i] : "");
                    occupants.add(o);
                }
            }
        }

        boolean success = staffBookingService.saveOccupants(bookingId, occupants);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings/occupants?bookingId=" + bookingId + "&success=saved");
        } else {
            request.setAttribute("error", "Không thể lưu thông tin khách. Vui lòng thử lại.");
            handleOccupantsGet(request, response);
        }
    }

    // UC-19.5: Checkout
    private void handleCheckoutGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int bookingId = parseIntParam(request, "bookingId");
        if (bookingId <= 0) {
            response.sendError(400, "Invalid booking ID");
            return;
        }

        Booking booking = staffBookingService.getBookingDetail(bookingId);
        if (booking == null) {
            response.sendError(404, "Booking not found");
            return;
        }

        List<Occupant> occupants = staffBookingService.getOccupants(bookingId);

        request.setAttribute("booking", booking);
        request.setAttribute("occupants", occupants);
        request.setAttribute("activePage", "bookings");
        request.setAttribute("pageTitle", "Check-out - Booking #" + bookingId);
        request.getRequestDispatcher("/WEB-INF/views/staff/bookings/checkout.jsp").forward(request, response);
    }

    private void handleCheckoutPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int bookingId = parseIntParam(request, "bookingId");
        if (bookingId <= 0) {
            response.sendError(400, "Invalid booking ID");
            return;
        }

        boolean success = staffBookingService.processCheckout(bookingId);

        if (success) {
            // Redirect to payment processing
            response.sendRedirect(request.getContextPath() + "/staff/payments/process?bookingId=" + bookingId);
        } else {
            request.setAttribute("error", "Không thể xử lý check-out. Vui lòng thử lại.");
            handleCheckoutGet(request, response);
        }
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
