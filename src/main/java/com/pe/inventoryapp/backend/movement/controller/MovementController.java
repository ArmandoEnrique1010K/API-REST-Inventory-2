package com.pe.inventoryapp.backend.movement.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.request.MovementAllocateRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReturnRequest;
import com.pe.inventoryapp.backend.movement.model.response.MovementListResponse;
import com.pe.inventoryapp.backend.movement.service.MovementService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/movements")
public class MovementController {
  @Autowired
  private AuthenticationContextService authenticationContextService;

  @Autowired
  private MovementService movementService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private ResponseService responseService;

  @PostMapping("/allocate")
  public ResponseEntity<CommonResponse> allocateDeliveryLine(Authentication authentication,
      @Valid @RequestBody MovementAllocateRequest movementAllocateRequest,
      BindingResult result) {
    Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    movementService.saveMovementAllocate(movementAllocateRequest, id);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatus.SUCCESS,
            "Se ha preparado una linea de entrega"));
  }

  @PostMapping("/return")
  public ResponseEntity<CommonResponse> returnDeliveryLine(Authentication authentication,
          @Valid @RequestBody MovementReturnRequest movementReturnRequest,
          BindingResult result) {
      Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
      validationService.validateFieldsAndThrowResponse(result);
      movementService.saveMovementReturn(movementReturnRequest, id);

      return ResponseEntity.status(201)
              .body(responseService.generateCommonResponse("success", ResponseStatus.SUCCESS,
                      "Se ha devuelto una linea de entrega"));
  }
  

  // TODO: SEPARAR ESTE CONTROLADOR EN 2 CONTROLADORES: UNO PARA MOVIMIENTOS DE LINEAS DE ENTREGA Y OTRO PARA MOVIMIENTOS SIMULTANEOS
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

    Page<MovementListResponse> movements = movementService.findAllMovements(minQuantity, maxQuantity, minCreatedAt, maxCreatedAt, movementType, username, productName, pageable);

    return ResponseEntity.status(200).body(movements);
  }
  

}
