package com.pe.inventoryapp.backend.movement.model.data;

public enum MovementType {
  // Relacionados a lotes de stock
  MOVEMENT_STOCK_RECEIVE, // Recibir un lote
  MOVEMENT_STOCK_INCREASE, // Agregar cantidad a un lote
  MOVEMENT_STOCK_DECREASE, // Quitar cantidad de un lote y repotarlo como perdida
  MOVEMENT_STOCK_RECOVERY, // Recuperar cantidad de un lote perdido
  MOVEMENT_STOCK_TRANSFER, // Transferir cantidad entre 2 lotes de stocks del mismo producto
  MOVEMENT_STOCK_REFUND, // Reembolso de productos que estaban listo en una orden de entrega cancelada

  // Relacionados a la linea de entrega
  MOVEMENT_LINE_ALLOCATE, // Entregar parte de un lote de stock a una linea de entrega
  MOVEMENT_LINE_ALTER, // Alterar la cantidad requerida de una linea de entrega
  MOVEMENT_LINE_RETURN, // Devolver una linea de entrega que fue entregada
  MOVEMENT_LINE_CHANGE, // Modificar la cantidad de una linea de entrega que fue reportada como READY
  MOVEMENT_LINE_CANCELED, // Cancelar una linea de entrega
  MOVEMENT_LINE_LOST, // Marcar una linea de entrega como perdida (con la posibilidad de volver a
           // entregarlo)

  MOVEMENT_LINE_MISSING, // Marcar una linea de entrega como entregada, pero perdida durante la entrega o cuando se reporta que esta mal (sin la posibilidad de volver a
           // entregarlo)
  MOVEMENT_LINE_DELIVERED, // Marcar una linea de entrega como entregada

  // TODO: EN UNA FUTURA ACTUALIZACION SE PODRIA IMPLEMENTAR EL MOVIMIENTO MOVEMENT_LINE_SIMULTANEOUS
  // Relacionados a un lote de stock y linea de entrega a la vez
  MOVEMENT_LINE_SIMULTANEOUS // Crea un lote de stock y asigna la cantidad total de ese lote a un linea de
               // entrega
  // Si el movimiento MOVEMENT_LINE_SIMULTANEOUS se hace más de una vez antes de que pase 24
  // horas, se podra incrementar la cantidad total y seguir entregando de esa
  // cantidad
}
