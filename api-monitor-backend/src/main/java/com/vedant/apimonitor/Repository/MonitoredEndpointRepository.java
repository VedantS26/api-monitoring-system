package com.vedant.apimonitor.Repository;

import com.vedant.apimonitor.Model.MonitoredEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitoredEndpointRepository extends JpaRepository<MonitoredEndpoint,Long> {

List<MonitoredEndpoint> findByUserId(Long id);
  List<MonitoredEndpoint> findByIsActiveTrue();

}
