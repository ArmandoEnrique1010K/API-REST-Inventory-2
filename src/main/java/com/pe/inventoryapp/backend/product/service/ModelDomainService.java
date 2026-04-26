package com.pe.inventoryapp.backend.product.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ModelDomainService {
  private final ModelRepository modelRepository;
  private final String defaultImageUrl;
  private static final int DEFAULT_MINIMUM_STOCK = 1000;

  public ModelDomainService(ModelRepository modelRepository) {
    this.modelRepository = modelRepository;
    this.defaultImageUrl = Dotenv.load().get("DEFAULT_IMAGE_URL");
  }

  public LocalDate resolveAnyLocalDate(LocalDate localDate) {
    return localDate == null ? LocalDate.now() : localDate;
  }

  public String resolveImageUrl(String imageUrl) {
    return (imageUrl == null || imageUrl.isBlank())
        ? defaultImageUrl
        : imageUrl;
  }

  // METODO AUXILIAR PARA BUSCAR MODELOS POR NOMBRE EN EL MISMO PRODUCTO Y QUE NO
  // EXISTA UNO QUE SE REPITA CON EL MISMO NOMBRE, EN LA LISTA DE MODELOS CON EL
  // MISMO ID DE PRODUCTO
  public void verifyModelNameAvailableByProductId(String name, Long productId) {
    if (modelRepository.existsByNameAndProductId(name, productId)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  // EL MISMO METODO, PERO CON ACTUALIZAR EXCLUYENDO EL ID DEL MODELO QUE SE ESTÁ
  // ACTUALIZANDO, PARA QUE NO SE REPITA EL NOMBRE EN LOS DEMÁS MODELOS DEL MISMO
  // PRODUCTO
  public void verifyModelNameAvailableByProductIdExcludingId(String name, Long productId, Long id) {
    if (modelRepository.existsByNameAndProductIdAndIdNot(name, productId, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }

  // METODO PARA ESTABLECER EL STOCK MINIMO
  public void applyMinimumAvailableQuantity(Model model, Integer minimumAvailableQuantity) {
    // 1000 es el valor por defecto si no se ha establecido la cantidad minima
    int minimum = minimumAvailableQuantity != null
        ? minimumAvailableQuantity
        : DEFAULT_MINIMUM_STOCK;

    model.setMinimumAvailableQuantity(minimum);

    model.setLowStock(
        model.getTotalQuantityAvailable() <= minimum);
  }

  public void refreshLowStock(Model model) {
    model.setLowStock(
        model.getTotalQuantityAvailable() <= model.getMinimumAvailableQuantity());
  }
}
