package com.pe.inventoryapp.backend.deliveryline.model.mapper;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineListResponse;

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
          deliveryLine.getLineStatus(),
          deliveryLine.getModel().getId(),
          deliveryLine.getModel().getProduct().getName() + " " + deliveryLine.getModel().getName(),
          deliveryLine.getLocation().getId(),
          deliveryLine.getLocation().getName(),
          deliveryLine.getLocation().getSubregion().getId(),
          deliveryLine.getLocation().getSubregion().getName(),
          deliveryLine.getLocation().getSubregion().getRegion().getId(),
          deliveryLine.getLocation().getSubregion().getRegion().getName()

      );
    }
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
          deliveryLine.getLimitDate(),
          deliveryLine.getUpdatedAt(),
          deliveryLine.getLineStatus(),

          deliveryLine.getUserUpdater().getFirstname() + " " + deliveryLine.getUserUpdater().getLastname(),

          deliveryLine.getLocation().getId(),
          deliveryLine.getLocation().getName(),

          deliveryLine.getLocation().getSubregion().getId(),
          deliveryLine.getLocation().getSubregion().getName(),

          deliveryLine.getLocation().getSubregion().getRegion().getId(),
          deliveryLine.getLocation().getSubregion().getRegion().getName(),

          deliveryLine.getModel().getId(),
          deliveryLine.getModel().getName(),
          deliveryLine.getModel().getImageUrl(),
          
          deliveryLine.getModel().getProduct().getId(),
          deliveryLine.getModel().getProduct().getName(),

          deliveryLine.getModel().getProduct().getCategory().getId(),
          deliveryLine.getModel().getProduct().getCategory().getName(),

          deliveryLine.getModel().getProduct().getType().getId(),
          deliveryLine.getModel().getProduct().getType().getName(),

          deliveryLine.getModel_DeliveryOrder().getDeliveryOrder().getId(),
          deliveryLine.getModel_DeliveryOrder().getDeliveryOrder().getBatch(),
          deliveryLine.getModel_DeliveryOrder().getDeliveryOrder().getLimitDate()
          );
    }
  }

}
