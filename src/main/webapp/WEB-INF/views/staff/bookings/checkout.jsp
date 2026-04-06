<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Check-out - Staff Portal</title>
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
            <c:set var="pageTitle" value="Check-out" scope="request"/>
            <jsp:include page="../includes/header.jsp" />

            <div class="app-content">
                <div class="mb-3">
                    <a href="${pageContext.request.contextPath}/staff/bookings" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-1"></i>Quay lại
                    </a>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <div class="row">
                    <!-- Booking Summary -->
                    <div class="col-lg-6">
                        <div class="card mb-4">
                            <div class="card-header bg-white">
                                <h5 class="mb-0"><i class="bi bi-receipt me-2"></i>Thông tin đặt phòng</h5>
                            </div>
                            <div class="card-body">
                                <table class="table table-borderless">
                                    <tr>
                                        <th style="width: 40%">Mã booking:</th>
                                        <td><strong>#${booking.bookingId}</strong></td>
                                    </tr>
                                    <tr>
                                        <th>Phòng:</th>
                                        <td>${booking.room.roomNumber} - ${booking.room.roomType.typeName}</td>
                                    </tr>
                                    <tr>
                                        <th>Ngày nhận phòng:</th>
                                        <td>
                                            <fmt:parseDate value="${booking.checkInExpected}" pattern="yyyy-MM-dd'T'HH:mm" var="checkIn"/>
                                            <fmt:formatDate value="${checkIn}" pattern="dd/MM/yyyy HH:mm"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Ngày trả phòng:</th>
                                        <td>
                                            <fmt:parseDate value="${booking.checkOutExpected}" pattern="yyyy-MM-dd'T'HH:mm" var="checkOut"/>
                                            <fmt:formatDate value="${checkOut}" pattern="dd/MM/yyyy HH:mm"/>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </div>

                        <!-- Occupants -->
                        <div class="card mb-4">
                            <div class="card-header bg-white">
                                <h5 class="mb-0"><i class="bi bi-people me-2"></i>Khách lưu trú</h5>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty occupants}">
                                        <ul class="list-unstyled mb-0">
                                            <c:forEach var="occ" items="${occupants}">
                                                <li class="d-flex justify-content-between py-2 border-bottom">
                                                    <span>${occ.fullName}</span>
                                                    <span class="text-muted">${occ.idCardNumber}</span>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted mb-0">Không có thông tin khách</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <!-- Invoice Summary -->
                    <div class="col-lg-6">
                        <div class="card">
                            <div class="card-header bg-warning">
                                <h5 class="mb-0"><i class="bi bi-cash-stack me-2"></i>Hóa đơn thanh toán</h5>
                            </div>
                            <div class="card-body">
                                <table class="table table-borderless">
                                    <tr>
                                        <td>Tiền phòng:</td>
                                        <td class="text-end">
                                            <fmt:formatNumber value="${booking.totalPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                                        </td>
                                    </tr>
                                    <tr class="border-top">
                                        <td><strong>Tổng cộng:</strong></td>
                                        <td class="text-end">
                                            <strong class="fs-4 text-success">
                                                <fmt:formatNumber value="${booking.totalPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                                            </strong>
                                        </td>
                                    </tr>
                                </table>

                                <hr>

                                <div class="alert alert-info">
                                    <i class="bi bi-info-circle me-2"></i>
                                    Sau khi xác nhận check-out, phòng sẽ được chuyển sang trạng thái "Dọn dẹp".
                                </div>

                                <form action="${pageContext.request.contextPath}/staff/bookings/checkout" method="post">
                                    <input type="hidden" name="bookingId" value="${booking.bookingId}">
                                    <button type="submit" class="btn btn-warning btn-lg w-100">
                                        <i class="bi bi-box-arrow-right me-2"></i>Xác nhận Check-out
                                    </button>
                                </form>
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
