package com.vedant.apimonitor.Services;

import com.vedant.apimonitor.Model.MonitoredEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    private static final String RESEND_EMAILS_URL = "https://api.resend.com/emails";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${resend.api-key:}")
    private String resendApiKey;

    @Value("${resend.from-email:API Monitor <onboarding@resend.dev>}")
    private String fromEmail;

    public boolean sendDownAlert(MonitoredEndpoint endpoint, int statusCode) {
        logger.info("sendDownAlert called for: {}", endpoint.getUrl());
        logger.info("Sending to: {}", endpoint.getUser().getEmail());

        String text = "Hello,\n\n" +
                "Your API is DOWN!\n\n" +
                "Details:\n" +
                "Name:        " + endpoint.getName() + "\n" +
                "URL:         " + endpoint.getUrl() + "\n" +
                "Status Code: " + statusCode + "\n" +
                "Time:        " + LocalDateTime.now() + "\n\n" +
                "Please check your API immediately.\n\n" +
                "API Monitor System";

        return sendEmail(endpoint.getUser().getEmail(),
                "API Down Alert - " + endpoint.getName(),
                text);
    }

    public boolean RecoveryAlert(MonitoredEndpoint endpoint, int statusCode) {
        String text = "Hello,\n\n" +
                "Good news! Your API is back UP!\n\n" +
                "Details:\n" +
                "Name:      " + endpoint.getName() + "\n" +
                "URL:       " + endpoint.getUrl() + "\n" +
                "Recovered: " + LocalDateTime.now() + "\n\n" +
                "API Monitor System";

        return sendEmail(endpoint.getUser().getEmail(),
                "API Recovered - " + endpoint.getName(),
                text);
    }

    private boolean sendEmail(String toEmail, String subject, String text) {
        if (resendApiKey == null || resendApiKey.isBlank()) {
            logger.error("Email failed: RESEND_API_KEY is missing");
            return false;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(resendApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "from", fromEmail,
                "to", List.of(toEmail),
                "subject", subject,
                "text", text
        );

        try {
            Map response = restTemplate.postForObject(RESEND_EMAILS_URL,
                    new HttpEntity<>(body, headers),
                    Map.class);
            logger.info("Email sent successfully via Resend. response={}", response);
            return true;
        } catch (RestClientResponseException e) {
            logger.error("Resend email failed: status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            logger.error("Resend email failed: {}", e.getMessage());
            logger.error("Full Resend email error: ", e);
            return false;
        }
    }
}
