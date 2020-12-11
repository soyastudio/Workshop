package com.albertsons.esed.monitor.service;

import com.google.gson.JsonArray;

public interface ScanReport {
    String getPipeline();

    JsonArray getAll();

    JsonArray getAdded();

    JsonArray getUnchanged();

    JsonArray getChanged();

    JsonArray getDuplicated();

    ScanReport load(JsonArray data);

    void addFailedInsert(String insert);

    void addFailedUpdate(String update);

    void close();

    ScanStatistics createStatistics();

    String toJson();

}
