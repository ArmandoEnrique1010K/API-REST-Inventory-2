package com.pe.inventoryapp.backend.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.product.model.entity.Type;
import com.pe.inventoryapp.backend.product.model.mapper.TypeMapper;
import com.pe.inventoryapp.backend.product.model.request.TypeRequest;
import com.pe.inventoryapp.backend.product.model.response.TypeResponse;
import com.pe.inventoryapp.backend.product.repository.TypeRepository;

public class TypeServiceImpl implements TypeService {

  @Autowired
  private TypeRepository typeRepository;

  @Override
  @Transactional
  public void saveType(TypeRequest typeRequest) {
    String name = typeRequest.getName().trim();
    verifyTypeNameExist(name);

    Type type = new Type();
    type.setName(name);

    typeRepository.save(type);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TypeResponse> listAllTypes() {
    List<Type> types = (List<Type>) typeRepository.findAll();

    return types.stream().map(type -> TypeMapper.builder().setType(type).buildTypeListResponse())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void updateTypeById(Long id, TypeRequest typeRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Type type = typeRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El tipo no existe"));

    String newName = typeRequest.getName().trim();
    verifyTypeNameExistById(newName, id);
    type.setName(newName);

    typeRepository.save(type);
  }

  private void verifyTypeNameExist(String name) {
    if (typeRepository.existsByName(name)) {
      throw new FieldValidation("name", "Este nombre ya está en uso");
    }
  }

  private void verifyTypeNameExistById(String name, Long id) {
    if (typeRepository.existsByNameAndIdNot(name, id)) {
      throw new FieldValidation(
          "name",
          "Este nombre ya está en uso");
    }
  }

  @Override
  public TypeResponse findTypeById(Long id) {
        if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Type type = typeRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe"));

    return TypeMapper.builder().setType(type).buildTypeListResponse();
  }
}

