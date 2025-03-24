package com.kpi_service.kpi_service.app.model.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteEmployeeRequest {
    private Integer ClientCode;
    private Integer EmployeeCode;
}
