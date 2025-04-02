package com.kpi_service.kpi_service.app.service.client.auth;

import com.kpi_service.kpi_service.app.model.dto.client.CheckPermissionRequest;
import com.kpi_service.kpi_service.app.model.dto.client.CheckPermissionResponse;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class AuthServiceClient {
    private final WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger(AuthServiceClient.class);

    @Autowired
    public AuthServiceClient(WebClient.Builder webClientBuilder,
                             @Value("${auth.service}") String accountServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(accountServiceUrl).build();
    }

    public ResponseBodyModel<CheckPermissionResponse> checkPermission(CheckPermissionRequest request) {
        return webClient.post()
                .uri("/v1/auth/check-permission")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseBodyModel<CheckPermissionResponse>>() {
                }) //   Generic
                .doOnError(WebClientResponseException.class,
                        ex -> log.error("Error check permission {}", ex.getMessage()))
                .block();
    }
}
