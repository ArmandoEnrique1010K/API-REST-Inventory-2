package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.mapper.RoleMapper;
import com.pe.inventoryapp.backend.user.model.response.RoleResponse;
import com.pe.inventoryapp.backend.user.repository.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService{

  @Autowired
  private RoleRepository roleRepository;

  @Override
  public List<RoleResponse> findAllRoles() {

    List<Role> roles = (List<Role>) roleRepository.findAllByOrderByIdAsc();
    
    return roles.stream()
        .map(RoleMapper::toResponse)
        .collect(Collectors.toList());
  }
}
