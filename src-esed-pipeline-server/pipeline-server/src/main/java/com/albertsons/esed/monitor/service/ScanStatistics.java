package com.albertsons.esed.monitor.service;

import java.util.Date;
import java.util.List;

public class ScanStatistics {
    private String pipeline;
    private int round;
    private int startPoint;
    private int endPoint;
    private Date startTime;

    private int added;
    private int changed;
    private int unchanged;
    private int duplicated;

    private List<String> failedInserts;
    private List<String> failedUpdates;

    private int created;
    private int updated;

    private long fetchTime;
    private long persistTime;

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public int getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(int endPoint) {
        this.endPoint = endPoint;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getAdded() {
        return added;
    }

    public void setAdded(int added) {
        this.added = added;
    }

    public int getChanged() {
        return changed;
    }

    public void setChanged(int changed) {
        this.changed = changed;
    }

    public int getUnchanged() {
        return unchanged;
    }

    public void setUnchanged(int unchanged) {
        this.unchanged = unchanged;
    }

    public int getDuplicated() {
        return duplicated;
    }

    public void setDuplicated(int duplicated) {
        this.duplicated = duplicated;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public List<String> getFailedInserts() {
        return failedInserts;
    }

    public void setFailedInserts(List<String> failedInserts) {
        if (failedInserts != null && failedInserts.size() > 0) {
            this.failedInserts = failedInserts;
        }
    }

    public List<String> getFailedUpdates() {
        return failedUpdates;
    }

    public void setFailedUpdates(List<String> failedUpdates) {
        if (failedUpdates != null && failedUpdates.size() > 0) {
            this.failedUpdates = failedUpdates;
        }
    }

    public long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }

    public long getPersistTime() {
        return persistTime;
    }

    public void setPersistTime(long persistTime) {
        this.persistTime = persistTime;
    }
}
