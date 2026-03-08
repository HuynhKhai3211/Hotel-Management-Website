<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng ký - Luxury Hotel</title>
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
                <h2 class="mb-4" style="font-family: var(--font-display);">Tham gia cùng chúng tôi</h2>
                <p class="auth-hero-text">Đăng ký để nhận ưu đãi độc quyền và đặt phòng nhanh chóng.</p>
                <div class="mt-5">
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-gift" style="color: var(--secondary);"></i>
                        <span>Ưu đãi dành riêng thành viên</span>
                    </div>
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-lightning" style="color: var(--secondary);"></i>
                        <span>Đặt phòng nhanh chóng</span>
                    </div>
                    <div class="d-flex align-items-center gap-3">
                        <i class="bi bi-star" style="color: var(--secondary);"></i>
                        <span>Tích điểm thưởng mỗi lần đặt</span>
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

                <h2 class="auth-form-title">Tạo tài khoản</h2>
                <p class="auth-form-subtitle">Điền thông tin để đăng ký thành viên</p>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-circle me-2"></i>${error}
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/auth/register">
                    <div class="mb-3">
                        <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="${email}" placeholder="email@example.com" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="fullName" class="form-label">Họ và tên <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-person"></i></span>
                            <input type="text" class="form-control" id="fullName" name="fullName"
                                   value="${fullName}" placeholder="Nguyễn Văn A" required>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="phone" class="form-label">Số điện thoại</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-telephone"></i></span>
                                <input type="tel" class="form-control" id="phone" name="phone"
                                       value="${phone}" placeholder="0901234567">
                            </div>
                        </div>

                        <div class="col-md-6 mb-3">
                            <label for="address" class="form-label">Địa chỉ</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-geo-alt"></i></span>
                                <input type="text" class="form-control" id="address" name="address"
                                       value="${address}" placeholder="Thành phố">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="password" class="form-label">Mật khẩu <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-lock"></i></span>
                                <input type="password" class="form-control" id="password" name="password"
                                       minlength="8" placeholder="Tối thiểu 8 ký tự" required>
                            </div>
                        </div>

                        <div class="col-md-6 mb-3">
                            <label for="confirmPassword" class="form-label">Xác nhận <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                                <input type="password" class="form-control" id="confirmPassword"
                                       name="confirmPassword" placeholder="Nhập lại mật khẩu" required>
                            </div>
                        </div>
                    </div>

                    <div class="mb-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input" id="terms" name="terms" required>
                            <label class="form-check-label" for="terms">
                                Tôi đồng ý với <a href="#" style="color: var(--secondary-dark);">Điều khoản dịch vụ</a>
                            </label>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 btn-lg mb-3">
                        <i class="bi bi-person-plus me-2"></i>Đăng ký
                    </button>
                </form>

                <div class="text-center mt-4">
                    <span class="text-muted">Đã có tài khoản?</span>
                    <a href="${pageContext.request.contextPath}/auth/login" style="color: var(--secondary-dark); font-weight: 500;">
                        Đăng nhập
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
