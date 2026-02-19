package com.pe.inventoryapp.backend.stocklot.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotAdjustmentRequest;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotReceiveRequest;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotTransferRequest;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotDetailsResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotListResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotSameProductListResponse;
import com.pe.inventoryapp.backend.stocklot.service.StockLotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stock-lots")
public class StockLotController {
  private final StockLotService stockLotService;
  private final ValidationService validationService;
  private final ResponseService responseService;
  private final AuthenticationContextService authenticationContextService;

  public StockLotController(
      StockLotService stockLotService,
      ValidationService validationService,
      ResponseService responseService,
      AuthenticationContextService authenticationContextService) {
    this.stockLotService = stockLotService;
    this.validationService = validationService;
    this.responseService = responseService;
    this.authenticationContextService = authenticationContextService;
  }

  @PostMapping
  public ResponseEntity<CommonResponse> registerStockLot(Authentication authentication,
      @Valid @RequestBody StockLotReceiveRequest stockLotReceiveRequest,
      BindingResult result) {
    Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    stockLotService.saveStockLot(stockLotReceiveRequest, id);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro el lote de stock");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping
  public ResponseEntity<?> listAllStockLots(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) Integer minQuantityReceived,
      @RequestParam(required = false) Integer maxQuantityReceived,
      @RequestParam(required = false) Integer minQuantityAvailable,
      @RequestParam(required = false) Integer maxQuantityAvailable,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime minCreatedAt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maxCreatedAt,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long companyId,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long typeId,
      @RequestParam(required = false) Long modelId) {
    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<StockLotListResponse> products = stockLotService.searchAllStockLotsByParams(
        minQuantityReceived,
        maxQuantityReceived,
        minQuantityAvailable,
        maxQuantityAvailable, minCreatedAt, maxCreatedAt, keyword, companyId, categoryId, typeId, modelId, pageable);

    DataResponse<PageResponse<StockLotListResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, products);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/exclude/{idStocklot}/company/{idCompany}/model/{idModel}")
  public ResponseEntity<?> listAllStockLotsByModelAndExcludeOne(
      @PathVariable Long idStocklot,
      @PathVariable Long idModel,
      @PathVariable Long idCompany) {
    List<StockLotSameProductListResponse> stockLots = stockLotService.findAllStockLotsExceptOneStockLotByModelId(
        idModel,
        idCompany,
        idStocklot);
    DataResponse<List<StockLotSameProductListResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, stockLots);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getStockLot(@PathVariable Long id) {
    StockLotDetailsResponse stockLotDetailsResponse = stockLotService.findStockLotById(id);
    DataResponse<StockLotDetailsResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS,
        stockLotDetailsResponse);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping("/{id}/increase")
  public ResponseEntity<CommonResponse> increaseStockLot(Authentication authentication, @PathVariable Long id,
      @Valid @RequestBody StockLotAdjustmentRequest stockLotAdjustmentRequest,
      BindingResult result) {
    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    stockLotService.increaseStockLot(id, stockLotAdjustmentRequest, id_user);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se aumento la cantidad del lote de stock");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping("/{id}/decrease")
  public ResponseEntity<CommonResponse> decreaseStockLot(Authentication authentication, @PathVariable Long id,
      @Valid @RequestBody StockLotAdjustmentRequest stockLotAdjustmentRequest,
      BindingResult result) {
    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    stockLotService.decreaseStockLot(id, stockLotAdjustmentRequest, id_user);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se reporto una perdida en la cantidad del lote de stock");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping("/{id}/recovery")
  public ResponseEntity<CommonResponse> recoveryStockLot(Authentication authentication, @PathVariable Long id,
      @Valid @RequestBody StockLotAdjustmentRequest stockLotAdjustmentRequest,
      BindingResult result) {
    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    stockLotService.recoveryStockLot(id, stockLotAdjustmentRequest, id_user);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se recupero una parte de la cantidad dañada del lote de stock");
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping("{idEmitter}/transfer")
  public ResponseEntity<CommonResponse> transferStockLot(Authentication authentication, @PathVariable Long idEmitter,
      @Valid @RequestBody StockLotTransferRequest stockLotTransferRequest,
      BindingResult result) {
    Long id_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    stockLotService.transferStockLot(idEmitter, stockLotTransferRequest, id_user);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se hizo una transferencia entre 2 lotes de stocks del mismo modelo del producto");
    return ResponseEntity.status(response.status()).body(response);
  }

}
