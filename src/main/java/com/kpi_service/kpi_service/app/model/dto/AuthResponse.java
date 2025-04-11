package com.kpi_service.kpi_service.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String employeeId;
    private String accessToken;
    private String refreshToken;
    private Date expired;
    private Integer kpiEmployeeId;
    private String kpiRedirectUrl;
}
