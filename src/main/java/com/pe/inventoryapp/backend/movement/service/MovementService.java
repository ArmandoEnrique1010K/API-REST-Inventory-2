package com.pe.inventoryapp.backend.movement.service;

import com.pe.inventoryapp.backend.movement.model.request.MovementAdjustmentRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementAllocateRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReturnRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReceiveRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementTransferRequest;

public interface MovementService {

  // Movimiento hacia una linea de entrega (cuando sale del almacen)
  void saveMovementReceive(MovementReceiveRequest movementReceiveRequest, Long id_user);
  void saveMovementAdjustmentIncrease(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user);
  void saveMovementAdjustmentLoss(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user);
  void saveMovementAdjustmentRecovery(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user);

  // void saveMovementAdjustment(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user);
  void saveMovementTransfer(MovementTransferRequest movementTransferRequest, Long id_user);

  // void saveMovementLoss(MovementLossRequest movementLossRequest, Long id_user);

  void saveMovementAllocate(MovementAllocateRequest movementAllocateRequest, Long id_user);

  void saveMovementReturn(MovementReturnRequest movementReturnRequest, Long id_user);

}
