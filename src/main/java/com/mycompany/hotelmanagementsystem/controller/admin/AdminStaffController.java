package com.mycompany.hotelmanagementsystem.controller.admin;

import com.mycompany.hotelmanagementsystem.constant.RoleConstant;
import com.mycompany.hotelmanagementsystem.service.AdminStaffService;
import com.mycompany.hotelmanagementsystem.entity.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/staff", "/admin/staff/create", "/admin/staff/edit", "/admin/staff/toggle-status"})
public class AdminStaffController extends HttpServlet {
    private AdminStaffService adminStaffService;

    @Override
    public void init() {
        adminStaffService = new AdminStaffService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/staff" -> handleList(request, response);
            case "/admin/staff/create" -> handleCreateForm(request, response);
            case "/admin/staff/edit" -> handleEditForm(request, response);
            default -> response.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/staff/create" -> handleCreate(request, response);
            case "/admin/staff/edit" -> handleEdit(request, response);
            case "/admin/staff/toggle-status" -> handleToggleStatus(request, response);
            default -> response.sendError(404);
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Account> staffList = adminStaffService.getAllStaff();

        String success = request.getParameter("success");
        if ("created".equals(success)) {
            request.setAttribute("success", "Tạo nhân viên thành công!");
        } else if ("updated".equals(success)) {
            request.setAttribute("success", "Cập nhật nhân viên thành công!");
        } else if ("toggled".equals(success)) {
            request.setAttribute("success", "Cập nhật trạng thái thành công!");
        }

        request.setAttribute("staffList", staffList);
        request.setAttribute("activePage", "staff");
        request.setAttribute("pageTitle", "Quản lý nhân viên");
        request.getRequestDispatcher("/WEB-INF/views/admin/staff/list.jsp").forward(request, response);
    }

    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "staff");
        request.setAttribute("pageTitle", "Thêm nhân viên");
        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/WEB-INF/views/admin/staff/form.jsp").forward(request, response);
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Account staff = adminStaffService.getStaffById(id);

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/admin/staff?error=notfound");
            return;
        }

        request.setAttribute("staff", staff);
        request.setAttribute("activePage", "staff");
        request.setAttribute("pageTitle", "Sửa nhân viên");
        request.setAttribute("isEdit", true);
        request.getRequestDispatcher("/WEB-INF/views/admin/staff/form.jsp").forward(request, response);
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        int result = adminStaffService.createStaff(email, password, fullName, phone, address);

        if (result == -1) {
            request.setAttribute("error", "Email đã tồn tại!");
            request.setAttribute("isEdit", false);
            request.getRequestDispatcher("/WEB-INF/views/admin/staff/form.jsp").forward(request, response);
            return;
        }

        if (result > 0) {
            response.sendRedirect(request.getContextPath() + "/admin/staff?success=created");
        } else {
            request.setAttribute("error", "Không thể tạo nhân viên!");
            request.getRequestDispatcher("/WEB-INF/views/admin/staff/form.jsp").forward(request, response);
        }
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        int roleId;
        try {
            roleId = Integer.parseInt(request.getParameter("roleId"));
        } catch (NumberFormatException e) {
            roleId = RoleConstant.STAFF;
        }

        boolean success = adminStaffService.updateStaff(id, fullName, phone, address, roleId);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/staff?success=updated");
        } else {
            request.setAttribute("error", "Không thể cập nhật nhân viên!");
            handleEditForm(request, response);
        }
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        adminStaffService.toggleStaffStatus(id);
        response.sendRedirect(request.getContextPath() + "/admin/staff?success=toggled");
    }
}
