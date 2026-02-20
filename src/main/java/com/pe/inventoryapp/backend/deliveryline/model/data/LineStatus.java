package com.pe.inventoryapp.backend.deliveryline.model.data;

public enum LineStatus {
  LINE_MISSING, // Perdido
  LINE_READY,  // Listo
  LINE_PENDING, // Pendiente
  LINE_DELIVERED, // Entregado
  LINE_CANCELED, // Cancelado
  LINE_EXCEEDED // Excedente
}
