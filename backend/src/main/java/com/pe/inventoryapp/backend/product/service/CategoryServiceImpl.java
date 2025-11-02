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
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Override
  @Transactional
  public String save(CategoryResponse categoryResponse) {

    Category category = new Category();
    category.setName(categoryResponse.getName());
    category.setStatus(true);

    categoryRepository.save(category);
    return "Se guardo la categoria";
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryResponse> findAll() {
    List<Category> categories = (List<Category>) categoryRepository.findAll();

    return categories.stream()
        .map(category -> CategoryMapper.builder().setCategory(category).buildListCategoriesResponse())
        .collect(Collectors.toList());
  }

  @Override
  public List<CategoryResponse> findAllByStatusTrue() {
    List<Category> categories = (List<Category>) categoryRepository.findAllByStatusTrue();

    return categories.stream()
        .map(category -> CategoryMapper.builder().setCategory(category).buildListCategoriesResponse())
        .collect(Collectors.toList());
  }

  @Override
  public Optional<CategoryResponse> findById(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findCategoryById'");
  }

  @Override
  public Optional<CategoryResponse> findByName(String name) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findByName'");
  }

  @Override
  public Boolean getStatusById(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getStatusById'");
  }

  @Override
  public Optional<CategoryResponse> update(Long id, CategoryResponse categoryRequest) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  @Override
  public void verifyUserEmailExists(String email) {
    if (categoryRepository.findByName(email).isPresent()) {
      throw new FieldValidation("name", "La categoria con ese nombre ya existe, introduzca otra categoria");
    }
  }
}