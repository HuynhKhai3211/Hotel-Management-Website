package com.mycompany.hotelmanagementsystem.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EmailHelper {

    private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

    private static String smtpHost;
    private static String smtpPort;
    private static String username;
    private static String password;
    private static boolean configured = false;

    static {
        loadConfig();
    }

    public static void sendBookingConfirmation(String email, int bookingId, String fullName, String checkInFormatted, String checkOutFormatted, List<String> roomDetails, BigDecimal total, BigDecimal depositAmount, String paymentStatus, BigDecimal totalEarlySurcharge, BigDecimal totalLateSurcharge, BigDecimal totalPromotionDiscount, BigDecimal voucherDiscount) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private EmailHelper() {}

    private static void loadConfig() {
        // Try loading from properties file first
        System.out.println("=== EMAIL CONFIG DEBUG ===");
        try (InputStream input = EmailHelper.class.getClassLoader()
                .getResourceAsStream("mail.properties")) {
            System.out.println("InputStream: " + input);
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                smtpHost = props.getProperty("mail.smtp.host", "smtp.gmail.com");
                smtpPort = props.getProperty("mail.smtp.port", "587");
                username = props.getProperty("mail.username");
                password = props.getProperty("mail.password");
                System.out.println("SMTP Host: " + smtpHost);
                System.out.println("SMTP Port: " + smtpPort);
                System.out.println("Username: " + username);
                System.out.println("Password length: " + (password != null ? password.length() : "null"));
                configured = (username != null && password != null && !username.isEmpty() && !password.isEmpty());
                if (configured) {
                    logger.info("Email config loaded from mail.properties");
                    System.out.println("Email configured: TRUE");
                } else {
                    System.out.println("Email configured: FALSE - username or password is null/empty");
                }
            } else {
                System.out.println("mail.properties NOT FOUND in classpath!");
            }
        } catch (Exception e) {
            logger.warn("Could not load mail.properties: {}", e.getMessage());
            System.out.println("Exception loading mail.properties: " + e.getMessage());
            e.printStackTrace();
        }

        // Fallback to environment variables
        if (!configured) {
            System.out.println("Trying environment variables...");
            smtpHost = System.getenv("MAIL_SMTP_HOST");
            if (smtpHost == null) smtpHost = "smtp.gmail.com";

            smtpPort = System.getenv("MAIL_SMTP_PORT");
            if (smtpPort == null) smtpPort = "587";

            username = System.getenv("MAIL_USERNAME");
            password = System.getenv("MAIL_PASSWORD");
            System.out.println("ENV Username: " + username);
            System.out.println("ENV Password: " + (password != null ? "***" : "null"));
            configured = (username != null && password != null);
            if (configured) {
                logger.info("Email config loaded from environment variables");
            }
        }

        if (!configured) {
            logger.warn("Email not configured. Create mail.properties or set MAIL_USERNAME/MAIL_PASSWORD env vars.");
            System.out.println("=== EMAIL NOT CONFIGURED ===");
        }
        System.out.println("=== END EMAIL CONFIG DEBUG ===");
    }

    /**
     * Send OTP email for password reset
     * @param toEmail recipient email
     * @param otp the OTP code
     * @return true if sent successfully
     */
    public static boolean sendOtp(String toEmail, String otp) {
        System.out.println("=== SEND OTP DEBUG ===");
        System.out.println("To Email: " + toEmail);
        System.out.println("OTP: " + otp);
        System.out.println("Configured: " + configured);

        if (!configured) {
            logger.error("Email credentials not configured.");
            System.out.println("FAILED: Email credentials not configured");
            return false;
        }

        System.out.println("Using SMTP: " + smtpHost + ":" + smtpPort);
        System.out.println("From: " + username);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", smtpHost);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "Luxury Hotel"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Ma xac thuc dat lai mat khau - Luxury Hotel");

            String htmlContent = buildOtpEmailContent(otp);
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);
            logger.info("OTP email sent to: {}", toEmail);
            System.out.println("=== EMAIL SENT SUCCESSFULLY ===");
            return true;

        } catch (Exception e) {
            logger.error("Failed to send OTP email to: {}", toEmail, e);
            System.out.println("=== EMAIL SEND FAILED ===");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static String buildOtpEmailContent(String otp) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #1a1a2e 0%%, #16213e 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: #d4af37; margin: 0;">Luxury Hotel</h1>
                </div>
                <div style="background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px;">
                    <h2 style="color: #1a1a2e; margin-top: 0;">Dat lai mat khau</h2>
                    <p style="color: #666;">Ban da yeu cau dat lai mat khau cho tai khoan Luxury Hotel.</p>
                    <p style="color: #666;">Ma xac thuc OTP cua ban la:</p>
                    <div style="background: #1a1a2e; padding: 20px; text-align: center; border-radius: 8px; margin: 20px 0;">
                        <span style="font-size: 32px; font-weight: bold; color: #d4af37; letter-spacing: 8px;">%s</span>
                    </div>
                    <p style="color: #666;">Ma nay se het han sau <strong>5 phut</strong>.</p>
                    <p style="color: #999; font-size: 12px;">Neu ban khong yeu cau dat lai mat khau, vui long bo qua email nay.</p>
                    <hr style="border: none; border-top: 1px solid #ddd; margin: 20px 0;">
                    <p style="color: #999; font-size: 12px; text-align: center;">© 2024 Luxury Hotel. All rights reserved.</p>
                </div>
            </body>
            </html>
            """.formatted(otp);
    }

    /**
     * Send walk-in account credentials to customer email.
     * @param toEmail recipient email
     * @param fullName customer name
     * @param generatedPassword the plain-text password
     * @return true if sent successfully
     */
    public static boolean sendWalkInCredentials(String toEmail, String fullName, String generatedPassword) {
        System.out.println("=== SEND WALK-IN CREDENTIALS DEBUG ===");
        System.out.println("To Email: " + toEmail);
        System.out.println("FullName: " + fullName);
        System.out.println("Configured: " + configured);

        if (!configured) {
            logger.warn("Email not configured - cannot send walk-in credentials to: {}", toEmail);
            System.out.println("FAILED: Email credentials not configured");
            return false;
        }

        System.out.println("Using SMTP: " + smtpHost + ":" + smtpPort);
        System.out.println("From: " + username);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", smtpHost);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "Luxury Hotel"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Thong tin tai khoan - Luxury Hotel");

            String htmlContent = buildWalkInEmailContent(fullName, toEmail, generatedPassword);
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);
            logger.info("Walk-in credentials email sent to: {}", toEmail);
            System.out.println("=== WALK-IN EMAIL SENT SUCCESSFULLY ===");
            return true;
        } catch (Exception e) {
            logger.error("Failed to send walk-in credentials to: {}", toEmail, e);
            System.out.println("=== WALK-IN EMAIL SEND FAILED ===");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static String buildWalkInEmailContent(String fullName, String email, String password) {
        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #1a1a2e 0%%, #16213e 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: #d4af37; margin: 0;">Luxury Hotel</h1>
                </div>
                <div style="background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px;">
                    <h2 style="color: #1a1a2e; margin-top: 0;">Chao mung %s!</h2>
                    <p style="color: #666;">Tai khoan cua ban da duoc tao tai Luxury Hotel. Ban co the dang nhap de xem thong tin dat phong va su dung cac dich vu truc tuyen.</p>
                    <div style="background: #fff; padding: 20px; border-radius: 8px; border: 1px solid #ddd; margin: 20px 0;">
                        <p style="margin: 5px 0; color: #333;"><strong>Email dang nhap:</strong> %s</p>
                        <p style="margin: 5px 0; color: #333;"><strong>Mat khau:</strong> <code style="background: #f0f0f0; padding: 2px 8px; border-radius: 4px; font-size: 16px;">%s</code></p>
                    </div>
                    <p style="color: #e74c3c; font-size: 13px;"><strong>Luu y:</strong> Vui long doi mat khau sau khi dang nhap lan dau.</p>
                    <hr style="border: none; border-top: 1px solid #ddd; margin: 20px 0;">
                    <p style="color: #999; font-size: 12px; text-align: center;">Luxury Hotel - Cam on quy khach!</p>
                </div>
            </body>
            </html>
            """.formatted(fullName, email, password);
    }
}
