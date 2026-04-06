<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="app-topbar">
    <div class="topbar-left">
        <label for="sidebar-toggle" class="sidebar-toggle-btn d-none d-lg-block">
            <i class="bi bi-list fs-5"></i>
        </label>
        <label for="sidebar-toggle" class="sidebar-toggle-btn d-lg-none">
            <i class="bi bi-list fs-5"></i>
        </label>
        <h1 class="topbar-title">${pageTitle != null ? pageTitle : 'Dashboard'}</h1>
    </div>
    <div class="topbar-right">
        <div class="topbar-search d-none d-md-block">
            <i class="bi bi-search topbar-search-icon"></i>
            <input type="text" class="topbar-search-input" placeholder="Tìm kiếm...">
        </div>
        <button class="topbar-notification">
            <i class="bi bi-bell fs-5"></i>
            <span class="topbar-notification-badge"></span>
        </button>
        <div class="topbar-user dropdown">
            <div data-bs-toggle="dropdown" aria-expanded="false">
                <div class="topbar-user-avatar">
                    ${sessionScope.loggedInAccount.fullName.substring(0, 1)}
                </div>
            </div>
            <ul class="dropdown-menu dropdown-menu-end">
                <li><span class="dropdown-item-text fw-semibold">${sessionScope.loggedInAccount.fullName}</span></li>
                <li><span class="dropdown-item-text text-muted small">Administrator</span></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/settings">
                    <i class="bi bi-gear me-2"></i>Cài đặt</a></li>
                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout">
                    <i class="bi bi-box-arrow-left me-2"></i>Đăng xuất</a></li>
            </ul>
        </div>
    </div>
</header>
