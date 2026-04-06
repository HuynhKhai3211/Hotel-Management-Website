package com.mycompany.hotelmanagementsystem.service;
<<<<<<< HEAD
=======

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
import com.mycompany.hotelmanagementsystem.config.VNPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VNPay Sandbox Integration Service
<<<<<<< HEAD
 * Class này chịu trách nhiệm:
 * - tạo URL thanh toán gửi sang VNPay
 * - verify chữ ký callback từ VNPay
 * - kiểm tra giao dịch thành công hay không
 * - sinh mã giao dịch
 * - lấy IP client
=======
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
 */
public class VNPayService {

    /**
     * Generate VNPay payment URL (default return URL)
<<<<<<< HEAD
     * Hàm này dùng khi muốn tạo link thanh toán VNPay với returnUrl mặc định
     */
    public static String createPaymentUrl(String baseUrl, String txnRef, long amount,
            String orderInfo, String ipAddress) {

        // Gọi sang hàm overload bên dưới
        // return URL mặc định sẽ là: baseUrl + "/payment/vnpay-return"
=======
     */
    public static String createPaymentUrl(String baseUrl, String txnRef, long amount,
            String orderInfo, String ipAddress) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        return createPaymentUrl(baseUrl, txnRef, amount, orderInfo, ipAddress,
                baseUrl + "/payment/vnpay-return");
    }

    /**
     * Generate VNPay payment URL with custom return URL
<<<<<<< HEAD
     * Hàm này là hàm chính để build link thanh toán VNPay
=======
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
     */
    public static String createPaymentUrl(String baseUrl, String txnRef, long amount,
            String orderInfo, String ipAddress, String returnUrl) {

<<<<<<< HEAD
        // Tạo map chứa toàn bộ tham số gửi sang VNPay
        Map<String, String> vnp_Params = new HashMap<>();

        // Lấy thời gian hiện tại theo múi giờ GMT+7
        // Lưu ý: Etc/GMT+7 trong Java thực tế là offset âm theo quy ước Etc/*
        // nhưng ở đây code đang dùng theo ý đồ hệ thống hiện tại
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        // Định dạng ngày giờ đúng chuẩn VNPay yêu cầu: yyyyMMddHHmmss
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        // Tạo thời gian tạo giao dịch
        String vnp_CreateDate = formatter.format(cld.getTime());

        // Cộng thêm số phút hết hạn thanh toán từ config
        cld.add(Calendar.MINUTE, VNPayConfig.VNP_EXPIRE_MINUTES);

        // Tạo thời gian hết hạn giao dịch
        String vnp_ExpireDate = formatter.format(cld.getTime());

        // Version API của VNPay
        vnp_Params.put("vnp_Version", VNPayConfig.VNP_VERSION);

        // Command gửi sang VNPay, thường là "pay"
        vnp_Params.put("vnp_Command", VNPayConfig.VNP_COMMAND);

        // Mã terminal do VNPay cấp cho merchant
        vnp_Params.put("vnp_TmnCode", VNPayConfig.VNP_TMN_CODE);

        // Số tiền thanh toán
        // VNPay yêu cầu amount * 100
        // Ví dụ 100000 VND sẽ gửi thành 10000000
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));

        // Mã tiền tệ, ví dụ VND
        vnp_Params.put("vnp_CurrCode", VNPayConfig.VNP_CURR_CODE);

        // Mã giao dịch duy nhất của hệ thống
        vnp_Params.put("vnp_TxnRef", txnRef);

        // Nội dung thanh toán hiển thị phía VNPay
        vnp_Params.put("vnp_OrderInfo", orderInfo);

        // Loại đơn hàng, ví dụ billpayment hoặc other tùy config
        vnp_Params.put("vnp_OrderType", VNPayConfig.VNP_ORDER_TYPE);

        // Ngôn ngữ hiển thị trên cổng thanh toán
        vnp_Params.put("vnp_Locale", VNPayConfig.VNP_LOCALE);

        // URL để VNPay redirect người dùng quay lại sau khi thanh toán
        vnp_Params.put("vnp_ReturnUrl", returnUrl);

        // Địa chỉ IP của client
        vnp_Params.put("vnp_IpAddr", ipAddress);

        // Ngày giờ tạo giao dịch
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Ngày giờ hết hạn giao dịch
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Sắp xếp tên các field theo thứ tự tăng dần
        // Đây là yêu cầu quan trọng để hash đúng theo chuẩn VNPay
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        // StringBuilder lưu chuỗi dùng để tạo secure hash
        StringBuilder hashData = new StringBuilder();

        // StringBuilder lưu query string cuối cùng gắn vào URL
        StringBuilder query = new StringBuilder();

        // Tạo iterator để duyệt lần lượt từng field
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {

            // Lấy tên field hiện tại
            String fieldName = itr.next();

            // Lấy giá trị tương ứng của field
            String fieldValue = vnp_Params.get(fieldName);

            // Chỉ xử lý nếu value không null và không rỗng
            if (fieldValue != null && !fieldValue.isEmpty()) {

                // Build chuỗi hashData theo format:
                // key=value&key=value...
                // Lưu ý: theo comment ở đây chỉ encode VALUE, không encode KEY
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Build chuỗi query thật để gửi lên URL
                // Ở đây encode cả key và value
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Nếu vẫn còn field phía sau thì thêm dấu &
=======
        Map<String, String> vnp_Params = new HashMap<>();

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, VNPayConfig.VNP_EXPIRE_MINUTES);
        String vnp_ExpireDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_Version", VNPayConfig.VNP_VERSION);
        vnp_Params.put("vnp_Command", VNPayConfig.VNP_COMMAND);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.VNP_TMN_CODE);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", VNPayConfig.VNP_CURR_CODE);
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", VNPayConfig.VNP_ORDER_TYPE);
        vnp_Params.put("vnp_Locale", VNPayConfig.VNP_LOCALE);
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddress);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Sort fields
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // Build hash data - encode VALUE only (key không encode)
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                // Build query - encode cả key và value
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

<<<<<<< HEAD
        // Tạo chữ ký bảo mật từ hashData bằng HMAC SHA512
        // dùng secret key mà VNPay cấp
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.VNP_HASH_SECRET, hashData.toString());

        // Gắn chữ ký vào cuối query string
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        // Trả về URL thanh toán hoàn chỉnh:
        // base VNPay URL + ? + toàn bộ query
=======
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.VNP_HASH_SECRET, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        return VNPayConfig.VNP_PAY_URL + "?" + query;
    }

    /**
     * Verify VNPay callback signature
<<<<<<< HEAD
     * Hàm kiểm tra chữ ký khi VNPay trả callback về hệ thống
     */
    public static boolean verifySignature(Map<String, String> params) {

        // Lấy chữ ký VNPay gửi sang
        String receivedHash = params.get("vnp_SecureHash");

        // Nếu không có chữ ký thì coi như không hợp lệ
        if (receivedHash == null) return false;

        // Tạo TreeMap để tự động sort key theo thứ tự tăng dần
        Map<String, String> sortedParams = new TreeMap<>(params);

        // Loại bỏ chính chữ ký khỏi dữ liệu cần hash lại
        sortedParams.remove("vnp_SecureHash");

        // Loại bỏ cả kiểu chữ ký nếu có
        sortedParams.remove("vnp_SecureHashType");

        // Chuỗi dùng để tính lại hash
        StringBuilder hashData = new StringBuilder();

        // Iterator duyệt từng entry trong map đã sắp xếp
        Iterator<Map.Entry<String, String>> itr = sortedParams.entrySet().iterator();
        while (itr.hasNext()) {

            // Lấy từng cặp key-value
            Map.Entry<String, String> entry = itr.next();

            // Chỉ xử lý khi value không null và không rỗng
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {

                // Ghép theo format key=value
                hashData.append(entry.getKey());
                hashData.append('=');

                // Encode value theo ASCII
                hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));

                // Nếu còn phần tử sau thì thêm dấu &
=======
     */
    public static boolean verifySignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null) return false;

        Map<String, String> sortedParams = new TreeMap<>(params);
        sortedParams.remove("vnp_SecureHash");
        sortedParams.remove("vnp_SecureHashType");

        StringBuilder hashData = new StringBuilder();
        Iterator<Map.Entry<String, String>> itr = sortedParams.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                hashData.append(entry.getKey());
                hashData.append('=');
                hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

<<<<<<< HEAD
        // Tính lại hash từ dữ liệu callback
        String calculatedHash = VNPayConfig.hmacSHA512(VNPayConfig.VNP_HASH_SECRET, hashData.toString());

        // So sánh hash hệ thống tự tính với hash VNPay gửi về
        // equalsIgnoreCase để tránh lệch hoa thường
=======
        String calculatedHash = VNPayConfig.hmacSHA512(VNPayConfig.VNP_HASH_SECRET, hashData.toString());
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        return calculatedHash.equalsIgnoreCase(receivedHash);
    }

    /**
     * Check if payment is successful
<<<<<<< HEAD
     * Hàm kiểm tra mã phản hồi từ VNPay có phải giao dịch thành công không
     */
    public static boolean isPaymentSuccess(String responseCode) {

        // Theo chuẩn VNPay, "00" nghĩa là thành công
=======
     */
    public static boolean isPaymentSuccess(String responseCode) {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        return "00".equals(responseCode);
    }

    /**
     * Generate transaction reference
<<<<<<< HEAD
     * Hàm sinh mã giao dịch ngẫu nhiên
     */
    public static String generateTxnRef() {

        // Sinh chuỗi số ngẫu nhiên 8 ký tự
        // dùng làm mã tham chiếu giao dịch của hệ thống
=======
     */
    public static String generateTxnRef() {
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
        return VNPayConfig.getRandomNumber(8);
    }

    /**
     * Get client IP address
<<<<<<< HEAD
     * Hàm lấy IP client từ request
     */
    public static String getIpAddress(HttpServletRequest request) {

        // Gọi helper trong VNPayConfig để lấy IP thực của client
        return VNPayConfig.getIpAddress(request);
    }
}
=======
     */
    public static String getIpAddress(HttpServletRequest request) {
        return VNPayConfig.getIpAddress(request);
    }
}
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
