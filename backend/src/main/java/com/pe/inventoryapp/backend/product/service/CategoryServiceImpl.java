package com.pe.inventoryapp.backend.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.model.mapper.CategoryMapper;
import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Override
  @Transactional
  public void save(CategoryRequest categoryRequest) {

    Category category = new Category();
    category.setName(categoryRequest.getName());
    category.setStatus(true);

    categoryRepository.save(category);
    // return "Se guardo la categoria";
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryResponse> findAll() {
    List<Category> categories = (List<Category>) categoryRepository.findAll();

    return categories.stream()
        .map(category -> CategoryMapper.builder().setCategory(category).buildCategoriesResponse())
        .collect(Collectors.toList());
  }

  @Override
  public List<CategoryResponse> findAllByStatusTrue() {
    List<Category> categories = (List<Category>) categoryRepository.findAllByStatusTrue();

    return categories.stream()
        .map(category -> CategoryMapper.builder().setCategory(category).buildCategoriesResponse())
        .collect(Collectors.toList());
  }

  @Override
  public CategoryResponse findById(Long id) {

    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La caregoria no existe"));

    return CategoryMapper.builder().setCategory(category).buildCategoriesResponse();
  }

  @Override
  public void update(Long id, CategoryRequest categoryRequest) {

    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La caregoria no existe"));

    category.setName(categoryRequest.getName());

    categoryRepository.save(category);
  }

  // return "Se actualizo la categoria";

  @Override
  public void verifyCategoryNameExist(String name) {
    if (categoryRepository.findByName(name).isPresent()) {
      throw new FieldValidation("name", "La categoria con ese nombre ya existe");
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