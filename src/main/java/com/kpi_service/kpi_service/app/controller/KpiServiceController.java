package com.kpi_service.kpi_service.app.controller;


import com.kpi_service.kpi_service.app.model.dto.AddKpiEmployeeRequest;
import com.kpi_service.kpi_service.app.service.KpiService;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class KpiServiceController {

    private final KpiService kpiService;

    public KpiServiceController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @PostMapping(path = "/s/kpi",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<String>> addKpiEmployee(@Valid @RequestBody AddKpiEmployeeRequest request) {
        ResponseBodyModel<String> response = kpiService.addKpiEmployee(request);
        return ResponseEntity.ok(response);
    }

}
