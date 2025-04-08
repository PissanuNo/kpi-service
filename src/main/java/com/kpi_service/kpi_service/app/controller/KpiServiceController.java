package com.kpi_service.kpi_service.app.controller;


import com.kpi_service.kpi_service.app.model.dto.AddKpiEmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.KpiEmployeeResponse;
import com.kpi_service.kpi_service.app.service.KpiService;
import com.kpi_service.kpi_service.core.model.Permission;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kpi_service.kpi_service.app.constant.Permissions.menuCode.KPI_MANAGEMENT;
import static com.kpi_service.kpi_service.app.constant.Permissions.permissionFlag.*;

@RestController
@RequestMapping("v1")
public class KpiServiceController {

    private final KpiService kpiService;

    public KpiServiceController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @Permission(menu = KPI_MANAGEMENT, permission = CREATE)
    @PostMapping(path = "/s/kpi",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<String>> addKpiEmployee(@Valid @RequestBody AddKpiEmployeeRequest request) {
        ResponseBodyModel<String> response = kpiService.addKpiEmployee(request);
        return ResponseEntity.ok(response);
    }

    @Permission(menu = KPI_MANAGEMENT, permission = DELETE)
    @DeleteMapping(path = "/s/kpi/{employeeId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<String>> deleteKpiEmployee(@PathVariable("employeeId") String employeeId) {
        ResponseBodyModel<String> response = kpiService.deleteKpiEmployee(employeeId);
        return ResponseEntity.ok(response);
    }

    @Permission(menu = KPI_MANAGEMENT, permission = READ)
    @GetMapping(path = "/s/kpi/{employeeId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<KpiEmployeeResponse>> getKpiEmployee(@PathVariable("employeeId") Integer employeeId) {
        ResponseBodyModel<KpiEmployeeResponse> response = kpiService.getKpiEmployee(employeeId);
        return ResponseEntity.ok(response);
    }


}
