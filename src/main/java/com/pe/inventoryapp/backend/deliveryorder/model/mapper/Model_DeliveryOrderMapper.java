package com.pe.inventoryapp.backend.deliveryorder.model.mapper;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.response.Model_DeliveryOrderResponse;

public class Model_DeliveryOrderMapper {
    private Model_DeliveryOrder model_DeliveryOrder;

  private Model_DeliveryOrderMapper() {
  }

  public static Model_DeliveryOrderMapper builder() {
    return new Model_DeliveryOrderMapper();
  }

  public Model_DeliveryOrderMapper setModel_DeliveryOrder(Model_DeliveryOrder model_DeliveryOrder) {
    this.model_DeliveryOrder = model_DeliveryOrder;
    return this;
  }

  public Model_DeliveryOrderResponse buildModel_DeliveryOrderListResponse() {
    if (model_DeliveryOrder == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryOrder");
    } else {
      return new Model_DeliveryOrderResponse(
        model_DeliveryOrder.getId(),
        model_DeliveryOrder.getRequiredQuantityTotal(),
        model_DeliveryOrder.getModel().getId(),
        model_DeliveryOrder.getModel().getName(),
        model_DeliveryOrder.getModel().getImageUrl(),
        model_DeliveryOrder.getModel().getProduct().getName()
      );
    }
  }

}
