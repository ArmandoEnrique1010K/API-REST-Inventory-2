package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.mapper.RoleMapper;
import com.pe.inventoryapp.backend.user.model.response.RoleResponse;
import com.pe.inventoryapp.backend.user.repository.RoleRepository;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class RoleServiceImpl implements RoleService{

  private final RoleRepository roleRepository;

  public RoleServiceImpl(RoleRepository roleRepository){
    this.roleRepository = roleRepository;
  }

  @Override
  public List<RoleResponse> findAllRoles() {
    List<Role> roles = (List<Role>) roleRepository.findAllByOrderByIdDesc();
    
    return roles.stream().map(RoleMapper::toResponse)
        .collect(Collectors.toList());
  }

}
