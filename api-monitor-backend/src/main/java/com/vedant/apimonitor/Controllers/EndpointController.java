package com.vedant.apimonitor.Controllers;

import com.vedant.apimonitor.Model.MonitoredEndpoint;
import com.vedant.apimonitor.Services.EndpointService;
import com.vedant.apimonitor.dto.EndpointRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/endpoints")
@Validated
public class EndpointController {

    @Autowired
    private EndpointService endpointService;

    @PostMapping
    public ResponseEntity<?> addEndpoint(@Valid @RequestBody EndpointRequest request){

        MonitoredEndpoint endpoint = endpointService.addEndpoint(request);

      return ResponseEntity.status(201).body(endpoint);

    }

    @GetMapping
    public ResponseEntity<List<MonitoredEndpoint>> getAllEndpoints(){
        return  ResponseEntity.ok(endpointService.getAllEndpoints());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEndpoint(@PathVariable("id") @Min(value = 1, message = "Id must be greater than 0")  Long endpointId){

      String result = endpointService.deleteEndpoint(endpointId);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEndpoint(@PathVariable("id") @Min(1) Long endpointId,
                                            @RequestBody EndpointRequest request){

        MonitoredEndpoint updated = endpointService.updateEndpoint(endpointId, request);

        return ResponseEntity.ok(updated);


    }

    @PatchMapping("/{id}/alert-interval")
    public ResponseEntity<?> updateAlertInterval(@PathVariable("id") @Min(1) Long endpointId,
                                                 @RequestParam @Min(value = 1, message = "Alert interval must be at least 1 minute") int minutes) {

        MonitoredEndpoint updated = endpointService.updateAlertInterval(endpointId, minutes);

        return ResponseEntity.ok(updated);
    }


}
