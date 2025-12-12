package com.pe.inventoryapp.backend.delivery.model.mapper;

import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderResponse;

public class DeliveryOrderMapper {
  private DeliveryOrder deliveryOrder;

  private DeliveryOrderMapper() {
  }

  public static DeliveryOrderMapper builder() {
    return new DeliveryOrderMapper();
  }

  public DeliveryOrderMapper setDeliveryOrder(DeliveryOrder deliveryOrder) {
    this.deliveryOrder = deliveryOrder;
    return this;
  }

  public DeliveryOrderResponse buildDeliveryOrderResponse() {
    if (deliveryOrder == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryOrder");
    } else {

      // En el caso de que no se guarde el valor del enum en la base de datos
      String status = deliveryOrder.getPreparationStatus() != null
          ? deliveryOrder.getPreparationStatus().name()
          : "UNKNOWN";

      return new DeliveryOrderResponse(
          deliveryOrder.getId(),
          deliveryOrder.getBatch(),
          deliveryOrder.getDeliveredDate(),
          deliveryOrder.getCreatedAt().toString(),
          deliveryOrder.getQuantityTotal(),
          status);
    }

  }
}
