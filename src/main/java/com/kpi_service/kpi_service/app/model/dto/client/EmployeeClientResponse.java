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
public class EmployeeClientResponse {
    @JsonProperty("IsSuccess")
    private Boolean isSuccess;

    @JsonProperty("MessageHeader")
    private String messageHeader;

    @JsonProperty("MessageBody")
    private String messageBody;

    @JsonProperty("MessageType")
    private Integer messageType;
}
