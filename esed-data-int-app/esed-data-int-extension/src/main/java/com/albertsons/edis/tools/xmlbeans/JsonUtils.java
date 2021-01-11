package com.albertsons.edis.tools.xmlbeans;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    private JsonUtils() {
    }

    public static Map<String, Object> toMap(JsonObject jsonObject) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        jsonObject.entrySet().forEach(e -> {
            String key = e.getKey();
            JsonElement value = e.getValue();
            if (value.isJsonArray()) {
                map.put(key, toList(value.getAsJsonArray()));

            } else if (value.isJsonObject()) {
                map.put(key, toMap(value.getAsJsonObject()));

            } else {
                map.put(key, value.getAsString());
            }

        });

        return map;
    }

    public static List<Object> toList(JsonArray array) {
        List<Object> list = new ArrayList<Object>();
        array.forEach(e -> {
            if (e.isJsonArray()) {
                list.add(toList(e.getAsJsonArray()));

            } else if (e.isJsonObject()) {
                list.add(toMap(e.getAsJsonObject()));

            } else {
                list.add(e.getAsString());
            }

        });

        return list;
    }
}
