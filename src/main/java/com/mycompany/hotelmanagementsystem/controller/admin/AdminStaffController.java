package com.mycompany.hotelmanagementsystem.controller.admin;

import com.mycompany.hotelmanagementsystem.constant.RoleConstant;
import com.mycompany.hotelmanagementsystem.service.AdminStaffService;
<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.entity.Account;
=======
import com.mycompany.hotelmanagementsystem.model.Account;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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
<<<<<<< HEAD
=======
        String errorParam = request.getParameter("error");

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        if ("created".equals(success)) {
            request.setAttribute("success", "Tạo nhân viên thành công!");
        } else if ("updated".equals(success)) {
            request.setAttribute("success", "Cập nhật nhân viên thành công!");
        } else if ("toggled".equals(success)) {
            request.setAttribute("success", "Cập nhật trạng thái thành công!");
        }

<<<<<<< HEAD
=======
        if ("notfound".equals(errorParam)) {
            request.setAttribute("error", "Không tìm thấy nhân viên!");
        } else if ("invalid".equals(errorParam)) {
            request.setAttribute("error", "Yêu cầu không hợp lệ!");
        } else if ("toggle_failed".equals(errorParam)) {
            request.setAttribute("error", "Không thể thay đổi trạng thái nhân viên!");
        }

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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
<<<<<<< HEAD
        int id = Integer.parseInt(request.getParameter("id"));
=======
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/staff?error=invalid");
            return;
        }

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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

<<<<<<< HEAD
        int result = adminStaffService.createStaff(email, password, fullName, phone, address);

        if (result == -1) {
            request.setAttribute("error", "Email đã tồn tại!");
            request.setAttribute("isEdit", false);
=======
        // Validate required fields
        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ các trường bắt buộc!");
            preserveCreateFormData(request, email, fullName, phone, address);
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            request.getRequestDispatcher("/WEB-INF/views/admin/staff/form.jsp").forward(request, response);
            return;
        }

<<<<<<< HEAD
        if (result > 0) {
            response.sendRedirect(request.getContextPath() + "/admin/staff?success=created");
        } else {
            request.setAttribute("error", "Không thể tạo nhân viên!");
=======
        try {
            int result = adminStaffService.createStaff(email.trim(), password, fullName.trim(),
                    phone != null ? phone.trim() : "", address != null ? address.trim() : "");

            if (result == -1) {
                request.setAttribute("error", "Email đã tồn tại!");
                preserveCreateFormData(request, email, fullName, phone, address);
                request.getRequestDispatcher("/WEB-INF/views/admin/staff/form.jsp").forward(request, response);
                return;
            }

            if (result > 0) {
                response.sendRedirect(request.getContextPath() + "/admin/staff?success=created");
            } else {
                request.setAttribute("error", "Không thể tạo nhân viên!");
                preserveCreateFormData(request, email, fullName, phone, address);
                request.getRequestDispatcher("/WEB-INF/views/admin/staff/form.jsp").forward(request, response);
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", "Lỗi hệ thống khi tạo nhân viên! Vui lòng thử lại.");
            preserveCreateFormData(request, email, fullName, phone, address);
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            request.getRequestDispatcher("/WEB-INF/views/admin/staff/form.jsp").forward(request, response);
        }
    }

<<<<<<< HEAD
    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
=======
    /**
     * Giu lai data form khi create fail, forward ve form.jsp
     */
    private void preserveCreateFormData(HttpServletRequest request,
            String email, String fullName, String phone, String address)
            throws ServletException, IOException {
        Account formData = new Account();
        formData.setEmail(email != null ? email : "");
        formData.setFullName(fullName != null ? fullName : "");
        formData.setPhone(phone != null ? phone : "");
        formData.setAddress(address != null ? address : "");
        request.setAttribute("staff", formData);
        request.setAttribute("isEdit", false);
        request.setAttribute("activePage", "staff");
        request.setAttribute("pageTitle", "Thêm nhân viên");
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/staff?error=invalid");
            return;
        }

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        int roleId;
        try {
            roleId = Integer.parseInt(request.getParameter("roleId"));
        } catch (NumberFormatException e) {
            roleId = RoleConstant.STAFF;
        }

<<<<<<< HEAD
        boolean success = adminStaffService.updateStaff(id, fullName, phone, address, roleId);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/staff?success=updated");
        } else {
            request.setAttribute("error", "Không thể cập nhật nhân viên!");
            handleEditForm(request, response);
        }
=======
        // Validate required fields
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("error", "Họ và tên không được để trống!");
            preserveEditFormData(request, response, id, fullName, phone, address, roleId);
            return;
        }

        try {
            boolean success = adminStaffService.updateStaff(id, fullName.trim(),
                    phone != null ? phone.trim() : "", address != null ? address.trim() : "", roleId);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/staff?success=updated");
            } else {
                request.setAttribute("error", "Không thể cập nhật nhân viên!");
                preserveEditFormData(request, response, id, fullName, phone, address, roleId);
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", "Lỗi hệ thống khi cập nhật! Vui lòng thử lại.");
            preserveEditFormData(request, response, id, fullName, phone, address, roleId);
        }
    }

    /**
     * Giu lai data form khi edit fail, forward ve form.jsp
     */
    private void preserveEditFormData(HttpServletRequest request, HttpServletResponse response,
            int id, String fullName, String phone, String address, int roleId)
            throws ServletException, IOException {
        Account formData = new Account();
        formData.setAccountId(id);
        formData.setFullName(fullName != null ? fullName : "");
        formData.setPhone(phone != null ? phone : "");
        formData.setAddress(address != null ? address : "");
        formData.setRoleId(roleId);
        request.setAttribute("staff", formData);
        request.setAttribute("isEdit", true);
        request.setAttribute("activePage", "staff");
        request.setAttribute("pageTitle", "Sửa nhân viên");
        request.getRequestDispatcher("/WEB-INF/views/admin/staff/form.jsp").forward(request, response);
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
<<<<<<< HEAD
        int id = Integer.parseInt(request.getParameter("id"));
        adminStaffService.toggleStaffStatus(id);
        response.sendRedirect(request.getContextPath() + "/admin/staff?success=toggled");
=======
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/staff?error=invalid");
            return;
        }

        boolean success = adminStaffService.toggleStaffStatus(id);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/staff?success=toggled");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/staff?error=toggle_failed");
        }
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    }
}
