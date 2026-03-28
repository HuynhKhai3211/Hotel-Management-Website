package com.mycompany.hotelmanagementsystem.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateHelper {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

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
        return nights <= 0 ? 1 : nights;
    }

    public static boolean isFutureDate(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }
}
