package com.mycompany.hotelmanagementsystem.controller.staff;

import com.mycompany.hotelmanagementsystem.service.StaffBookingService;
import com.mycompany.hotelmanagementsystem.service.BookingService;
import com.mycompany.hotelmanagementsystem.constant.PaymentType;
import com.mycompany.hotelmanagementsystem.entity.Booking;
import com.mycompany.hotelmanagementsystem.entity.Occupant;
import com.mycompany.hotelmanagementsystem.entity.BookingExtension;
import com.mycompany.hotelmanagementsystem.entity.Room;
import com.mycompany.hotelmanagementsystem.entity.RoomType;
import com.mycompany.hotelmanagementsystem.util.BookingCalcResponse;
import com.mycompany.hotelmanagementsystem.util.BookingResult;
import com.mycompany.hotelmanagementsystem.util.WalkInCustomerResult;
import com.mycompany.hotelmanagementsystem.util.EmailHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {
    "/staff/bookings",
    "/staff/bookings/detail",
    "/staff/bookings/assign",
    "/staff/bookings/occupants",
    "/staff/bookings/checkout",
    "/staff/bookings/walkin",
    "/staff/bookings/walkin-room",
    "/staff/bookings/walkin-confirm"
})
public class StaffBookingController extends HttpServlet {
    private StaffBookingService staffBookingService;
    private BookingService bookingService;

    @Override
    public void init() {
        staffBookingService = new StaffBookingService();
        bookingService = new BookingService();
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
            case "/staff/bookings/walkin" -> handleWalkInStep1Get(request, response);
            case "/staff/bookings/walkin-room" -> handleWalkInStep2Get(request, response);
            case "/staff/bookings/walkin-confirm" -> handleWalkInStep3Get(request, response);
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
            case "/staff/bookings/walkin" -> handleWalkInStep1Post(request, response);
            case "/staff/bookings/walkin-room" -> handleWalkInStep2Post(request, response);
            case "/staff/bookings/walkin-confirm" -> handleWalkInStep3Post(request, response);
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
        List<BookingExtension> extensions = staffBookingService.getExtensions(bookingId);

        // Handle success messages from checkout redirect
        if ("checkedout".equals(request.getParameter("success"))) {
            request.setAttribute("success", "Check-out thanh cong!");
        }

        request.setAttribute("booking", booking);
        request.setAttribute("occupants", occupants);
        request.setAttribute("extensions", extensions);
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
        List<BookingExtension> extensions = staffBookingService.getExtensions(bookingId);

        // Check if payment is needed at checkout
        boolean needsPayment = staffBookingService.needsCheckoutPayment(bookingId);
        request.setAttribute("needsCheckoutPayment", needsPayment);
        if (needsPayment) {
            request.setAttribute("checkoutPaymentAmount", staffBookingService.getCheckoutPaymentAmount(bookingId));
        }

        request.setAttribute("booking", booking);
        request.setAttribute("occupants", occupants);
        request.setAttribute("extensions", extensions);
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
            // Check if payment is needed (Standard room unpaid, or Deposit remaining)
            if (staffBookingService.needsCheckoutPayment(bookingId)) {
                Booking booking = staffBookingService.getBookingDetail(bookingId);
                String invoiceType = PaymentType.DEPOSIT.equals(booking.getPaymentType()) ? "Remaining" : "Booking";
                response.sendRedirect(request.getContextPath() + "/staff/payments/process?bookingId=" + bookingId + "&invoiceType=" + invoiceType);
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/bookings/detail?id=" + bookingId + "&success=checkedout");
            }
        } else {
            request.setAttribute("error", "Không thể xử lý check-out. Vui lòng thử lại.");
            handleCheckoutGet(request, response);
        }
    }

    // === Walk-in Booking Flow ===

    // Step 1: Customer info form
    private void handleWalkInStep1Get(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "walkin");
        request.setAttribute("pageTitle", "Dat phong tai quay");
        request.getRequestDispatcher("/WEB-INF/views/staff/bookings/walkin-step1.jsp").forward(request, response);
    }

    // Step 1: Process customer info
    private void handleWalkInStep1Post(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String idCard = request.getParameter("idCard");
        String confirmEmailLink = request.getParameter("confirmEmailLink");
        String skipEmail = request.getParameter("skipEmail");

        // Validate required fields
        if (fullName == null || fullName.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            request.setAttribute("error", "Vui long nhap ho ten va so dien thoai");
            setWalkInFormAttributes(request, fullName, phone, email, idCard);
            handleWalkInStep1Get(request, response);
            return;
        }

        try {
            // If staff confirmed to link existing email account
            if ("true".equals(confirmEmailLink)) {
                WalkInCustomerResult result = staffBookingService.findOrCreateWalkInCustomer(
                        fullName.trim(), phone.trim(), email, false);
                saveWalkInSession(request, result.getAccountId(), fullName, phone, email, idCard);
                response.sendRedirect(request.getContextPath() + "/staff/bookings/walkin-room");
                return;
            }

            // If staff chose to skip email (create without email)
            boolean doSkipEmail = "true".equals(skipEmail);

            WalkInCustomerResult result = staffBookingService.findOrCreateWalkInCustomer(
                    fullName.trim(), phone.trim(), email, doSkipEmail);

            if (result.isFoundByEmail()) {
                // Email exists with different phone - ask staff to confirm
                request.setAttribute("emailConflict", true);
                request.setAttribute("conflictName", result.getExistingName());
                request.setAttribute("conflictPhone", maskPhone(result.getExistingPhone()));
                setWalkInFormAttributes(request, fullName, phone, email, idCard);
                handleWalkInStep1Get(request, response);
                return;
            }

            // FOUND_BY_PHONE or CREATED - proceed
            saveWalkInSession(request, result.getAccountId(), fullName, phone, email, idCard);

            // Send credentials email if new account was created and has real email
            if (result.isCreated() && result.getGeneratedPassword() != null
                    && result.getEmail() != null && !result.getEmail().contains("@walkin.local")) {
                System.out.println("=== WALK-IN EMAIL DEBUG ===");
                System.out.println("Sending credentials to: " + result.getEmail());
                System.out.println("FullName: " + fullName.trim());
                System.out.println("Password length: " + result.getGeneratedPassword().length());
                boolean sent = EmailHelper.sendWalkInCredentials(result.getEmail(),
                        fullName.trim(), result.getGeneratedPassword());
                System.out.println("Email sent result: " + sent);
            } else {
                System.out.println("=== WALK-IN EMAIL SKIPPED ===");
                System.out.println("Status: " + result.getStatus());
                System.out.println("isCreated: " + result.isCreated());
                System.out.println("Password: " + (result.getGeneratedPassword() != null ? "set" : "null"));
                System.out.println("Email: " + result.getEmail());
            }

            response.sendRedirect(request.getContextPath() + "/staff/bookings/walkin-room");

        } catch (Exception e) {
            request.setAttribute("error", "Loi khi xu ly thong tin khach: " + e.getMessage());
            setWalkInFormAttributes(request, fullName, phone, email, idCard);
            handleWalkInStep1Get(request, response);
        }
    }

    private void setWalkInFormAttributes(HttpServletRequest request,
            String fullName, String phone, String email, String idCard) {
        request.setAttribute("fullName", fullName);
        request.setAttribute("phone", phone);
        request.setAttribute("email", email);
        request.setAttribute("idCard", idCard);
    }

    private void saveWalkInSession(HttpServletRequest request, int customerId,
            String fullName, String phone, String email, String idCard) {
        HttpSession session = request.getSession();
        session.setAttribute("walkin_customerId", customerId);
        session.setAttribute("walkin_fullName", fullName.trim());
        session.setAttribute("walkin_phone", phone.trim());
        session.setAttribute("walkin_email", email != null ? email.trim() : "");
        session.setAttribute("walkin_idCard", idCard != null ? idCard.trim() : "");
    }

    // Mask phone for privacy: 0901234567 -> 090***4567
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "***" + phone.substring(phone.length() - 4);
    }

    // Step 2: Select room type + dates
    private void handleWalkInStep2Get(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("walkin_customerId") == null) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings/walkin");
            return;
        }

        List<RoomType> roomTypes = staffBookingService.getAllRoomTypes();
        request.setAttribute("roomTypes", roomTypes);
        request.setAttribute("activePage", "walkin");
        request.setAttribute("pageTitle", "Chon phong - Dat phong tai quay");
        request.getRequestDispatcher("/WEB-INF/views/staff/bookings/walkin-step2.jsp").forward(request, response);
    }

    // Step 2: Process room selection (2 phases: search rooms, then confirm room choice)
    private void handleWalkInStep2Post(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("walkin_customerId") == null) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings/walkin");
            return;
        }

        // Phase 2: Staff has chosen a specific room -> calculate and go to step 3
        String selectedRoomId = request.getParameter("roomId");
        if (selectedRoomId != null && !selectedRoomId.isEmpty()) {
            handleWalkInRoomConfirm(request, response, session, selectedRoomId);
            return;
        }

        // Phase 1: Staff selected type + dates -> show available rooms
        int typeId = parseIntParam(request, "typeId");
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");

        if (typeId <= 0 || checkInStr == null || checkOutStr == null
                || checkInStr.isEmpty() || checkOutStr.isEmpty()) {
            request.setAttribute("error", "Vui long chon loai phong va ngay nhan/tra phong");
            handleWalkInStep2Get(request, response);
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime checkIn = LocalDateTime.parse(checkInStr, formatter);
            LocalDateTime checkOut = LocalDateTime.parse(checkOutStr, formatter);

            if (!checkOut.isAfter(checkIn)) {
                request.setAttribute("error", "Ngay tra phong phai sau ngay nhan phong");
                handleWalkInStep2Get(request, response);
                return;
            }

            // Find available rooms
            List<Room> availableRooms = staffBookingService.findAvailableRoomsForDates(typeId, checkIn, checkOut);
            if (availableRooms.isEmpty()) {
                request.setAttribute("error", "Khong con phong trong cho loai phong nay trong khoang thoi gian da chon");
                handleWalkInStep2Get(request, response);
                return;
            }

            // Store search params in session for phase 2
            session.setAttribute("walkin_typeId", typeId);
            session.setAttribute("walkin_checkIn", checkIn);
            session.setAttribute("walkin_checkOut", checkOut);

            // Show room selection UI
            RoomType roomType = staffBookingService.getRoomTypeById(typeId);
            request.setAttribute("availableRooms", availableRooms);
            request.setAttribute("selectedType", roomType);
            request.setAttribute("selectedCheckIn", checkInStr);
            request.setAttribute("selectedCheckOut", checkOutStr);
            request.setAttribute("selectedTypeId", typeId);

            // Re-populate form fields
            List<RoomType> roomTypes = staffBookingService.getAllRoomTypes();
            request.setAttribute("roomTypes", roomTypes);
            request.setAttribute("activePage", "walkin");
            request.setAttribute("pageTitle", "Chon phong - Dat phong tai quay");
            request.getRequestDispatcher("/WEB-INF/views/staff/bookings/walkin-step2.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Loi xu ly: " + e.getMessage());
            handleWalkInStep2Get(request, response);
        }
    }

    // Phase 2: Staff confirmed a specific room -> calculate price and go to step 3
    private void handleWalkInRoomConfirm(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, String selectedRoomId) throws ServletException, IOException {
        try {
            int roomId = Integer.parseInt(selectedRoomId);
            int typeId = (int) session.getAttribute("walkin_typeId");
            LocalDateTime checkIn = (LocalDateTime) session.getAttribute("walkin_checkIn");
            LocalDateTime checkOut = (LocalDateTime) session.getAttribute("walkin_checkOut");

            // Calculate booking price
            BookingCalcResponse calc = bookingService.calculateBooking(typeId, roomId, checkIn, checkOut, null);
            if (calc == null) {
                request.setAttribute("error", "Khong the tinh gia phong. Vui long thu lai.");
                handleWalkInStep2Get(request, response);
                return;
            }

            // Store in session
            session.setAttribute("walkin_roomId", roomId);
            session.setAttribute("walkin_calc", calc);

            response.sendRedirect(request.getContextPath() + "/staff/bookings/walkin-confirm");
        } catch (Exception e) {
            request.setAttribute("error", "Loi xu ly: " + e.getMessage());
            handleWalkInStep2Get(request, response);
        }
    }

    // Step 3: Confirm + show summary
    private void handleWalkInStep3Get(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("walkin_customerId") == null
                || session.getAttribute("walkin_calc") == null) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings/walkin");
            return;
        }

        BookingCalcResponse calc = (BookingCalcResponse) session.getAttribute("walkin_calc");
        request.setAttribute("calc", calc);
        request.setAttribute("walkin_fullName", session.getAttribute("walkin_fullName"));
        request.setAttribute("walkin_phone", session.getAttribute("walkin_phone"));
        request.setAttribute("walkin_email", session.getAttribute("walkin_email"));
        request.setAttribute("walkin_idCard", session.getAttribute("walkin_idCard"));
        request.setAttribute("activePage", "walkin");
        request.setAttribute("pageTitle", "Xac nhan dat phong tai quay");
        request.getRequestDispatcher("/WEB-INF/views/staff/bookings/walkin-step3.jsp").forward(request, response);
    }

    // Step 3: Create booking + redirect to payment
    private void handleWalkInStep3Post(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("walkin_customerId") == null
                || session.getAttribute("walkin_calc") == null) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings/walkin");
            return;
        }

        int customerId = (int) session.getAttribute("walkin_customerId");
        int typeId = (int) session.getAttribute("walkin_typeId");
        LocalDateTime checkIn = (LocalDateTime) session.getAttribute("walkin_checkIn");
        LocalDateTime checkOut = (LocalDateTime) session.getAttribute("walkin_checkOut");
        BookingCalcResponse calc = (BookingCalcResponse) session.getAttribute("walkin_calc");
        String note = request.getParameter("note");

        // Parse occupants from form
        List<Occupant> occupants = new ArrayList<>();
        String[] names = request.getParameterValues("occFullName");
        String[] idCards = request.getParameterValues("occIdCard");
        String[] phones = request.getParameterValues("occPhone");

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

        // Validate occupant count against room capacity
        if (calc.getRoomType() != null && occupants.size() > calc.getRoomType().getCapacity()) {
            request.setAttribute("error", "So luong khach vuot qua suc chua phong (toi da "
                    + calc.getRoomType().getCapacity() + " nguoi)");
            handleWalkInStep3Get(request, response);
            return;
        }

        try {
            BookingResult result = staffBookingService.createWalkInBooking(
                    customerId, typeId, checkIn, checkOut,
                    calc.getTotal(), note, occupants);

            if (result.isSuccess()) {
                int bookingId = result.getBooking().getBookingId();

                // Clear walk-in session data
                session.removeAttribute("walkin_customerId");
                session.removeAttribute("walkin_fullName");
                session.removeAttribute("walkin_phone");
                session.removeAttribute("walkin_email");
                session.removeAttribute("walkin_idCard");
                session.removeAttribute("walkin_typeId");
                session.removeAttribute("walkin_roomId");
                session.removeAttribute("walkin_checkIn");
                session.removeAttribute("walkin_checkOut");
                session.removeAttribute("walkin_calc");

                // Redirect to payment flow
                response.sendRedirect(request.getContextPath()
                        + "/staff/payments/process?bookingId=" + bookingId + "&invoiceType=Booking");
            } else {
                request.setAttribute("error", result.getMessage());
                handleWalkInStep3Get(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Loi khi tao booking: " + e.getMessage());
            handleWalkInStep3Get(request, response);
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
