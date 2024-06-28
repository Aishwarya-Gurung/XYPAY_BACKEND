package com.machpay.affiliate.util;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@UtilityClass
public class DateTimeUtils {
    public static String getCurrentDateTime() {
        String pattern = "MMMM dd, yyyy hh:mm a z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(new Date());
    }

    public static String getCurrentDateTime(String pattern) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        return currentDateTime.format(formatter);
    }

    public static Date getStartOfMonth(Date date, int monthsAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, monthsAgo);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date getEndOfMonth(Date date, int monthsAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, monthsAgo + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        return calendar.getTime();
    }

    public static String convertUtcToPst(Date utcDate) {
        Instant instant = utcDate.toInstant();
        ZoneId utcZone = ZoneId.of("UTC");
        ZoneId pstZone = ZoneId.of("America/Los_Angeles");

        ZonedDateTime utcDateTime = ZonedDateTime.ofInstant(instant, utcZone);
        ZonedDateTime pstDateTime = utcDateTime.withZoneSameInstant(pstZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm:ss a", Locale.US);
        String formattedDateTime = pstDateTime.format(formatter);

        return formattedDateTime;
    }
}
