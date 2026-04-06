package com.mycompany.hotelmanagementsystem.filter;

import com.mycompany.hotelmanagementsystem.constant.RoleConstant;
<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.util.SessionHelper;
import com.mycompany.hotelmanagementsystem.entity.Account;
=======
import com.mycompany.hotelmanagementsystem.utils.SessionHelper;
import com.mycompany.hotelmanagementsystem.model.Account;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebFilter(urlPatterns = {"/staff/*"})
public class StaffAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String servletPath = httpRequest.getServletPath();
<<<<<<< HEAD
        if ("/staff/login".equals(servletPath)) {
            chain.doFilter(request, response);
            return;
        }

=======
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        // Check if logged in
        if (!SessionHelper.isLoggedIn(httpRequest)) {
            redirectToLogin(httpRequest, httpResponse);
            return;
        }

        // Check if user is staff
        Account account = SessionHelper.getLoggedInAccount(httpRequest);
        if (account == null || account.getRoleId() != RoleConstant.STAFF) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login?error=staff_required");
            return;
        }

        chain.doFilter(request, response);
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
<<<<<<< HEAD
        String loginUrl = request.getContextPath() + "/staff/login";
=======
        String loginUrl = request.getContextPath() + "/auth/login";
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        String returnUrl = request.getRequestURI();
        if (request.getQueryString() != null) {
            returnUrl += "?" + request.getQueryString();
        }
        response.sendRedirect(loginUrl + "?returnUrl=" +
            URLEncoder.encode(returnUrl, StandardCharsets.UTF_8));
    }
}
