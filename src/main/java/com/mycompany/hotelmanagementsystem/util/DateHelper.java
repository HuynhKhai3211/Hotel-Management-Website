package com.mycompany.hotelmanagementsystem.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateHelper {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final LocalTime STANDARD_CHECK_IN = LocalTime.of(14, 0);
    public static final LocalTime STANDARD_CHECK_OUT = LocalTime.of(12, 0);

    private DateHelper() {}

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return LocalDate.parse(dateStr, DATE_FORMAT);
    }

    // Default check-in time: 14:00
    public static LocalDateTime toCheckInTime(LocalDate date) {
        return date.atTime(14, 0);
    }

    // Default check-out time: 12:00
    public static LocalDateTime toCheckOutTime(LocalDate date) {
        return date.atTime(12, 0);
    }

    /**
     * Parse check-in date with custom time from form (e.g. "14:30", "09:15").
     * Falls back to default 14:00 if timeStr is invalid.
     */
    public static LocalDateTime toCheckInTime(LocalDate date, String timeStr) {
        LocalTime time = parseTime(timeStr, LocalTime.of(14, 0));
        return date.atTime(time);
    }

    /**
     * Parse check-out date with custom time from form (e.g. "12:00", "10:30").
     * Falls back to default 12:00 if timeStr is invalid.
     */
    public static LocalDateTime toCheckOutTime(LocalDate date, String timeStr) {
        LocalTime time = parseTime(timeStr, LocalTime.of(12, 0));
        return date.atTime(time);
    }

    // Parse "HH:mm" string, fallback to default if invalid
    private static LocalTime parseTime(String timeStr, LocalTime defaultTime) {
        if (timeStr == null || timeStr.isEmpty()) return defaultTime;
        try {
            return LocalTime.parse(timeStr, TIME_FORMAT);
        } catch (Exception e) {
            return defaultTime;
        }
    }

    public static long calculateNights(LocalDateTime checkIn, LocalDateTime checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
        return Math.max(nights, 0);  // return 0 for same-day instead of forcing 1
    }

    public static boolean isFutureDate(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Calculate early/late surcharges based on actual check-in/out times.
     * Standard check-in: 14:00, Standard check-out: 12:00.
     * Early = before 14:00 check-in, Late = after 12:00 check-out.
     * Same-day bookings (same date) are charged entirely by hours.
     */
    public static SurchargeResult calculateSurcharges(
            LocalDateTime checkIn, LocalDateTime checkOut, BigDecimal pricePerHour) {

        SurchargeResult result = new SurchargeResult();

        if (pricePerHour == null || pricePerHour.compareTo(BigDecimal.ZERO) == 0) {
            return result;  // no surcharge if no hourly rate
        }

        // Same-day booking: charge by hours only (no nightly rate)
        if (checkIn.toLocalDate().equals(checkOut.toLocalDate())) {
            long totalMinutes = ChronoUnit.MINUTES.between(checkIn, checkOut);
            if (totalMinutes <= 0) totalMinutes = 60;  // minimum 1 hour
            long totalHours = (long) Math.ceil(totalMinutes / 60.0);
            result.setSameDayBooking(true);
            result.setTotalHours(totalHours);
            result.setHourlyTotal(pricePerHour.multiply(BigDecimal.valueOf(totalHours)));
            return result;
        }

        result.setSameDayBooking(false);

        // Early check-in surcharge: checkInTime before 14:00
        LocalTime checkInTime = checkIn.toLocalTime();
        if (checkInTime.isBefore(STANDARD_CHECK_IN)) {
            long earlyMinutes = ChronoUnit.MINUTES.between(checkInTime, STANDARD_CHECK_IN);
            long earlyHours = (long) Math.ceil(earlyMinutes / 60.0);
            result.setEarlyHours(earlyHours);
            result.setEarlySurcharge(pricePerHour.multiply(BigDecimal.valueOf(earlyHours)));
        }

        // Late check-out surcharge: checkOutTime after 12:00
        LocalTime checkOutTime = checkOut.toLocalTime();
        if (checkOutTime.isAfter(STANDARD_CHECK_OUT)) {
            long lateMinutes = ChronoUnit.MINUTES.between(STANDARD_CHECK_OUT, checkOutTime);
            long lateHours = (long) Math.ceil(lateMinutes / 60.0);
            result.setLateHours(lateHours);
            result.setLateSurcharge(pricePerHour.multiply(BigDecimal.valueOf(lateHours)));
        }

        return result;
    }
}
