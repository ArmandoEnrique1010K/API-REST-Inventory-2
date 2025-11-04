package com.pe.inventoryapp.backend.product.model.mapper;

import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.model.response.ProductDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ProductListResponse;

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

  public ProductListResponse buildProductListResponse() {

    if (product == null) {
      throw new RuntimeException("Debe pasar la entidad Product");
    }
    // Devuelve una nueva instancia de UserDto con los datos mapeados
    return new ProductListResponse(
        product.getId(),
        product.getName().trim(),
        product.getStock(),
        product.getImageUrl()
            .trim(),
        product.getCategory().getName().trim());
  }

  public ProductDetailsResponse buildProductDetailsResponse() {

    if (product == null) {
      throw new RuntimeException("Debe pasar la entidad Product");
    }
    // Devuelve una nueva instancia de UserDto con los datos mapeados
    return new ProductDetailsResponse(
        product.getId(),
        product.getName().trim(),
        product.getEntryDate(),
        product.getCaducityDate(),
        product.getLength(),
        product.getWidth(),
        product.getHeight(),
        product.getStock(),
        product.getImageUrl().trim(),
        product.isStatus(),
        product.getCategory().getName().trim());
  }
}
