package com.pe.inventoryapp.backend.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.product.model.entity.Type;
import com.pe.inventoryapp.backend.product.model.mapper.TypeMapper;
import com.pe.inventoryapp.backend.product.model.request.TypeRequest;
import com.pe.inventoryapp.backend.product.model.response.TypeResponse;
import com.pe.inventoryapp.backend.product.repository.TypeRepository;

@Service
public class TypeServiceImpl implements TypeService {
  private final TypeRepository typeRepository;
  private final TypeDomainService typeDomainService;

  public TypeServiceImpl(TypeRepository typeRepository, TypeDomainService typeDomainService) {
    this.typeRepository = typeRepository;
    this.typeDomainService = typeDomainService;
  }

  @Override
  @Transactional
  public void saveType(TypeRequest typeRequest) {
    String name = typeRequest.getName().trim();
    typeDomainService.verifyTypeNameAvailable(name);

    Type type = new Type();
    type.setName(name);
    type.setStatus(true);

    typeRepository.save(type);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TypeResponse> listAllTypes() {
    List<Type> types = (List<Type>) typeRepository.findAllAndSortById();

    return types.stream().map(type -> TypeMapper.builder().setType(type).buildTypeListResponse())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void updateTypeById(Long id, TypeRequest typeRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Type type = typeRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El tipo no existe"));

    String newName = typeRequest.getName().trim();
    typeDomainService.verifyTypeNameAvailableExcludingId(newName, id);
    type.setName(newName);

    typeRepository.save(type);
  }

  @Override
  public TypeResponse findTypeById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Type type = typeRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El tipo no existe"));

    return TypeMapper.builder().setType(type).buildTypeListResponse();
  }

  @Override
  public List<TypeResponse> findAllActiveTypes() {
    List<Type> types = (List<Type>) typeRepository.findAllActivesAndSortById();

    return types.stream()
        .map(
            type -> TypeMapper.builder().setType(type).buildTypeListResponse())
        .collect(Collectors.toList());
  }

  @Override
  public void changeStatusTypeById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Type type = typeRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El tipo no existe"));

    type.setStatus(!type.isStatus());
    typeRepository.save(type);
  }
}
