package com.kpi_service.kpi_service.app.service;


import com.kpi_service.kpi_service.app.model.dto.kpi.AddKpiEmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.kpi.KpiEmployeeResponse;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.transaction.Transactional;

public interface KpiService {

    @Transactional
    ResponseBodyModel<String> addKpiEmployee(AddKpiEmployeeRequest request);

    ResponseBodyModel<String> updateKpiEmployee(String employeeId, Boolean isEffective);

    ResponseBodyModel<String> deleteKpiEmployee(String employeeId);

    ResponseBodyModel<KpiEmployeeResponse> getKpiEmployee(Integer kpiEmployeeId);
}
