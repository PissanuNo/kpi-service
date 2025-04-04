package com.kpi_service.kpi_service.app.controller;


import com.kpi_service.kpi_service.app.service.KpiService;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("v3")
public class InternalKpiServiceController {

    private final KpiService kpiService;

    @DeleteMapping(path = "/kpi/{employeeId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<String>> deleteKpiEmployee(@PathVariable("employeeId") String employeeId) {
        ResponseBodyModel<String> response = kpiService.deleteKpiEmployee(employeeId);
        return ResponseEntity.ok(response);
    }


    @PatchMapping(path = "/kpi",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<String>> updateKpiEmployee(@RequestParam("employeeId") String employeeId,
                                                                       @RequestParam("isEffective") Boolean isEffective) {
        ResponseBodyModel<String> response = kpiService.updateKpiEmployee(employeeId, isEffective);
        return ResponseEntity.ok(response);
    }

}
