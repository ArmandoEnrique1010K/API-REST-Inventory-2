package com.pe.inventoryapp.backend.summary.model.mapper;

import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Region;
import com.pe.inventoryapp.backend.summary.model.response.Model_DeliveryOrder_RegionResponse;

public class Model_DeliveryOrder_RegionMapper {
  private Model_DeliveryOrder_Region model_DeliveryOrder_Region;

  private Model_DeliveryOrder_RegionMapper() {
  }

  public static Model_DeliveryOrder_RegionMapper builder() {
    return new Model_DeliveryOrder_RegionMapper();
  }

  public Model_DeliveryOrder_RegionMapper setProduct_DeliveryOrder_Region(
      Model_DeliveryOrder_Region model_DeliveryOrder_Region) {
    this.model_DeliveryOrder_Region = model_DeliveryOrder_Region;
    return this;
  }

  public Model_DeliveryOrder_RegionResponse buildProduct_DeliveryOrder_RegionResponse() {
    if (model_DeliveryOrder_Region == null) {
      throw new RuntimeException("Debe pasar la entidad Model_DeliveryOrder_Region");
    } else {
      return new Model_DeliveryOrder_RegionResponse(
          model_DeliveryOrder_Region.getId(),
          model_DeliveryOrder_Region.getRequiredTotalQuantity(),
          model_DeliveryOrder_Region.getUpdatedAt(),
          model_DeliveryOrder_Region.getModel_DeliveryOrder().getModel().getId(),
          model_DeliveryOrder_Region.getModel_DeliveryOrder().getModel().getName(),
          model_DeliveryOrder_Region.getModel_DeliveryOrder().getModel().getImageUrl(),
          model_DeliveryOrder_Region.getRegion().getId(),
          model_DeliveryOrder_Region.getRegion().getName()
        );
    }
  }
}
