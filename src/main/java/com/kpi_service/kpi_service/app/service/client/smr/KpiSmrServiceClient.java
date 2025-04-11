package com.kpi_service.kpi_service.app.service.client.smr;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpi_service.kpi_service.app.model.dto.client.CreateEmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.client.DeleteEmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.client.EmployeeClientResponse;
import com.kpi_service.kpi_service.app.model.dto.client.UpdateEmployeeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static com.kpi_service.kpi_service.app.constant.Kpi.headerKey.*;


@Service
public class KpiSmrServiceClient {

    private final WebClient webClient;
    private final KpiConnection kpiConnection;
    private final ObjectMapper objectMapper;

    @Value("${smr.key}")
    public String privateKey;

    @Value("${header.key.source.value}")
    public String sourceValue;

    private static final Logger log = LoggerFactory.getLogger(KpiSmrServiceClient.class);


    @Autowired
    public KpiSmrServiceClient(WebClient.Builder webClientBuilder,
                               KpiConnection kpiConnection,
                               @Value("${sandmerit.kpi.url}") String sandmeritUrl, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl(sandmeritUrl)
                .filter(logRequest())
                .filter(logResponse())
                .build();
        this.kpiConnection = kpiConnection;
        this.objectMapper = objectMapper;
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("➡️ Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> log.info("➡️ Header: {}={}", name, value))
            );
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("⬅️ Response Status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

    public EmployeeClientResponse createEmployee(CreateEmployeeRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            log.info("📦 JSON Body Add Employee: {}", json);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while processing JSON for add employee", e);
        }
        return webClient.post()
                .uri("/Employee_Add")
                .headers(httpHeaders -> {
                    httpHeaders.add(KEY, privateKey);
                    httpHeaders.add(TOKEN, kpiConnection.getConnection());
                    httpHeaders.add(SOURCES, sourceValue);
                })
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmployeeClientResponse.class)// map to response
                .doOnNext(response -> log.info("Received add employee Response: {}", response))
                .doOnError(WebClientResponseException.class,
                        ex -> log.error("Error add employee sandmerit client {}", ex.getMessage()))
                .block();
    }

    public EmployeeClientResponse updateEmployee(UpdateEmployeeRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            log.info("📦 JSON Body Update Employee: {}", json);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while processing JSON for updating employee", e);
        }
        return webClient.post()
                .uri("/Employee_Edit")
                .headers(httpHeaders -> {
                    httpHeaders.add(KEY, privateKey);
                    httpHeaders.add(TOKEN, kpiConnection.getConnection());
                    httpHeaders.add(SOURCES, sourceValue);
                })
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmployeeClientResponse.class)
                .doOnError(WebClientResponseException.class,
                        ex -> log.error("Error update employee client {}", ex.getMessage()))
                .block();
    }

    public EmployeeClientResponse deleteEmployee(DeleteEmployeeRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            log.info("📦 JSON Body Delete Employee: {}", json);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while processing JSON for deleting employee", e);
        }
        return webClient.post()
                .uri("/Employee_Delete")
                .headers(httpHeaders -> {
                    httpHeaders.add(KEY, privateKey);
                    httpHeaders.add(TOKEN, kpiConnection.getConnection());
                    httpHeaders.add(SOURCES, sourceValue);
                })
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmployeeClientResponse.class) // map to response
                .doOnError(WebClientResponseException.class,
                        ex -> log.error("Error delete employee client {}", ex.getMessage()))
                .block();
    }
}
