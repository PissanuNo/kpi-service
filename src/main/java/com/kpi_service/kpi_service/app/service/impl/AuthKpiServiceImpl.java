package com.kpi_service.kpi_service.app.service.impl;


import com.kpi_service.kpi_service.app.model.dbs.KpiEmployeeModel;
import com.kpi_service.kpi_service.app.model.dto.auth.AuthRequest;
import com.kpi_service.kpi_service.app.model.dto.auth.AuthResponse;
import com.kpi_service.kpi_service.app.model.dto.client.AuthClientResponse;
import com.kpi_service.kpi_service.app.repositories.KpiEmployeeRepository;
import com.kpi_service.kpi_service.app.service.AuthKpiService;
import com.kpi_service.kpi_service.app.service.UtilService;
import com.kpi_service.kpi_service.app.service.client.account.AccountServiceClient;
import com.kpi_service.kpi_service.app.service.client.smr.KpiConnection;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static com.kpi_service.kpi_service.app.constant.Constants.ResponseCode.*;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseMessage.*;


@RequiredArgsConstructor
@Service
public class AuthKpiServiceImpl implements AuthKpiService {

    Logger logger = LoggerFactory.getLogger(AuthKpiServiceImpl.class);

    private final KpiConnection kpiConnection;
    private final KpiEmployeeRepository kpiEmployeeRepository;
    private final AccountServiceClient accountServiceClient;
    private final UtilService utilService;

    @Value("${signon.sandmerit.path}")
    String signonSandmeritPath;

    @Override
    public ResponseBodyModel<AuthResponse> login(AuthRequest request) {
        ResponseBodyModel<AuthResponse> response = new ResponseBodyModel<>();
        try {

            //call api to account service
            AuthClientResponse authRes = accountServiceClient.auth(request);
            if (Objects.isNull(authRes.getAccessToken())) {
                logger.error("Auth failed : {}", authRes );
                response.setOperationError(ERROR_CODE_BUSINESS, EXTERNAL_SERVER_ERROR_MSG, null);
                return response;
            }
            String employeeId = authRes.getEmployeeId();
            //check user have kpi
            Optional<KpiEmployeeModel> employee = kpiEmployeeRepository.findByEmployeeId(employeeId);
            if (employee.isEmpty()) {
                response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
                return response;
            }
            //generate token sign on sandmerit internal
            String token = kpiConnection.getToken(employee.get().getKpiEmployeeId());
            String encodeEmployee = utilService.encodeToBase64(employee.get().getKpiEmployeeId().toString());
            String redirect = String.format(signonSandmeritPath, encodeEmployee, token);
            response.setOperationSuccess(SUCCESS_CODE, SUCCESS,
                    AuthResponse.builder()
                            .employeeId(employee.get().getEmployeeId())
                            .accessToken(authRes.getAccessToken())
                            .refreshToken(authRes.getRefreshToken())
                            .expired(authRes.getExpired())
                            .kpiRedirectUrl(redirect)
                            .kpiEmployeeId(employee.get().getKpiEmployeeId())
                            .build());

        } catch (Exception ex) {
            logger.error("Authentication failed: ", ex);
            response.setOperationError(FAIL_CODE_INTERNAL, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

}
