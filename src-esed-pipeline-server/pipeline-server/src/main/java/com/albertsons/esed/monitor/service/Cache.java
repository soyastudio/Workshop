package com.albertsons.esed.monitor.service;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface Cache {
    String getName();

    void reload(JsonArray jsonArray);

    boolean contains(JsonObject value);

    void put(JsonObject value);

    JsonObject get(String key);

    int size();

    boolean isChanged();
}
