<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard - Admin Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/ui-kit.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/layout.css" rel="stylesheet">
</head>
<body>
    <input type="checkbox" id="sidebar-toggle">
    <div class="app-layout">
        <jsp:include page="includes/sidebar.jsp" />

        <main class="app-main">
            <c:set var="pageTitle" value="Dashboard" scope="request"/>
            <jsp:include page="includes/header.jsp" />

            <div class="app-content">
                <!-- Breadcrumb -->
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item active">Dashboard</li>
                    </ol>
                </nav>

                <!-- Page Header -->
                <div class="page-header">
                    <div>
                        <h1 class="page-header-title">Xin chào, ${sessionScope.loggedInAccount.fullName}!</h1>
                        <p class="page-header-subtitle">Dưới đây là tổng quan hệ thống khách sạn.</p>
                    </div>
                </div>

                <!-- Stats Cards -->
                <div class="row g-4 mb-4">
                    <div class="col-md-6 col-lg-3">
                        <div class="card-stat">
                            <div class="card-stat-icon rooms">
                                <i class="bi bi-door-open"></i>
                            </div>
                            <div>
                                <div class="card-stat-value">${stats.totalRooms}</div>
                                <div class="card-stat-label">Tổng số phòng</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-3">
                        <div class="card-stat">
                            <div class="card-stat-icon bookings">
                                <i class="bi bi-calendar-check"></i>
                            </div>
                            <div>
                                <div class="card-stat-value">${stats.totalBookings}</div>
                                <div class="card-stat-label">Tổng booking</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-3">
                        <div class="card-stat">
                            <div class="card-stat-icon revenue">
                                <i class="bi bi-currency-dollar"></i>
                            </div>
                            <div>
                                <div class="card-stat-value">
                                    <fmt:formatNumber value="${stats.totalRevenue}" type="currency" currencySymbol="" maxFractionDigits="0"/>
                                </div>
                                <div class="card-stat-label">Doanh thu (VNĐ)</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-3">
                        <div class="card-stat">
                            <div class="card-stat-icon customers">
                                <i class="bi bi-people"></i>
                            </div>
                            <div>
                                <div class="card-stat-value">${stats.totalCustomers}</div>
                                <div class="card-stat-label">Khách hàng</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Quick Links & Room Status -->
                <div class="row g-4">
                    <div class="col-lg-6">
                        <div class="card h-100">
                            <div class="card-header">
                                <i class="bi bi-lightning me-2"></i>Truy cập nhanh
                            </div>
                            <div class="card-body">
                                <div class="row g-3">
                                    <div class="col-6">
                                        <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-outline-secondary w-100 py-3">
                                            <i class="bi bi-door-open d-block fs-3 mb-2"></i>
                                            Quản lý phòng
                                        </a>
                                    </div>
                                    <div class="col-6">
                                        <a href="${pageContext.request.contextPath}/admin/customers" class="btn btn-outline-secondary w-100 py-3">
                                            <i class="bi bi-people d-block fs-3 mb-2"></i>
                                            Khách hàng
                                        </a>
                                    </div>
                                    <div class="col-6">
                                        <a href="${pageContext.request.contextPath}/admin/staff" class="btn btn-outline-secondary w-100 py-3">
                                            <i class="bi bi-person-badge d-block fs-3 mb-2"></i>
                                            Nhân viên
                                        </a>
                                    </div>
                                    <div class="col-6">
                                        <a href="${pageContext.request.contextPath}/admin/vouchers" class="btn btn-outline-secondary w-100 py-3">
                                            <i class="bi bi-ticket-perforated d-block fs-3 mb-2"></i>
                                            Voucher
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="card h-100">
                            <div class="card-header">
                                <i class="bi bi-pie-chart me-2"></i>Trạng thái phòng
                            </div>
                            <div class="card-body">
                                <div class="row text-center">
                                    <div class="col-6 mb-3">
                                        <div class="p-3 rounded" style="background: var(--success-light);">
                                            <div class="fs-2 fw-bold" style="color: var(--success);">${stats.availableRooms}</div>
                                            <div class="small text-secondary">Sẵn sàng</div>
                                        </div>
                                    </div>
                                    <div class="col-6 mb-3">
                                        <div class="p-3 rounded" style="background: var(--danger-light);">
                                            <div class="fs-2 fw-bold" style="color: var(--danger);">${stats.occupiedRooms}</div>
                                            <div class="small text-secondary">Đang sử dụng</div>
                                        </div>
                                    </div>
                                </div>
                                <a href="${pageContext.request.contextPath}/admin/reports/utilization" class="btn btn-sm btn-light w-100 mt-2">
                                    Xem báo cáo chi tiết <i class="bi bi-arrow-right"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- Mobile Toggle -->
    <label for="sidebar-toggle" class="mobile-toggle">
        <i class="bi bi-list"></i>
    </label>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
