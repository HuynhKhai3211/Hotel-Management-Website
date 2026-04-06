package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.BookingStatus;
import com.mycompany.hotelmanagementsystem.utils.ServiceResult;
import com.mycompany.hotelmanagementsystem.model.Booking;
import com.mycompany.hotelmanagementsystem.model.ServiceRequest;
import com.mycompany.hotelmanagementsystem.dao.BookingRepository;
import com.mycompany.hotelmanagementsystem.dao.ServiceRequestRepository;
import java.util.List;

public class ServiceRequestService {
    private final ServiceRequestRepository serviceRequestRepository;
    private final BookingRepository bookingRepository;

    public ServiceRequestService() {
        this.serviceRequestRepository = new ServiceRequestRepository();
        this.bookingRepository = new BookingRepository();
    }

    public ServiceResult createCleaningRequest(int bookingId, int customerId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null || booking.getCustomerId() != customerId) {
            return ServiceResult.failure("Không tìm thấy đặt phòng");
        }

        if (!BookingStatus.CHECKED_IN.equals(booking.getStatus())) {
            return ServiceResult.failure("Chỉ có thể yêu cầu dịch vụ khi đã nhận phòng");
        }

        if (serviceRequestRepository.hasPendingRequest(bookingId, "Cleaning")) {
            return ServiceResult.failure("Bạn đã có yêu cầu dọn phòng đang chờ xử lý");
        }

        ServiceRequest request = new ServiceRequest();
        request.setBookingId(bookingId);
        request.setServiceType("Cleaning");
        request.setStatus("Pending");

        int requestId = serviceRequestRepository.insert(request);
        if (requestId <= 0) {
            return ServiceResult.failure("Không thể tạo yêu cầu");
        }

        return ServiceResult.success("Yêu cầu dọn phòng đã được gửi");
    }

    public List<ServiceRequest> getBookingRequests(int bookingId) {
        return serviceRequestRepository.findByBookingId(bookingId);
    }

    public ServiceResult cancelRequest(int requestId, int customerId) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(requestId);
        if (serviceRequest == null) {
            return ServiceResult.failure("Không tìm thấy yêu cầu dịch vụ");
        }
        Booking booking = bookingRepository.findById(serviceRequest.getBookingId());
        if (booking == null || booking.getCustomerId() != customerId) {
            return ServiceResult.failure("Bạn không có quyền hủy yêu cầu này");
        }
        if (!"Pending".equals(serviceRequest.getStatus())) {
            return ServiceResult.failure("Chỉ có thể hủy yêu cầu đang ở trạng thái chờ xử lý");
        }
        if (serviceRequestRepository.updateStatus(requestId, "Cancelled") > 0) {
            return ServiceResult.success("Yêu cầu dịch vụ đã được hủy");
        }
        return ServiceResult.failure("Không thể hủy yêu cầu");
    }
}
