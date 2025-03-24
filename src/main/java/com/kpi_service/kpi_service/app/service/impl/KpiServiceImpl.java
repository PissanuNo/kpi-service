package com.kpi_service.kpi_service.app.service.impl;


import com.kpi_service.kpi_service.app.model.dbs.KpiEmployeeModel;
import com.kpi_service.kpi_service.app.model.dto.AddKpiEmployeeRequest;
import com.kpi_service.kpi_service.app.repositories.KpiEmployeeRepository;
import com.kpi_service.kpi_service.app.service.KpiService;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.kpi_service.kpi_service.app.constant.Constants.ResponseCode.*;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseMessage.*;


@Service
public class KpiServiceImpl implements KpiService {
    Logger logger = LoggerFactory.getLogger("KpiService");
    private final KpiEmployeeRepository kpiEmployeeRepository;

    public KpiServiceImpl(KpiEmployeeRepository kpiEmployeeRepository) {
        this.kpiEmployeeRepository = kpiEmployeeRepository;
    }

    @Transactional
    @Override
    public ResponseBodyModel<String> AddKpiEmployee(AddKpiEmployeeRequest request) {
        ResponseBodyModel<String> response = new ResponseBodyModel<>();
        try {

            if (kpiEmployeeRepository.findByEmployeeId(request.getEmployeeId()).isPresent()) {
                response.setOperationError(ERROR_CODE_BUSINESS, DATA_DUPLICATE, null);
                return response;
            }

            kpiEmployeeRepository.saveAndFlush(KpiEmployeeModel.builder()
                    .transId(UUID.randomUUID().toString())
                    .employeeId(request.getEmployeeId())
                    .accessLevel(request.getAccessLevel())
                    .userGroup(request.getUserGroup())
                    .build());

            //send to sandmerit

            response.setOperationSuccess(SUCCESS_CODE, SUCCESS, null);
        } catch (Exception ex) {
            logger.error("Error Add KPI Employee: ", ex);
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

    @Override
    public ResponseBodyModel<String> updateKpiEmployee(String employeeId) {
        ResponseBodyModel<String> response = new ResponseBodyModel<>();
        try {
            Optional<KpiEmployeeModel> employeeModel = kpiEmployeeRepository.findByEmployeeId(employeeId);
            if (employeeModel.isEmpty()) {
                response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
            }

            //send to sandmerit

            response.setOperationSuccess(SUCCESS_CODE, SUCCESS, null);
        } catch (Exception ex) {
            logger.error("Error Update KPI Employee: ", ex);
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

    public ResponseBodyModel<String> deleteKpiEmployee(String employeeId) {
        ResponseBodyModel<String> response = new ResponseBodyModel<>();
        try {
            kpiEmployeeRepository.deleteById(employeeId);
            //send delete to sandmerit

            response.setOperationSuccess(SUCCESS_CODE, SUCCESS, null);
        } catch (Exception ex) {
            logger.error("Error Delete KPI Employee: ", ex);
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

}
