package com.pe.inventoryapp.backend.deliveryline.model.data;

public enum LineStatus {
  MISSING, // Perdido
  READY,  // Listo
  PENDING, // Pendiente
  DELIVERED, // Entregado
  CANCELED, // Cancelado
  EXCESS // Excedente
}
