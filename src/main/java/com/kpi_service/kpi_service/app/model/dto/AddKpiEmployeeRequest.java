package com.kpi_service.kpi_service.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddKpiEmployeeRequest {
    private String employeeId;
    private String accessLevel;
    private String userGroup;
}
