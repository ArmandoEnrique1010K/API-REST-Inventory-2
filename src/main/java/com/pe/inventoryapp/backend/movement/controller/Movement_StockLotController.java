package com.pe.inventoryapp.backend.movement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.movement.model.response.Movement_StockLotResponse;
import com.pe.inventoryapp.backend.movement.service.Movement_StockLotService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/movement-stocklots")
public class Movement_StockLotController {

  private final Movement_StockLotService movement_StockLotService;
  private final ResponseService responseService;

  public Movement_StockLotController(
      Movement_StockLotService movement_StockLotService,
      ResponseService responseService) {
    this.movement_StockLotService = movement_StockLotService;
    this.responseService = responseService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> listAllMovement_StockLotsByMovement(@PathVariable Long id) {
    List<Movement_StockLotResponse> movement_StockLots = movement_StockLotService.findAllByMovementId(id);
    DataResponse<List<Movement_StockLotResponse>> dataResponse = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        movement_StockLots);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

}
