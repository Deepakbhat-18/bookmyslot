package com.college.bookmyslot.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;

public class GoogleCalendarUtil {

    public static String generateEventLink(
            String title,
            String description,
            String venue,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        try {
            String start = date.toString().replace("-", "")
                    + "T" + startTime.toString().replace(":", "") + "00";

            String end = date.toString().replace("-", "")
                    + "T" + endTime.toString().replace(":", "") + "00";

            return "https://www.google.com/calendar/render?action=TEMPLATE"
                    + "&text=" + encode(title)
                    + "&details=" + encode(description)
                    + "&location=" + encode(venue)
                    + "&dates=" + start + "/" + end;

        } catch (Exception e) {
            return null;
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
