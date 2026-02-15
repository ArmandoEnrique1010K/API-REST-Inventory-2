package com.pe.inventoryapp.backend.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.product.model.entity.Category;
import com.pe.inventoryapp.backend.product.model.mapper.CategoryMapper;
import com.pe.inventoryapp.backend.product.model.request.CategoryRequest;
import com.pe.inventoryapp.backend.product.model.response.CategoryResponse;
import com.pe.inventoryapp.backend.product.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryDomainService categoryDomainService;

  public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryDomainService categoryDomainService) {
    this.categoryRepository = categoryRepository;
    this.categoryDomainService = categoryDomainService;
  }


  @Override
  @Transactional
  public void saveCategory(CategoryRequest categoryRequest) {
    String name = categoryRequest.getName().trim();

    categoryDomainService.verifyCategoryNameAvailable(name);

    Category category = new Category();
    category.setName(name);
    category.setStatus(true);

    categoryRepository.save(category);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryResponse> searchAllCategoriesByStatus(Boolean status) {
    List<Category> categories = (List<Category>) categoryRepository.findAllByParams(status);

    return categories.stream()
        .map(category -> CategoryMapper.builder().setCategory(category).buildCategoriesResponse())
        .collect(Collectors.toList());
  }


  @Override
  @Transactional(readOnly = true)
  public CategoryResponse findCategoryById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe"));

    if (category.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La categoria se encuentra desactivada");
    }

    return CategoryMapper.builder().setCategory(category).buildCategoriesResponse();
  }

  @Override
  @Transactional
  public void updateCategoryById(Long id, CategoryRequest categoryRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.CONFLICT, "Esta categoria no se puede editar");
    }

    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe"));

    if (category.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La categoria se encuentra desactivada");
    }

    String newName = categoryRequest.getName().trim();
    categoryDomainService.verifyCategoryNameAvailableExcludingId(newName, id);
    
    category.setName(newName);

    categoryRepository.save(category);
  }

  // Obtiene una categoria por su nombre y verifica que no exista
  @Override
  @Transactional
  public void changeStatusCategoryById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(ResponseStatus.CONFLICT, "No se puede cambiar el estado de esta categoria");
    }

    Category category = categoryRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe"));

    // Cambia el estado de la categoria a false y lo guarda
    category.setStatus(!category.isStatus());
    categoryRepository.save(category);
  }
}