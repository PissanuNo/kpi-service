package com.kpi_service.kpi_service.app.model.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteEmployeeRequest {
    @JsonProperty("ClientCode")
    private Integer clientCode;

    @JsonProperty("EmployeeCode")
    private Integer employeeCode;
}
