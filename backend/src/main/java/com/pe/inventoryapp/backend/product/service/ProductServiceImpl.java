package com.pe.inventoryapp.backend.product.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.model.mapper.ProductMapper;
import com.pe.inventoryapp.backend.product.model.request.ProductRequest;
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
  public String save(ProductRequest productRequest) {

    // Buscar la categoria por su ID
    Category categoryId = categoryRepository.findById(productRequest.getIdCategory()).orElseThrow();

    Product product = new Product();
    product.setName(productRequest.getName());
    product.setEntryDate(productRequest.getEntryDate());
    product.setCaducityDate(productRequest.getCaducityDate());
    product.setLength(productRequest.getLength());
    product.setWidth(productRequest.getWidth());
    product.setHeight(productRequest.getHeight());
    product.setStock(productRequest.getStock());
    product.setImageUrl(productRequest.getImageUrl());

    product.setStatus(true);
    product.setCreatedAt(LocalDateTime.now());
    product.setUpdatedAt(LocalDateTime.now());
    product.setCategory(categoryId);

    productRepository.save(product);
    return "Se guardo el producto";

  }

  @Override
  public Page<ProductListResponse> findAll(Pageable pageable) {
    // Obtiene la lista de productos desde el repositorio
    Page<Product> productPage = productRepository
        .findAll(pageable);

    // // Convierte la lista de entidades Product en una lista de ProductDto
    // List<ProductListResponse> productDtoList = productPage.stream()
    // .map(product ->
    // ProductMapper.builder().setProduct(product).buildProductListResponse())
    // .collect(Collectors.toList());

    // // Retorna la página de ProductDto
    // return new PageImpl<>(productDtoList, pageable,
    // productPage.getTotalElements());

    return productPage.map(product -> ProductMapper.builder().setProduct(product).buildProductListResponse());
  }

  @Override
  public void verifyProductNameExist(String name) {
    if (productRepository.findByName(name).isPresent()) {
      throw new FieldValidation("name", "El producto con ese nombre ya existe, introduzca otro producto");
    }
  }

}
