package com.kpi_service.kpi_service.app.service.impl;


import com.google.common.base.Strings;
import com.kpi_service.kpi_service.app.model.dbs.KpiCorporateModel;
import com.kpi_service.kpi_service.app.model.dbs.KpiEmployeeModel;
import com.kpi_service.kpi_service.app.model.dbs.MasterDataModel;
import com.kpi_service.kpi_service.app.model.dto.kpi.AddKpiEmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.kpi.KpiEmployeeResponse;
import com.kpi_service.kpi_service.app.model.dto.client.*;
import com.kpi_service.kpi_service.app.repositories.KpiCorporateRepository;
import com.kpi_service.kpi_service.app.repositories.KpiEmployeeRepository;
import com.kpi_service.kpi_service.app.repositories.MasterDataRepository;
import com.kpi_service.kpi_service.app.service.KpiService;
import com.kpi_service.kpi_service.app.service.client.account.AccountServiceClient;
import com.kpi_service.kpi_service.app.service.client.smr.KpiSmrServiceClient;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.kpi_service.kpi_service.app.constant.Constants.ResponseCode.*;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseMessage.*;
import static com.kpi_service.kpi_service.app.constant.Kpi.JobGrad.GENERAL;


@Service
@RequiredArgsConstructor
public class KpiServiceImpl implements KpiService {
    Logger log = LoggerFactory.getLogger(KpiServiceImpl.class);
    private final KpiEmployeeRepository kpiEmployeeRepository;
    private final KpiSmrServiceClient kpiSmrServiceClient;
    private final KpiCorporateRepository kpiCorporateRepository;
    private final AccountServiceClient accountServiceClient;
    private final MasterDataRepository masterDataRepository;

    private static final String ERROR_GET_EMPLOYEE_DETAIL_FROM_ACCOUNT_SERVICE_CLIENT = "Error get employee detail from account service client : {}";

    @Transactional
    @Override
    public ResponseBodyModel<String> addKpiEmployee(AddKpiEmployeeRequest request) {
        ResponseBodyModel<String> response = new ResponseBodyModel<>();
        try {

            if (kpiEmployeeRepository.findByEmployeeId(request.getEmployeeId()).isPresent()) {
                response.setOperationError(ERROR_CODE_BUSINESS, DATA_DUPLICATE, null);
                return response;
            }

            //get user detail in account service client by account id
            ResponseBodyModel<ViewEmployeeDetailResponse> employeeDetail = accountServiceClient.getEmployeeDetail(request.getEmployeeId());
            if (!employeeDetail.isStatus()) {
                log.error(ERROR_GET_EMPLOYEE_DETAIL_FROM_ACCOUNT_SERVICE_CLIENT, employeeDetail.getMessage());
                response.setOperationError(ERROR_CODE_BUSINESS, DATA_NOT_FOUND, null);
                return response;
            }

            Integer kpiCorporateId = updateCorporate(employeeDetail.getObjectValue().getCorporateId());
            KpiEmployeeModel newKpiEmployee = kpiEmployeeRepository.save(KpiEmployeeModel.builder()
                    .employeeId(request.getEmployeeId())
                    .kpiCorporateId(kpiCorporateId)
                    .accessLevel(request.getAccessLevel())
                    .userGroup(request.getUserGroup())
                    .build());

            Integer kpiEmployeeId = newKpiEmployee.getKpiEmployeeId();

            EmployeeClientRequest employeeClientRequest = setEmployeeRequest(employeeDetail.getObjectValue(), kpiEmployeeId);

            Optional<MasterDataModel> accessLevel = masterDataRepository.findById(request.getAccessLevel());
            if (accessLevel.isEmpty()) {
                response.setOperationError(ERROR_CODE_BUSINESS, DATA_NOT_FOUND, null);
                return response;
            }

            Optional<MasterDataModel> userGroup = masterDataRepository.findById(request.getUserGroup());
            if (userGroup.isEmpty()) {
                response.setOperationError(ERROR_CODE_BUSINESS, DATA_NOT_FOUND, null);
                return response;
            }

            CreateEmployeeRequest createEmployeeRequest = CreateEmployeeRequest.builder()
                    .clientCode(kpiCorporateId)
                    .employee(employeeClientRequest)
                    .accessLevel(accessLevel.get().getDataDetailEn())
                    .userGroup(userGroup.get().getDataDetailEn())
                    .build();

            //send add to sandmerit kpi
            EmployeeClientResponse result = kpiSmrServiceClient.createEmployee(createEmployeeRequest);

            if (Objects.isNull(result) || result.getIsSuccess().equals(Boolean.FALSE)) {
                log.error("Error Creating employee in sandmerit client: {}", result);
                response.setOperationError(FAIL_CODE_EXTERNAL, ERROR, null);
                return response;
            } else {
                log.info("Success to add employee in sandmerit client: {}", result);
            }

            response.setOperationSuccess(SUCCESS_CODE, SUCCESS, createEmployeeRequest.toString());
        } catch (Exception ex) {
            log.error("Error Add KPI Employee: ", ex);
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

    private EmployeeClientRequest setEmployeeRequest(ViewEmployeeDetailResponse employeeDetail, Integer kpiEmployeeId) {
        return EmployeeClientRequest.builder()
                .companyName(employeeDetail.getCorporateNameEn())
                .departmentName1(employeeDetail.getDepartmentLevel0())
                .departmentName2(employeeDetail.getDepartmentLevel1())
                .departmentName3(employeeDetail.getDepartmentLevel2())
                .departmentName4(employeeDetail.getDepartmentLevel3())
                .departmentName5(employeeDetail.getDepartmentLevel4())
                .employeeCode(kpiEmployeeId)
                .employeeName(employeeDetail.getEmployeeName())
                .gender(employeeDetail.getGender())
                .birthDate(employeeDetail.getBirthDate())
                .joinDate(employeeDetail.getJoinDate())
                .lastWorkingDate(employeeDetail.getLastWorkingDate())
                .jobPosition(employeeDetail.getJobPositionNameEn())
                .jobGrade(Strings.isNullOrEmpty(employeeDetail.getJobGradeNameEn())
                        ? GENERAL :
                        employeeDetail.getJobGradeNameEn())
                .nationality(employeeDetail.getNationalityNameEn())
                .race(employeeDetail.getRaceNameEn())
                .religion(employeeDetail.getReligionNameEn())
                .maritalStatus(employeeDetail.getMarital())
                .email(employeeDetail.getEmail())
                .emailPersonal(employeeDetail.getPersonalEmail())
                .contactNo(employeeDetail.getContactNo())
                .mobileNo(employeeDetail.getMobileContactNo())
                .directSuperior(updateReviewer(employeeDetail.getDirectSuperiorId()))
                .reviewer1(updateReviewer(employeeDetail.getReviewer1Id()))
                .reviewer2(updateReviewer(employeeDetail.getReviewer2Id()))
                .reviewer3(updateReviewer(employeeDetail.getReviewer3Id()))
                .build();
    }

    private Integer updateReviewer(String reviewer) {
        Integer reviewerId = null;
        Optional<KpiEmployeeModel> kpiEmployeeModel = kpiEmployeeRepository.findByEmployeeId(reviewer);
        if (kpiEmployeeModel.isPresent()) {
            reviewerId = kpiEmployeeModel.get().getKpiEmployeeId();
        }
        return reviewerId;
    }


    /*
     * Function to check if this company already exists or not.
     * If not in the table, add it. If already exists,
     * return kpi corporate id.
     */
    private Integer updateCorporate(String corporateId) {
        Integer kpiCorporateId;
        Optional<KpiCorporateModel> corporateModel = kpiCorporateRepository.findByCorporateId(corporateId);
        if (corporateModel.isPresent()) {
            kpiCorporateId = corporateModel.get().getKpiCorporateId();
        } else {
            KpiCorporateModel newCorporate = kpiCorporateRepository.saveAndFlush(
                    KpiCorporateModel.builder()
                            .corporateId(corporateId)
                            .build());
            kpiCorporateId = newCorporate.getKpiCorporateId();
        }
        return kpiCorporateId;
    }

    @Transactional
    @Override
    public ResponseBodyModel<String> updateKpiEmployee(String employeeId, Boolean isEffective) {
        ResponseBodyModel<String> response = new ResponseBodyModel<>();
        try {
            Optional<KpiEmployeeModel> employeeModel = kpiEmployeeRepository.findByEmployeeId(employeeId);
            if (employeeModel.isEmpty()) {
                response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
                return response;
            }

            //get employee detail
            ResponseBodyModel<ViewEmployeeDetailResponse> employeeDetail = accountServiceClient.getEmployeeDetail(employeeModel.get().getEmployeeId());
            if (!employeeDetail.isStatus()) {
                log.error(ERROR_GET_EMPLOYEE_DETAIL_FROM_ACCOUNT_SERVICE_CLIENT, employeeDetail.getMessage());
                response.setOperationError(ERROR_CODE_BUSINESS, DATA_NOT_FOUND, null);
                return response;
            }
            //find corporate
            Integer kpiCorporateId = updateCorporate(employeeDetail.getObjectValue().getCorporateId());
            if (kpiCorporateId == null) {
                response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
                return response;
            }

            //update kpi corporate if change
            if (!kpiCorporateId.equals(employeeModel.get().getKpiCorporateId())) {
                employeeModel.get().setKpiCorporateId(kpiCorporateId);
                kpiEmployeeRepository.saveAndFlush(employeeModel.get());
            }

            Date effectiveDate = null;
            if (isEffective.equals(Boolean.TRUE)) {
                effectiveDate = new Date();
                log.info("Convert effect date format: {}", effectiveDate);
            }

            EmployeeClientRequest employeeClientRequest = setEmployeeRequest(employeeDetail.getObjectValue(), employeeModel.get().getKpiEmployeeId());

            //send to sandmerit
            EmployeeClientResponse result = kpiSmrServiceClient.updateEmployee(UpdateEmployeeRequest.builder()
                    .clientCode(kpiCorporateId)
                    .employee(employeeClientRequest)
                    .effectiveDate(effectiveDate)
                    .build());

            if (result.getIsSuccess().equals(Boolean.FALSE)) {
                log.error("Error Updating employee in smr client: {}", result);
                response.setOperationError(FAIL_CODE_EXTERNAL, ERROR, null);
                return response;
            }
            log.info("Success to Update Employee in smr client: {}", result);

            response.setOperationSuccess(SUCCESS_CODE, SUCCESS, null);
        } catch (Exception ex) {
            log.error("Error Update KPI Employee: ", ex);
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

    @Transactional
    @Override
    public ResponseBodyModel<String> deleteKpiEmployee(String employeeId) {
        ResponseBodyModel<String> response = new ResponseBodyModel<>();
        try {
            Optional<KpiEmployeeModel> employeeModel = kpiEmployeeRepository.findByEmployeeId(employeeId);
            if (employeeModel.isPresent()) {

                Integer kpiCorporateId = employeeModel.get().getKpiCorporateId();

                //send delete api to sandmerit
                EmployeeClientResponse result = kpiSmrServiceClient.deleteEmployee(DeleteEmployeeRequest
                        .builder()
                        .clientCode(kpiCorporateId)
                        .employeeCode(employeeModel.get().getKpiEmployeeId())
                        .build());

                if (result.getIsSuccess().equals(Boolean.FALSE)) {
                    log.error("Error Deleting employee in smr client: {}", result);
                    response.setOperationError(FAIL_CODE_EXTERNAL, ERROR, null);
                    return response;
                }
                log.info("Success to delete employee in smr client: {} ", result);

                //delete employee
                kpiEmployeeRepository.deleteById(employeeModel.get().getKpiEmployeeId());

                response.setOperationSuccess(SUCCESS_CODE, SUCCESS, null);
            } else {
                response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
            }
        } catch (Exception ex) {
            log.error("Error Delete KPI Employee: ", ex);
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }


    @Override
    public ResponseBodyModel<KpiEmployeeResponse> getKpiEmployee(Integer kpiEmployeeId) {
        ResponseBodyModel<KpiEmployeeResponse> response = new ResponseBodyModel<>();
        try {
            Optional<KpiEmployeeModel> kpiEmployeeModel = kpiEmployeeRepository.findById(kpiEmployeeId);
            if (kpiEmployeeModel.isPresent()) {
                response.setOperationSuccess(SUCCESS_CODE, SUCCESS, KpiEmployeeResponse.builder()
                        .employeeId(kpiEmployeeModel.get().getEmployeeId())
                        .accessLevel(kpiEmployeeModel.get().getAccesslevelModel().getDataDetailEn())
                        .userGroup(kpiEmployeeModel.get().getUserGroupModel().getDataDetailEn())
                        .build());
            } else {
                response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
            }
        } catch (Exception ex) {
            log.error("Error Retrieving KPI Employee: ", ex);
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

}
