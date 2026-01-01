package com.pe.inventoryapp.backend.deliveryorder.model.mapper;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;

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

  public DeliveryOrderListResponse buildDeliveryOrderListResponse() {
    if (deliveryOrder == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryOrder");
    } else {
      return new DeliveryOrderListResponse(
          deliveryOrder.getId(),
          deliveryOrder.getBatch(),
          deliveryOrder.getLimitDate(),
          deliveryOrder.getCreatedByUser(),
          deliveryOrder.getPreparationStatus());
    }
  }

  public DeliveryOrderDetailsResponse buildDeliveryOrderDetailsResponse() {
    if (deliveryOrder == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryOrder");
    } else {

      // En el caso de que no se guarde el valor del enum en la base de datos
      String status = deliveryOrder.getPreparationStatus() != null
          ? deliveryOrder.getPreparationStatus().name()
          : "UNKNOWN";

      return new DeliveryOrderDetailsResponse(
          deliveryOrder.getId(),
          deliveryOrder.getBatch(),
          deliveryOrder.getLimitDate(),
          deliveryOrder.getCreatedByUser(),
          deliveryOrder.getUpdatedByUser(),
          deliveryOrder.getCreatedAt(),
          deliveryOrder.getUpdatedAt(),
          status);
    }

  }
}
