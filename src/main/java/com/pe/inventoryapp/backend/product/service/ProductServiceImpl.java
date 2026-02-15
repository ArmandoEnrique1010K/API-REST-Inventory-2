package com.pe.inventoryapp.backend.product.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.model.entity.Type;
import com.pe.inventoryapp.backend.product.model.mapper.ProductMapper;
import com.pe.inventoryapp.backend.product.model.request.ProductCreateRequest;
import com.pe.inventoryapp.backend.product.model.request.ProductUpdateRequest;
import com.pe.inventoryapp.backend.product.model.response.ProductResponse;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.product.repository.TypeRepository;

@Service
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;
  private final TypeRepository typeRepository;
  private final CategoryRepository categoryRepository;
  private final ModelRepository modelRepository;
  private final ProductDomainService productDomainService;
  private final ModelDomainService modelDomainService;

  public ProductServiceImpl(
      ProductRepository productRepository,
      TypeRepository typeRepository,
      CategoryRepository categoryRepository,
      ModelRepository modelRepository,
      ProductDomainService productDomainService,
      ModelDomainService modelDomainService) {
    this.productRepository = productRepository;
    this.typeRepository = typeRepository;
    this.categoryRepository = categoryRepository;
    this.modelRepository = modelRepository;
    this.productDomainService = productDomainService;
    this.modelDomainService = modelDomainService;
  }

  @Override
  @Transactional
  public void saveProduct(ProductCreateRequest productCreateRequest) {
    String name = productCreateRequest.getName().trim();
    productDomainService.verifyProductNameAvailable(name);

    Long idCategory = productCreateRequest.getCategoryId();
    Long idType = productCreateRequest.getTypeId();

    if (idCategory == null || idType == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Buscar la categoria por su ID
    Category category = categoryRepository.findById(idCategory).orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe"));

    if (category.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La categoria se encuentra desactivada");
    }

    Type type = typeRepository.findById(idType)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El tipo no existe"));


    Product product = new Product();
    product.setName(name);
    product.setLength(productDomainService.normalizeDecimal(productCreateRequest.getLength()));
    product.setWidth(productDomainService.normalizeDecimal(productCreateRequest.getWidth()));
    product.setHeight(productDomainService.normalizeDecimal(productCreateRequest.getHeight()));
    product.setStatus(true);
    product.setQuantityModels(1);
    product.setCategory(category);
    product.setType(type);
    productRepository.save(product);

    // GUARDAR EL MODELO DEL PRODUCTO
    Model model = new Model();
    model.setName(productCreateRequest.getModelName());
    model.setImageUrl(modelDomainService.resolveImageUrl(productCreateRequest.getModelImageUrl()));
    model.setEntryDate(modelDomainService.resolveAnyLocalDate(productCreateRequest.getModelEntryDate()));

    // Nota: CaducityDate puede ser nulo
    model.setCaducityDate(productCreateRequest.getModelCaducityDate());
    model.setTotalQuantityAvailable(0);
    model.setTotalQuantityReceived(0);
    model.setTotalQuantityDelivered(0);
    model.setStatus(true);
    model.setProduct(product);

    modelRepository.save(model);
  }

  // TODO: ESTE METODO SE PODRIA ELIMINAR YA QUE SE PIENSA BUSCAR POR NOMBRE DE PRODUCTO Y NOMBRE DE MODELO
  @Override
  @Transactional(readOnly = true)
  public PageResponse<ProductResponse> searchAllProductsByParams(
      Pageable pageable,
      String name,
      Boolean status,
      Long categoryId,
      Long typeId
      ) {
    if (categoryId != null && !categoryRepository.existsById(categoryId)) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          "La categoria no existe");
    }

    Page<Product> products = productRepository.findAllByParams(pageable, name, status, categoryId, typeId);

    List<ProductResponse> result = products.getContent().stream().map(
        product -> ProductMapper.builder()
            .setProduct(product).buildProductResponse())
        .toList();

    PageResponse<ProductResponse> pageResponse = new PageResponse<>(
        result,
        products.getNumber(),
        products.getSize(),
        products.getTotalElements(),
        products.getTotalPages(),
        products.isFirst(),
        products.isLast());

    return pageResponse;
  }

  @Override
  @Transactional(readOnly = true)
  public ProductResponse findProductById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe"));

    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El producto se encuentra desactivado");
    }

    return ProductMapper.builder().setProduct(product).buildProductResponse();
  }

  @Override
  @Transactional
  public void updateProductById(Long id, ProductUpdateRequest productUpdateRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe"));

    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El producto se encuentra desactivado");
    }

    String newName = productUpdateRequest.getName().trim();
    productDomainService.verifyProductNameAvailableExcludingId(newName, id);

    Long categoryId = productUpdateRequest.getCategoryId();

    if (categoryId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Category category = categoryRepository.findById(
        categoryId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe"));
    
    Long typeId = productUpdateRequest.getTypeId();

    if (typeId == null){
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Type type = typeRepository.findById(
        typeId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El tipo no existe"));

    product.setName(newName);
    product.setLength(productDomainService.normalizeDecimal(productUpdateRequest.getLength()));
    product.setWidth(productDomainService.normalizeDecimal(productUpdateRequest.getWidth()));
    product.setHeight(productDomainService.normalizeDecimal(productUpdateRequest.getHeight()));
    product.setCategory(category);
    product.setType(type);
    productRepository.save(product);
  }

  // Cambia el estado del producto a false y lo guarda
  @Override
  @Transactional
  public void changeStatusProductById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe"));

    product.setStatus(!product.isStatus());
    productRepository.save(product);
  }
}
