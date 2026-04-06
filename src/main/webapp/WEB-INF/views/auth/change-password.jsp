<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đổi mật khẩu - Luxury Hotel</title>
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
        <div class="container">
            <h1 class="public-hero-title"><i class="bi bi-key me-2"></i>Đổi mật khẩu</h1>
        </div>
    </section>

    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card">
                    <div class="card-header">
                        <i class="bi bi-shield-lock me-2"></i>Cập nhật mật khẩu
                    </div>
                    <div class="card-body p-4">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">
                                <i class="bi bi-exclamation-circle me-2"></i>${error}
                            </div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="alert alert-success">
                                <i class="bi bi-check-circle me-2"></i>${success}
                            </div>
                        </c:if>

                        <form method="post">
                            <div class="mb-3">
                                <label for="currentPassword" class="form-label">Mật khẩu hiện tại <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-lock"></i></span>
                                    <input type="password" class="form-control" id="currentPassword"
                                           name="currentPassword" required>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="newPassword" class="form-label">Mật khẩu mới <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                                    <input type="password" class="form-control" id="newPassword"
                                           name="newPassword" minlength="8" required>
                                </div>
                                <div class="form-text">Tối thiểu 8 ký tự</div>
                            </div>

                            <div class="mb-4">
                                <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                                    <input type="password" class="form-control" id="confirmPassword"
                                           name="confirmPassword" required>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary w-100">
                                <i class="bi bi-check-lg me-2"></i>Đổi mật khẩu
                            </button>
                        </form>

                        <div class="text-center mt-4 pt-3 border-top">
                            <a href="${pageContext.request.contextPath}/customer/profile" class="text-muted">
                                <i class="bi bi-arrow-left me-1"></i>Quay về hồ sơ
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/includes/footer.jsp"/>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
