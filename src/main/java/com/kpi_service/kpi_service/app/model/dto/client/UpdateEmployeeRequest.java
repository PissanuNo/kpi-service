package com.kpi_service.kpi_service.app.model.dto.client;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequest
{
    @NotNull
    private Integer ClientCode;

    private EmployeeRequest Employee;

    private LocalDate EffectiveDate;
}

