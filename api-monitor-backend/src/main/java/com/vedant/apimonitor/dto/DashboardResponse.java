package com.vedant.apimonitor.dto;

import java.time.LocalDateTime;

public class DashboardResponse {

    private Long endpointId;

    private String endpointName;

    private String endpointUrl;

    private String tag;

    private boolean isUp;

    private long latestResponseTime;

    private LocalDateTime lastChecked;

    public DashboardResponse(){}

    public DashboardResponse(Long endpointId, String endpointName, String endpointUrl, String tag, boolean isUp, long latestResponseTime, LocalDateTime lastChecked) {
        this.endpointId = endpointId;
        this.endpointName = endpointName;
        this.endpointUrl = endpointUrl;
        this.tag = tag;
        this.isUp = isUp;
        this.latestResponseTime = latestResponseTime;
        this.lastChecked = lastChecked;
    }

    public Long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(Long endpointId) {
        this.endpointId = endpointId;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean getIsUp() {
        return isUp;
    }

    public void setIsUp(boolean up) {
        isUp = up;
    }

    public long getLatestResponseTime() {
        return latestResponseTime;
    }

    public void setLatestResponseTime(long latestResponseTime) {
        this.latestResponseTime = latestResponseTime;
    }

    public LocalDateTime getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(LocalDateTime lastChecked) {
        this.lastChecked = lastChecked;
    }
}
