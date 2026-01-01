package com.pe.inventoryapp.backend.movement.model.data;

public enum MovementType {
  
  RECEIVE, // Envio de stock al almacen
  ADD, // Ajuste manual de la cantidad de stock
  RECOVERY, // Recuperación de stock dañado del almacen
  LOSS, // Perdida de stock

  // ADJUSTMENT_QUANTITY_AVAILABLE,
  // ADJUSTMENT_QUANTITY_RECEIVED,
  TRANSFER,
  SALE,
  ALLOCATE,
  RETURN_BY_DAMAGE, // Retorno por daño de producto
  RETURN_BY_CHANGE, // Retorno por cambio de orden de entrega
}
