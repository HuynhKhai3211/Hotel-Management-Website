package com.mycompany.hotelmanagementsystem.controller.customer;

import com.mycompany.hotelmanagementsystem.model.Account;
import com.mycompany.hotelmanagementsystem.utils.SessionHelper;
import com.mycompany.hotelmanagementsystem.service.*;
import com.mycompany.hotelmanagementsystem.dao.AccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/customer/profile", "/customer/bookings", "/customer/booking",
    "/customer/service-request", "/customer/feedback",
    "/customer/booking/cancel", "/customer/feedback/update", "/customer/feedback/delete",
    "/customer/request/cancel", "/customer/reviews", "/customer/requests"})
public class CustomerController extends HttpServlet {
    private AccountRepository accountRepository;
    private ServiceRequestService serviceRequestService;
    private FeedbackService feedbackService;

    @Override
    public void init() {
        accountRepository = new AccountRepository();
        new BookingService();
        serviceRequestService = new ServiceRequestService();
        feedbackService = new FeedbackService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/customer/profile" -> handleProfileGet(request, response);
            
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/customer/profile" -> handleProfilePost(request, response);
            
        }
    }

    private void handleProfileGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account account = SessionHelper.getLoggedInAccount(request);
        account = accountRepository.findById(account.getAccountId());
        request.setAttribute("account", account);
        request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
    }

  

    private Integer parseIntParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null && !value.isEmpty()) {
            try { return Integer.parseInt(value); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}
