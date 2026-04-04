package com.pe.inventoryapp.backend.deliveryorder.model.data;

public enum OrderStatus {
  ORDER_READY,
  ORDER_PENDING,
  ORDER_DELIVERED,
  ORDER_CANCELED,
  ORDER_PARTIALLY_DELIVERED;

  // ORDER_READY("Listo"),  // Listo
  // ORDER_PENDING("Pendiente"), // Pendiente
  // ORDER_DELIVERED("Entregado"), // Entregado
  // ORDER_CANCELED("Cancelado"), // Cancelado
  // ORDER_PARTIALLY_DELIVERED("Parcialmente entregado"); // Parcialmente cancelado

  // private final String displayName;

  //  OrderStatus(String displayName){
  //   this.displayName = displayName;
  // }

  // public String getDisplayName() {
  //   return displayName;
  // }
}
