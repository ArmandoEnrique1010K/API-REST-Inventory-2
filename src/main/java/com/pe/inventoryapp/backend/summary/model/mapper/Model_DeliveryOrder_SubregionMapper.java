package com.pe.inventoryapp.backend.summary.model.mapper;

import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Subregion;
import com.pe.inventoryapp.backend.summary.model.response.Model_DeliveryOrder_SubregionResponse;

public class Model_DeliveryOrder_SubregionMapper {
  private Model_DeliveryOrder_Subregion model_DeliveryOrder_Subregion;

  private Model_DeliveryOrder_SubregionMapper() {
  }

  public static Model_DeliveryOrder_SubregionMapper builder() {
    return new Model_DeliveryOrder_SubregionMapper();
  }

  public Model_DeliveryOrder_SubregionMapper setProduct_DeliveryOrder_Region(
      Model_DeliveryOrder_Subregion model_DeliveryOrder_Subregion) {
    this.model_DeliveryOrder_Subregion = model_DeliveryOrder_Subregion;
    return this;
  }

  public Model_DeliveryOrder_SubregionResponse buildModel_DeliveryOrder_SubregionResponse() {
    if (model_DeliveryOrder_Subregion == null) {
      throw new RuntimeException("Debe pasar la entidad Model_DeliveryOrder_Subregion");
    } else {
      return new Model_DeliveryOrder_SubregionResponse(
          model_DeliveryOrder_Subregion.getId(),
          model_DeliveryOrder_Subregion.getRequiredTotalQuantity(),
          model_DeliveryOrder_Subregion.getUpdatedAt(),
          model_DeliveryOrder_Subregion.getModel_DeliveryOrder().getModel().getId(),
          model_DeliveryOrder_Subregion.getModel_DeliveryOrder().getModel().getName(),
          model_DeliveryOrder_Subregion.getModel_DeliveryOrder().getModel().getImageUrl(),
          model_DeliveryOrder_Subregion.getSubregion().getId(),
          model_DeliveryOrder_Subregion.getSubregion().getName());
    }
  }
}
