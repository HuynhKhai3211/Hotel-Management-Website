<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="app-topbar">
    <div class="topbar-left">
<<<<<<< HEAD
        <label for="sidebar-toggle" class="sidebar-toggle-btn d-none d-lg-block">
            <i class="bi bi-list fs-5"></i>
        </label>
        <label for="sidebar-toggle" class="sidebar-toggle-btn d-lg-none">
            <i class="bi bi-list fs-5"></i>
        </label>
        <h1 class="topbar-title">${pageTitle != null ? pageTitle : 'Dashboard'}</h1>
=======
        <label for="sidebar-toggle" class="sidebar-toggle-btn">
            <i class="bi bi-list fs-5"></i>
        </label>
        <h1 class="topbar-title">${pageTitle != null ? pageTitle : 'Bảng điều khiển'}</h1>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    </div>
    <div class="topbar-right">
        <div class="topbar-search d-none d-md-block">
            <i class="bi bi-search topbar-search-icon"></i>
            <input type="text" class="topbar-search-input" placeholder="Tìm kiếm...">
        </div>
<<<<<<< HEAD
        <button class="topbar-notification">
=======
        <button class="topbar-notification" title="Thông báo">
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
            <i class="bi bi-bell fs-5"></i>
            <span class="topbar-notification-badge"></span>
        </button>
        <div class="topbar-user dropdown">
            <div data-bs-toggle="dropdown" aria-expanded="false">
<<<<<<< HEAD
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
=======
                <div class="d-flex align-items-center gap-2">
                    <div class="topbar-user-avatar">
                        ${sessionScope.loggedInAccount.fullName.substring(0, 1)}
                    </div>
                    <div class="d-none d-lg-block">
                        <div class="topbar-user-name">${sessionScope.loggedInAccount.fullName}</div>
                        <div class="topbar-user-role">Quản trị viên</div>
                    </div>
                    <i class="bi bi-chevron-down d-none d-lg-block" style="font-size: 0.7rem; color: var(--text-muted);"></i>
                </div>
            </div>
            <ul class="dropdown-menu dropdown-menu-end" style="min-width: 220px;">
                <li class="px-3 py-2">
                    <div class="fw-semibold">${sessionScope.loggedInAccount.fullName}</div>
                    <small class="text-muted">Quản trị viên</small>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/settings">
                    <i class="bi bi-gear me-2"></i>Cài đặt</a></li>
                <li><hr class="dropdown-divider"></li>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout">
                    <i class="bi bi-box-arrow-left me-2"></i>Đăng xuất</a></li>
            </ul>
        </div>
    </div>
</header>
