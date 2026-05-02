package com.vedant.apimonitor.Controllers;

import com.vedant.apimonitor.Model.HealthLog;
import com.vedant.apimonitor.Services.DashboardService;
import com.vedant.apimonitor.dto.DashboardResponse;
import com.vedant.apimonitor.dto.UptimeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;


    @GetMapping("/dashboard")
    public ResponseEntity<List<DashboardResponse>> getDashboard(){

        return ResponseEntity.ok(dashboardService.getDashboard());

    }

    @GetMapping("/endpoints/{id}/status")
    public ResponseEntity<HealthLog> getLatestStatus(@PathVariable Long id){

        return ResponseEntity.ok(dashboardService.getLatestStatus(id));

    }

    @GetMapping("/endpoints/{id}/logs")
    public ResponseEntity<List<HealthLog>> getLogs(@PathVariable Long id){

        return ResponseEntity.ok(dashboardService.getLogs(id));

    }

    @GetMapping("/endpoints/{id}/uptime")
    public ResponseEntity<UptimeResponse> getUptimePercentage(@PathVariable Long id,
                                                              @RequestParam(defaultValue = "7") int days){

        return ResponseEntity.ok(dashboardService.getUptimePercentage(id,days));


    }


}
