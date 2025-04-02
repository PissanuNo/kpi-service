package com.kpi_service.kpi_service.app.controller;


import com.kpi_service.kpi_service.app.service.KpiService;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v3")
public class InternalKpiServiceController {

    private final KpiService kpiService;

    public InternalKpiServiceController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @DeleteMapping(path = "/kpi/{employeeId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<String>> deleteKpiEmployee(@PathVariable("employeeId") String employeeId) {
        ResponseBodyModel<String> response = kpiService.deleteKpiEmployee(employeeId);
        return ResponseEntity.ok(response);
    }

}
