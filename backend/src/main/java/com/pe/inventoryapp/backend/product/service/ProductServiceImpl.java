package com.pe.inventoryapp.backend.product.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public Page<ProductListResponse> findAllByStatusTrue(Pageable pageable) {

    return productRepository.findAllByStatusTrue(pageable)
        .map(product -> ProductMapper.builder().setProduct(product).buildProductListResponse());
  }

  @Override
  public void verifyProductNameExist(String name) {
    if (productRepository.findByName(name).isPresent()) {
      throw new FieldValidation("name", "El producto con ese nombre ya existe, introduzca otro producto");
    }
  }

  @Override
  public void changeStatus(Long id) {
    Product product = productRepository.findById(id).orElseThrow();

    // Cambia el estado de la categoria a false y lo guarda
    product.setStatus(!product.isStatus());
    productRepository.save(product);
  }

  @Override
  public Optional<ProductDetailsResponse> findById(Long id) {

    return productRepository.findById(id)
        .map(product -> ProductMapper.builder().setProduct(product).buildProductDetailsResponse());
  }

  @Override
  public String update(Long id, ProductRequest productRequest) {

    Optional<Product> productById = productRepository.findById(id);

    if (productById.isPresent()) {

      Category categoryId = categoryRepository.findById(productRequest.getIdCategory()).orElseThrow();

      Product productData = productById.orElseThrow();
      productData.setName(productRequest.getName());
      productData.setEntryDate(productRequest.getEntryDate());
      productData.setCaducityDate(productRequest.getCaducityDate());
      productData.setLength(productRequest.getLength());
      productData.setWidth(productRequest.getWidth());
      productData.setHeight(productRequest.getHeight());
      productData.setStock(productRequest.getStock());
      productData.setImageUrl(productRequest.getImageUrl());
      productData.setCategory(categoryId);

      productRepository.save(productData);
    }

    return "Se actualizo el producto";
  }

}
