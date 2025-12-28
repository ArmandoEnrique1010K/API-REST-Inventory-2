package com.pe.inventoryapp.backend.movement.model.data;

public enum MovementType {
  SEND,
  ADJUSTMENT_QUANTITY_AVAILABLE,
  ADJUSTMENT_QUANTITY_RECEIVED,
  TRANSFER,
  LOSS,
  SALE,
  ALLOCATE,
  RETURN_BY_DAMAGE, // Retorno por daño de producto
  RETURN_BY_CHANGE, // Retorno por cambio de orden de entrega
}
