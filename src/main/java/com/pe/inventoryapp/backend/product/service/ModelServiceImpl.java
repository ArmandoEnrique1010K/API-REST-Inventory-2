package com.pe.inventoryapp.backend.product.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


@Service
public class ModelServiceImpl implements ModelService {
  private final ProductRepository productRepository;
  private final TypeRepository typeRepository;
  private final CategoryRepository categoryRepository;
  private final ModelRepository modelRepository;
  private final ModelDomainService modelDomainService;

  public ModelServiceImpl(
      ProductRepository productRepository,
      TypeRepository typeRepository,
      CategoryRepository categoryRepository,
      ModelRepository modelRepository,
      ModelDomainService modelDomainService) {
    this.productRepository = productRepository;
    this.typeRepository = typeRepository;
    this.categoryRepository = categoryRepository;
    this.modelRepository = modelRepository;
    this.modelDomainService = modelDomainService;
  }

  @Override
  @Transactional
  public void saveModelInProductId(ModelRequest modelRequest, Long productId) {
    String name = modelRequest.getName().trim();

    modelDomainService.verifyModelNameAvailableByProductId(name, productId);

    if (productId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND,
            "El producto no existe"));
    product.setQuantityModels(product.getQuantityModels() + 1);
    productRepository.save(product);

    Model model = new Model();
    model.setName(name);
    model.setImageUrl(modelDomainService.resolveImageUrl(modelRequest.getImageUrl()));
    model.setEntryDate(modelDomainService.resolveAnyLocalDate(modelRequest.getEntryDate()));
    model.setCaducityDate(modelRequest.getCaducityDate());
    model.setTotalQuantityAvailable(0);
    model.setTotalQuantityReceived(0);
    model.setTotalQuantityTaken(0);
    model.setTotalQuantityDelivered(0);
    model.setStatus(true);
    model.setProduct(product);

    modelRepository.save(model);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ModelListResponse> searchAllModelsByParams(Pageable pageable, String keyword, Integer minStock,
      Integer maxStock,
      LocalDate minEntryDate, LocalDate maxEntryDate, Boolean status, Long categoryId, Long typeId) {

    if (categoryId != null && !categoryRepository.existsById(categoryId)) {
      throw new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe");
    }

    if (typeId != null && !typeRepository.existsById(typeId)) {
      throw new BusinessException(ResponseStatus.NOT_FOUND, "El tipo no existe");
    }

    Page<Model> models = modelRepository.findAllByParams(pageable, keyword, minStock, maxStock, minEntryDate,
        maxEntryDate, status, categoryId, typeId);

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
        models.isLast());

    return pageResponse;
  }

  @Override
  public List<ModelListResponse> findAllModelsByProductId(Long productId) {
    if (productId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    List<Model> models = (List<Model>) modelRepository.findAllByProductId(productId);

    return models.stream()
        .map(model -> ModelMapper.builder().setModel(model).buildModelListResponse())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
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

  @Override
  @Transactional
  public void updateModelById(Long id, ModelRequest modelRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Model model = modelRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo no existe"));

    if (model.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El modelo se encuentra desactivado");
    }

    Long productId = model.getProduct().getId();

    Product product = productRepository.findById(productId).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto asociado al modelo no existe"));

    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El producto asociado al modelo se encuentra desactivado");
    }

    String newName = modelRequest.getName().trim();
    modelDomainService.verifyModelNameAvailableByProductIdExcludingId(newName, productId, id);

    model.setName(newName);
    model.setImageUrl(modelDomainService.resolveImageUrl(modelRequest.getImageUrl()));
    model.setEntryDate(modelDomainService.resolveAnyLocalDate(modelRequest.getEntryDate()));
    model.setCaducityDate(modelRequest.getCaducityDate());
    modelRepository.save(model);
  }

  @Override
  @Transactional
  public void changeStatusModelById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Model model = modelRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo no existe"));

    model.setStatus(!model.isStatus());
    modelRepository.save(model);
  }

}
