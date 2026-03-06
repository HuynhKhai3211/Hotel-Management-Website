package com.mycompany.hotelmanagementsystem.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateHelper {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateHelper() {}

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return LocalDate.parse(dateStr, DATE_FORMAT);
    }

    public static LocalDateTime toCheckInTime(LocalDate date) {
        return date.atTime(14, 0); // 2:00 PM
    }

    public static LocalDateTime toCheckOutTime(LocalDate date) {
        return date.atTime(12, 0); // 12:00 PM
    }

    public static long calculateNights(LocalDateTime checkIn, LocalDateTime checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
        return nights <= 0 ? 1 : nights;
    }

    public static boolean isFutureDate(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }
}
