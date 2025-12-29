package com.pe.inventoryapp.backend.product.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.model.mapper.ProductMapper;
import com.pe.inventoryapp.backend.product.model.request.ProductRequest;
import com.pe.inventoryapp.backend.product.model.response.ProductDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ProductListResponse;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;

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
       throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
     }

    // Buscar la categoria por su ID
    Category category = categoryRepository.findById(
        idCategory)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La categoria no existe en el sistema"));

    String name = productRequest.getName().trim();

    Product product = new Product();
    product.setName(name);

    if (productRequest.getEntryDate() == null) {
      product.setEntryDate(LocalDate.now());
    } else {
      product.setEntryDate(productRequest.getEntryDate());
    }

    product.setCaducityDate(productRequest.getCaducityDate());
    product.setLength(productRequest.getLength());
    product.setWidth(productRequest.getWidth());
    product.setImageUrl(productRequest.getImageUrl());

    product.setStatus(true);
    product.setCreatedAt(LocalDateTime.now());
    product.setUpdatedAt(LocalDateTime.now());
    product.setStock(0);

    product.setCategory(category);
    productRepository.save(product);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductListResponse> searchAllProductsByParams(
      String name,
      Integer minStock,
      Integer maxStock,
      Boolean status,
      Long categoryId,
      Pageable pageable) {
    if (categoryId != null && !categoryRepository.existsById(categoryId)) {
      throw new BusinessException(
          ResponseStatusCodes.ENTITY_NOT_FOUND,
          "La categoria no existe en el sistema");
    }

    Page<Product> products = productRepository.findAllByParams(name, minStock, maxStock, status, categoryId, pageable);

    return products.map(product -> ProductMapper.builder().setProduct(product).buildProductListResponse());
  }

  @Override
  @Transactional(readOnly = true)
  public ProductDetailsResponse findProductById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Product product = productRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El producto no existe en el sistema"));

    return ProductMapper.builder().setProduct(product).buildProductDetailsResponse();
  }

  @Override
  @Transactional
  public void updateProductById(Long id, ProductRequest productRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Product product = productRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El producto no existe en el sistema"));

    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "El producto se encuentra desactivado");
    }

    String newName = productRequest.getName().trim();

    verifyProductNameExistById(newName, id);

    Long categoryId = productRequest.getIdCategory();

    if (categoryId == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Category category = categoryRepository.findById(
        categoryId)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La categoria no existe en el sistema"));

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
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Product product = productRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El producto no existe en el sistema"));

    product.setStatus(!product.isStatus());
    productRepository.save(product);
  }

  // METODOS AUXILIARES
  private void verifyProductNameExist(String name) {
    if (productRepository.existsByName(name)) {
      throw new FieldValidation("name", "El producto con ese nombre ya existe, introduzca otro nombre");
    }
  }

  private void verifyProductNameExistById(String name, Long id) {
    if (productRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "El producto con ese nombre ya existe, introduzca otro nombre");
    }
  }
}
