package com.vedant.apimonitor.Services;

import com.vedant.apimonitor.Model.HealthLog;
import com.vedant.apimonitor.Model.MonitoredEndpoint;
import com.vedant.apimonitor.Model.User;
import com.vedant.apimonitor.Repository.HealthLogRepository;
import com.vedant.apimonitor.Repository.MonitoredEndpointRepository;
import com.vedant.apimonitor.Repository.UserRepository;
import com.vedant.apimonitor.dto.DashboardResponse;
import com.vedant.apimonitor.dto.UptimeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private HealthLogRepository healthLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    private User getLoggedUser(){

        String email = SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


    }


    public HealthLog getLatestStatus(Long id){

        User user =  getLoggedUser();

        MonitoredEndpoint endpoint = monitoredEndpointRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Endpoint not found"));

        if(!endpoint.getUser().getId().equals(user.getId())){

            throw new RuntimeException("Unauthorized - you don't own this endpoint");
        }

        HealthLog healthLog = healthLogRepository.findTopByEndpoint_IdOrderByCheckedAtDesc(endpoint.getId());

        return healthLog;

    }


    public List<HealthLog> getLogs(Long Id){

        User user =  getLoggedUser();

        MonitoredEndpoint endpoint = monitoredEndpointRepository.findById(Id)
                .orElseThrow(()->new RuntimeException("Endpoint not found"));

        if(!endpoint.getUser().getId().equals(user.getId())){

            throw new RuntimeException("Unauthorized - you don't own this endpoint");
        }

        return healthLogRepository.findTop100ByEndpoint_IdOrderByCheckedAtDesc(endpoint.getId());

    }

    public List<DashboardResponse> getDashboard(){

        User user =  getLoggedUser();

        List<DashboardResponse> dashboardResponses = new ArrayList<>();
        

        List<MonitoredEndpoint> monitoredEndpoints = monitoredEndpointRepository.findByUserId(user.getId());

          for(MonitoredEndpoint endpoints : monitoredEndpoints){

              HealthLog healthLog = healthLogRepository.findTopByEndpoint_IdOrderByCheckedAtDesc(endpoints.getId());

              DashboardResponse dashboardResponse = new DashboardResponse();

              dashboardResponse.setEndpointId(endpoints.getId());
              dashboardResponse.setTag(endpoints.getTag());
              dashboardResponse.setEndpointName(endpoints.getName());
              dashboardResponse.setEndpointUrl(endpoints.getUrl());
              
              if (healthLog != null) {
                  dashboardResponse.setIsUp(healthLog.getIsUp());
                  dashboardResponse.setLatestResponseTime(healthLog.getResponseTimeMs());
                  dashboardResponse.setLastChecked(healthLog.getCheckedAt());
              } else {
                  // endpoint added but never checked yet
                  dashboardResponse.setIsUp(false);
                  dashboardResponse.setLatestResponseTime(0);
                  dashboardResponse.setLastChecked(null);
              }
               
              dashboardResponses.add(dashboardResponse);

          }

          return dashboardResponses;

    }


    public UptimeResponse getUptimePercentage(Long Id, int days){

        User user =  getLoggedUser();

        MonitoredEndpoint endpoint = monitoredEndpointRepository.findById(Id)
                .orElseThrow(()->new RuntimeException("Endpoint not found"));

        if(!endpoint.getUser().getId().equals(user.getId())){

            throw new RuntimeException("Unauthorized - you don't own this endpoint");
        }

        LocalDateTime cutoff =  LocalDateTime.now().minusDays(days);

       Long totalLogs = healthLogRepository.countByEndpoint_IdAndCheckedAtAfter(Id,cutoff);

       Long upLogs = healthLogRepository.countByEndpoint_IdAndIsUpTrueAndCheckedAtAfter(Id,cutoff);

        double uptimePercentage = totalLogs == 0 ? 0 :
                ((double) upLogs / totalLogs) * 100;

       UptimeResponse uptimeResponse = new UptimeResponse();

       uptimeResponse.setUptimePercentage(uptimePercentage);
       uptimeResponse.setDays(days);
       uptimeResponse.setEndpointId(Id);
       uptimeResponse.setTotalChecks(totalLogs);
       uptimeResponse.setUpChecks(upLogs);

       return uptimeResponse;


    }




}
