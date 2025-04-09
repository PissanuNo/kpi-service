package com.kpi_service.kpi_service.app.service.impl;


import com.google.common.base.Strings;
import com.kpi_service.kpi_service.app.model.dbs.KpiCorporateModel;
import com.kpi_service.kpi_service.app.model.dbs.KpiEmployeeModel;
import com.kpi_service.kpi_service.app.model.dbs.MasterDataModel;
import com.kpi_service.kpi_service.app.model.dto.AddKpiEmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.KpiEmployeeResponse;
import com.kpi_service.kpi_service.app.model.dto.client.EmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.client.EmployeeResponse;
import com.kpi_service.kpi_service.app.model.dto.client.UpdateEmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.client.ViewEmployeeDetailResponse;
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
import java.util.Optional;
import java.util.UUID;

import static com.kpi_service.kpi_service.app.constant.Constants.ResponseCode.*;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseMessage.*;


@Service
@RequiredArgsConstructor
public class KpiServiceImpl implements KpiService {
    Logger log = LoggerFactory.getLogger(KpiServiceImpl.class);
    private final KpiEmployeeRepository kpiEmployeeRepository;
    private final KpiSmrServiceClient kpiSmrServiceClient;
    private final KpiCorporateRepository kpiCorporateRepository;
    private final AccountServiceClient accountServiceClient;
    private final MasterDataRepository masterDataRepository;

    private static final String errorAccountServiceClient = "Error get employee detail from account service client : ";

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
                log.error(errorAccountServiceClient, employeeDetail.getMessage());
                response.setOperationError(ERROR_CODE_BUSINESS, DATA_NOT_FOUND, null);
                return response;
            }

            Integer kpiCorporateId = updateCorporate(employeeDetail.getObjectValue().getCorporateId());
            Integer kpiEmployeeId = Math.abs((int) (UUID.randomUUID().getMostSignificantBits() & 0xFFFFFFFFL));

            kpiEmployeeRepository.saveAndFlush(KpiEmployeeModel.builder()
                    .kpiEmployeeId(kpiEmployeeId)
                    .employeeId(request.getEmployeeId())
                    .accessLevel(request.getAccessLevel())
                    .userGroup(request.getUserGroup())
                    .build());

            EmployeeRequest employeeRequest = setEmployeeRequest(employeeDetail.getObjectValue(), kpiEmployeeId);

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

            //send add to sandmerit
//            EmployeeResponse result = kpiSmrServiceClient.createEmployee(
//                    CreateEmployeeRequest.builder()
//                            .ClientCode(kpiCorporateId)
//                            .Employee(employeeRequest)
//                            .AccessLevel(accessLevel.get().getDataDetailEn())
//                            .UserGroup(userGroup.get().getDataDetailEn())
//                            .build()
//            );

//            if (result.getIsSuccess().equals(Boolean.FALSE)) {
//                log.error("Error Creating employee in smr client: {}", result);
//                response.setOperationError(FAIL_CODE_EXTERNAL, ERROR, null);
//                return response;
//            }

            response.setOperationSuccess(SUCCESS_CODE, SUCCESS, null);
        } catch (Exception ex) {
            log.error("Error Add KPI Employee: ", ex);
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

    private EmployeeRequest setEmployeeRequest(ViewEmployeeDetailResponse employeeDetail, Integer kpiEmployeeId) {
        return EmployeeRequest.builder()
                .CompanyName(employeeDetail.getCorporateNameEn())
                .DepartmentName1(employeeDetail.getDepartmentLevel0())
                .DepartmentName2(employeeDetail.getDepartmentLevel1())
                .DepartmentName3(employeeDetail.getDepartmentLevel2())
                .DepartmentName4(employeeDetail.getDepartmentLevel3())
                .DepartmentName5(employeeDetail.getDepartmentLevel4())
                .EmployeeCode(kpiEmployeeId)
                .EmployeeName(employeeDetail.getEmployeeName())
                .Gender(employeeDetail.getGender())
                .BirthDate(employeeDetail.getBirthDate())
                .JoinDate(employeeDetail.getJoinDate())
                .LastWorkingDate(employeeDetail.getLastWorkingDate())
                .JobPosition(employeeDetail.getJobPositionNameEn())
                .JobGrade(Strings.isNullOrEmpty(employeeDetail.getJobGradeNameEn())
                        ? "General" :
                        employeeDetail.getJobGradeNameEn())
                .Nationality(employeeDetail.getNationalityNameEn())
                .Race(employeeDetail.getRaceNameEn())
                .Religion(employeeDetail.getReligionNameEn())
                .MaritalStatus(employeeDetail.getMarital())
                .Email(employeeDetail.getEmail())
                .EmailPersonal(employeeDetail.getPersonalEmail())
                .ContactNo(employeeDetail.getContactNo())
                .MobileNo(employeeDetail.getMobileContactNo())
                .DirectSuperior(updateReviewer(employeeDetail.getDirectSuperiorId()))
                .Reviewer1(updateReviewer(employeeDetail.getReviewer1Id()))
                .Reviewer2(updateReviewer(employeeDetail.getReviewer2Id()))
                .Reviewer3(updateReviewer(employeeDetail.getReviewer3Id()))
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
            kpiCorporateId = Math.abs((int) (UUID.randomUUID().getMostSignificantBits() & 0xFFFFFFFFL));
            kpiCorporateRepository.saveAndFlush(
                    KpiCorporateModel.builder()
                            .kpiCorporateId(kpiCorporateId)
                            .corporateId(corporateId)
                            .build());
        }
        return kpiCorporateId;
    }

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
            ResponseBodyModel<ViewEmployeeDetailResponse> employeeDetail = accountServiceClient.getEmployeeDetail(employeeId);
            if (!employeeDetail.isStatus()) {
                log.error(errorAccountServiceClient, employeeDetail.getMessage());
                response.setOperationError(ERROR_CODE_BUSINESS, DATA_NOT_FOUND, null);
                return response;
            }
            //find corporate
            Integer kpiCorporateId = updateCorporate(employeeDetail.getObjectValue().getCorporateId());
            if (kpiCorporateId == null) {
                response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
                return response;
            }

            Date effectiveDate = null;
            if (isEffective.equals(Boolean.TRUE)) {
                effectiveDate = new Date();
            }

            EmployeeRequest employeeRequest = setEmployeeRequest(employeeDetail.getObjectValue(), employeeModel.get().getKpiEmployeeId());

            //send to sandmerit
            EmployeeResponse result = kpiSmrServiceClient.updateEmployee(UpdateEmployeeRequest.builder()
                    .ClientCode(kpiCorporateId)
                    .Employee(employeeRequest)
                    .EffectiveDate(effectiveDate)
                    .build());

            if (result.getIsSuccess().equals(Boolean.FALSE)) {
                log.error("Error Updating employee in smr client: {}", result);
                response.setOperationError(FAIL_CODE_EXTERNAL, ERROR, null);
                return response;
            }

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
                //get employee detail
                ResponseBodyModel<ViewEmployeeDetailResponse> employeeDetail = accountServiceClient.getEmployeeDetail(employeeId);
                if (!employeeDetail.isStatus()) {
                    response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
                    return response;
                }
                Integer kpiCorporateId = updateCorporate(employeeDetail.getObjectValue().getCorporateId());
                if (kpiCorporateId == null) {
                    response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
                    return response;
                }
                //send delete to sandmerit
//                EmployeeResponse result = kpiSmrServiceClient.deleteEmployee(DeleteEmployeeRequest
//                        .builder()
//                        .ClientCode(kpiCorporateId)
//                        .EmployeeCode(employeeModel.get().getKpiEmployeeId())
//                        .build());
//
//                if (result.getIsSuccess().equals(Boolean.FALSE)) {
//                    log.error("Error Deleting employee in smr client: {}", result);
//                    response.setOperationError(FAIL_CODE_EXTERNAL, ERROR, null);
//                    return response;
//                }

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
