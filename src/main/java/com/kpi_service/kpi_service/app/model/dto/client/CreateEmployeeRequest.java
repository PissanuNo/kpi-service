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
public class CreateEmployeeRequest {
    @JsonProperty("ClientCode")
    private Integer clientCode;

    @JsonProperty("Employee")
    private EmployeeClientRequest employee;

    @JsonProperty("UserGroup")
    private String userGroup;

    @JsonProperty("AccessLevel")
    private String accessLevel;
}
