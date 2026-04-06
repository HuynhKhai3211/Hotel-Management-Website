<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
<<<<<<< HEAD
    <title>Xac thuc OTP - Luxury Hotel</title>
=======
    <title>Xác thực OTP - Luxury Hotel</title>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/ui-kit.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/layout.css" rel="stylesheet">
    <style>
        .otp-inputs {
            display: flex;
            gap: 10px;
            justify-content: center;
        }
        .otp-inputs input {
            width: 50px;
            height: 60px;
            text-align: center;
            font-size: 24px;
            font-weight: bold;
            border: 2px solid var(--border);
            border-radius: var(--radius-md);
            transition: all 0.2s;
        }
        .otp-inputs input:focus {
            border-color: var(--primary);
            outline: none;
            box-shadow: 0 0 0 3px rgba(26, 26, 46, 0.1);
        }
    </style>
</head>
<body>
    <div class="auth-layout">
        <!-- Hero Section - Left -->
        <div class="auth-hero d-none d-lg-flex">
            <div class="auth-hero-content">
                <div class="auth-hero-logo">Luxury<span>Hotel</span></div>
<<<<<<< HEAD
                <h2 class="mb-4" style="font-family: var(--font-display);">Xac thuc OTP</h2>
                <p class="auth-hero-text">Nhap ma 6 so da duoc gui den email cua ban de tiep tuc dat lai mat khau.</p>
                <div class="mt-5">
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-check-circle-fill" style="color: var(--secondary);"></i>
                        <span>Nhap email cua ban</span>
                    </div>
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-2-circle-fill" style="color: var(--secondary);"></i>
                        <span>Xac thuc ma OTP</span>
                    </div>
                    <div class="d-flex align-items-center gap-3">
                        <i class="bi bi-3-circle" style="color: var(--secondary); opacity: 0.5;"></i>
                        <span style="opacity: 0.5;">Dat mat khau moi</span>
=======
                <h2 class="mb-4" style="font-family: var(--font-display);">Xác thực OTP</h2>
                <p class="auth-hero-text">Nhập mã 6 số đã được gửi đến email của bạn để tiếp tục đặt lại mật khẩu.</p>
                <div class="mt-5">
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-check-circle-fill" style="color: var(--secondary);"></i>
                        <span>Nhập email của bạn</span>
                    </div>
                    <div class="d-flex align-items-center gap-3 mb-3">
                        <i class="bi bi-2-circle-fill" style="color: var(--secondary);"></i>
                        <span>Xác thực mã OTP</span>
                    </div>
                    <div class="d-flex align-items-center gap-3">
                        <i class="bi bi-3-circle" style="color: var(--secondary); opacity: 0.5;"></i>
                        <span style="opacity: 0.5;">Đặt mật khẩu mới</span>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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

<<<<<<< HEAD
                <h2 class="auth-form-title">Nhap ma OTP</h2>
                <p class="auth-form-subtitle">Ma xac thuc da duoc gui den <strong>${email}</strong></p>
=======
                <h2 class="auth-form-title">Nhập mã OTP</h2>
                <p class="auth-form-subtitle">Mã xác thực đã được gửi đến <strong>${email}</strong></p>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-circle me-2"></i>${error}
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/auth/verify-otp" id="otpForm">
                    <input type="hidden" name="otp" id="otpHidden">

                    <div class="mb-4">
<<<<<<< HEAD
                        <label class="form-label text-center d-block mb-3">Nhap ma 6 so</label>
=======
                        <label class="form-label text-center d-block mb-3">Nhập mã 6 số</label>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                        <div class="otp-inputs">
                            <input type="text" maxlength="1" class="otp-input" data-index="0" inputmode="numeric" pattern="[0-9]" required>
                            <input type="text" maxlength="1" class="otp-input" data-index="1" inputmode="numeric" pattern="[0-9]" required>
                            <input type="text" maxlength="1" class="otp-input" data-index="2" inputmode="numeric" pattern="[0-9]" required>
                            <input type="text" maxlength="1" class="otp-input" data-index="3" inputmode="numeric" pattern="[0-9]" required>
                            <input type="text" maxlength="1" class="otp-input" data-index="4" inputmode="numeric" pattern="[0-9]" required>
                            <input type="text" maxlength="1" class="otp-input" data-index="5" inputmode="numeric" pattern="[0-9]" required>
                        </div>
<<<<<<< HEAD
                        <div class="form-text text-center mt-3">Ma OTP se het han sau 5 phut</div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 btn-lg mb-3">
                        <i class="bi bi-shield-check me-2"></i>Xac thuc
=======
                        <div class="form-text text-center mt-3">Mã OTP sẽ hết hạn sau 5 phút</div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 btn-lg mb-3">
                        <i class="bi bi-shield-check me-2"></i>Xác thực
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                    </button>
                </form>

                <div class="text-center mt-4">
<<<<<<< HEAD
                    <span class="text-muted">Khong nhan duoc ma?</span>
                    <a href="${pageContext.request.contextPath}/auth/forgot-password" style="color: var(--secondary-dark); font-weight: 500;">
                        Gui lai
=======
                    <span class="text-muted">Không nhận được mã?</span>
                    <a href="${pageContext.request.contextPath}/auth/forgot-password" style="color: var(--secondary-dark); font-weight: 500;">
                        Gửi lại
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                    </a>
                </div>

                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/auth/login" class="text-muted small">
<<<<<<< HEAD
                        <i class="bi bi-arrow-left me-1"></i>Quay lai dang nhap
=======
                        <i class="bi bi-arrow-left me-1"></i>Quay lại đăng nhập
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const inputs = document.querySelectorAll('.otp-input');
            const form = document.getElementById('otpForm');
            const otpHidden = document.getElementById('otpHidden');

            inputs.forEach((input, index) => {
                input.addEventListener('input', function(e) {
                    const value = e.target.value;
                    if (value.length === 1 && index < inputs.length - 1) {
                        inputs[index + 1].focus();
                    }
                    updateHiddenOtp();
                });

                input.addEventListener('keydown', function(e) {
                    if (e.key === 'Backspace' && !e.target.value && index > 0) {
                        inputs[index - 1].focus();
                    }
                });

                input.addEventListener('paste', function(e) {
                    e.preventDefault();
                    const pastedData = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
                    pastedData.split('').forEach((char, i) => {
                        if (inputs[i]) {
                            inputs[i].value = char;
                        }
                    });
                    updateHiddenOtp();
                    if (pastedData.length === 6) {
                        inputs[5].focus();
                    }
                });
            });

            function updateHiddenOtp() {
                let otp = '';
                inputs.forEach(input => {
                    otp += input.value;
                });
                otpHidden.value = otp;
            }

            form.addEventListener('submit', function(e) {
                updateHiddenOtp();
                if (otpHidden.value.length !== 6) {
                    e.preventDefault();
<<<<<<< HEAD
                    alert('Vui long nhap du 6 so OTP');
=======
                    alert('Vui lòng nhập đủ 6 số OTP');
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                }
            });

            // Focus first input
            inputs[0].focus();
        });
    </script>
</body>
</html>
