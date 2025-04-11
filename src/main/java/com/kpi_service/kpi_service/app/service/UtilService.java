package com.kpi_service.kpi_service.app.service;


import com.kpi_service.kpi_service.core.model.PageBodyModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

public interface UtilService {
    boolean isEmailValid(@NotNull String email);

    Pageable pageBodyconvertToPageable(PageBodyModel pageBodyModel);

    PageBodyModel pageableConvertToPageBodyModel(Pageable pageable, long totalElements, int totalPages);

}