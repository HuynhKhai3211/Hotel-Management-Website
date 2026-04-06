<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Thanh toán Momo - Staff Portal</title>
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
            <c:set var="pageTitle" value="Thanh toán Momo" scope="request"/>
            <jsp:include page="../includes/header.jsp" />

            <div class="app-content">
                <div class="mb-3">
                    <a href="${pageContext.request.contextPath}/staff/payments/process?bookingId=${booking.bookingId}" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-1"></i>Quay lại
                    </a>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <div class="row justify-content-center">
                    <div class="col-lg-6">
                        <div class="card">
                            <div class="card-header text-white" style="background: #d82d8b;">
                                <h5 class="mb-0"><i class="bi bi-qr-code me-2"></i>Thanh toán qua Momo</h5>
                            </div>
                            <div class="card-body text-center">
                                <div class="mb-4">
                                    <p class="text-muted mb-1">Số tiền cần thanh toán</p>
                                    <p class="fs-1 fw-bold mb-0" style="color: #d82d8b;">
                                        <fmt:formatNumber value="${invoice.totalAmount}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ
                                    </p>
                                </div>

                                <!-- QR Code Placeholder -->
                                <div class="mb-4 p-4 bg-light rounded">
                                    <div class="border border-3 rounded p-3 d-inline-block bg-white" style="border-color: #d82d8b !important;">
                                        <i class="bi bi-qr-code" style="font-size: 150px; color: #d82d8b;"></i>
                                    </div>
                                    <p class="mt-3 text-muted small">${qrData}</p>
                                </div>

                                <div class="alert alert-info text-start">
                                    <h6><i class="bi bi-phone me-2"></i>Hướng dẫn thanh toán:</h6>
                                    <ol class="mb-0 ps-3">
                                        <li>Mở ứng dụng Momo trên điện thoại</li>
                                        <li>Chọn "Quét mã QR"</li>
                                        <li>Quét mã QR trên màn hình</li>
                                        <li>Xác nhận thanh toán trên điện thoại</li>
                                        <li>Nhập mã giao dịch bên dưới để xác nhận</li>
                                    </ol>
                                </div>

                                <hr>

                                <form action="${pageContext.request.contextPath}/staff/payments/momo" method="post">
                                    <input type="hidden" name="invoiceId" value="${invoice.invoiceId}">
                                    <input type="hidden" name="customerId" value="${booking.customerId}">
                                    <input type="hidden" name="amount" value="${invoice.totalAmount}">

                                    <div class="mb-3">
                                        <label class="form-label">Mã giao dịch Momo (nếu có)</label>
                                        <input type="text" class="form-control" name="transactionCode"
                                               placeholder="Nhập mã giao dịch từ Momo">
                                        <div class="form-text">Để trống nếu không có mã giao dịch</div>
                                    </div>

                                    <button type="submit" class="btn btn-lg w-100" style="background: #d82d8b; color: white;">
                                        <i class="bi bi-check-circle me-2"></i>Xác nhận đã thanh toán
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
