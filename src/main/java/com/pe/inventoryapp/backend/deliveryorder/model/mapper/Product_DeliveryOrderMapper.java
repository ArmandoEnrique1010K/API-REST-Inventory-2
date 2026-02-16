package com.pe.inventoryapp.backend.deliveryorder.model.mapper;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.response.Product_DeliveryOrderResponse;

public class Product_DeliveryOrderMapper {
    private Model_DeliveryOrder product_DeliveryOrder;

  private Product_DeliveryOrderMapper() {
  }

  public static Product_DeliveryOrderMapper builder() {
    return new Product_DeliveryOrderMapper();
  }

  public Product_DeliveryOrderMapper setProduct_DeliveryOrder(Model_DeliveryOrder product_DeliveryOrder) {
    this.product_DeliveryOrder = product_DeliveryOrder;
    return this;
  }

  public Product_DeliveryOrderResponse buildProduct_DeliveryOrderListResponse() {
    if (product_DeliveryOrder == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryOrder");
    } else {
      return new Product_DeliveryOrderResponse(
        product_DeliveryOrder.getId(),
        product_DeliveryOrder.getModel().getId(),
        product_DeliveryOrder.getModel().getName(),
        product_DeliveryOrder.getModel().getImageUrl(),
        product_DeliveryOrder.getRequiredQuantityTotal()
      );
    }
  }

}
