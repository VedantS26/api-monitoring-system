package com.vedant.apimonitor.Services;

import com.vedant.apimonitor.Model.HealthLog;
import com.vedant.apimonitor.Model.MonitoredEndpoint;
import com.vedant.apimonitor.Repository.HealthLogRepository;
import com.vedant.apimonitor.Repository.MonitoredEndpointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HealthCheckerService {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckerService.class);
    private static final int DEFAULT_BACKOFF_MINUTES = 10;

    // Fix 1 — backoff map: endpointId → backoff expiry time
    private final Map<Long, LocalDateTime> rateLimitBackoffMap = new ConcurrentHashMap<>();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MonitoredEndpointRepository monitoredEndpointRepository;

    @Autowired
    private HealthLogRepository healthLogRepository;

    @Autowired
    private AlertService alertService;

    @Async
    public synchronized void checkEndpoint(MonitoredEndpoint endpoint) {

        endpoint = monitoredEndpointRepository.findById(endpoint.getId())
                .orElse(endpoint);

        // Fix 1 — skip check if endpoint is still in backoff window
        LocalDateTime backoffUntil = rateLimitBackoffMap.get(endpoint.getId());
        if (backoffUntil != null && LocalDateTime.now().isBefore(backoffUntil)) {
            logger.info("[SKIPPED - Rate Limited] {} | Backoff until: {}", endpoint.getUrl(), backoffUntil);
            return;
        }

        HealthLog healthLog = new HealthLog();
        boolean success = false;
        long start = System.currentTimeMillis();

        for (int attempt = 0; attempt < 3; attempt++) {

            try {
                ResponseEntity<String> response = restTemplate
                        .getForEntity(endpoint.getUrl(), String.class);

                int statusCode = response.getStatusCode().value();

                if (statusCode >= 200 && statusCode < 400) {
                    // SUCCESS — clear any existing backoff since endpoint is healthy again
                    rateLimitBackoffMap.remove(endpoint.getId());

                    long responseTimeMs = System.currentTimeMillis() - start;
                    healthLog.setIsUp(true);
                    healthLog.setStatusCode(statusCode);
                    healthLog.setResponseTimeMs((int) responseTimeMs);
                    healthLog.setCheckedAt(LocalDateTime.now());
                    healthLog.setEndpoint(endpoint);
                    success = true;
                    break;

                } else if (statusCode == 429) {
                    // Fix 2 — read Retry-After header, fall back to DEFAULT_BACKOFF_MINUTES
                    int backoffMinutes = DEFAULT_BACKOFF_MINUTES;
                    String retryAfter = response.getHeaders().getFirst("Retry-After");
                    if (retryAfter != null) {
                        try {
                            int seconds = Integer.parseInt(retryAfter.trim());
                            backoffMinutes = (int) Math.ceil(seconds / 60.0);
                            logger.info("[RATE LIMITED] Retry-After header: {}s → backing off {}min",
                                    seconds, backoffMinutes);
                        } catch (NumberFormatException ignored) {
                            logger.warn("[RATE LIMITED] Could not parse Retry-After header: {}", retryAfter);
                        }
                    }

                    // Fix 1 — store backoff expiry in map
                    rateLimitBackoffMap.put(endpoint.getId(),
                            LocalDateTime.now().plusMinutes(backoffMinutes));

                    logger.info("[RATE LIMITED 429] {} | Backing off for {} min",
                            endpoint.getUrl(), backoffMinutes);

                    // Log as UP — 429 is not a real outage
                    long responseTimeMs = System.currentTimeMillis() - start;
                    healthLog.setIsUp(true);
                    healthLog.setStatusCode(statusCode);
                    healthLog.setResponseTimeMs((int) responseTimeMs);
                    healthLog.setCheckedAt(LocalDateTime.now());
                    healthLog.setEndpoint(endpoint);
                    success = true;
                    break;  // no retries for 429

                } else if (statusCode >= 500) {
                    // SERVER ERROR — worth retrying
                    if (attempt < 2) Thread.sleep(5000);

                } else {
                    // CLIENT ERROR (4xx) — don't retry
                    long responseTimeMs = System.currentTimeMillis() - start;
                    healthLog.setIsUp(false);
                    healthLog.setStatusCode(statusCode);
                    healthLog.setResponseTimeMs((int) responseTimeMs);
                    healthLog.setCheckedAt(LocalDateTime.now());
                    healthLog.setEndpoint(endpoint);
                    success = true;
                    break;
                }

            } catch (HttpClientErrorException.TooManyRequests e) {
                // specifically 429
                int statusCode = e.getStatusCode().value();
                String body = e.getResponseBodyAsString();

                if (statusCode == 429 ||
                        (statusCode == 403 && body.contains("rate limit"))) {

                    // rate limited — treat as UP
                    rateLimitBackoffMap.put(endpoint.getId(),
                            LocalDateTime.now().plusMinutes(DEFAULT_BACKOFF_MINUTES));

                    logger.info("[RATE LIMITED {}}] {} | Backing off {}min",
                            statusCode, endpoint.getUrl(), DEFAULT_BACKOFF_MINUTES);

                    healthLog.setIsUp(true);
                    healthLog.setStatusCode(statusCode);
                    healthLog.setResponseTimeMs((int) (System.currentTimeMillis() - start));
                    healthLog.setCheckedAt(LocalDateTime.now());
                    healthLog.setEndpoint(endpoint);
                    success = true;
                    break;

                }
            }



            catch(Exception e){

                    logger.error("Attempt {} failed for {} → {}", attempt + 1, endpoint.getUrl(), e.getMessage());

                    if (attempt < 2) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            // All 3 attempts failed
            if (!success) {
                long responseTimeMs = System.currentTimeMillis() - start;
                healthLog.setIsUp(false);
                healthLog.setStatusCode(0);
                healthLog.setResponseTimeMs((int) responseTimeMs);
                healthLog.setCheckedAt(LocalDateTime.now());
                healthLog.setEndpoint(endpoint);
            }

            healthLogRepository.save(healthLog);

            List<HealthLog> recentLogs = healthLogRepository
                    .findTop2ByEndpoint_IdOrderByCheckedAtDesc(endpoint.getId());

            HealthLog previousLog = recentLogs.size() > 1 ? recentLogs.get(1) : null;

            int interval = Optional
                    .ofNullable(endpoint.getAlertIntervalMinutes())
                    .orElse(5);

            if (!healthLog.getIsUp()) {

                logger.info("lastAlertSentAt: {}", endpoint.getLastAlertSentAt());
                logger.info("cooldown cutoff: {}", LocalDateTime.now().minusMinutes(interval));
                logger.info("interval: {}", interval);

                boolean isNewOutage = previousLog == null || previousLog.getIsUp();
                boolean cooldownPassed = endpoint.getLastAlertSentAt() == null
                        || endpoint.getLastAlertSentAt()
                        .isBefore(LocalDateTime.now().minusMinutes(interval));

                logger.info("isNewOutage: {}", isNewOutage);
                logger.info("cooldownPassed: {}", cooldownPassed);
                logger.info("lastAlertSentAt: {}", endpoint.getLastAlertSentAt());
                logger.info("cooldown cutoff: {}", LocalDateTime.now().minusMinutes(interval));
                logger.info("Will send alert: {}", isNewOutage || cooldownPassed);

                if (isNewOutage && cooldownPassed) {
                    logger.info("Calling alertService.sendDownAlert...");
                    alertService.sendDownAlert(endpoint, healthLog.getStatusCode());
                    logger.info("alertService.sendDownAlert called!");
                    endpoint.setLastAlertSentAt(LocalDateTime.now());
                    monitoredEndpointRepository.save(endpoint);
                }

            } else {
                boolean justRecovered = previousLog != null && !previousLog.getIsUp();
                if (justRecovered) {
                    alertService.RecoveryAlert(endpoint, healthLog.getStatusCode());
                }
            }
        }
    }
