package com.pe.inventoryapp.backend.product.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.model.mapper.CategoryMapper;
import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.CategoryListResponse;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Override
  @Transactional
  public String save(CategoryRequest categoryRequest) {

    Category category = new Category();
    category.setName(categoryRequest.getName());
    category.setStatus(true);

    categoryRepository.save(category);
    return "Se guardo la categoria";
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryListResponse> findAll() {
    List<Category> categories = (List<Category>) categoryRepository.findAll();

    return categories.stream()
        .map(category -> CategoryMapper.builder().setCategory(category).buildListCategoriesResponse())
        .collect(Collectors.toList());
  }

  @Override
  public List<CategoryListResponse> findAllByStatusTrue() {
    List<Category> categories = (List<Category>) categoryRepository.findAllByStatusTrue();

    return categories.stream()
        .map(category -> CategoryMapper.builder().setCategory(category).buildListCategoriesResponse())
        .collect(Collectors.toList());
  }

  @Override
  public String update(Long id, CategoryRequest categoryRequest) {
    Optional<Category> categoryById = categoryRepository.findById(id);

    // Category categoryOptional = null;

    // Si la categoria existe, lo actualiza con los datos proporcionados
    if (categoryById.isPresent()) {
      Category categoryData = categoryById.orElseThrow();

      categoryData.setName(categoryRequest.getName());

      // categoryOptional = categoryRepository.save(categoryData);
      categoryRepository.save(categoryData);
    }

    // Optional.ofNullable(CategoryMapper.builder().setCategory(categoryOptional).buildListCategoriesResponse());
    return "Se actualizo la categoria";
  }

  @Override
  public Optional<CategoryDetailsResponse> findById(Long id) {
    return categoryRepository.findById(id)
        .map(category -> CategoryMapper.builder().setCategory(category).buildCategoriesResponse());
  }

  @Override
  public Optional<CategoryDetailsResponse> findByName(String name) {
    return categoryRepository.findByName(name)
        .map(category -> CategoryMapper.builder().setCategory(category).buildCategoriesResponse());
  }

  @Override
  public Boolean getStatusById(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getStatusById'");
  }

  @Override
  public void verifyCategoryNameExist(String name) {
    if (categoryRepository.findByName(name).isPresent()) {
      throw new FieldValidation("name", "La categoria con ese nombre ya existe, introduzca otra categoria");
    }
  }

  @Override
  public void changeStatus(Long id) {
    Category changedCategory = categoryRepository.findById(id).orElseThrow();

    // Cambia el estado de la categoria a false y lo guarda
    changedCategory.setStatus(!changedCategory.isStatus());
    categoryRepository.save(changedCategory);
  }
}