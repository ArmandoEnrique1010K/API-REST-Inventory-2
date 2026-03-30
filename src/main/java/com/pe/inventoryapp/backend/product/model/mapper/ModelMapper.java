package com.pe.inventoryapp.backend.product.model.mapper;

import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.model.response.ModelDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelSearchResponse;
import com.pe.inventoryapp.backend.product.model.response.SearchFirstTenModelsResponse;


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
      model.getPublicImageId(),
      model.getEntryDate(),
      model.getCaducityDate(),
      model.getTotalQuantityAvailable(),
      model.getTotalQuantityReceived(),
      model.getTotalQuantityDelivered(),
      model.isStatus(),

      model.getProduct().getId(),
      model.getProduct().getName(),
      model.getProduct().getLength(),
      model.getProduct().getWidth(),
      model.getProduct().getHeight(),
      model.getProduct().isStatus(),

      model.getProduct().getCategory().getId(),
      model.getProduct().getCategory().getName(),

      model.getProduct().getType().getId(),
      model.getProduct().getType().getName()
    );
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

      model.getProduct().getId(),
      model.getProduct().getName(),
      model.getProduct().getType().getName(),
      model.getProduct().getCategory().getName()
    );
  }

  public ModelSearchResponse buildModelSearchResponse() {
    if (model == null) {
      throw new RuntimeException("Debe pasar la entidad Model");
    }

    return new ModelSearchResponse(
        model.getId(),
        model.getName(),
        model.getImageUrl(),
        model.getProduct().getName(),
        model.getProduct().getType().getName(),
        model.getProduct().getCategory().getId(),
        model.getProduct().getCategory().getName());
  }

  public SearchFirstTenModelsResponse buildSearchFirstTenModelsResponse() {
    if (model == null) {
      throw new RuntimeException("Debe pasar la entidad Model");
    }

    return new SearchFirstTenModelsResponse(
        model.getId(),
        model.getProduct().getName() + " " + model.getName()
      );
  }
}


