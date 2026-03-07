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

    

    private Integer parseIntParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null && !value.isEmpty()) {
            try { return Integer.parseInt(value); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}
