package com.vedant.apimonitor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EndpointRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "URL cannot be empty")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "Invalid URL format. Must start with http:// or https://"
    )
    private String url;

    private String tag;

    private Integer checkIntervalSeconds;

    @Min(value = 1, message = "Alert interval must be at least 1 minute")
    private int alertIntervalMinutes;

    public int getAlertIntervalMinutes() {
        return alertIntervalMinutes;
    }

    public void setAlertIntervalMinutes(int alertIntervalMinutes) {
        this.alertIntervalMinutes = alertIntervalMinutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getCheckIntervalSeconds() {
        return checkIntervalSeconds;
    }

    public void setCheckIntervalSeconds(Integer checkIntevalSeconds) {
        this.checkIntervalSeconds = checkIntevalSeconds;
    }
}
