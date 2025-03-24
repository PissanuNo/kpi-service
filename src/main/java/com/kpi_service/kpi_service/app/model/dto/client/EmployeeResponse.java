package com.kpi_service.kpi_service.app.model.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Boolean IsSuccess;
    private String MessageHeader;
    private String MessageBody;
    private Integer MessageType;
}
