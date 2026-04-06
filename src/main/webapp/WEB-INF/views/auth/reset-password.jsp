<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dat lai mat khau - Luxury Hotel</title>
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
                <h2 class="mb-4" style="font-family: var(--font-display);">Dat mat khau moi</h2>
                <p class="auth-hero-text">Tao mat khau moi cho tai khoan cua ban. Mat khau phai co it nhat 8 ky tu.</p>
                <div class="mt-5">
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-check-circle-fill" style="color: var(--secondary);"></i>
                        <span>Nhap email cua ban</span>
                    </div>
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-check-circle-fill" style="color: var(--secondary);"></i>
                        <span>Xac thuc ma OTP</span>
                    </div>
                    <div class="d-flex align-items-center gap-3">
                        <i class="bi bi-3-circle-fill" style="color: var(--secondary);"></i>
                        <span>Dat mat khau moi</span>
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

                <h2 class="auth-form-title">Dat mat khau moi</h2>
                <p class="auth-form-subtitle">Nhap mat khau moi cho tai khoan cua ban</p>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-circle me-2"></i>${error}
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/auth/reset-password">
                    <div class="mb-3">
                        <label for="newPassword" class="form-label">Mat khau moi</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-lock"></i></span>
                            <input type="password" class="form-control" id="newPassword"
                                   name="newPassword" placeholder="Nhap mat khau moi" required minlength="8">
                        </div>
                        <div class="form-text">Mat khau phai co it nhat 8 ky tu</div>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label">Xac nhan mat khau</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                            <input type="password" class="form-control" id="confirmPassword"
                                   name="confirmPassword" placeholder="Nhap lai mat khau" required minlength="8">
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 btn-lg mb-3">
                        <i class="bi bi-check-lg me-2"></i>Dat lai mat khau
                    </button>
                </form>

                <div class="text-center mt-4">
                    <a href="${pageContext.request.contextPath}/auth/login" class="text-muted small">
                        <i class="bi bi-arrow-left me-1"></i>Quay lai dang nhap
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.querySelector('form').addEventListener('submit', function(e) {
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (newPassword !== confirmPassword) {
                e.preventDefault();
                alert('Mat khau xac nhan khong khop');
            }
        });
    </script>
</body>
</html>
