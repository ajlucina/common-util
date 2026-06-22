package com.ajlucina.commonutil.common;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public final class DateTimeUtil {
    private DateTimeUtil() {}

    public static TimeZone utcTimeZone() {
        return TimeZone.getTimeZone("UTC");
    }

    public static ZoneId utcZoneId() {
        return ZoneId.of("UTC");
    }

    public static LocalDate date2LocalDate(Date date) {
        return date2LocalDate(date, null);
    }

    public static LocalDate date2LocalDate(Date date, ZoneId zoneId) {
        var zone = zoneId == null ? utcZoneId() : zoneId;
        return date.toInstant().atZone(zone).toLocalDate();
    }

    public  static long minutesToMillis(long minutes) {
        return minutes * 60 * 1000;
    }
    public static long hoursToMillis(long hours) {
        return minutesToMillis(hours * 60);
    }

    public static long daysToMillis(long days) {
        return hoursToMillis(days * 24);
    }
}
