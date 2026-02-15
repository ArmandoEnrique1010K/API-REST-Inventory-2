package com.pe.inventoryapp.backend.summary.model.mapper;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Region;
import com.pe.inventoryapp.backend.summary.model.response.Product_DeliveryOrder_RegionResponse;

public class Product_DeliveryOrder_RegionMapper {
    private Model_DeliveryOrder_Region product_DeliveryOrder_Region;

  private Product_DeliveryOrder_RegionMapper() {
  }

  public static Product_DeliveryOrder_RegionMapper builder() {
    return new Product_DeliveryOrder_RegionMapper();
  }

  public Product_DeliveryOrder_RegionMapper setProduct_DeliveryOrder_Region(
      Model_DeliveryOrder_Region product_DeliveryOrder_Region) {
    this.product_DeliveryOrder_Region = product_DeliveryOrder_Region;
    return this;
  }

  public Product_DeliveryOrder_RegionResponse buildProduct_DeliveryOrder_RegionResponse() {
    if (product_DeliveryOrder_Region == null) {
      throw new RuntimeException("Debe pasar la entidad Product_DeliveryOrder_Region");
    } else {
      return new Product_DeliveryOrder_RegionResponse(
          product_DeliveryOrder_Region.getProduct_DeliveryOrder().getProduct().getId(),
          product_DeliveryOrder_Region.getProduct_DeliveryOrder().getProduct().getName(),
          product_DeliveryOrder_Region.getRegion().getId(),
          product_DeliveryOrder_Region.getRegion().getName(),
          product_DeliveryOrder_Region.getUpdatedAt(),
          product_DeliveryOrder_Region.getRequiredTotalQuantity()
        );
    }
  }

}
