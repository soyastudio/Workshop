package com.albertsons.esed.monitor.service;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface Cache {

    void reload(JsonArray jsonArray);

    void put(JsonObject value);

    JsonObject get(String key);

    int size();

    boolean isChanged();
}
