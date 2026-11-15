
package com.tripmind.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {

        return builder
                .defaultHeader(
                        HttpHeaders.ACCEPT_ENCODING,
                        "identity"
                )
                .build();
    }
}
