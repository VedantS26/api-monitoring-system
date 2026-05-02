package com.vedant.apimonitor.Schedular;

import com.vedant.apimonitor.Model.MonitoredEndpoint;
import com.vedant.apimonitor.Repository.MonitoredEndpointRepository;
import com.vedant.apimonitor.Services.HealthCheckerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HealthCheckSchedular {

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Autowired
    private HealthCheckerService healthCheckerService;
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckSchedular.class);

    @Scheduled(fixedRate = 30000)
    public void runHealthChecks(){

        List<MonitoredEndpoint> allEndpoints = monitoredEndpointRepository.findByIsActiveTrue();

        logger.info("Schedular fired - checking "
                   +allEndpoints.size()+ " endpoints");

        for(MonitoredEndpoint endpoints : allEndpoints){

          healthCheckerService.checkEndpoint(endpoints);

        }

    }

}
