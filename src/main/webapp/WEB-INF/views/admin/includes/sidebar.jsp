<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="app-sidebar">
    <div class="sidebar-header">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="sidebar-logo">
            Luxury<span>Hotel</span>
        </a>
<<<<<<< HEAD
        <span class="sidebar-badge">Admin</span>
=======
        <span class="sidebar-badge">Quản trị</span>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    </div>

    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/admin/dashboard"
           class="sidebar-nav-item ${activePage == 'dashboard' ? 'active' : ''}">
            <i class="bi bi-speedometer2"></i>
<<<<<<< HEAD
            <span>Dashboard</span>
=======
            <span>Bảng điều khiển</span>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        </a>

        <div class="sidebar-section">Quản lý</div>

        <a href="${pageContext.request.contextPath}/admin/rooms"
           class="sidebar-nav-item ${activePage == 'rooms' ? 'active' : ''}">
            <i class="bi bi-door-open"></i>
            <span>Phòng</span>
        </a>

<<<<<<< HEAD
=======
        <a href="${pageContext.request.contextPath}/admin/rooms/map"
           class="sidebar-nav-item ${activePage == 'rooms-map' ? 'active' : ''}">
            <i class="bi bi-grid-3x3-gap"></i>
            <span>Sơ đồ phòng</span>
        </a>

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        <a href="${pageContext.request.contextPath}/admin/room-types"
           class="sidebar-nav-item ${activePage == 'room-types' ? 'active' : ''}">
            <i class="bi bi-grid"></i>
            <span>Loại phòng</span>
        </a>

        <a href="${pageContext.request.contextPath}/admin/customers"
           class="sidebar-nav-item ${activePage == 'customers' ? 'active' : ''}">
            <i class="bi bi-people"></i>
            <span>Khách hàng</span>
        </a>

        <a href="${pageContext.request.contextPath}/admin/staff"
           class="sidebar-nav-item ${activePage == 'staff' ? 'active' : ''}">
            <i class="bi bi-person-badge"></i>
            <span>Nhân viên</span>
        </a>

        <a href="${pageContext.request.contextPath}/admin/vouchers"
           class="sidebar-nav-item ${activePage == 'vouchers' ? 'active' : ''}">
            <i class="bi bi-ticket-perforated"></i>
            <span>Voucher</span>
        </a>

<<<<<<< HEAD
=======
        <a href="${pageContext.request.contextPath}/admin/promotions"
           class="sidebar-nav-item ${activePage == 'promotions' ? 'active' : ''}">
            <i class="bi bi-megaphone"></i>
            <span>Khuyến mãi</span>
        </a>

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        <a href="${pageContext.request.contextPath}/admin/feedback"
           class="sidebar-nav-item ${activePage == 'feedback' ? 'active' : ''}">
            <i class="bi bi-chat-dots"></i>
            <span>Phản hồi</span>
        </a>

<<<<<<< HEAD
=======
        <a href="${pageContext.request.contextPath}/admin/service-requests"
           class="sidebar-nav-item ${activePage == 'service-requests' ? 'active' : ''}">
            <i class="bi bi-bell"></i>
            <span>Yêu cầu dịch vụ</span>
        </a>

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        <div class="sidebar-section">Báo cáo</div>

        <a href="${pageContext.request.contextPath}/admin/reports/utilization"
           class="sidebar-nav-item ${activePage == 'utilization' ? 'active' : ''}">
            <i class="bi bi-bar-chart"></i>
            <span>Công suất phòng</span>
        </a>

        <a href="${pageContext.request.contextPath}/admin/reports/revenue"
           class="sidebar-nav-item ${activePage == 'revenue' ? 'active' : ''}">
            <i class="bi bi-graph-up"></i>
            <span>Doanh thu</span>
        </a>

        <div class="sidebar-section">Hệ thống</div>

        <a href="${pageContext.request.contextPath}/admin/users"
           class="sidebar-nav-item ${activePage == 'users' ? 'active' : ''}">
            <i class="bi bi-person-gear"></i>
            <span>Người dùng</span>
        </a>

        <a href="${pageContext.request.contextPath}/admin/settings"
           class="sidebar-nav-item ${activePage == 'settings' ? 'active' : ''}">
            <i class="bi bi-gear"></i>
            <span>Cài đặt</span>
        </a>

        <a href="${pageContext.request.contextPath}/admin/content/hotel-info"
           class="sidebar-nav-item ${activePage == 'hotel-info' ? 'active' : ''}">
            <i class="bi bi-building"></i>
            <span>Thông tin KS</span>
        </a>
    </nav>

    <div class="sidebar-footer">
        <div class="sidebar-user">
            <div class="sidebar-user-avatar">
                ${sessionScope.loggedInAccount.fullName.substring(0, 1)}
            </div>
            <div class="sidebar-user-info">
                <div class="sidebar-user-name">${sessionScope.loggedInAccount.fullName}</div>
<<<<<<< HEAD
                <div class="sidebar-user-role">Administrator</div>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/auth/logout" class="sidebar-nav-item" style="color: var(--danger);">
            <i class="bi bi-box-arrow-left"></i>
            <span>Đăng xuất</span>
        </a>
=======
                <div class="sidebar-user-role">Quản trị viên</div>
            </div>
        </div>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    </div>
</aside>
