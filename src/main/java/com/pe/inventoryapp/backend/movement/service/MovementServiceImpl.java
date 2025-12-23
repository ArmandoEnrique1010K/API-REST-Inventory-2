package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.data.Reason;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.request.MovementRequest;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.stock.model.entity.StockLot;
import com.pe.inventoryapp.backend.stock.repository.StockLotRepository;

// TODO: AUTOMATIZAR EL MOVIMIENTO
@Service
public class MovementServiceImpl implements MovementService {

  @Autowired
  private DeliveryLineRepository deliveryLineRepository;

  @Autowired
  private MovementRepository movementRepository;

  @Autowired
  private StockLotRepository stockLotRepository;

  // MOVIMIENTO DE DELIVERY
  @Override
  public void movementToDeliveryLine(MovementRequest movementRequest) {

    // Obtener la linea de entrega por id
    DeliveryLine deliveryLine = deliveryLineRepository.findById(movementRequest.getIdDeliveryLine())
        .orElseThrow(() -> new RuntimeException("DeliveryLine no existe"));

    StockLot stockLot = stockLotRepository.findById(movementRequest.getIdStockLot())
        .orElseThrow(() -> new RuntimeException("StockLot no existe"));

    MovementType movementType = movementRequest.getMovementType();
    Reason reason = movementRequest.getReason();
    Long idStockLot = movementRequest.getIdStockLot();
    Long idDeliveryLine = movementRequest.getIdDeliveryLine();

    Movement movement = new Movement();
    movement.setQuantity(movementRequest.getQuantity());
    movement.setComment(movementRequest.getComment());
    movement.setMovementType(movementRequest.getMovementType());
    movement.setReason(movementRequest.getReason());
    movement.setDeliveryLine(deliveryLine);
    movement.setReason(reason);
    movement.setMovementType(movementType);

    // TODO: CORREGIR ESTA LINEA (LLAMAR A LA ENTIDAD POR ID)
    movement.setStockLot(stockLot);

    // AQUI SE HACEN LAS MODIFICACIONES RESPECTIVAS
    // SALIDA
    if (movementType.equals(MovementType.OUT) && reason.equals(Reason.DELIVERY)) {
      // Cantidad entregada
      deliveryLine.setDeliveredQuantity(movementRequest.getQuantity());
      // Cantidad pendiente
      deliveryLine.setPendingQuantity(deliveryLine.getRequiredQuantity() - deliveryLine.getDeliveredQuantity());
      deliveryLine.setUpdatedAt(LocalDateTime.now());

      // Si la cantidad entregada es igual a la cantidad requerida
      if (deliveryLine.getPendingQuantity() == 0) {
        deliveryLine.setPreparationStatus(PreparationStatus.READY);
      }

    }

    movementRepository.save(movement);

  }

  @Override
  public void movementToStockLot(MovementRequest movementRequest) {
  }

}
