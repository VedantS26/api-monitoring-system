package com.vedant.apimonitor.Services;

import com.vedant.apimonitor.Model.MonitoredEndpoint;
import com.vedant.apimonitor.Model.User;
import com.vedant.apimonitor.Repository.HealthLogRepository;
import com.vedant.apimonitor.Repository.MonitoredEndpointRepository;
import com.vedant.apimonitor.Repository.UserRepository;
import com.vedant.apimonitor.dto.EndpointRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EndpointService {

    @Autowired
  private UserRepository userRepository;


    @Autowired
  private HealthLogRepository healthLogRepository;

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    //Helper : get logged in User

    private User getLoggedUser(){

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

    }

    //Helper : get endpoint and Verify Owner

    private MonitoredEndpoint getEndpointAndVerifyOwner(Long endpointId, User user){

        MonitoredEndpoint endpoint = monitoredEndpointRepository.findById(endpointId)
                .orElseThrow(() -> new RuntimeException("Endpoint not found"));


        if(!endpoint.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Unauthorized - you don't own this endpoint");
        }

        return endpoint;

    }

    public MonitoredEndpoint addEndpoint(EndpointRequest request){

        User user = getLoggedUser();

        MonitoredEndpoint endpoint = new MonitoredEndpoint();
        endpoint.setName(request.getName());
        endpoint.setUrl(request.getUrl());
        endpoint.setIsActive(true);
        endpoint.setTag(request.getTag());
        endpoint.setUser(user);
        endpoint.setAlertIntervalMinutes(request.getAlertIntervalMinutes());
        endpoint.setCreatedAt(LocalDateTime.now());
        endpoint.setCheckIntervalSeconds(request.getCheckIntervalSeconds());

       return  monitoredEndpointRepository.save(endpoint);

    }


    @Transactional
    public String deleteEndpoint(Long endpointId){

        User user = getLoggedUser();

        MonitoredEndpoint endpoint = getEndpointAndVerifyOwner(endpointId, user);


        healthLogRepository.deleteByEndpoint(endpoint);

        monitoredEndpointRepository.delete(endpoint);


       return "Endpoint deleted successfully";


    }

    public List<MonitoredEndpoint> getAllEndpoints(){

        User user = getLoggedUser();

        return monitoredEndpointRepository.findByUserId(user.getId());

    }

    public MonitoredEndpoint updateEndpoint(Long endpointId,  EndpointRequest request){

      User user = getLoggedUser();

      MonitoredEndpoint endpoint = getEndpointAndVerifyOwner(endpointId,  user);

      if(request.getName() != null){

          endpoint.setName(request.getName());
      }

      if(request.getUrl() != null){

          endpoint.setUrl(request.getUrl());
      }

      if(request.getTag() != null){

          endpoint.setTag(request.getTag());
      }


        if (request.getCheckIntervalSeconds() != null) {
            endpoint.setCheckIntervalSeconds(request.getCheckIntervalSeconds());
        }

       return monitoredEndpointRepository.save(endpoint);

    }


}
