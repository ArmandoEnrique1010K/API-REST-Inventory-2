package com.pe.inventoryapp.backend.user.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.user.model.data.RoleName;
import com.pe.inventoryapp.backend.user.model.mapper.RoleMapper;
import com.pe.inventoryapp.backend.user.model.response.RoleResponse;

@Service
public class RoleServiceImpl implements RoleService{

  // private final RoleRepository roleRepository;

  // public RoleServiceImpl(RoleRepository roleRepository){
  //   this.roleRepository = roleRepository;
  // }

  @Override
  public List<RoleResponse> findAllRoles() {
    // List<Role> roles = (List<Role>) roleRepository.findAllByOrderByIdDesc();

    List<RoleName> roles = Arrays.asList(RoleName.values());

    return roles.stream().map(RoleMapper::toResponse).collect(Collectors.toList());
    // return roles.stream().map(RoleMapper::toResponse)
    //     .collect(Collectors.toList());
  }

}
