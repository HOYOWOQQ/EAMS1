package com.eams.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static LocalDateTime parseLocalDateTimeOrNow(String timeStr) {
        try {
            return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS]"));
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
    public static String nowString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public static java.time.LocalDate parseLocalDate(String dateStr) {
        if(dateStr == null || dateStr.trim().isEmpty()) return null;
        return java.time.LocalDate.parse(dateStr);
    }
    
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
