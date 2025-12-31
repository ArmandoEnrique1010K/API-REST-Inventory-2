package com.pe.inventoryapp.backend.delivery.model.mapper;

import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineListResponse;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;

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

  public DeliveryLineDetailsResponse buildDeliveryLineDetailsResponse() {
    if (deliveryLine == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryLine");
    } else {
      return new DeliveryLineDetailsResponse(
          deliveryLine.getId(),
          deliveryLine.getRequiredQuantity(),
          deliveryLine.getDeliveredQuantity(),
          deliveryLine.getPendingQuantity(),
          deliveryLine.getUpdatedAt(),
          deliveryLine.getLimitDate(),
          deliveryLine.getUpdatedByUser(),
          deliveryLine.getPreparationStatus(),
          deliveryLine.getLocation().getName(),
          deliveryLine.getLocation().getRegion().getName());
    }
  }

  public DeliveryLineListResponse buildDeliveryLineListResponse() {
    if (deliveryLine == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryLine");
    } else {
      return new DeliveryLineListResponse(
          deliveryLine.getId(),
          deliveryLine.getRequiredQuantity(),
          deliveryLine.getDeliveredQuantity(),
          deliveryLine.getPendingQuantity(),
          deliveryLine.getLimitDate(),
          deliveryLine.getPreparationStatus(),
          deliveryLine.getLocation().getName(),
          deliveryLine.getLocation().getRegion().getName());
    }
  }

}
