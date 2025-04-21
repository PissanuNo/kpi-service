package com.kpi_service.kpi_service.app.model.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kpi_service.kpi_service.app.utils.MicrosoftDateSerializer;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequest {

    @JsonProperty("ClientCode")
    @NotNull
    private Integer clientCode;

    @JsonProperty("Employee")
    private EmployeeClientRequest employee;

    @JsonProperty("EffectiveDate")
    @JsonSerialize(using = MicrosoftDateSerializer.class)
    private Date effectiveDate;
}

