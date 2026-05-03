package com.vedant.apimonitor.Model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class HealthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "endpoint_id")
    private MonitoredEndpoint endpoint;

    @Column(name = "status_code")
    private int statusCode;

    @Column(name = "response_time_ms")
    private long responseTimeMs;

    @Column(name = "is_up")
    private boolean isUp;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MonitoredEndpoint getEndpoint() { return endpoint; }
    public void setEndpoint(MonitoredEndpoint endpoint) { this.endpoint = endpoint; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public boolean getIsUp() { return isUp; }
    public void setIsUp(boolean isUp) { this.isUp = isUp; }



    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
}
