<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Chi tiết Booking #${booking.bookingId} - Staff Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/ui-kit.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/layout.css" rel="stylesheet">
</head>
<body>
    <input type="checkbox" id="sidebar-toggle">
    <div class="app-layout">
        <c:set var="activePage" value="bookings" scope="request"/>
        <jsp:include page="../includes/sidebar.jsp" />

        <main class="app-main">
            <c:set var="pageTitle" value="Chi tiết Booking #${booking.bookingId}" scope="request"/>
            <jsp:include page="../includes/header.jsp" />

            <div class="app-content">
                <div class="mb-3">
                    <a href="${pageContext.request.contextPath}/staff/bookings" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách
                    </a>
                </div>

                <div class="row">
                    <div class="col-lg-8">
                        <div class="card mb-4">
                            <div class="card-header bg-white d-flex justify-content-between align-items-center">
                                <h5 class="mb-0"><i class="bi bi-receipt me-2"></i>Booking #${booking.bookingId}</h5>
                                <c:choose>
                                    <c:when test="${booking.status == 'Pending'}">
                                        <span class="badge bg-warning text-dark">Chờ xác nhận</span>
                                    </c:when>
                                    <c:when test="${booking.status == 'Confirmed'}">
                                        <span class="badge bg-success">Chờ check-in</span>
                                    </c:when>
                                    <c:when test="${booking.status == 'CheckedIn'}">
                                        <span class="badge bg-info">Đang ở</span>
                                    </c:when>
                                    <c:when test="${booking.status == 'CheckedOut'}">
                                        <span class="badge bg-secondary">Đã check-out</span>
                                    </c:when>
                                    <c:when test="${booking.status == 'Cancelled'}">
                                        <span class="badge bg-danger">Đã hủy</span>
                                    </c:when>
                                </c:choose>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <h6 class="text-muted mb-3">Thông tin phòng</h6>
                                        <p><strong>Số phòng:</strong> ${booking.room.roomNumber}</p>
                                        <p><strong>Loại phòng:</strong> ${booking.room.roomType.typeName}</p>
                                        <p><strong>Giá cơ bản:</strong>
                                            <fmt:formatNumber value="${booking.room.roomType.basePrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ/đêm
                                        </p>
                                    </div>
                                    <div class="col-md-6">
                                        <h6 class="text-muted mb-3">Thời gian</h6>
                                        <p><strong>Ngày đặt:</strong>
                                            <fmt:parseDate value="${booking.bookingDate}" pattern="yyyy-MM-dd'T'HH:mm" var="bookDate"/>
                                            <fmt:formatDate value="${bookDate}" pattern="dd/MM/yyyy HH:mm"/>
                                        </p>
                                        <p><strong>Check-in:</strong>
                                            <fmt:parseDate value="${booking.checkInExpected}" pattern="yyyy-MM-dd'T'HH:mm" var="checkIn"/>
                                            <fmt:formatDate value="${checkIn}" pattern="dd/MM/yyyy HH:mm"/>
                                        </p>
                                        <p><strong>Check-out:</strong>
                                            <fmt:parseDate value="${booking.checkOutExpected}" pattern="yyyy-MM-dd'T'HH:mm" var="checkOut"/>
                                            <fmt:formatDate value="${checkOut}" pattern="dd/MM/yyyy HH:mm"/>
                                        </p>
                                    </div>
                                </div>

                                <hr>

                                <h6 class="text-muted mb-3">Khách lưu trú</h6>
                                <c:choose>
                                    <c:when test="${not empty occupants}">
                                        <div class="table-responsive">
                                            <table class="table table-sm">
                                                <thead>
                                                    <tr>
                                                        <th>Họ tên</th>
                                                        <th>CCCD/Passport</th>
                                                        <th>SĐT</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="occ" items="${occupants}">
                                                        <tr>
                                                            <td>${occ.fullName}</td>
                                                            <td>${occ.idCardNumber}</td>
                                                            <td>${occ.phoneNumber}</td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted">Chưa có thông tin khách</p>
                                    </c:otherwise>
                                </c:choose>

                                <c:if test="${not empty booking.note}">
                                    <hr>
                                    <h6 class="text-muted mb-2">Ghi chú</h6>
                                    <p class="mb-0">${booking.note}</p>
                                </c:if>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-4">
                        <div class="card mb-4">
                            <div class="card-header bg-success text-white">
                                <h5 class="mb-0"><i class="bi bi-cash me-2"></i>Thanh toán</h5>
                            </div>
                            <div class="card-body">
                                <p class="fs-3 text-success fw-bold text-center mb-0">
                                    <fmt:formatNumber value="${booking.totalPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                                </p>
                            </div>
                        </div>

                        <div class="card">
                            <div class="card-header bg-white">
                                <h5 class="mb-0"><i class="bi bi-gear me-2"></i>Thao tác</h5>
                            </div>
                            <div class="card-body d-grid gap-2">
                                <c:if test="${booking.status == 'Confirmed'}">
                                    <a href="${pageContext.request.contextPath}/staff/bookings/assign?bookingId=${booking.bookingId}"
                                       class="btn btn-success">
                                        <i class="bi bi-box-arrow-in-right me-1"></i>Check-in
                                    </a>
                                </c:if>
                                <c:if test="${booking.status == 'CheckedIn'}">
                                    <a href="${pageContext.request.contextPath}/staff/bookings/occupants?bookingId=${booking.bookingId}"
                                       class="btn btn-primary">
                                        <i class="bi bi-people me-1"></i>Quản lý khách
                                    </a>
                                    <a href="${pageContext.request.contextPath}/staff/bookings/checkout?bookingId=${booking.bookingId}"
                                       class="btn btn-warning">
                                        <i class="bi bi-box-arrow-right me-1"></i>Check-out
                                    </a>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <jsp:include page="../includes/footer.jsp" />
</body>
</html>
