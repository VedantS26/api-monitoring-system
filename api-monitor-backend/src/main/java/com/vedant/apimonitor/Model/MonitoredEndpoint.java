package com.vedant.apimonitor.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="monitored_endpoints")
public class MonitoredEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String tag;

    private String url;

    @Column(name = "check_interval_seconds")
    private Integer checkIntervalSeconds;

    private LocalDateTime createdAt;

    private boolean isActive;


    @Column(name = "alert_interval_minutes", columnDefinition = "integer default 5")
    private Integer alertIntervalMinutes = 5;

    @Column(name = "last_alert_sent_at")
    private LocalDateTime lastAlertSentAt;



    @OneToMany(mappedBy = "endpoint",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnore
    private List<HealthLog> healthLogs;

    public Long getId() {
        return id;
    }




    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Integer getCheckIntervalSeconds() {
        return checkIntervalSeconds;
    }

    public void setCheckIntervalSeconds(Integer checkIntervalSeconds) {
        this.checkIntervalSeconds = checkIntervalSeconds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getAlertIntervalMinutes() {
        return alertIntervalMinutes;
    }

    public void setAlertIntervalMinutes(Integer alertIntervalMinutes) {
        this.alertIntervalMinutes = alertIntervalMinutes;
    }

    public LocalDateTime getLastAlertSentAt() {
        return lastAlertSentAt;
    }

    public void setLastAlertSentAt(LocalDateTime lastAlertSentAt) {
        this.lastAlertSentAt = lastAlertSentAt;
    }
}
