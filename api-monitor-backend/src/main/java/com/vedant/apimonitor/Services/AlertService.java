package com.vedant.apimonitor.Services;

import com.vedant.apimonitor.Model.MonitoredEndpoint;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendDownAlert(MonitoredEndpoint endpoint, int statusCode){

           logger.info("sendDownAlert called for: {}", endpoint.getUrl());
           logger.info("Sending to: {}", endpoint.getUser().getEmail());

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(endpoint.getUser().getEmail());
            message.setSubject("🚨 API Down Alert - " + endpoint.getName());
            message.setText(
                    "Hello,\n\n" +
                            "Your API is DOWN!\n\n" +
                            "Details:\n" +
                            "→ Name:        " + endpoint.getName() + "\n" +
                            "→ URL:         " + endpoint.getUrl() + "\n" +
                            "→ Status Code: " + statusCode + "\n" +
                            "→ Time:        " + LocalDateTime.now() + "\n\n" +
                            "Please check your API immediately.\n\n" +
                            "API Monitor System"
            );
            mailSender.send(message);


              logger.info("Email sent successfully!");
              return true;
            } catch(Exception e){

                logger.error("Email failed: {}", e.getMessage());
                logger.error("Full error: ", e);
                return false;
            }

    }

    public boolean RecoveryAlert(MonitoredEndpoint endpoint, int statusCode){

        try{
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(endpoint.getUser().getEmail());
            message.setSubject("✅ API Recovered - " + endpoint.getName());
            message.setText(
                    "Hello,\n\n" +
                            "Good news! Your API is back UP!\n\n" +
                            "Details:\n" +
                            "→ Name:      " + endpoint.getName() + "\n" +
                            "→ URL:       " + endpoint.getUrl() + "\n" +
                            "→ Recovered: " + LocalDateTime.now() + "\n\n" +
                            "API Monitor System"

            );

            mailSender.send(message);
            logger.info("Recovery email sent successfully!");
            return true;
            } catch (Exception e) {
                logger.error("Recovery email failed: {}", e.getMessage());
                logger.error("Full recovery email error: ", e);
                return false;
            }

    }


}
