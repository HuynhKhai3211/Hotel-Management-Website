package com.mycompany.hotelmanagementsystem.controller.staff;

import com.mycompany.hotelmanagementsystem.service.StaffCleaningService;
<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.model.Room;
=======
import com.mycompany.hotelmanagementsystem.model.RoomCleaningInfo;
import com.mycompany.hotelmanagementsystem.model.Account;
import com.mycompany.hotelmanagementsystem.utils.SessionHelper;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

<<<<<<< HEAD
@WebServlet(urlPatterns = {"/staff/cleaning", "/staff/cleaning/update"})
=======
@WebServlet(urlPatterns = {"/staff/cleaning", "/staff/cleaning/update", "/staff/cleaning/accept"})
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
public class StaffCleaningController extends HttpServlet {
    private StaffCleaningService staffCleaningService;

    @Override
    public void init() {
        staffCleaningService = new StaffCleaningService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/staff/cleaning".equals(path)) {
            handleCleaningList(request, response);
        } else {
            response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/staff/cleaning/update".equals(path)) {
            handleUpdateCleaning(request, response);
<<<<<<< HEAD
=======
        } else if ("/staff/cleaning/accept".equals(path)) {
            handleAcceptCleaning(request, response);
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        } else {
            response.sendError(404);
        }
    }

    // UC-20.1: View Cleaning Requests
    private void handleCleaningList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
<<<<<<< HEAD
        List<Room> rooms = staffCleaningService.getRoomsNeedingCleaning();

        String success = request.getParameter("success");
        if ("cleaned".equals(success)) {
            request.setAttribute("success", "Đã đánh dấu phòng hoàn thành dọn dẹp!");
=======
        List<RoomCleaningInfo> rooms = staffCleaningService.getRoomsNeedingCleaning();

        String success = request.getParameter("success");
        if ("cleaned".equals(success)) {
            request.setAttribute("success", "�ã đánh dấu phòng hoàn thành dọn dẹp!");
        } else if ("accepted".equals(success)) {
            request.setAttribute("success", "Đã nhận yêu cầu dọn phòng!");
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        }

        request.setAttribute("rooms", rooms);
        request.setAttribute("activePage", "cleaning");
        request.setAttribute("pageTitle", "Quản lý dọn phòng");
        request.getRequestDispatcher("/WEB-INF/views/staff/cleaning/list.jsp").forward(request, response);
    }

<<<<<<< HEAD
=======
    // UC-20.2: Accept cleaning request
    private void handleAcceptCleaning(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String roomIdParam = request.getParameter("roomId");
        if (roomIdParam == null || roomIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/cleaning");
            return;
        }

        try {
            int roomId = Integer.parseInt(roomIdParam);
            Account account = SessionHelper.getLoggedInAccount(request);
            boolean success = staffCleaningService.acceptCleaningRequest(roomId, account.getAccountId());
            if (success) {
                response.sendRedirect(request.getContextPath() + "/staff/cleaning?success=accepted");
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/cleaning");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/cleaning");
        }
    }

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    // UC-20.3: Update Cleaning Status
    private void handleUpdateCleaning(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String roomIdParam = request.getParameter("roomId");
        String status = request.getParameter("status");

        if (roomIdParam == null || roomIdParam.isEmpty()) {
            response.sendError(400, "Missing room ID");
            return;
        }

        try {
            int roomId = Integer.parseInt(roomIdParam);

            // Mark as available if status is "Available"
            if ("Available".equals(status)) {
                boolean success = staffCleaningService.markRoomAsClean(roomId);
                if (success) {
                    // Check if came from room detail page
                    String referer = request.getHeader("Referer");
                    if (referer != null && referer.contains("/staff/rooms/detail")) {
                        response.sendRedirect(request.getContextPath() + "/staff/rooms/detail?id=" + roomId);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/staff/cleaning?success=cleaned");
                    }
                    return;
                }
            }

            request.setAttribute("error", "Không thể cập nhật trạng thái phòng");
            handleCleaningList(request, response);
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid room ID");
        }
    }
}
