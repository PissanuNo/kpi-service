package com.kpi_service.kpi_service.app.service;


import com.kpi_service.kpi_service.app.model.dto.auth.AuthRequest;
import com.kpi_service.kpi_service.app.model.dto.auth.AuthResponse;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;

public interface AuthService {
    ResponseBodyModel<AuthResponse> login(AuthRequest request);
}
