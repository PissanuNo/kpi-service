package com.kpi_service.kpi_service.app.service.client.smr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/*
For internal token generation with Sandmerit
* */
@Component
public class KpiConnection {

    @Value("${smr.key}")
    public String privateKey;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    /*
    API Connection
    * */
    public String getConnection() {
        return getTokenByType("connection", privateKey);
    }

    /*
     * Single Sign On
     * */
    public String getToken(Object employeeId) {
        return getTokenByType("token", employeeId);
    }

    private String getTokenByType(String type, Object key) {
        Date now = new Date();
        String formattedDate = DATE_FORMAT.format(now);

        // Parse date components
        int year = Integer.parseInt(formattedDate.substring(0, 4));
        int month = Integer.parseInt(formattedDate.substring(4, 6));
        int day = Integer.parseInt(formattedDate.substring(6, 8));
        int hour = Integer.parseInt(formattedDate.substring(8, 10));
        int minutes = Integer.parseInt(formattedDate.substring(10, 12));
        int seconds = Integer.parseInt(formattedDate.substring(12, 14));

        // Step 1: Compute hash
        int step1 = (year + month + day) * (hour + minutes + seconds);
        int step2 = step1 % 100; // Get last 2 digits

        String finalString = "";
        if (type.equalsIgnoreCase("connection")) {
            // Concatenate values
            finalString = getLast10Chars(privateKey) + formattedDate + String.format("%02d", step2);
        } else if (type.equalsIgnoreCase("token")) {
            finalString = key + ":" + formattedDate + ":" + String.format("%02d", step2);
        }

        // Encode in Base64
        return Base64.getEncoder().encodeToString(finalString.getBytes(StandardCharsets.UTF_8));
    }

    public String getLast10Chars(String input) {
        if (input == null || input.length() <= 10) {
            return input;
        }
        return input.substring(input.length() - 10);
    }
}
