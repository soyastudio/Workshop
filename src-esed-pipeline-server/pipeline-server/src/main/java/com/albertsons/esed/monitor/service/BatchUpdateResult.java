package com.albertsons.esed.monitor.service;

import java.util.ArrayList;
import java.util.List;

public class BatchUpdateResult {
    private int updated;
    private List<String> statements = new ArrayList<>();

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public List<String> getStatements() {
        return statements;
    }
}
