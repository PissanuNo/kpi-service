package com.kpi_service.kpi_service.app.service.client.smr;


import com.google.common.net.HttpHeaders;
import com.kpi_service.kpi_service.app.model.dto.client.CreateEmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.client.DeleteEmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.client.EmployeeResponse;
import com.kpi_service.kpi_service.app.model.dto.client.UpdateEmployeeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class KpiSmrServiceClient {
    private final WebClient webClient;
    private final KpiConnection kpiConnection;

    public static final String BEARER = "Bearer ";

    private static final Logger log = LoggerFactory.getLogger(KpiSmrServiceClient.class);


    @Autowired
    public KpiSmrServiceClient(WebClient.Builder webClientBuilder,
                               KpiConnection kpiConnection,
                               @Value("${sandmerit.kpi.service}") String sandmeritUrl) {
        this.webClient = webClientBuilder.baseUrl(sandmeritUrl).build();
        this.kpiConnection = kpiConnection;
    }

    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        return webClient.post()
                .uri("/Employee_Add")
                .header(HttpHeaders.AUTHORIZATION, BEARER + kpiConnection.getConnection())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmployeeResponse.class) // map to response
                .doOnError(WebClientResponseException.class,
                        ex -> log.error("Error create employee client {}", ex.getMessage()))
                .block();
    }

    public EmployeeResponse updateEmployee(UpdateEmployeeRequest request) {
        return webClient.patch()
                .uri("/Employee_Edit")
                .header(HttpHeaders.AUTHORIZATION, BEARER + kpiConnection.getConnection())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmployeeResponse.class)
                .doOnError(WebClientResponseException.class,
                        ex -> log.error("Error update employee client {}", ex.getMessage()))
                .block();
    }

    public EmployeeResponse deleteEmployee(DeleteEmployeeRequest request) {
        return webClient.post()
                .uri("/Employee_Delete")
                .header(HttpHeaders.AUTHORIZATION, BEARER + kpiConnection.getConnection())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmployeeResponse.class) // map to response
                .doOnError(WebClientResponseException.class,
                        ex -> log.error("Error delete employee client {}", ex.getMessage()))
                .block();
    }
}
