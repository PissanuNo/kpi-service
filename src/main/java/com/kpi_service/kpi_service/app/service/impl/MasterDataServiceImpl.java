package com.kpi_service.kpi_service.app.service.impl;


import com.kpi_service.kpi_service.app.model.dbs.MasterDataModel;
import com.kpi_service.kpi_service.app.model.dto.masterdata.MasterDataRequest;
import com.kpi_service.kpi_service.app.model.dto.masterdata.MasterDataResponse;
import com.kpi_service.kpi_service.app.repositories.MasterDataRepository;
import com.kpi_service.kpi_service.app.service.MasterDataService;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.kpi_service.kpi_service.app.constant.Constants.ResponseCode.*;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseMessage.*;


@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    Logger log = LoggerFactory.getLogger(MasterDataServiceImpl.class);
    private final MasterDataRepository masterDataRepository;

    @Transactional
    @Override
    public ResponseBodyModel<String> createMasterData(MasterDataRequest request) {
        ResponseBodyModel<String> response = new ResponseBodyModel<>();
        try {
            masterDataRepository.saveAndFlush(MasterDataModel.builder()
                    .dataId(UUID.randomUUID().toString())
                    .dataDetailEn(request.getDataDetailEn())
                    .dataDetailTh(request.getDataDetailTh())
                    .dataType(request.getDataType())
                    .sortOrder(request.getSortOrder())
                    .remark(request.getRemark())
                    .build());

            response.setOperationSuccess(SUCCESS_CODE, SUCCESS, null);
        } catch (Exception ex) {
            log.error("Error creating master data {}", ex.getMessage());
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

    @Transactional
    @Override
    public ResponseBodyModel<String> updateMasterData(MasterDataRequest request) {
        ResponseBodyModel<String> response = new ResponseBodyModel<>();
        try {
           Optional<MasterDataModel> masterDataModel = masterDataRepository.findById(request.getDataId());
           if (masterDataModel.isPresent()) {
               masterDataModel.get().setDataDetailEn(request.getDataDetailEn());
               masterDataModel.get().setDataDetailTh(request.getDataDetailTh());
               masterDataModel.get().setDataType(request.getDataType());
               masterDataModel.get().setSortOrder(request.getSortOrder());
               masterDataModel.get().setRemark(request.getRemark());
               masterDataRepository.saveAndFlush(masterDataModel.get());
               response.setOperationSuccess(SUCCESS_CODE, SUCCESS, null);
           }else {
              response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
           }

        } catch (Exception ex) {
            log.error("Error updating master data {}", ex.getMessage());
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }

    @Transactional
    @Override
    public ResponseBodyModel<String> deleteMasterData(String dataId) {
        ResponseBodyModel<String> response = new ResponseBodyModel<>();
        try {
           Optional<MasterDataModel> masterDataModel = masterDataRepository.findById(dataId);
           if (masterDataModel.isPresent()) {
               masterDataRepository.deleteById(dataId);
               response.setOperationSuccess(SUCCESS_CODE, SUCCESS, null);
           }else {
              response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
           }

        } catch (Exception ex) {
            log.error("Error deleting master data {}", ex.getMessage());
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }


    @Transactional
    @Override
    public ResponseBodyModel<MasterDataResponse> getMasterDataById(String dataId) {
        ResponseBodyModel<MasterDataResponse> response = new ResponseBodyModel<>();
        try {
           Optional<MasterDataModel> masterDataModel = masterDataRepository.findById(dataId);
           if (masterDataModel.isPresent()) {

               response.setOperationSuccess(SUCCESS_CODE, SUCCESS, MasterDataResponse
                       .builder()
                               .dataId(masterDataModel.get().getDataId())
                               .dataDetailEn(masterDataModel.get().getDataDetailEn())
                               .dataDetailTh(masterDataModel.get().getDataDetailTh())
                               .dataType(masterDataModel.get().getDataType())
                               .sortOrder(masterDataModel.get().getSortOrder())
                               .remark(masterDataModel.get().getRemark())
                       .build()
               );
           }else {
              response.setOperationError(ERROR_CODE_DATA_NOT_FOUND, DATA_NOT_FOUND, null);
           }

        } catch (Exception ex) {
            log.error("Error getting master data by id {}", ex.getMessage());
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }



    @Override
    public ResponseBodyModel<List<MasterDataModel>> getAllMasterData(String dataDetail, String dataType) {
        ResponseBodyModel<List<MasterDataModel>> response = new ResponseBodyModel<>();
        try {
            Sort sortOrder = Sort.by(Sort.Order.asc("dataType"),
                    Sort.Order.asc("sortOrder"));

            List<MasterDataModel> masterDataList = masterDataRepository
                    .findByDataTypeAndDataDetailEnContainingIgnoreCaseOrDataDetailThContainingIgnoreCase(
                            dataType,
                            dataDetail,
                            dataDetail,
                            sortOrder
                    );

            response.setOperationSuccess(SUCCESS_CODE, SUCCESS, masterDataList);
        } catch (Exception ex) {
            log.error("Error getting all master data {}", ex.getMessage());
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }
}
