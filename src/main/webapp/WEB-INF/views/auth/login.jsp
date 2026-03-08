<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập - Luxury Hotel</title>
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
                <h2 class="mb-4" style="font-family: var(--font-display);">Chào mừng trở lại</h2>
                <p class="auth-hero-text">Trải nghiệm dịch vụ đẳng cấp 5 sao. Quản lý đặt phòng dễ dàng, nhanh chóng và tiện lợi.</p>
                <div class="mt-5">
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-check-circle-fill" style="color: var(--secondary);"></i>
                        <span>Đặt phòng trực tuyến 24/7</span>
                    </div>
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-check-circle-fill" style="color: var(--secondary);"></i>
                        <span>Xác nhận tức thì</span>
                    </div>
                    <div class="d-flex align-items-center gap-3">
                        <i class="bi bi-check-circle-fill" style="color: var(--secondary);"></i>
                        <span>Hỗ trợ khách hàng tận tình</span>
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

                <h2 class="auth-form-title">Đăng nhập</h2>
                <p class="auth-form-subtitle">Đăng nhập để quản lý đặt phòng của bạn</p>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-circle me-2"></i>${error}
                    </div>
                </c:if>

                <c:if test="${param.registered == 'true'}">
                    <div class="alert alert-success">
                        <i class="bi bi-check-circle me-2"></i>Dang ky thanh cong! Vui long dang nhap.
                    </div>
                </c:if>

                <c:if test="${param.reset == 'success'}">
                    <div class="alert alert-success">
                        <i class="bi bi-check-circle me-2"></i>Dat lai mat khau thanh cong! Vui long dang nhap.
                    </div>
                </c:if>

                <c:if test="${param.error == 'admin_required'}">
                    <div class="alert alert-warning">
                        <i class="bi bi-exclamation-triangle me-2"></i>Bạn cần đăng nhập với tài khoản Admin để truy cập trang này.
                    </div>
                </c:if>

                <c:if test="${param.error == 'staff_required'}">
                    <div class="alert alert-warning">
                        <i class="bi bi-exclamation-triangle me-2"></i>Bạn cần đăng nhập với tài khoản Nhân viên để truy cập trang này.
                    </div>
                </c:if>

                <c:if test="${param.error == 'google_not_configured'}">
                    <div class="alert alert-warning">
                        <i class="bi bi-exclamation-triangle me-2"></i>Đăng nhập Google chưa được cấu hình.
                    </div>
                </c:if>

                <c:if test="${param.error == 'google_denied'}">
                    <div class="alert alert-warning">
                        <i class="bi bi-exclamation-triangle me-2"></i>Bạn đã từ chối đăng nhập bằng Google.
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/auth/login">
                    <input type="hidden" name="returnUrl" value="${returnUrl}">

                    <div class="mb-3">
                        <label for="email" class="form-label">Địa chỉ Email</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="${email}" placeholder="email@example.com" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label">Mật khẩu</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-lock"></i></span>
                            <input type="password" class="form-control" id="password"
                                   name="password" placeholder="Nhập mật khẩu" required>
                        </div>
                    </div>

                    <div class="mb-4 d-flex justify-content-between align-items-center">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input" id="remember" name="remember">
                            <label class="form-check-label" for="remember">Ghi nhớ đăng nhập</label>
                        </div>
                        <a href="${pageContext.request.contextPath}/auth/forgot-password" class="small" style="color: var(--secondary-dark);">Quen mat khau?</a>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 btn-lg mb-3">
                        <i class="bi bi-box-arrow-in-right me-2"></i>Đăng nhập
                    </button>
                </form>

                <!-- Divider -->
                <div class="d-flex align-items-center my-3">
                    <hr class="flex-grow-1">
                    <span class="px-3 text-muted small">hoặc</span>
                    <hr class="flex-grow-1">
                </div>

                <!-- Google Login Button -->
                <a href="${pageContext.request.contextPath}/auth/google" class="btn btn-outline-secondary w-100 d-flex align-items-center justify-content-center gap-2">
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 48 48">
                        <path fill="#FFC107" d="M43.611,20.083H42V20H24v8h11.303c-1.649,4.657-6.08,8-11.303,8c-6.627,0-12-5.373-12-12c0-6.627,5.373-12,12-12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C12.955,4,4,12.955,4,24c0,11.045,8.955,20,20,20c11.045,0,20-8.955,20-20C44,22.659,43.862,21.35,43.611,20.083z"/>
                        <path fill="#FF3D00" d="M6.306,14.691l6.571,4.819C14.655,15.108,18.961,12,24,12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C16.318,4,9.656,8.337,6.306,14.691z"/>
                        <path fill="#4CAF50" d="M24,44c5.166,0,9.86-1.977,13.409-5.192l-6.19-5.238C29.211,35.091,26.715,36,24,36c-5.202,0-9.619-3.317-11.283-7.946l-6.522,5.025C9.505,39.556,16.227,44,24,44z"/>
                        <path fill="#1976D2" d="M43.611,20.083H42V20H24v8h11.303c-0.792,2.237-2.231,4.166-4.087,5.571c0.001-0.001,0.002-0.001,0.003-0.002l6.19,5.238C36.971,39.205,44,34,44,24C44,22.659,43.862,21.35,43.611,20.083z"/>
                    </svg>
                    Đăng nhập bằng Google
                </a>

                <div class="text-center mt-4">
                    <span class="text-muted">Chưa có tài khoản?</span>
                    <a href="${pageContext.request.contextPath}/auth/register" style="color: var(--secondary-dark); font-weight: 500;">
                        Đăng ký ngay
                    </a>
                </div>

                <div class="text-center mt-4 pt-4 border-top">
                    <a href="${pageContext.request.contextPath}/" class="text-muted small">
                        <i class="bi bi-arrow-left me-1"></i>Quay lại trang chủ
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
