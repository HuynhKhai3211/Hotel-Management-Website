<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Thanh toán thành công - Staff Portal</title>
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
            <c:set var="pageTitle" value="Thanh toán thành công" scope="request"/>
            <jsp:include page="../includes/header.jsp" />

            <div class="app-content">
                <div class="row justify-content-center">
                    <div class="col-lg-6">
                        <div class="card">
                            <div class="card-body text-center py-5">
                                <div class="mb-4">
                                    <i class="bi bi-check-circle-fill text-success" style="font-size: 80px;"></i>
                                </div>
                                <h2 class="mb-3">Thanh toán thành công!</h2>
                                <p class="text-muted mb-4">Giao dịch đã được ghi nhận vào hệ thống.</p>

                                <div class="bg-light rounded p-4 mb-4 text-start">
                                    <h6 class="border-bottom pb-2 mb-3">Chi tiết giao dịch</h6>
                                    <table class="table table-borderless table-sm mb-0">
                                        <tr>
                                            <td>Mã hóa đơn:</td>
                                            <td class="text-end"><strong>#${invoice.invoiceId}</strong></td>
                                        </tr>
                                        <tr>
                                            <td>Mã booking:</td>
                                            <td class="text-end">#${booking.bookingId}</td>
                                        </tr>
                                        <tr>
                                            <td>Phòng:</td>
                                            <td class="text-end">${booking.room.roomNumber}</td>
                                        </tr>
                                        <c:if test="${not empty payment}">
                                            <tr>
                                                <td>Phương thức:</td>
                                                <td class="text-end">
                                                    <c:choose>
                                                        <c:when test="${payment.paymentMethod == 'Cash'}">
                                                            <span class="badge bg-success">Tiền mặt</span>
                                                        </c:when>
                                                        <c:when test="${payment.paymentMethod == 'Momo'}">
                                                            <span class="badge" style="background: #d82d8b;">Momo</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-secondary">${payment.paymentMethod}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>Mã giao dịch:</td>
                                                <td class="text-end"><code>${payment.transactionCode}</code></td>
                                            </tr>
                                            <tr>
                                                <td>Thời gian:</td>
                                                <td class="text-end">
                                                    <fmt:parseDate value="${payment.paymentTime}" pattern="yyyy-MM-dd'T'HH:mm" var="payTime"/>
                                                    <fmt:formatDate value="${payTime}" pattern="dd/MM/yyyy HH:mm"/>
                                                </td>
                                            </tr>
                                        </c:if>
                                        <tr class="border-top">
                                            <td><strong>Số tiền:</strong></td>
                                            <td class="text-end">
                                                <strong class="text-success fs-5">
                                                    <fmt:formatNumber value="${invoice.totalAmount}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                                                </strong>
                                            </td>
                                        </tr>
                                    </table>
                                </div>

                                <div class="d-grid gap-2">
                                    <a href="${pageContext.request.contextPath}/staff/dashboard" class="btn btn-staff-primary btn-lg">
                                        <i class="bi bi-house me-2"></i>Về Dashboard
                                    </a>
                                    <a href="${pageContext.request.contextPath}/staff/bookings" class="btn btn-outline-secondary">
                                        <i class="bi bi-list me-2"></i>Danh sách Booking
                                    </a>
                                </div>
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
