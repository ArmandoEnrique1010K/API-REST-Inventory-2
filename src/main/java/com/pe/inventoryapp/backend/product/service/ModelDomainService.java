package com.pe.inventoryapp.backend.product.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ModelDomainService {
  private final ModelRepository modelRepository;
  private final String defaultImageUrl;

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

  // METODO AUXILIAR PARA BUSCAR MODELOS POR NOMBRE EN EL MISMO PRODUCTO Y QUE NO EXISTA UNO QUE SE REPITA CON EL MISMO NOMBRE, EN LA LISTA DE MODELOS CON EL MISMO ID DE PRODUCTO
  public void verifyModelNameAvailableByProductId(String name, Long productId) {
    if (modelRepository.existsByNameAndProductId(name, productId)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  // EL MISMO METODO, PERO CON ACTUALIZAR EXCLUYENDO EL ID DEL MODELO QUE SE ESTÁ ACTUALIZANDO, PARA QUE NO SE REPITA EL NOMBRE EN LOS DEMÁS MODELOS DEL MISMO PRODUCTO
  public void verifyModelNameAvailableByProductIdExcludingId(String name, Long productId, Long id) {
    if (modelRepository.existsByNameAndProductIdAndIdNot(name, productId, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }

}
