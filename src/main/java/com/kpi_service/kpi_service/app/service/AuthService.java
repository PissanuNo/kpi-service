package com.kpi_service.kpi_service.app.service;


import com.kpi_service.kpi_service.app.model.dto.AuthRequest;
import com.kpi_service.kpi_service.app.model.dto.AuthResponse;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;

public interface AuthService {
    ResponseBodyModel<AuthResponse> login(AuthRequest request);
}
