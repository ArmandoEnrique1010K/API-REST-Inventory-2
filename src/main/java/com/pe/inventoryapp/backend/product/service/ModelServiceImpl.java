package com.pe.inventoryapp.backend.product.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.model.mapper.ModelMapper;
import com.pe.inventoryapp.backend.product.model.request.ModelRequest;
import com.pe.inventoryapp.backend.product.model.response.ModelDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListResponse;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.product.repository.TypeRepository;

import io.github.cdimascio.dotenv.Dotenv;

public class ModelServiceImpl implements ModelService {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ModelRepository modelRepository;

  @Autowired
  private TypeRepository typeRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Override
  public void saveModelInProductId(ModelRequest modelRequest, Long productId) {
    String name = modelRequest.getName().trim();

    // TODO: Verificar que el producto exista, sino lanzar una excepción de negocio

    if (productId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new BusinessException(com.pe.inventoryapp.backend.common.data.ResponseStatus.NOT_FOUND, "El producto no existe"));

    Model model = new Model();
    model.setName(name);
    model.setImageUrl(getImageUrl(modelRequest.getImageUrl()));
    model.setEntryDate(setEntryDateOrNow(modelRequest.getEntryDate()));
    model.setCaducityDate(modelRequest.getCaducityDate());
    model.setTotalQuantityAvailable(0);
    model.setTotalQuantityReceived(0);
    model.setTotalQuantityDelivered(0);
    model.setStatus(false);
    model.setProduct(product);

    modelRepository.save(model);
  }

  @Override
  public PageResponse<ModelListResponse> searchAllModelsByParams(Pageable pageable, String keyword, Integer minStock, Integer maxStock,
      LocalDate minEntryDate, LocalDate maxEntryDate, Boolean status, Long categoryId, Long typeId) {

      if (categoryId != null && !categoryRepository.existsById(categoryId)) {
        throw new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe");
      }

      if (typeId != null && !typeRepository.existsById(typeId)) {
        throw new BusinessException(ResponseStatus.NOT_FOUND, "El tipo no existe");
      }
      
      Page<Model> models = modelRepository.findAllByParams(pageable, keyword, minStock, maxStock, minEntryDate, maxEntryDate, status, categoryId, typeId);

      List<ModelListResponse> result = models.getContent().stream().map(
          model -> ModelMapper.builder()
              .setModel(model).buildModelListResponse())
          .toList();

      PageResponse<ModelListResponse> pageResponse = new PageResponse<>(
          result,
          models.getNumber(),
          models.getSize(),
          models.getTotalElements(),
          models.getTotalPages(), 
          models.isFirst(), 
          models.isLast()
      );

      return pageResponse;
  }

  @Override
  public ModelDetailsResponse findModelById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Model model = modelRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo no existe"));

    if (model.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El modelo se encuentra inactivo");
    }

    return ModelMapper.builder().setModel(model).buildModelResponse();
  }

  // TODO: CONTINUAR AQUI
  @Override
  public void updateModelById(Long id, ModelRequest modelRequest) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateModelById'");
  }

  @Override
  public void changeStatusModelById(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'changeStatusModelById'");
  }
    private String getImageUrl(String imageUrl) {
    // Cargar variables de entorno
    Dotenv dotenv = Dotenv.load();

    if (imageUrl == null || imageUrl.isEmpty() || imageUrl.isBlank() || imageUrl.equals("")) {
      return dotenv.get("DEFAULT_IMAGE_URL").toString();
    } else {
      return imageUrl;
    }
  }
  private BigDecimal setZeroDecimal(BigDecimal number) {
    return number == null ? BigDecimal.ZERO : number;
  }

  private LocalDate setEntryDateOrNow(LocalDate date) {
    return date == null ? LocalDate.now() : date;
  }

}
