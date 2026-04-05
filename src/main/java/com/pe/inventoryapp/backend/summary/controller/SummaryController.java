package com.pe.inventoryapp.backend.summary.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.summary.model.response.RegionSummaryDTO;
import com.pe.inventoryapp.backend.summary.service.SummaryService;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {
  private final SummaryService summaryService;
  private final ResponseService responseService;

  public SummaryController(
      SummaryService summaryService,
      ResponseService responseService) {
    this.summaryService = summaryService;
    this.responseService = responseService;
  }

  @GetMapping("/{deliveryOrderId}")
  public ResponseEntity<?> getSummaryByDeliveryOrder(@PathVariable Long deliveryOrderId) {
    List<RegionSummaryDTO> regionWithSubregionsDTO = summaryService.buildSummary(deliveryOrderId);

    DataResponse<List<RegionSummaryDTO>> dataResponse = responseService.generateDataResponse(
        ResponseStatus.SUCCESS,
        regionWithSubregionsDTO);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);

  }

}
