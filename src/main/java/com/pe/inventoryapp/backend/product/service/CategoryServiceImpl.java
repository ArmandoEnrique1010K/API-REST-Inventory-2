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
  public void saveCategory(CategoryRequest categoryRequest) {
    verifyCategoryNameExist(categoryRequest.getName());

    Category category = new Category();
    category.setName(categoryRequest.getName().trim());
    category.setStatus(true);

    categoryRepository.save(category);
    // return "Se guardo la categoria";
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryResponse> findAllCategories() {
    List<Category> categories = (List<Category>) categoryRepository.findAll();

    return categories.stream()
        .map(category -> CategoryMapper.builder().setCategory(category).buildCategoriesResponse())
        .collect(Collectors.toList());
  }

  @Override
  public List<CategoryResponse> findAllCategoriesByStatusTrue() {
    List<Category> categories = (List<Category>) categoryRepository.findAllByStatusTrue();

    return categories.stream()
        .map(category -> CategoryMapper.builder().setCategory(category).buildCategoriesResponse())
        .collect(Collectors.toList());
  }

  @Override
  public CategoryResponse findCategoryById(Long id) {

    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La categoria no existe"));

    return CategoryMapper.builder().setCategory(category).buildCategoriesResponse();
  }

  @Override
  public void updateCategoryById(Long id, CategoryRequest categoryRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "Esta categoria no se puede editar");
    }

    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La categoria no existe"));

    if (category.isStatus() == false) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La categoria se encuentra desactivada");
    }

    verifyCategoryNameExist(categoryRequest.getName().trim());

    category.setName(categoryRequest.getName().trim());

    categoryRepository.save(category);
  }

  // Obtiene una categoria por su nombre y verifica que no exista
  @Override
  public void changeStatusCategoryById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "Esta categoria no se puede inhabilitar");
    }

    Category changedCategory = categoryRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La categoria no existe"));

    // Cambia el estado de la categoria a false y lo guarda
    changedCategory.setStatus(!changedCategory.isStatus());
    categoryRepository.save(changedCategory);
  }

  // METODOS AUXILIARES
  private void verifyCategoryNameExist(String name) {
    if (categoryRepository.findByName(name).isPresent()) {
      throw new FieldValidation("name", "La categoria con ese nombre ya existe");
    }
  }
}