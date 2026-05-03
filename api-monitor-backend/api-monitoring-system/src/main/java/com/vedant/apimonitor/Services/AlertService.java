package com.vedant.apimonitor.Services;

import com.vedant.apimonitor.Model.MonitoredEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AlertService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendDownAlert(MonitoredEndpoint endpoint, int statusCode){

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
            } catch(Exception e){
            }

    }

    public void RecoveryAlert(MonitoredEndpoint endpoint, int statusCode){

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
            } catch (Exception e) {
            }

    }


}
