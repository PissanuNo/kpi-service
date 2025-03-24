package com.kpi_service.kpi_service.app.service.impl;


import com.kpi_service.kpi_service.app.model.dbs.KpiEmployeeModel;
import com.kpi_service.kpi_service.app.model.dto.AuthRequest;
import com.kpi_service.kpi_service.app.model.dto.AuthResponse;
import com.kpi_service.kpi_service.app.repositories.KpiEmployeeRepository;
import com.kpi_service.kpi_service.app.service.AuthService;
import com.kpi_service.kpi_service.app.service.client.account.AccountServiceClient;
import com.kpi_service.kpi_service.app.service.client.smr.KpiConnection;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.kpi_service.kpi_service.app.constant.Constants.ResponseCode.*;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseMessage.*;


@Service
public class AuthServiceImpl implements AuthService {
    Logger logger = LoggerFactory.getLogger("LoggingService");
    private final KpiConnection kpiConnection;
    private final KpiEmployeeRepository kpiEmployeeRepository;
    private final AccountServiceClient accountServiceClient;
    public AuthServiceImpl(KpiConnection kpiConnection,
                           KpiEmployeeRepository kpiEmployeeRepository,
                           AccountServiceClient accountServiceClient) {
        this.kpiConnection = kpiConnection;
        this.kpiEmployeeRepository = kpiEmployeeRepository;
        this.accountServiceClient = accountServiceClient;
    }


    @Override
    public ResponseBodyModel<AuthResponse> login(AuthRequest request) {
        ResponseBodyModel<AuthResponse> response = new ResponseBodyModel<>();
        try {

            //call api to account service
            AuthResponse authRes = accountServiceClient.auth(request);
            String employeeId = authRes.getEmployeeId();
            //check user have kpi
            Optional<KpiEmployeeModel> employee = kpiEmployeeRepository.findByEmployeeId(employeeId);
            if (employee.isEmpty()) {
                response.setOperationError(ERROR_CODE_DATA_NOT_FOUND ,DATA_NOT_FOUND,null);
                return response;
            }
            //generate token sign on sandmerit internal
            String token = kpiConnection.getToken(employee.get().getEmployeeId());

            response.setOperationSuccess(SUCCESS_CODE, SUCCESS,
                    AuthResponse.builder()
                    .token(token)
                    .employeeId(employee.get().getEmployeeId())
                    .build());

        } catch (Exception ex) {
            logger.error("Authentication failed: ", ex);
            response.setOperationError(FAIL_CODE_INTERNAL, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

}
