<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Phòng nghỉ - Luxury Hotel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/ui-kit.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/layout.css" rel="stylesheet">
</head>
<body>
    <jsp:include page="/WEB-INF/includes/header.jsp"/>

    <!-- Page Header -->
    <section class="public-hero public-hero-small">
        <div class="container text-center">
            <h1 class="public-hero-title">Phòng nghỉ</h1>
            <p class="public-hero-subtitle">Chọn loại phòng phù hợp với nhu cầu của bạn</p>
        </div>
    </section>

    <div class="container py-5">
        <div class="row">
            <!-- Filter Sidebar -->
            <div class="col-lg-3 mb-4">
                <div class="card">
                    <div class="card-header">
                        <i class="bi bi-funnel me-2"></i>Lọc phòng
                    </div>
                    <div class="card-body">
                        <form method="get" action="${pageContext.request.contextPath}/rooms">
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Loại phòng</label>
                                <select name="typeId" class="form-select">
                                    <option value="">Tất cả</option>
                                    <c:forEach var="type" items="${allTypes}">
                                        <option value="${type.typeId}" ${selectedTypeId == type.typeId ? 'selected' : ''}>
                                            ${type.typeName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Giá tối thiểu (VNĐ)</label>
                                <input type="number" name="minPrice" class="form-control" value="${minPrice}" placeholder="0">
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Giá tối đa (VNĐ)</label>
                                <input type="number" name="maxPrice" class="form-control" value="${maxPrice}" placeholder="Không giới hạn">
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Số khách</label>
                                <select name="capacity" class="form-select">
                                    <option value="">Tất cả</option>
                                    <option value="1" ${capacity == 1 ? 'selected' : ''}>1+ khách</option>
                                    <option value="2" ${capacity == 2 ? 'selected' : ''}>2+ khách</option>
                                    <option value="3" ${capacity == 3 ? 'selected' : ''}>3+ khách</option>
                                    <option value="4" ${capacity == 4 ? 'selected' : ''}>4+ khách</option>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary w-100 mb-2">
                                <i class="bi bi-search me-2"></i>Tìm kiếm
                            </button>
                            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-secondary w-100">
                                <i class="bi bi-x-circle me-2"></i>Xóa bộ lọc
                            </a>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Room List -->
            <div class="col-lg-9">
                <c:if test="${empty roomTypes}">
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>Không tìm thấy phòng phù hợp với tiêu chí của bạn.
                    </div>
                </c:if>

                <div class="row g-4">
                    <c:forEach var="room" items="${roomTypes}">
                        <div class="col-md-6 col-lg-4">
                            <div class="room-card">
                                <div class="room-card-image">
                                    <c:choose>
                                        <c:when test="${not empty room.images}">
                                            <img src="${room.images[0].imageUrl}" alt="${room.typeName}">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="room-card-placeholder">
                                                <i class="bi bi-image"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="room-card-badge">
                                        <i class="bi bi-people me-1"></i>${room.capacity} khách
                                    </span>
                                </div>
                                <div class="room-card-body">
                                    <h3 class="room-card-title">${room.typeName}</h3>
                                    <p class="room-card-desc">${room.description}</p>
                                    <div class="room-card-price">
                                        <span class="price">
                                            <fmt:formatNumber value="${room.basePrice}" type="number" groupingUsed="true"/>đ
                                        </span>
                                        <span class="period">/đêm</span>
                                    </div>
                                    <c:if test="${not empty room.amenities}">
                                        <div class="room-card-amenities">
                                            <c:forEach var="amenity" items="${room.amenities}" end="2">
                                                <span class="room-card-amenity">
                                                    <i class="bi bi-check-circle"></i> ${amenity.name}
                                                </span>
                                            </c:forEach>
                                        </div>
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/rooms/detail?typeId=${room.typeId}"
                                       class="btn btn-primary w-100">
                                        Xem chi tiết <i class="bi bi-arrow-right ms-1"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <jsp:include page="/WEB-INF/includes/footer.jsp"/>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
