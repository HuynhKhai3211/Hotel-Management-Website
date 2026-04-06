<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quên mật khẩu - Luxury Hotel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/ui-kit.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/layout.css" rel="stylesheet">
</head>
<body>
    <div class="auth-layout">
        <!-- Hero Section - Left -->
        <div class="auth-hero d-none d-lg-flex">
            <div class="auth-hero-content">
                <div class="auth-hero-logo">Luxury<span>Hotel</span></div>
                <h2 class="mb-4" style="font-family: var(--font-display);">Khoi phuc tai khoan</h2>
                <p class="auth-hero-text">Nhap email da dang ky de nhan ma xac thuc OTP va dat lai mat khau cua ban.</p>
                <div class="mt-5">
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-1-circle-fill" style="color: var(--secondary);"></i>
                        <span>Nhap email cua ban</span>
                    </div>
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-2-circle" style="color: var(--secondary); opacity: 0.5;"></i>
                        <span style="opacity: 0.5;">Xac thuc ma OTP</span>
                    </div>
                    <div class="d-flex align-items-center gap-3">
                        <i class="bi bi-3-circle" style="color: var(--secondary); opacity: 0.5;"></i>
                        <span style="opacity: 0.5;">Dat mat khau moi</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Form Section - Right -->
        <div class="auth-form-container">
            <div class="auth-form">
                <!-- Logo for mobile -->
                <div class="text-center d-lg-none mb-4">
                    <div class="auth-hero-logo" style="color: var(--primary);">Luxury<span>Hotel</span></div>
                </div>

                <h2 class="auth-form-title">Quen mat khau</h2>
                <p class="auth-form-subtitle">Nhap email de nhan ma xac thuc OTP</p>

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

                <form method="post" action="${pageContext.request.contextPath}/auth/forgot-password">
                    <div class="mb-4">
                        <label for="email" class="form-label">Dia chi Email</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="${email}" placeholder="email@example.com" required>
                        </div>
                        <div class="form-text">Chung toi se gui ma OTP den email nay</div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 btn-lg mb-3">
                        <i class="bi bi-send me-2"></i>Gui ma OTP
                    </button>
                </form>

                <div class="text-center mt-4">
                    <a href="${pageContext.request.contextPath}/auth/login" style="color: var(--secondary-dark); font-weight: 500;">
                        <i class="bi bi-arrow-left me-1"></i>Quay lai dang nhap
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
