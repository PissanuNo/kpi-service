package com.kpi_service.kpi_service.app.service;


import com.kpi_service.kpi_service.app.model.dto.AddKpiEmployeeRequest;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.transaction.Transactional;

public interface KpiService {

    @Transactional
    ResponseBodyModel<String> AddKpiEmployee(AddKpiEmployeeRequest request);

    ResponseBodyModel<String> updateKpiEmployee(String employeeId);
}
