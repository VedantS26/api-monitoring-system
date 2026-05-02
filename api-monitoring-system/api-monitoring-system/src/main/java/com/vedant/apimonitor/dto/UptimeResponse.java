package com.vedant.apimonitor.dto;

public class UptimeResponse {

    private Long endpointId;
    private int days;
    private long totalChecks;
    private long upChecks;
    private double uptimePercentage;

    public Long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(Long endpointId) {
        this.endpointId = endpointId;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public long getTotalChecks() {
        return totalChecks;
    }

    public void setTotalChecks(long totalChecks) {
        this.totalChecks = totalChecks;
    }

    public long getUpChecks() {
        return upChecks;
    }

    public void setUpChecks(long upChecks) {
        this.upChecks = upChecks;
    }

    public double getUptimePercentage() {
        return uptimePercentage;
    }

    public void setUptimePercentage(double uptimePercentage) {
        this.uptimePercentage = uptimePercentage;
    }
}
