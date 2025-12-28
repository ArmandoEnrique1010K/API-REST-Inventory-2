package com.pe.inventoryapp.backend.movement.service;

import com.pe.inventoryapp.backend.movement.model.request.MovementAdjustmentRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementAllocateRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementLossRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReturnRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementSendRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementTransferRequest;

public interface MovementService {

  // Movimiento hacia una linea de entrega (cuando sale del almacen)
  void saveMovementSend(MovementSendRequest movementSendRequest, Long id_user);
  void saveMovementAdjustment(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user);
  void saveMovementTransfer(MovementTransferRequest movementTransferRequest, Long id_user);

  void saveMovementLoss(MovementLossRequest movementLossRequest, Long id_user);
  // void saveMovementSale(MovementLossRequest movementExitRequest, Long id_user);

  void saveMovementAllocate(MovementAllocateRequest movementAllocateRequest, Long id_user);

  void saveMovementReturn(MovementReturnRequest movementReturnRequest, Long id_user);

}
