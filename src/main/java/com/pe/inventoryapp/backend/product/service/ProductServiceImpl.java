package com.pe.inventoryapp.backend.product.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.model.mapper.ProductMapper;
import com.pe.inventoryapp.backend.product.model.request.ProductRequest;
import com.pe.inventoryapp.backend.product.model.response.ProductDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ProductListResponse;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ProductServiceImpl implements ProductService {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Override
  @Transactional
  public void saveProduct(ProductRequest productRequest) {
    verifyProductNameExist(productRequest.getName());

    Long idCategory = productRequest.getIdCategory();

     if (idCategory == null) {
       throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
     }

    // Buscar la categoria por su ID
    Category category = categoryRepository.findById(
        idCategory)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe en el sistema"));

    if (category.isStatus() == false) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La categoria se encuentra desactivada");
    }
    
    String name = productRequest.getName().trim();

    Product product = new Product();
    product.setName(name);

    if (productRequest.getEntryDate() == null) {
      product.setEntryDate(LocalDate.now());
    } else {
      product.setEntryDate(productRequest.getEntryDate());
    }

    Dotenv dotenv = Dotenv.load();

    if (productRequest.getImageUrl() == null || productRequest.getImageUrl().isEmpty() || productRequest.getImageUrl().isBlank() || productRequest.getImageUrl().equals("")) {
      // Toma la URL de la imagen que se encuentra en la variable de entorno
      product.setImageUrl(dotenv.get("DEFAULT_IMAGE_URL").toString());
    } else {
      product.setImageUrl(productRequest.getImageUrl());
    }

    product.setCaducityDate(productRequest.getCaducityDate());
    product.setLength(productRequest.getLength());
    product.setWidth(productRequest.getWidth());

    product.setStatus(true);
    product.setCreatedAt(LocalDateTime.now());
    product.setUpdatedAt(LocalDateTime.now());
    product.setTotalQuantityAvailable(0);

    product.setCategory(category);
    productRepository.save(product);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ProductListResponse> searchAllProductsByParams(
      String name,
      Integer minStock,
      Integer maxStock,
      Boolean status,
      Long categoryId,
      Pageable pageable) {
    if (categoryId != null && !categoryRepository.existsById(categoryId)) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          "La categoria no existe en el sistema");
    }

    Page<Product> products = productRepository.findAllByParams(name, minStock, maxStock, status, categoryId, pageable);

    var result = products.getContent().stream().map(
      product -> ProductMapper.builder()
      .setProduct(product).buildProductListResponse()
    ).toList();

    PageResponse<ProductListResponse> pageResponse = new PageResponse<>(
      result,
      products.getNumber(),
      products.getSize(),
      products.getTotalElements(),
      products.getTotalPages(),
      products.isFirst(),
      products.isLast()
    );

    return pageResponse;
  }

  @Override
  @Transactional(readOnly = true)
  public ProductDetailsResponse findProductById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe en el sistema"));


    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "El producto se encuentra desactivado");
    }

    return ProductMapper.builder().setProduct(product).buildProductDetailsResponse();
  }

  @Override
  @Transactional
  public void updateProductById(Long id, ProductRequest productRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe en el sistema"));

    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "El producto se encuentra desactivado");
    }

    String newName = productRequest.getName().trim();

    verifyProductNameExistById(newName, id);

    Long categoryId = productRequest.getIdCategory();

    if (categoryId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Category category = categoryRepository.findById(
        categoryId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe en el sistema"));

    product.setName(newName);
    product.setEntryDate(productRequest.getEntryDate());
    product.setCaducityDate(productRequest.getCaducityDate());
    product.setLength(productRequest.getLength());
    product.setWidth(productRequest.getWidth());
    product.setImageUrl(productRequest.getImageUrl());
    product.setCategory(category);

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
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe en el sistema"));

    product.setStatus(!product.isStatus());
    productRepository.save(product);
  }

  // METODOS AUXILIARES
  private void verifyProductNameExist(String name) {
    if (productRepository.existsByName(name)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  private void verifyProductNameExistById(String name, Long id) {
    if (productRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }
}
