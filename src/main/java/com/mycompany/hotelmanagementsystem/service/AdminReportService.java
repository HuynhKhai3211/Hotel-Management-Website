package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.RoomStatus;
import com.mycompany.hotelmanagementsystem.dao.BookingRepository;
import com.mycompany.hotelmanagementsystem.dao.CustomerRepository;
import com.mycompany.hotelmanagementsystem.dao.RoomRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AdminReportService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;

    public AdminReportService() {
        this.roomRepository = new RoomRepository();
        this.bookingRepository = new BookingRepository();
        this.customerRepository = new CustomerRepository();
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRooms", roomRepository.countAll());
        stats.put("totalBookings", bookingRepository.countAll());
        stats.put("totalRevenue", bookingRepository.sumTotalPrice());
        stats.put("totalCustomers", customerRepository.countAll());
        stats.put("occupiedRooms", roomRepository.countByStatus(RoomStatus.OCCUPIED));
        stats.put("availableRooms", roomRepository.countByStatus(RoomStatus.AVAILABLE));
        return stats;
    }

    public Map<String, Object> getRoomUtilizationStats() {
        Map<String, Object> stats = new HashMap<>();
        int totalRooms = roomRepository.countAll();
        int occupied = roomRepository.countByStatus(RoomStatus.OCCUPIED);
        int available = roomRepository.countByStatus(RoomStatus.AVAILABLE);
        int cleaning = roomRepository.countByStatus(RoomStatus.CLEANING);
        int maintenance = roomRepository.countByStatus(RoomStatus.MAINTENANCE);

        stats.put("totalRooms", totalRooms);
        stats.put("occupied", occupied);
        stats.put("available", available);
        stats.put("cleaning", cleaning);
        stats.put("maintenance", maintenance);

        // Calculate utilization percentage
        double utilizationRate = totalRooms > 0 ? (double) occupied / totalRooms * 100 : 0;
        stats.put("utilizationRate", String.format("%.1f", utilizationRate));

        return stats;
    }

    public Map<String, Object> getRevenueReport(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> report = new HashMap<>();

        BigDecimal totalRevenue = bookingRepository.sumTotalPriceByDateRange(startDate, endDate);
        int bookingCount = bookingRepository.countByDateRange(startDate, endDate);

        report.put("totalRevenue", totalRevenue);
        report.put("bookingCount", bookingCount);
        report.put("averageBookingValue", bookingCount > 0
            ? totalRevenue.divide(BigDecimal.valueOf(bookingCount), 0, java.math.RoundingMode.HALF_UP)
            : BigDecimal.ZERO);

        return report;
    }
}
