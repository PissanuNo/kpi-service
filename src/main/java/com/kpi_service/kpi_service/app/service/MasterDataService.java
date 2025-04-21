package com.kpi_service.kpi_service.app.service;


import com.kpi_service.kpi_service.app.model.dbs.MasterDataModel;
import com.kpi_service.kpi_service.app.model.dto.masterdata.MasterDataRequest;
import com.kpi_service.kpi_service.app.model.dto.masterdata.MasterDataResponse;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.transaction.Transactional;

import java.util.List;

public interface MasterDataService {

    @Transactional
    ResponseBodyModel<String> createMasterData(MasterDataRequest request);

    @Transactional
    ResponseBodyModel<String> updateMasterData(MasterDataRequest request);

    @Transactional
    ResponseBodyModel<String> deleteMasterData(String dataId);

    @Transactional
    ResponseBodyModel<MasterDataResponse> getMasterDataById(String dataId);

    ResponseBodyModel<List<MasterDataModel>> getAllMasterData(String dataDetail, String dataType);
}
