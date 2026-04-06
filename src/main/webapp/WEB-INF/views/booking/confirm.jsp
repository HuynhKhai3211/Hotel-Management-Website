<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Xác nhận đặt phòng - Luxury Hotel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/ui-kit.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/layout.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="/WEB-INF/includes/header.jsp"/>

    <div class="container py-5">
        <div class="page-header mb-4">
            <h1 class="page-header-title">Xác nhận đặt phòng</h1>
            <p class="page-header-subtitle">Kiểm tra thông tin và hoàn tất đặt phòng</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i>${error}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/booking/confirm">
            <div class="row g-4">
                <!-- Occupant Info -->
                <div class="col-lg-7">
                    <div class="card">
                        <div class="card-header">
                            <i class="bi bi-people me-2"></i>Thông tin khách lưu trú
                        </div>
                        <div class="card-body">
                            <div id="occupants">
                                <div class="p-3 mb-3 rounded" style="background: var(--surface-hover);">
                                    <h6 class="mb-3">Khách 1 (Chính)</h6>
                                    <div class="row g-3">
                                        <div class="col-12">
                                            <label class="form-label">Họ và tên <span class="text-danger">*</span></label>
                                            <input type="text" name="occupantName" class="form-control"
                                                   value="${account.fullName}" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label">Số CMND/CCCD</label>
                                            <input type="text" name="occupantIdCard" class="form-control"
                                                   placeholder="Nhập số CMND/CCCD">
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label">Số điện thoại</label>
                                            <input type="text" name="occupantPhone" class="form-control"
                                                   value="${account.phone}" placeholder="Nhập số điện thoại">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <button type="button" class="btn btn-outline-secondary btn-sm mb-4" onclick="addOccupant()">
                                <i class="bi bi-plus-circle me-1"></i> Thêm khách
                            </button>

                            <h6 class="mb-3"><i class="bi bi-chat-left-text me-2"></i>Ghi chú</h6>
                            <textarea name="note" class="form-control mb-4" rows="3"
                                      placeholder="Yêu cầu đặc biệt, giờ nhận phòng dự kiến..."></textarea>

                            <div class="d-flex gap-3">
                                <a href="${pageContext.request.contextPath}/booking/create?typeId=${booking.roomType.typeId}"
                                   class="btn btn-outline-secondary">
                                    <i class="bi bi-arrow-left me-1"></i> Quay lại
                                </a>
                                <button type="submit" class="btn btn-primary flex-grow-1">
                                    <i class="bi bi-credit-card me-2"></i>Tiến hành thanh toán
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Booking Summary -->
                <div class="col-lg-5">
                    <div class="card" style="background: var(--primary-gradient); color: white;">
                        <div class="card-header" style="background: transparent; border-bottom: 1px solid rgba(255,255,255,0.1);">
                            <i class="bi bi-receipt me-2"></i>Chi tiết đặt phòng
                        </div>
                        <div class="card-body">
                            <div class="mb-3">
                                <h5 style="font-family: var(--font-display);">${booking.roomType.typeName}</h5>
                                <p style="opacity: 0.75;" class="mb-0">Phòng ${booking.room.roomNumber}</p>
                            </div>

                            <div class="mb-3 pb-3" style="border-bottom: 1px solid rgba(255,255,255,0.2);">
                                <div class="row">
                                    <div class="col-6">
                                        <small style="opacity: 0.75;">Nhận phòng</small>
                                        <p class="mb-0 fw-semibold">
                                            ${booking.checkInFormatted}
                                            <br><small>14:00</small>
                                        </p>
                                    </div>
                                    <div class="col-6">
                                        <small style="opacity: 0.75;">Trả phòng</small>
                                        <p class="mb-0 fw-semibold">
                                            ${booking.checkOutFormatted}
                                            <br><small>12:00</small>
                                        </p>
                                    </div>
                                </div>
                            </div>

                            <div class="d-flex justify-content-between py-2">
                                <span>Số đêm</span>
                                <span>${booking.nights} đêm</span>
                            </div>
                            <div class="d-flex justify-content-between py-2">
                                <span>Giá phòng</span>
                                <span><fmt:formatNumber value="${booking.roomType.basePrice}" type="number" groupingUsed="true"/>đ/đêm</span>
                            </div>
                            <div class="d-flex justify-content-between py-2">
                                <span>Tạm tính</span>
                                <span><fmt:formatNumber value="${booking.subtotal}" type="number" groupingUsed="true"/>đ</span>
                            </div>
                            <c:if test="${booking.discount != null && booking.discount > 0}">
                                <div class="d-flex justify-content-between py-2" style="color: var(--success-light);">
                                    <span>Giảm giá (${booking.voucher.code})</span>
                                    <span>-<fmt:formatNumber value="${booking.discount}" type="number" groupingUsed="true"/>đ</span>
                                </div>
                            </c:if>
                            <div class="d-flex justify-content-between pt-3 mt-2" style="border-top: 1px solid rgba(255,255,255,0.2);">
                                <span class="h5 mb-0">Tổng cộng</span>
                                <span class="h4 mb-0" style="color: var(--secondary);">
                                    <fmt:formatNumber value="${booking.total}" type="number" groupingUsed="true"/>đ
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <jsp:include page="/WEB-INF/includes/footer.jsp"/>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let occupantCount = 1;
        function addOccupant() {
            occupantCount++;
            const html = `
                <div class="p-3 mb-3 rounded" style="background: var(--surface-hover);">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h6 class="mb-0">Khách ${occupantCount}</h6>
                        <button type="button" class="btn btn-sm btn-outline-danger" onclick="this.parentElement.parentElement.remove()">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label">Họ và tên</label>
                            <input type="text" name="occupantName" class="form-control">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Số CMND/CCCD</label>
                            <input type="text" name="occupantIdCard" class="form-control">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Số điện thoại</label>
                            <input type="text" name="occupantPhone" class="form-control">
                        </div>
                    </div>
                </div>
            `;
            document.getElementById('occupants').insertAdjacentHTML('beforeend', html);
        }
    </script>
</body>
</html>
