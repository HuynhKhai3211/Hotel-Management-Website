package com.mycompany.hotelmanagementsystem.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/admin/content/hotel-info", "/admin/content/hotel-info/save"})
public class AdminHotelInfoController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "hotel-info");
        request.setAttribute("pageTitle", "Thông tin khách sạn");
        request.getRequestDispatcher("/WEB-INF/views/admin/content/hotel-info.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/content/hotel-info?success=saved");
    }
}
