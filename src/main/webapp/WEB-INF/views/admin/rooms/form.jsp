<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${not empty room ? 'Sửa phòng' : 'Thêm phòng'} - Admin Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/ui-kit.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/layout.css" rel="stylesheet">
</head>
<body>
    <input type="checkbox" id="sidebar-toggle">
    <div class="app-layout">
        <c:set var="activePage" value="rooms" scope="request"/>
        <jsp:include page="../includes/sidebar.jsp" />

        <main class="app-main">
            <c:set var="pageTitle" value="${not empty room ? 'Sửa phòng' : 'Thêm phòng'}" scope="request"/>
            <jsp:include page="../includes/header.jsp" />

            <div class="app-content">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/rooms">Phòng</a></li>
                        <li class="breadcrumb-item active">${not empty room ? 'Sửa' : 'Thêm'}</li>
                    </ol>
                </nav>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-circle me-2"></i>${error}
                    </div>
                </c:if>

                <div class="card" style="max-width: 600px;">
                    <div class="card-header">
                        <i class="bi bi-door-open me-2"></i>${not empty room ? 'Sửa' : 'Thêm'} phòng
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty room}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/rooms/edit">
                                    <input type="hidden" name="roomId" value="${room.roomId}">
                                    <input type="hidden" name="id" value="${room.roomId}">
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="${pageContext.request.contextPath}/admin/rooms/create">
                            </c:otherwise>
                        </c:choose>

                            <div class="mb-3">
                                <label for="roomNumber" class="form-label">Số phòng <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="roomNumber" name="roomNumber"
                                       value="${room != null ? room.roomNumber : ''}"
                                       placeholder="VD: 101, 202A" required maxlength="10">
                            </div>

                            <div class="mb-3">
                                <label for="typeId" class="form-label">Loại phòng <span class="text-danger">*</span></label>
                                <select class="form-select" id="typeId" name="typeId" required>
                                    <option value="">-- Chọn loại phòng --</option>
                                    <c:forEach var="rt" items="${roomTypes}">
                                        <option value="${rt.typeId}"
                                            <c:if test="${room != null && room.typeId == rt.typeId}">selected</c:if>>
                                            ${rt.typeName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="mb-4">
                                <label for="status" class="form-label">Trạng thái <span class="text-danger">*</span></label>
                                <select class="form-select" id="status" name="status" required>
                                    <c:forEach var="s" items="${statuses}">
                                        <option value="${s}"
                                            <c:if test="${room != null && room.status == s}">selected</c:if>
                                            <c:if test="${room == null && s == 'Available'}">selected</c:if>>
                                            <c:choose>
                                                <c:when test="${s == 'Available'}">Sẵn sàng</c:when>
                                                <c:when test="${s == 'Occupied'}">Đang sử dụng</c:when>
                                                <c:when test="${s == 'Cleaning'}">Đang dọn</c:when>
                                                <c:when test="${s == 'Maintenance'}">Bảo trì</c:when>
                                                <c:otherwise>${s}</c:otherwise>
                                            </c:choose>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-check-lg me-1"></i>
                                    ${not empty room ? 'Cập nhật' : 'Tạo phòng'}
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/rooms"
                                   class="btn btn-secondary">Hủy</a>
                            </div>

                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <label for="sidebar-toggle" class="mobile-toggle">
        <i class="bi bi-list"></i>
    </label>

    <jsp:include page="../includes/footer.jsp" />
</body>
</html>
