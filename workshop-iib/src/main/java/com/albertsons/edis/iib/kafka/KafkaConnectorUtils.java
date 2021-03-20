package com.albertsons.edis.iib.kafka;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KafkaConnectorUtils {
    public KafkaConnectorUtils() {
    }

    public static boolean validateTopicName(String var0) {
        return var0.length() <= 255 && var0.matches("^[a-zA-Z0-9\\._\\-]*$");
    }

    public static boolean validateClientId(String var0) {
        return var0.length() <= 255 && var0.matches("^[a-zA-Z0-9\\._\\-]*$");
    }

    public static boolean validateGroupId(String var0) {
        return var0.length() <= 255 && var0.matches("^[a-zA-Z0-9\\._\\-]*$");
    }

    public static String buildClientId(String var0, String var1, String var2, String var3) {
        StringBuilder var4 = new StringBuilder();
        if (var0 != null) {
            var4.append(var0);
        }

        if (var1 != null) {
            if (var4.length() > 0) {
                var4.append("-");
            }

            var4.append(var1);
        }

        if (var2 != null) {
            if (var4.length() > 0) {
                var4.append("-");
            }

            var4.append(var2);
        }

        if (var3 != null) {
            if (var4.length() > 0) {
                var4.append("-");
            }

            var4.append(var3);
        }

        Pattern var5 = Pattern.compile("^[a-zA-Z0-9\\._\\-]*$");
        int var6 = 0;

        while (var6 < var4.length()) {
            String var7 = var4.substring(var6, var6 + 1);
            Matcher var8 = var5.matcher(var7);
            if (!var8.matches()) {
                var4.deleteCharAt(var6);
            } else {
                ++var6;
            }
        }

        if (var4.length() > 255) {
            var4.setLength(255);
        }

        return var4.toString();
    }
}

