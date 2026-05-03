package com.vedant.apimonitor.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    RestTemplate restTemplate(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

           factory.setConnectTimeout(Duration.ofSeconds(5000)); //max 5s to connect
           factory.setReadTimeout(Duration.ofSeconds(5000));  //max 5s to read response

        return new RestTemplate(factory);

    }

}
