package com.pe.inventoryapp.backend.product.model.mapper;

import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.model.response.ProductResponse;

public class ProductMapper {
  private Product product;

  private ProductMapper() {

  }

  public static ProductMapper builder() {
    return new ProductMapper();
  }

  public ProductMapper setProduct(Product product) {
    this.product = product;
    return this;
  }

  public ProductResponse buildProductResponse() {

    if (product == null) {
      throw new RuntimeException("Debe pasar la entidad Product");
    }

    // Devuelve una nueva instancia de UserDto con los datos mapeados
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getLength(),
        product.getWidth(),
        product.getHeight(),
        product.getQuantityModels(),
        product.isStatus(),

        product.getCategory().getId(),
        product.getCategory().getName(),

        product.getType().getId(),
        product.getType().getName()
      );
  }
}
