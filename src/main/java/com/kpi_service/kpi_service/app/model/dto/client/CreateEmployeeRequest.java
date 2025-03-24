package com.kpi_service.kpi_service.app.model.dto.client;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest {
    @NotNull
    private Integer ClientCode;

    private EmployeeRequest Employee;

    private String UserGroup;

    private String AccessLevel;
}
