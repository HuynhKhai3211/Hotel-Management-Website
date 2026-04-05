package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.constant.RoomStatus;
import com.mycompany.hotelmanagementsystem.dal.BookingRepository;
import com.mycompany.hotelmanagementsystem.dal.CustomerRepository;
import com.mycompany.hotelmanagementsystem.dal.RoomRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        stats.put("cleaningRooms", roomRepository.countByStatus(RoomStatus.CLEANING));
        stats.put("maintenanceRooms", roomRepository.countByStatus(RoomStatus.MAINTENANCE));
        return stats;
    }

    /**
     * Returns monthly booking counts for the last 6 months.
     * Result: int[] of size 6, index 0 = oldest month, index 5 = current month.
     */
    public int[] getMonthlyBookingCounts() {
        int[] counts = new int[6];
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            LocalDateTime start = monthDate.withDayOfMonth(1).atStartOfDay();
            LocalDateTime end = monthDate.withDayOfMonth(monthDate.lengthOfMonth()).atTime(23, 59, 59);
            counts[5 - i] = bookingRepository.countByDateRange(start, end);
        }
        return counts;
    }

    /**
     * Returns month labels for the last 6 months (e.g. "T10", "T11", ...).
     */
    public String[] getMonthlyLabels() {
        String[] labels = new String[6];
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            labels[5 - i] = "T" + monthDate.getMonthValue();
        }
        return labels;
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
