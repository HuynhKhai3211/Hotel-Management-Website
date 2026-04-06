package com.mycompany.hotelmanagementsystem.controller.admin;

import com.mycompany.hotelmanagementsystem.constant.RoomStatus;
import com.mycompany.hotelmanagementsystem.service.AdminRoomService;
import com.mycompany.hotelmanagementsystem.entity.Room;
import com.mycompany.hotelmanagementsystem.entity.RoomType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(urlPatterns = {
    "/admin/rooms",
    "/admin/rooms/create",
    "/admin/rooms/edit",
    "/admin/rooms/delete",
    "/admin/room-types",
    "/admin/room-types/create",
    "/admin/room-types/edit",
    "/admin/room-types/delete"
})
public class AdminRoomController extends HttpServlet {
    private AdminRoomService adminRoomService;

    @Override
    public void init() {
        adminRoomService = new AdminRoomService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/rooms" -> handleRoomList(request, response);
            case "/admin/rooms/create" -> showRoomForm(request, response, 0);
            case "/admin/rooms/edit" -> showRoomEditForm(request, response);
            case "/admin/room-types" -> handleRoomTypeList(request, response);
            case "/admin/room-types/create" -> showRoomTypeForm(request, response, 0);
            case "/admin/room-types/edit" -> showRoomTypeEditForm(request, response);
            default -> response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/rooms/create" -> handleRoomCreate(request, response);
            case "/admin/rooms/edit" -> handleRoomUpdate(request, response);
            case "/admin/rooms/delete" -> handleRoomDelete(request, response);
            case "/admin/room-types/create" -> handleRoomTypeCreate(request, response);
            case "/admin/room-types/edit" -> handleRoomTypeUpdate(request, response);
            case "/admin/room-types/delete" -> handleRoomTypeDelete(request, response);
            default -> response.sendError(404);
        }
    }

    // --- Room handlers ---

    private void handleRoomList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("rooms", adminRoomService.getAllRooms());
        request.setAttribute("roomTypes", adminRoomService.getAllRoomTypes());
        request.setAttribute("activePage", "rooms");
        request.setAttribute("pageTitle", "Quản lý phòng");
        request.getRequestDispatcher("/WEB-INF/views/admin/rooms/list.jsp").forward(request, response);
    }

    private void showRoomForm(HttpServletRequest request, HttpServletResponse response, int roomId)
            throws ServletException, IOException {
        request.setAttribute("roomTypes", adminRoomService.getAllRoomTypes());
        request.setAttribute("statuses", new String[]{
            RoomStatus.AVAILABLE, RoomStatus.OCCUPIED, RoomStatus.CLEANING, RoomStatus.MAINTENANCE
        });
        request.setAttribute("activePage", "rooms");
        request.setAttribute("pageTitle", "Thêm phòng mới");
        request.getRequestDispatcher("/WEB-INF/views/admin/rooms/form.jsp").forward(request, response);
    }

    private void showRoomEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
            return;
        }
        int roomId = Integer.parseInt(idParam);
        Room room = adminRoomService.getRoomById(roomId);
        if (room == null) {
            response.sendError(404, "Phòng không tồn tại");
            return;
        }
        request.setAttribute("room", room);
        request.setAttribute("roomTypes", adminRoomService.getAllRoomTypes());
        request.setAttribute("statuses", new String[]{
            RoomStatus.AVAILABLE, RoomStatus.OCCUPIED, RoomStatus.CLEANING, RoomStatus.MAINTENANCE
        });
        request.setAttribute("activePage", "rooms");
        request.setAttribute("pageTitle", "Chỉnh sửa phòng");
        request.getRequestDispatcher("/WEB-INF/views/admin/rooms/form.jsp").forward(request, response);
    }

    private void handleRoomCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Room room = buildRoomFromRequest(request);
            boolean success = adminRoomService.createRoom(room);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/rooms?success=created");
            } else {
                request.setAttribute("error", "Không thể tạo phòng. Vui lòng thử lại.");
                showRoomForm(request, response, 0);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ: " + e.getMessage());
            showRoomForm(request, response, 0);
        }
    }

    private void handleRoomUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Room room = buildRoomFromRequest(request);
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            room.setRoomId(roomId);
            boolean success = adminRoomService.updateRoom(room);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/rooms?success=updated");
            } else {
                request.setAttribute("error", "Không thể cập nhật phòng. Vui lòng thử lại.");
                showRoomEditForm(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ: " + e.getMessage());
            showRoomEditForm(request, response);
        }
    }

    private void handleRoomDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int roomId = Integer.parseInt(request.getParameter("id"));
            adminRoomService.deleteRoom(roomId);
            response.sendRedirect(request.getContextPath() + "/admin/rooms?success=deleted");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/rooms?error=deleteFailed");
        }
    }

    private Room buildRoomFromRequest(HttpServletRequest request) {
        Room room = new Room();
        room.setRoomNumber(request.getParameter("roomNumber").trim());
        room.setTypeId(Integer.parseInt(request.getParameter("typeId")));
        room.setStatus(request.getParameter("status"));
        return room;
    }

    // --- RoomType handlers ---

    private void handleRoomTypeList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("roomTypes", adminRoomService.getAllRoomTypes());
        request.setAttribute("activePage", "room-types");
        request.setAttribute("pageTitle", "Quản lý loại phòng");
        request.getRequestDispatcher("/WEB-INF/views/admin/room-types/list.jsp").forward(request, response);
    }

    private void showRoomTypeForm(HttpServletRequest request, HttpServletResponse response, int typeId)
            throws ServletException, IOException {
        request.setAttribute("activePage", "room-types");
        request.setAttribute("pageTitle", "Thêm loại phòng mới");
        request.getRequestDispatcher("/WEB-INF/views/admin/room-types/form.jsp").forward(request, response);
    }

    private void showRoomTypeEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/room-types");
            return;
        }
        int typeId = Integer.parseInt(idParam);
        RoomType roomType = adminRoomService.getRoomTypeById(typeId);
        if (roomType == null) {
            response.sendError(404, "Loại phòng không tồn tại");
            return;
        }
        request.setAttribute("roomType", roomType);
        request.setAttribute("activePage", "room-types");
        request.setAttribute("pageTitle", "Chỉnh sửa loại phòng");
        request.getRequestDispatcher("/WEB-INF/views/admin/room-types/form.jsp").forward(request, response);
    }

    private void handleRoomTypeCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            RoomType roomType = buildRoomTypeFromRequest(request);
            boolean success = adminRoomService.createRoomType(roomType);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/room-types?success=created");
            } else {
                request.setAttribute("error", "Không thể tạo loại phòng. Vui lòng thử lại.");
                showRoomTypeForm(request, response, 0);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ: " + e.getMessage());
            showRoomTypeForm(request, response, 0);
        }
    }

    private void handleRoomTypeUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            RoomType roomType = buildRoomTypeFromRequest(request);
            int typeId = Integer.parseInt(request.getParameter("typeId"));
            roomType.setTypeId(typeId);
            boolean success = adminRoomService.updateRoomType(roomType);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/room-types?success=updated");
            } else {
                request.setAttribute("error", "Không thể cập nhật loại phòng. Vui lòng thử lại.");
                showRoomTypeEditForm(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ: " + e.getMessage());
            showRoomTypeEditForm(request, response);
        }
    }

    private void handleRoomTypeDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int typeId = Integer.parseInt(request.getParameter("id"));
            adminRoomService.deleteRoomType(typeId);
            response.sendRedirect(request.getContextPath() + "/admin/room-types?success=deleted");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/room-types?error=deleteFailed");
        }
    }

    private RoomType buildRoomTypeFromRequest(HttpServletRequest request) {
        RoomType roomType = new RoomType();
        roomType.setTypeName(request.getParameter("typeName").trim());
        roomType.setBasePrice(new BigDecimal(request.getParameter("basePrice")));
        roomType.setCapacity(Integer.parseInt(request.getParameter("capacity")));
        String description = request.getParameter("description");
        roomType.setDescription(description != null ? description.trim() : "");
        return roomType;
    }
}
