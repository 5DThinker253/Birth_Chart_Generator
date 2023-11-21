package com.ejs.birthchart.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter dateTimeFormatterShort = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT).withLocale(Locale.getDefault());
    public static final DateTimeFormatter dateTimeFormatter12 = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm a").withLocale(Locale.getDefault());
    public static final DateTimeFormatter dateTimeFormatter24 = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm").withLocale(Locale.getDefault());
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.getDefault());
    public static final DateTimeFormatter dateTimeFormatter24US = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").withLocale(Locale.US);
    public static final DateTimeFormatter TimeFormatter12 = DateTimeFormatter.ofPattern("hh:mm a").withLocale(Locale.getDefault());
    public static final DateTimeFormatter TimeFormatter24 = DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale.getDefault());
    public static final String timeZone = TimeZone.getDefault().getID();
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";

    /**
     * Convert UTC datetime to Local datetime
     * @param utcDateString string datetime UTC
     * @return return local datetime in string
     */
    public static String convertUTCtoLocalTime24(String utcDateString) {
        return Instant.parse(utcDateString).atZone(ZoneId.systemDefault()).format(dateTimeFormatter24);
    }

    /**
     * Convert UTC datetime to Local datetime
     * @param utcDateString string datetime UTC
     * @param dateTimeFormatter formatter for datetime
     * @return return local datetime in string
     */
    public static String convertUTCtoLocalTime(String utcDateString, DateTimeFormatter dateTimeFormatter) {
        return Instant.parse(utcDateString).atZone(ZoneId.systemDefault()).toString();
    }
    public static String convertLocalTimeToUTC(String localDateString, DateTimeFormatter dateTimeFormatter) {
        return Instant.parse(localDateString).atZone(ZoneId.of("UTC")).toString();
    }
    public static String convertLocalTimeToGMT(String localDateString, DateTimeFormatter dateTimeFormatter) {
        return Instant.parse(localDateString).atZone(ZoneId.of("GMT")).format(dateTimeFormatter);
    }

    public static String convertLocalTimeToUTC1(String localTimeString, DateTimeFormatter dateTimeFormatter) {
        LocalDateTime localDateTime = LocalDateTime.parse(localTimeString, dateTimeFormatter);
        ZoneId localZone = ZoneId.systemDefault();
        ZoneOffset utcOffset = ZoneOffset.UTC;
        LocalDateTime utcDateTime = localDateTime.atZone(localZone).withZoneSameInstant(utcOffset).toLocalDateTime();
        return utcDateTime.format(dateTimeFormatter);
    }
    /**
     * Parse date and time into ZonedDateTime
     * @param dateText date in string value for LocalDate
     * @param timeText time in string value for LocalTime
     * @param timeZoneId timezone in string
     * @return return ZonedDateTime object
     */
    public static ZonedDateTime parseDateTime(String dateText, String timeText, String timeZoneId) {
        // Parsear la fecha
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dateText, dateFormatter);

        // Parsear la hora
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(timeText, timeFormatter);

        // Combinar la fecha y hora en un objeto ZonedDateTime
        ZoneId timeZone = ZoneId.of(timeZoneId);
        ZonedDateTime dateTime = ZonedDateTime.of(date, time, timeZone);

        return dateTime;
    }

    public static LocalDateTime parseDateTime(String dateText, String timeText) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // Parsear la fecha
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dateText, dateFormatter);
        // Parsear la hora
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(timeText, timeFormatter);
        return LocalDateTime.of(date, time);
    }
    public static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            return LocalDateTime.parse(dateTimeString, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            // El formato de fecha y hora es incorrecto
            return null;
        }
    }

    public static Boolean checkDateFormat(String date){
        if (date == null || !date.matches("^(1[0-9]|0[1-9]|3[0-1]|2[1-9])/(0[1-9]|1[0-2])/[0-9]{4}$"))
            return false;
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
        try {
            format.parse(date);
            return true;
        }catch (ParseException e){
            return false;
        }
    }

    public static String getSQLdate(String fechaString) {
        LocalDateTime fecha;
        try {
            fecha = LocalDateTime.parse(fechaString, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            // Manejar el error de formato de fecha inválido
            return null;
        }

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return fecha.format(outputFormatter);
    }

}
