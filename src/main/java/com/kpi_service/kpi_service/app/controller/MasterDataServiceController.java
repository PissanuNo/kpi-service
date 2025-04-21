package com.kpi_service.kpi_service.app.controller;

import com.kpi_service.kpi_service.app.model.dbs.MasterDataModel;
import com.kpi_service.kpi_service.app.model.dto.masterdata.MasterDataRequest;
import com.kpi_service.kpi_service.app.model.dto.masterdata.MasterDataResponse;
import com.kpi_service.kpi_service.app.service.MasterDataService;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1")
public class MasterDataServiceController {

    private final MasterDataService masterDataService;

    @GetMapping(path = "/master-data",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<List<MasterDataModel>>> getAllMasterData(@RequestParam("dataDetail") String dataDetail,
                                                                                     @RequestParam("dataType") String dataType) {
        ResponseBodyModel<List<MasterDataModel>> response = masterDataService.getAllMasterData(dataDetail, dataType);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/master-data/{dataId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<MasterDataResponse>> getMasterDataById(@PathVariable("dataId") String dataId) {
        ResponseBodyModel<MasterDataResponse> response = masterDataService.getMasterDataById(dataId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/master-data",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<String>> createMasterData(@Valid @RequestBody MasterDataRequest request) {
        ResponseBodyModel<String> response = masterDataService.createMasterData(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/master-data",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<String>> updateMasterData(@Valid @RequestBody MasterDataRequest request) {
        ResponseBodyModel<String> response = masterDataService.updateMasterData(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/master-data/{dataId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<String>> deleteMasterDataById(@PathVariable("dataId") String dataId) {
        ResponseBodyModel<String> response = masterDataService.deleteMasterData(dataId);
        return ResponseEntity.ok(response);
    }


}
