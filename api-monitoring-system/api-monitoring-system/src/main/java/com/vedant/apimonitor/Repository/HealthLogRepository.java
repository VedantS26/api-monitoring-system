package com.vedant.apimonitor.Repository;

import com.vedant.apimonitor.Model.HealthLog;
import com.vedant.apimonitor.Model.MonitoredEndpoint;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HealthLogRepository extends JpaRepository<HealthLog,Long> {

    List<HealthLog> findTop100ByEndpoint_IdOrderByCheckedAtDesc(Long endpointId);
    HealthLog findTopByEndpoint_IdOrderByCheckedAtDesc(Long endpointId);

    @Transactional
    void deleteByEndpoint(MonitoredEndpoint endpoint);

    Long countByEndpoint_IdAndCheckedAtAfter(Long Id,LocalDateTime cutoff);

    Long countByEndpoint_IdAndIsUpTrueAndCheckedAtAfter(Long Id,LocalDateTime cutoff);

    List<HealthLog> findTop2ByEndpoint_IdOrderByCheckedAtDesc(Long endpointId);
}
