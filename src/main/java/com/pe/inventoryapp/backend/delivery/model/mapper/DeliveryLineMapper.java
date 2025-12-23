package com.pe.inventoryapp.backend.delivery.model.mapper;

import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineResponse;

public class DeliveryLineMapper {
  private DeliveryLine deliveryLine;

  private DeliveryLineMapper() {
  }

  public static DeliveryLineMapper builder() {
    return new DeliveryLineMapper();
  }

  public DeliveryLineMapper setDeliveryLine(DeliveryLine deliveryLine) {
    this.deliveryLine = deliveryLine;
    return this;
  }

  public DeliveryLineResponse buildDeliveryLineResponse() {
    if (deliveryLine == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryLine");
    } else {
      return new DeliveryLineResponse(
          deliveryLine.getId(),
          deliveryLine.getRequiredQuantity(),
          deliveryLine.getDeliveredQuantity(),
          // deliveryLine.getPendingQuantity(),
          // deliveryLine.getUpdatedAt(),
          deliveryLine.getPreparationStatus(),
          deliveryLine.getLocation().getName(),
          deliveryLine.getLocation().getRegion().getName());
    }
  }
}
