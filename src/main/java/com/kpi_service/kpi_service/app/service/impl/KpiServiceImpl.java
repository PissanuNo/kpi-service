package com.kpi_service.kpi_service.app.service.impl;


import com.kpi_service.kpi_service.app.model.dbs.KpiCorporateModel;
import com.kpi_service.kpi_service.app.model.dbs.KpiEmployeeModel;
import com.kpi_service.kpi_service.app.model.dbs.MasterDataModel;
import com.kpi_service.kpi_service.app.model.dto.AddKpiEmployeeRequest;
import com.kpi_service.kpi_service.app.model.dto.KpiEmployeeResponse;
import com.kpi_service.kpi_service.app.model.dto.client.EmployeeRequest;
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

            //send add to sandmerit
            EmployeeRequest employeeRequest = EmployeeRequest.builder()
                    .CompanyName(employeeDetail.getObjectValue().getCorporateNameEn())
                    .DepartmentName1(employeeDetail.getObjectValue().getDepartmentLevel0())
                    .DepartmentName2(employeeDetail.getObjectValue().getDepartmentLevel1())
                    .DepartmentName3(employeeDetail.getObjectValue().getDepartmentLevel2())
                    .DepartmentName4(employeeDetail.getObjectValue().getDepartmentLevel3())
                    .DepartmentName5(employeeDetail.getObjectValue().getDepartmentLevel4())
                    .EmployeeCode(kpiEmployeeId)
                    .EmployeeName(employeeDetail.getObjectValue().getEmployeeName())
                    .Gender(employeeDetail.getObjectValue().getGender())
                    .BirthDate(employeeDetail.getObjectValue().getBirthDate())
                    .JoinDate(employeeDetail.getObjectValue().getJoinDate())
                    .LastWorkingDate(employeeDetail.getObjectValue().getLastWorkingDate())
                    .JobPosition(employeeDetail.getObjectValue().getJobPositionNameEn())
                    .JobGrade(employeeDetail.getObjectValue().getJobGradeNameEn())
                    .Nationality(employeeDetail.getObjectValue().getNationalityNameEn())
                    .Race(employeeDetail.getObjectValue().getRaceNameEn())
                    .Religion(employeeDetail.getObjectValue().getReligionNameEn())
                    .MaritalStatus(employeeDetail.getObjectValue().getMarital())
                    .Email(employeeDetail.getObjectValue().getEmail())
                    .EmailPersonal(employeeDetail.getObjectValue().getPersonalEmail())
                    .ContactNo(employeeDetail.getObjectValue().getContactNo())
                    .MobileNo(employeeDetail.getObjectValue().getMobileContactNo())
                    .DirectSuperior(updateReviewer(employeeDetail.getObjectValue().getDirectSuperiorId()))
                    .Reviewer1(updateReviewer(employeeDetail.getObjectValue().getReviewer1Id()))
                    .Reviewer2(updateReviewer(employeeDetail.getObjectValue().getReviewer2Id()))
                    .Reviewer3(updateReviewer(employeeDetail.getObjectValue().getReviewer3Id()))
                    .build();

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
            //send to sandmerit
//            EmployeeResponse result = kpiSmrServiceClient.updateEmployee(UpdateEmployeeRequest.builder()
//                    .ClientCode(kpiCorporateId)
//                    .Employee(EmployeeRequest.builder()
//                            .CompanyName(employeeDetail.getObjectValue().getCorporateNameEn())
//                            .DepartmentName1(employeeDetail.getObjectValue().getDepartmentLevel0())
//                            .DepartmentName2(employeeDetail.getObjectValue().getDepartmentLevel1())
//                            .DepartmentName3(employeeDetail.getObjectValue().getDepartmentLevel2())
//                            .DepartmentName4(employeeDetail.getObjectValue().getDepartmentLevel3())
//                            .DepartmentName5(employeeDetail.getObjectValue().getDepartmentLevel4())
//                            .EmployeeCode(employeeModel.get().getKpiEmployeeId())
//                            .EmployeeName(employeeDetail.getObjectValue().getEmployeeName())
//                            .Gender(employeeDetail.getObjectValue().getGender())
//                            .BirthDate(employeeDetail.getObjectValue().getBirthDate())
//                            .JoinDate(employeeDetail.getObjectValue().getJoinDate())
//                            .LastWorkingDate(employeeDetail.getObjectValue().getLastWorkingDate())
//                            .JobPosition(employeeDetail.getObjectValue().getJobPositionNameEn())
//                            .JobGrade(employeeDetail.getObjectValue().getJobGradeNameEn())
//                            .Nationality(employeeDetail.getObjectValue().getNationalityNameEn())
//                            .Race(employeeDetail.getObjectValue().getRaceNameEn())
//                            .Religion(employeeDetail.getObjectValue().getReligionNameEn())
//                            .MaritalStatus(employeeDetail.getObjectValue().getMarital())
//                            .Email(employeeDetail.getObjectValue().getEmail())
//                            .EmailPersonal(employeeDetail.getObjectValue().getPersonalEmail())
//                            .ContactNo(employeeDetail.getObjectValue().getContactNo())
//                            .MobileNo(employeeDetail.getObjectValue().getMobileContactNo())
//                            .DirectSuperior(updateReviewer(employeeDetail.getObjectValue().getDirectSuperiorId()))
//                            .Reviewer1(updateReviewer(employeeDetail.getObjectValue().getReviewer1Id()))
//                            .Reviewer2(updateReviewer(employeeDetail.getObjectValue().getReviewer2Id()))
//                            .Reviewer3(updateReviewer(employeeDetail.getObjectValue().getReviewer3Id()))
//                            .build())
//                    .EffectiveDate(effectiveDate)
//                    .build());
//
//            if (result.getIsSuccess().equals(Boolean.FALSE)) {
//                log.error("Error Updating employee in smr client: {}", result);
//                response.setOperationError(FAIL_CODE_EXTERNAL, ERROR, null);
//                return response;
//            }

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
