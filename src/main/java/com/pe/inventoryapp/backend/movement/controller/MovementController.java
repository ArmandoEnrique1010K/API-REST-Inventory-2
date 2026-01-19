package com.pe.inventoryapp.backend.movement.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.response.MovementDetailsResponse;
import com.pe.inventoryapp.backend.movement.model.response.MovementListResponse;
import com.pe.inventoryapp.backend.movement.service.MovementService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/movements")
public class MovementController {
  @Autowired
  private MovementService movementService;

  @Autowired
  private ResponseService responseService;

  @GetMapping
  public ResponseEntity<?> listAllMovements(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) Integer minQuantity,
      @RequestParam(required = false) Integer maxQuantity,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime minCreatedAt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maxCreatedAt,
        @RequestParam(required = false) MovementType movementType,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String productName
    ) {
    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<MovementListResponse> movements = movementService.findAllMovements(minQuantity, maxQuantity, minCreatedAt, maxCreatedAt, movementType, username, productName, pageable); 

    DataResponse<PageResponse<MovementListResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS, movements);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }


  @GetMapping("/delivery-line/{deliveryLineId}")
  public ResponseEntity<?> listAllMovementsByDeliveryLine(
      @PathVariable Long deliveryLineId,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) Integer minQuantity,
      @RequestParam(required = false) Integer maxQuantity,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime minCreatedAt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maxCreatedAt,
      @RequestParam(required = false) MovementType movementType,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String productName) {
    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<MovementListResponse> movements = movementService.findAllMovementsByDeliveryLine(deliveryLineId, minQuantity, maxQuantity,
        minCreatedAt, maxCreatedAt, movementType, username, productName, pageable);

    DataResponse<PageResponse<MovementListResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, movements);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/stock-lot/{stockLotId}")
  public ResponseEntity<?> listAllMovementsByStockLot(
      @PathVariable Long stockLotId,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) Integer minQuantity,
      @RequestParam(required = false) Integer maxQuantity,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime minCreatedAt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maxCreatedAt,
      @RequestParam(required = false) MovementType movementType,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String productName) {
    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<MovementListResponse> movements = movementService.findAllMovementsByDeliveryLine(stockLotId,
        minQuantity, maxQuantity,
        minCreatedAt, maxCreatedAt, movementType, username, productName, pageable);

    DataResponse<PageResponse<MovementListResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, movements);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/product/{productId}")
  public ResponseEntity<?> listAllMovementsByProduct(
      @PathVariable Long productId,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) Integer minQuantity,
      @RequestParam(required = false) Integer maxQuantity,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime minCreatedAt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maxCreatedAt,
      @RequestParam(required = false) MovementType movementType,
      @RequestParam(required = false) String username) {
    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<MovementListResponse> movements = movementService.findAllMovementsByProduct(
        productId,
        minQuantity, maxQuantity,
        minCreatedAt, maxCreatedAt, movementType, username, pageable);

    DataResponse<PageResponse<MovementListResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, movements);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }


  @GetMapping("/user/{userId}")
  public ResponseEntity<?> listAllMovementsByUser(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) Integer minQuantity,
      @RequestParam(required = false) Integer maxQuantity,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime minCreatedAt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maxCreatedAt,
      @RequestParam(required = false) MovementType movementType,
      @RequestParam(required = false) String productName) {
    Pageable pageable = PageRequest.of(page, 20);

    PageResponse<MovementListResponse> movements = movementService.findAllMovementsByUser(
        userId, minQuantity, maxQuantity, minCreatedAt, maxCreatedAt, movementType, productName,  pageable);

    DataResponse<PageResponse<MovementListResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, movements);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getMovement(@PathVariable Long id) {
    MovementDetailsResponse movementDetailsResponse = movementService.findMovementById(id);
    DataResponse<MovementDetailsResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS, 
        movementDetailsResponse);
    return ResponseEntity.status(response.status()).body(response);
  }
  
}
