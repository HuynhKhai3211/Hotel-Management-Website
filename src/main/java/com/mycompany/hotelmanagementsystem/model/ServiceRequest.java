package com.mycompany.hotelmanagementsystem.model;

import java.time.LocalDateTime;

public class ServiceRequest {
    private int requestId;
    private int bookingId;
    private Integer staffId;
    private String serviceType;
    private LocalDateTime requestTime;
    private String status;

    // Transient for display
    private Booking booking;

    public ServiceRequest() {}

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
}
