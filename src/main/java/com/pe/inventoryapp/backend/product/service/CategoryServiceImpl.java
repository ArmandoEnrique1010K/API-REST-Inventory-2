package com.pe.inventoryapp.backend.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
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
    String name = categoryRequest.getName().trim();

    verifyCategoryNameExist(name);

    Category category = new Category();
    category.setName(name);
    category.setStatus(true);

    categoryRepository.save(category);
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
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe en el sistema"));

    if (category.isStatus() == false) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La categoria se encuentra desactivada");
    }

    return CategoryMapper.builder().setCategory(category).buildCategoriesResponse();
  }

  @Override
  public void updateCategoryById(Long id, CategoryRequest categoryRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "Esta categoria no se puede editar");
    }

    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe en el sistema"));

    if (category.isStatus() == false) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La categoria se encuentra desactivada");
    }

    String newName = categoryRequest.getName().trim();

    verifyCategoryNameExistById(newName, id);

    category.setName(newName);

    categoryRepository.save(category);
  }

  // Obtiene una categoria por su nombre y verifica que no exista
  @Override
  public void changeStatusCategoryById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "No se puede cambiar el estado de esta categoria");
    }

    Category category = categoryRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe en el sistema"));

    // Cambia el estado de la categoria a false y lo guarda
    category.setStatus(!category.isStatus());
    categoryRepository.save(category);
  }

  // METODOS AUXILIARES
  private void verifyCategoryNameExist(String name) {
    if (categoryRepository.existsByName(name)) {
      throw new FieldValidation("name", "La categoria con ese nombre ya existe, introduzca otro nombre");
    }
  }

  private void verifyCategoryNameExistById(String name, Long id) {
    if (categoryRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "La categoria con ese nombre ya existe, introduzca otro nombre");
    }
  }
}