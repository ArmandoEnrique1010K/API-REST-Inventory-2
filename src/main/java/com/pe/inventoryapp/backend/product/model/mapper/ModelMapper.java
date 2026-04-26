package com.pe.inventoryapp.backend.product.model.mapper;

import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.model.response.ModelDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelExpiringSoonSummaryResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListSearchResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelLowStockSummaryResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelRecentsSummaryResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListSearchFirstTenResponse;

public class ModelMapper {
  private Model model;

  private ModelMapper() {

  }

  public static ModelMapper builder() {
    return new ModelMapper();
  }

  public ModelMapper setModel(Model model) {
    this.model = model;
    return this;
  }

  public ModelDetailsResponse buildModelResponse() {
    if (model == null) {
      throw new RuntimeException("Debe pasar la entidad Model");
    }

    return new ModelDetailsResponse(
        model.getId(),
        model.getName(),
        model.getImageUrl(),
        model.getEntryDate(),
        model.getCaducityDate(),
        model.getTotalQuantityAvailable(),
        model.getTotalQuantityReceived(),
        model.getTotalQuantityDelivered(),
        model.isStatus(),
        model.getMinimumAvailableQuantity(),
        model.isLowStock(),

        model.getProduct().getId(),
        model.getProduct().getName(),
        model.getProduct().getLength(),
        model.getProduct().getWidth(),
        model.getProduct().getHeight(),
        model.getProduct().isStatus(),

        model.getProduct().getCategory().getId(),
        model.getProduct().getCategory().getName(),

        model.getProduct().getType().getId(),
        model.getProduct().getType().getName());
  }

  public ModelListResponse buildModelListResponse() {
    if (model == null) {
      throw new RuntimeException("Debe pasar la entidad Model");
    }

    return new ModelListResponse(
        model.getId(),
        model.getName(),
        model.getImageUrl(),
        model.getEntryDate(),
        model.getCaducityDate(),
        model.getTotalQuantityAvailable(),
        model.getTotalQuantityReceived(),
        model.getTotalQuantityDelivered(),
        model.isStatus(),
        model.isLowStock(),
        model.getMinimumAvailableQuantity(),

        model.getProduct().getId(),
        model.getProduct().getName(),
        model.getProduct().getType().getName(),
        model.getProduct().getCategory().getName());
  }

  public ModelListSearchResponse buildModelListSearchResponse() {
    if (model == null) {
      throw new RuntimeException("Debe pasar la entidad Model");
    }

    return new ModelListSearchResponse(
        model.getId(),
        model.getName(),
        model.getImageUrl(),
        model.getProduct().getName(),
        model.getProduct().getType().getName(),
        model.getProduct().getCategory().getId(),
        model.getProduct().getCategory().getName());
  }

  public ModelListSearchFirstTenResponse buildModelListSearchFirstTenResponse() {
    if (model == null) {
      throw new RuntimeException("Debe pasar la entidad Model");
    }

    return new ModelListSearchFirstTenResponse(
        model.getId(),
        model.getProduct().getName() + " " + model.getName());
  }

  public ModelExpiringSoonSummaryResponse buildModelExpiringSoonSummaryResponse() {
    if (model == null) {
      throw new RuntimeException("Debe pasar la entidad Model");
    }

    return new ModelExpiringSoonSummaryResponse(
        model.getId(),
        model.getName(),
        model.getProduct().getId(),
        model.getProduct().getName(),
        model.getProduct().getCategory().getName(),
        model.getProduct().getType().getName(),
        model.getCaducityDate(), 
        model.getTotalQuantityAvailable());
  }

  public ModelLowStockSummaryResponse buildModelLowStockSummaryResponse() {
    if (model == null) {
      throw new RuntimeException("Debe pasar la entidad Model");
    }

    return new ModelLowStockSummaryResponse(
        model.getId(),
        model.getName(),
        model.getProduct().getId(),
        model.getProduct().getName(),
        model.getTotalQuantityAvailable(),
        model.getMinimumAvailableQuantity(),
        model.getProduct().getCategory().getName(),
        model.getProduct().getType().getName());
  }

  public ModelRecentsSummaryResponse buildModelRecentsSummaryResponse() {
    if (model == null) {
      throw new RuntimeException("Debe pasar la entidad Model");
    }

    return new ModelRecentsSummaryResponse(
        model.getId(),
        model.getName(),
        model.getProduct().getId(),
        model.getProduct().getName(),
        model.getEntryDate(),
        model.getTotalQuantityAvailable(),
        model.getProduct().getCategory().getName(),
        model.getProduct().getType().getName());
  }

}
