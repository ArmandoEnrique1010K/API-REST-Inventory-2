package com.pe.inventoryapp.backend.deliveryorder.model.data;

public enum OrderStatus {
  ORDER_READY,  // Listo
  ORDER_PENDING, // Pendiente
  ORDER_DELIVERED, // Entregado
  ORDER_CANCELED, // Cancelado
  ORDER_PARTIALLY_DELIVERED // Parcialmente cancelado
}
