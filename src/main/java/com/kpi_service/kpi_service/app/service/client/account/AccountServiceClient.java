package com.kpi_service.kpi_service.app.service.client.account;

import com.kpi_service.kpi_service.app.model.dto.AuthRequest;
import com.kpi_service.kpi_service.app.model.dto.AuthResponse;
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
public class AccountServiceClient {
    private final WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger(AccountServiceClient.class);

    @Autowired
    public AccountServiceClient(WebClient.Builder webClientBuilder,
                                @Value("${account.service}") String accountServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(accountServiceUrl).build();
    }

    public AuthResponse auth(AuthRequest request) {
        return webClient.post()
                .uri("/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseBodyModel<AuthResponse>>() {
                }) //   Generic
                .doOnNext(response -> log.info("Raw Response: {}", response)) //  Log response
                .map(ResponseBodyModel::getObjectValue) //  use `objectValue` from ResponseBodyModel
                .doOnError(WebClientResponseException.class,
                        ex -> log.error("Error auth employee client {}", ex.getMessage()))
                .block();
    }
}
