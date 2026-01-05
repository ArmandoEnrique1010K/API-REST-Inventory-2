package com.pe.inventoryapp.backend.movement.model.data;

public enum MovementType {
  // Relacionados a lotes de stock
  RECEIVE, // Recibir un lote
  ADD, // Agregar cantidad a un lote
  LOSS, // Quitar cantidad de un lote y repotarlo como perdida
  RECOVERY, // Recuperar cantidad de un lote perdido
  TRANSFER, // Transferir cantidad entre 2 lotes de stocks del mismo producto

  // Relacionados a la linea de entrega
  ALLOCATE, // Entregar parte de un lote de stock a una linea de entrega
  ALTER, // Alterar la cantidad requerida de una linea de entrega
  RETURN, // Devolver una linea de entrega que fue entregada
  CHANGE, // Modificar la cantidad de una linea de entrega que fue reportada como READY
  CANCELED, // Cancelar una linea de entrega
  MISSING, // Marcar una linea de entrega como perdida (con la posibilidad de volver a
           // entregarlo)

  // Relacionados a un lote de stock y linea de entrega a la vez
  SIMULTANEOUS // Crea un lote de stock y asigna la cantidad total de ese lote a un linea de
               // entrega
  // Si el movimiento SIMULTANEOUS se hace más de una vez antes de que pase 24
  // horas, se podra incrementar la cantidad total y seguir entregando de esa
  // cantidad
}
