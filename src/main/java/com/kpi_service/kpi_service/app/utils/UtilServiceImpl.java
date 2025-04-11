package com.kpi_service.kpi_service.app.utils;


import com.google.common.base.Strings;
import com.kpi_service.kpi_service.app.service.UtilService;
import com.kpi_service.kpi_service.core.model.PageBodyModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UtilServiceImpl implements UtilService {


    @Override
    public boolean isEmailValid(@NotNull String email) {
        return Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                .matcher(email)
                .matches();
    }


    @Override
    public Pageable pageBodyconvertToPageable(PageBodyModel pageBodyModel) {
        return PageRequest.of(
                Strings.isNullOrEmpty(pageBodyModel.getPage()) ? 0 : Integer.parseInt(pageBodyModel.getPage()),
                Strings.isNullOrEmpty(pageBodyModel.getPageSize()) ? 10 : Integer.parseInt(pageBodyModel.getPageSize()),
                Sort.by(Strings.isNullOrEmpty(pageBodyModel.getSortDirection())
                                ? Sort.Direction.ASC
                                : Sort.Direction.valueOf(pageBodyModel.getSortDirection()),
                        Strings.isNullOrEmpty(pageBodyModel.getSortBy())
                                ? ""
                                : pageBodyModel.getSortBy())
        );
    }

    @Override
    public PageBodyModel pageableConvertToPageBodyModel(Pageable pageable, long totalElements, int totalPages) {
        return PageBodyModel
                .builder()
                .page(String.valueOf(pageable.getPageNumber()))
                .pageSize(String.valueOf(pageable.getPageSize()))
                .total(String.valueOf(totalElements))
                .totalPage(String.valueOf(totalPages))
                .sortDirection(pageable.getSort().iterator().next().getDirection().name())
                .sortBy(pageable.getSort().iterator().next().getProperty())
                .build();
    }


}
