<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${not empty roomType ? 'Sửa loại phòng' : 'Thêm loại phòng'} - Admin Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/ui-kit.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/layout.css" rel="stylesheet">
</head>
<body>
    <input type="checkbox" id="sidebar-toggle">
    <div class="app-layout">
        <c:set var="activePage" value="room-types" scope="request"/>
        <jsp:include page="../includes/sidebar.jsp" />

        <main class="app-main">
            <c:set var="pageTitle" value="${not empty roomType ? 'Sửa loại phòng' : 'Thêm loại phòng'}" scope="request"/>
            <jsp:include page="../includes/header.jsp" />

            <div class="app-content">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/room-types">Loại phòng</a></li>
                        <li class="breadcrumb-item active">${not empty roomType ? 'Sửa' : 'Thêm'}</li>
                    </ol>
                </nav>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-circle me-2"></i>${error}
                    </div>
                </c:if>

                <div class="card" style="max-width: 600px;">
                    <div class="card-header">
                        <i class="bi bi-collection me-2"></i>${not empty roomType ? 'Sửa' : 'Thêm'} loại phòng
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty roomType}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/room-types/edit">
                                    <input type="hidden" name="typeId" value="${roomType.typeId}">
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="${pageContext.request.contextPath}/admin/room-types/create">
                            </c:otherwise>
                        </c:choose>

                            <div class="mb-3">
                                <label for="typeName" class="form-label">Tên loại phòng <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="typeName" name="typeName"
                                       value="${roomType != null ? roomType.typeName : ''}"
                                       placeholder="VD: Deluxe, Suite, Standard" required maxlength="100">
                            </div>

                            <div class="mb-3">
                                <label for="basePrice" class="form-label">Giá cơ bản (VND/đêm) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="basePrice" name="basePrice"
                                       value="${roomType != null ? roomType.basePrice : ''}"
                                       placeholder="VD: 500000" min="0" step="1000" required>
                            </div>

                            <div class="mb-3">
                                <label for="capacity" class="form-label">Sức chứa (người) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="capacity" name="capacity"
                                       value="${roomType != null ? roomType.capacity : ''}"
                                       placeholder="VD: 2" min="1" max="20" required>
                            </div>

                            <div class="mb-4">
                                <label for="description" class="form-label">Mô tả</label>
                                <textarea class="form-control" id="description" name="description"
                                          rows="4" placeholder="Mô tả về loại phòng này..."
                                          maxlength="500">${roomType != null ? roomType.description : ''}</textarea>
                            </div>

                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-check-lg me-1"></i>
                                    ${not empty roomType ? 'Cập nhật' : 'Tạo loại phòng'}
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/room-types"
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
