package com.kpi_service.kpi_service.app.model.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthClientResponse {
    private String employeeId;
    private String accessToken;
    private String refreshToken;
    private Date expired;
}
