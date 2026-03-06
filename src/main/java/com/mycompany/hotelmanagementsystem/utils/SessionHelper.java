package com.mycompany.hotelmanagementsystem.utils;

import com.mycompany.hotelmanagementsystem.model.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SessionHelper {
    public static final String ACCOUNT_KEY = "loggedInAccount";
    public static final String CUSTOMER_KEY = "loggedInCustomer";

    private SessionHelper() {}

    public static Account getLoggedInAccount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Account) session.getAttribute(ACCOUNT_KEY);
        }
        return null;
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoggedInAccount(request) != null;
    }

    public static void setLoggedInAccount(HttpServletRequest request, Account account) {
        request.getSession().setAttribute(ACCOUNT_KEY, account);
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
