package com.kpi_service.kpi_service.app.service.impl;


import com.kpi_service.kpi_service.app.model.dbs.MasterDataModel;
import com.kpi_service.kpi_service.app.repositories.MasterDataRepository;
import com.kpi_service.kpi_service.app.service.MasterDataService;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kpi_service.kpi_service.app.constant.Constants.ResponseCode.INTERNAL_SERVER_ERROR;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseCode.SUCCESS_CODE;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseMessage.INTERNAL_SERVER_ERROR_MSG;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseMessage.SUCCESS;


@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    Logger log = LoggerFactory.getLogger(MasterDataServiceImpl.class);
    private final MasterDataRepository masterDataRepository;

    @Override
    public ResponseBodyModel<List<MasterDataModel>> getMasterData(String dataDetail, String dataType) {
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
            log.error("Error getting master data", ex);
            response.setOperationError(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, null);
        }
        return response;
    }
}
