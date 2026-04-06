<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<<<<<<< HEAD
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Luxury Hotel - Trải nghiệm nghỉ dưỡng đẳng cấp 5 sao">
    <title>Luxury Hotel - Khách sạn sang trọng hàng đầu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Lato:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/main-styles.css" rel="stylesheet">
</head>
<body style="font-family: 'Lato', sans-serif;">

    <!-- ============================================
         Navigation - Fixed
         ============================================ -->
    <nav class="navbar navbar-expand-lg navbar-landing" id="mainNav">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                Luxury<span>Hotel</span>
            </a>
            <button class="navbar-toggler border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="#features">Dịch vụ</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#rooms">Phòng</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#amenities">Tiện nghi</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#testimonials">Đánh giá</a>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <c:choose>
                        <c:when test="${not empty sessionScope.loggedInAccount}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                                    <i class="bi bi-person-circle me-1"></i>
                                    ${sessionScope.loggedInAccount.fullName}
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/customer/profile">Hồ sơ</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/customer/bookings">Đặt phòng của tôi</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth/logout">Đăng xuất</a></li>
                                </ul>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/auth/login">Đăng nhập</a>
                            </li>
                            <li class="nav-item ms-2">
                                <a class="btn btn-sm px-3 py-2" href="${pageContext.request.contextPath}/auth/register"
                                   style="background: linear-gradient(135deg, #c9a227 0%, #a68419 100%); color: #fff; border-radius: 20px; font-weight: 500;">
                                    Đăng ký
                                </a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>

    <!-- ============================================
         Hero Section
         Image: 1920x1080px - /assets/images/home/hero-bg.jpg
         ============================================ -->
    <section class="hero-section" id="hero">
        <%-- <img src="${pageContext.request.contextPath}/assets/images/home/hero-bg.jpg" alt="Hotel" class="hero-bg"> --%>
        <div class="hero-overlay"></div>
        <div class="hero-content">
            <h1 class="animate-fade-in-up">
                Trải nghiệm nghỉ dưỡng<br><span>đẳng cấp 5 sao</span>
            </h1>
            <p class="animate-fade-in-up animate-delay-2">
                Khám phá không gian sang trọng, dịch vụ hoàn hảo và những khoảnh khắc đáng nhớ tại Luxury Hotel
            </p>
            <div class="hero-buttons animate-fade-in-up animate-delay-3">
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-hero-primary">
                    <i class="bi bi-calendar-check me-2"></i>Đặt phòng ngay
                </a>
                <a href="#rooms" class="btn btn-hero-outline">
                    <i class="bi bi-eye me-2"></i>Xem phòng
                </a>
            </div>
        </div>
        <a href="#features" class="hero-scroll-indicator">
            <i class="bi bi-chevron-down"></i>
        </a>
    </section>

    <!-- ============================================
         Features Section
         ============================================ -->
    <section class="section section-light" id="features">
        <div class="container">
            <h2 class="section-title">Tại sao chọn chúng tôi?</h2>
            <p class="section-subtitle">Chúng tôi cam kết mang đến trải nghiệm nghỉ dưỡng tuyệt vời nhất cho quý khách</p>

            <div class="row g-4">
                <div class="col-md-6 col-lg-3">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="bi bi-geo-alt"></i>
                        </div>
                        <h3>Vị trí đắc địa</h3>
                        <p>Tọa lạc tại trung tâm thành phố, thuận tiện di chuyển đến mọi địa điểm</p>
                    </div>
                </div>
                <div class="col-md-6 col-lg-3">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="bi bi-shield-check"></i>
                        </div>
                        <h3>An toàn tuyệt đối</h3>
                        <p>Hệ thống an ninh 24/7, đảm bảo sự an toàn cho quý khách</p>
                    </div>
                </div>
                <div class="col-md-6 col-lg-3">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="bi bi-star"></i>
                        </div>
                        <h3>Dịch vụ 5 sao</h3>
                        <p>Đội ngũ nhân viên chuyên nghiệp, tận tâm phục vụ quý khách</p>
                    </div>
                </div>
                <div class="col-md-6 col-lg-3">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="bi bi-currency-dollar"></i>
                        </div>
                        <h3>Giá cả hợp lý</h3>
                        <p>Mức giá cạnh tranh với nhiều ưu đãi hấp dẫn cho khách hàng</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- ============================================
         Rooms Section
         Images: 400x300px - /assets/images/home/room-*.jpg
         ============================================ -->
    <section class="section" id="rooms" style="background: #fff;">
        <div class="container">
            <h2 class="section-title">Phòng nghỉ của chúng tôi</h2>
            <p class="section-subtitle">Đa dạng loại phòng phù hợp với mọi nhu cầu của quý khách</p>

            <div class="row g-4">
                <!-- Standard Room -->
                <div class="col-md-6 col-lg-4">
                    <div class="room-card">
                        <div class="room-card-image">
                            <%-- <img src="${pageContext.request.contextPath}/assets/images/home/room-standard.jpg" alt="Standard Room"> --%>
                            <span class="room-card-badge">Phổ biến</span>
                        </div>
                        <div class="room-card-body">
                            <h3>Phòng Standard</h3>
                            <p>Phòng tiêu chuẩn với đầy đủ tiện nghi cơ bản, phù hợp cho khách công tác</p>
                            <div class="room-card-price">
                                <span class="price">800.000đ</span>
                                <span class="period">/đêm</span>
                            </div>
                            <div class="room-card-features">
                                <span><i class="bi bi-people"></i> 2 người</span>
                                <span><i class="bi bi-aspect-ratio"></i> 25m²</span>
                                <span><i class="bi bi-wifi"></i> WiFi</span>
                            </div>
                            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-room">Xem chi tiết</a>
                        </div>
                    </div>
                </div>

                <!-- Deluxe Room -->
                <div class="col-md-6 col-lg-4">
                    <div class="room-card">
                        <div class="room-card-image">
                            <%-- <img src="${pageContext.request.contextPath}/assets/images/home/room-deluxe.jpg" alt="Deluxe Room"> --%>
                            <span class="room-card-badge" style="background: #c9a227;">Hot</span>
                        </div>
                        <div class="room-card-body">
                            <h3>Phòng Deluxe</h3>
                            <p>Phòng cao cấp với view đẹp, không gian rộng rãi và tiện nghi hiện đại</p>
                            <div class="room-card-price">
                                <span class="price">1.500.000đ</span>
                                <span class="period">/đêm</span>
                            </div>
                            <div class="room-card-features">
                                <span><i class="bi bi-people"></i> 2 người</span>
                                <span><i class="bi bi-aspect-ratio"></i> 35m²</span>
                                <span><i class="bi bi-wifi"></i> WiFi</span>
                            </div>
                            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-room">Xem chi tiết</a>
                        </div>
                    </div>
                </div>

                <!-- Suite Room -->
                <div class="col-md-6 col-lg-4">
                    <div class="room-card">
                        <div class="room-card-image">
                            <%-- <img src="${pageContext.request.contextPath}/assets/images/home/room-suite.jpg" alt="Suite Room"> --%>
                            <span class="room-card-badge" style="background: #1a1a2e;">VIP</span>
                        </div>
                        <div class="room-card-body">
                            <h3>Phòng Suite</h3>
                            <p>Phòng hạng sang với phòng khách riêng, bồn tắm jacuzzi và dịch vụ VIP</p>
                            <div class="room-card-price">
                                <span class="price">3.000.000đ</span>
                                <span class="period">/đêm</span>
                            </div>
                            <div class="room-card-features">
                                <span><i class="bi bi-people"></i> 4 người</span>
                                <span><i class="bi bi-aspect-ratio"></i> 60m²</span>
                                <span><i class="bi bi-wifi"></i> WiFi</span>
                            </div>
                            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-room">Xem chi tiết</a>
                        </div>
                    </div>
                </div>
            </div>

            <div class="text-center mt-5">
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-hero-primary">
                    Xem tất cả phòng <i class="bi bi-arrow-right ms-2"></i>
                </a>
            </div>
        </div>
    </section>

    <!-- ============================================
         Amenities Section
         ============================================ -->
    <section class="section section-dark" id="amenities">
        <div class="container">
            <h2 class="section-title">Tiện nghi & Dịch vụ</h2>
            <p class="section-subtitle">Tận hưởng các tiện nghi đẳng cấp trong suốt kỳ nghỉ của bạn</p>

            <div class="row g-4">
                <div class="col-md-6 col-lg-4">
                    <div class="amenity-item">
                        <div class="amenity-icon"><i class="bi bi-wifi"></i></div>
                        <div>
                            <h4>WiFi miễn phí</h4>
                            <p>Kết nối internet tốc độ cao toàn khách sạn</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="amenity-item">
                        <div class="amenity-icon"><i class="bi bi-water"></i></div>
                        <div>
                            <h4>Hồ bơi</h4>
                            <p>Hồ bơi ngoài trời với view panorama tuyệt đẹp</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="amenity-item">
                        <div class="amenity-icon"><i class="bi bi-heart-pulse"></i></div>
                        <div>
                            <h4>Spa & Wellness</h4>
                            <p>Dịch vụ spa chuyên nghiệp, thư giãn toàn thân</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="amenity-item">
                        <div class="amenity-icon"><i class="bi bi-cup-hot"></i></div>
                        <div>
                            <h4>Nhà hàng</h4>
                            <p>Ẩm thực đa dạng từ Á đến Âu</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="amenity-item">
                        <div class="amenity-icon"><i class="bi bi-bicycle"></i></div>
                        <div>
                            <h4>Phòng Gym</h4>
                            <p>Trang thiết bị hiện đại, mở cửa 24/7</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="amenity-item">
                        <div class="amenity-icon"><i class="bi bi-car-front"></i></div>
                        <div>
                            <h4>Đưa đón sân bay</h4>
                            <p>Dịch vụ đưa đón tận nơi, xe sang trọng</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- ============================================
         Testimonials Section
         Avatars: 80x80px - /assets/images/home/avatar-*.jpg
         ============================================ -->
    <section class="section section-light" id="testimonials">
        <div class="container">
            <h2 class="section-title">Khách hàng nói gì về chúng tôi</h2>
            <p class="section-subtitle">Hàng nghìn khách hàng đã tin tưởng và hài lòng với dịch vụ của chúng tôi</p>

            <div class="row g-4">
                <div class="col-md-6 col-lg-4">
                    <div class="testimonial-card">
                        <div class="rating">
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                        </div>
                        <p class="quote">"Dịch vụ tuyệt vời, nhân viên thân thiện. Phòng sạch sẽ và view rất đẹp. Chắc chắn sẽ quay lại!"</p>
                        <div class="testimonial-author">
                            <div class="testimonial-avatar">NT</div>
                            <div>
                                <h5>Nguyễn Thảo</h5>
                                <span>Hà Nội</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="testimonial-card">
                        <div class="rating">
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                        </div>
                        <p class="quote">"Kỳ nghỉ gia đình tuyệt vời. Bể bơi rộng, buffet sáng đa dạng. Con tôi rất thích!"</p>
                        <div class="testimonial-author">
                            <div class="testimonial-avatar">TM</div>
                            <div>
                                <h5>Trần Minh</h5>
                                <span>TP. Hồ Chí Minh</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="testimonial-card">
                        <div class="rating">
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-fill"></i>
                            <i class="bi bi-star-half"></i>
                        </div>
                        <p class="quote">"Vị trí trung tâm, đi lại thuận tiện. Giá cả hợp lý so với chất lượng dịch vụ."</p>
                        <div class="testimonial-author">
                            <div class="testimonial-avatar">LH</div>
                            <div>
                                <h5>Lê Hương</h5>
                                <span>Đà Nẵng</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- ============================================
         CTA Section
         ============================================ -->
    <section class="cta-section">
        <div class="container">
            <h2>Sẵn sàng cho kỳ nghỉ của bạn?</h2>
            <p>Đặt phòng ngay hôm nay để nhận ưu đãi đặc biệt lên đến 20%</p>
            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-cta">
                <i class="bi bi-calendar-check me-2"></i>Đặt phòng ngay
            </a>
        </div>
    </section>

    <!-- ============================================
         Footer
         ============================================ -->
    <footer class="footer">
        <div class="container">
            <div class="row g-4">
                <div class="col-lg-4">
                    <div class="footer-brand">Luxury<span>Hotel</span></div>
                    <p class="footer-text">
                        Luxury Hotel - Điểm đến lý tưởng cho kỳ nghỉ của bạn.
                        Với dịch vụ đẳng cấp 5 sao, chúng tôi cam kết mang đến trải nghiệm nghỉ dưỡng tuyệt vời nhất.
                    </p>
                    <div class="footer-social">
                        <a href="#"><i class="bi bi-facebook"></i></a>
                        <a href="#"><i class="bi bi-instagram"></i></a>
                        <a href="#"><i class="bi bi-twitter-x"></i></a>
                        <a href="#"><i class="bi bi-youtube"></i></a>
                    </div>
                </div>
                <div class="col-6 col-lg-2">
                    <h5>Liên kết</h5>
                    <ul class="footer-links">
                        <li><a href="#hero">Trang chủ</a></li>
                        <li><a href="#rooms">Phòng nghỉ</a></li>
                        <li><a href="#amenities">Tiện nghi</a></li>
                        <li><a href="#testimonials">Đánh giá</a></li>
                    </ul>
                </div>
                <div class="col-6 col-lg-2">
                    <h5>Hỗ trợ</h5>
                    <ul class="footer-links">
                        <li><a href="#">Chính sách</a></li>
                        <li><a href="#">Điều khoản</a></li>
                        <li><a href="#">FAQ</a></li>
                        <li><a href="#">Liên hệ</a></li>
                    </ul>
                </div>
                <div class="col-lg-4">
                    <h5>Liên hệ</h5>
                    <div class="footer-contact">
                        <p><i class="bi bi-geo-alt"></i> 123 Đường ABC, Quận 1, TP.HCM</p>
                        <p><i class="bi bi-telephone"></i> (028) 1234 5678</p>
                        <p><i class="bi bi-envelope"></i> info@luxuryhotel.vn</p>
                    </div>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; 2026 Luxury Hotel. All rights reserved.</p>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Navbar scroll effect
        window.addEventListener('scroll', function() {
            const navbar = document.getElementById('mainNav');
            if (window.scrollY > 50) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        });

        // Smooth scroll for anchor links
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function(e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    </script>
</body>
</html>
=======
<% response.sendRedirect(request.getContextPath() + "/home"); %>
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
