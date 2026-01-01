package com.pe.inventoryapp.backend.movement.model.data;

public enum MovementType {
  SEND, // TODO: ELIMINAR ESTE DATO
  RECEIVE, // Envio de stock al almacen
  ADD, // Ajuste manual de la cantidad de stock
  LOSS, // Perdida de stock
  RECOVERY, // Recuperación de stock dañado del almacen
  

  TRANSFER, // Transferencia entre stocks del mismo producto
  SALE, // TODO: ELIMINAR ESTE DATO
  ALLOCATE, // Preparar entrega
  RETURN_BY_DAMAGE, // Retorno por daño de producto
  RETURN_BY_CHANGE, // Retorno por cambio de orden de entrega
}
