package com.pe.inventoryapp.backend.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.user.model.response.RoleResponse;
import com.pe.inventoryapp.backend.user.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
  @Autowired
  private RoleService roleService;
  
  @GetMapping
  public ResponseEntity<?> listAllRoles() {
    List<RoleResponse> roles = roleService.findAllRoles();
    return ResponseEntity.status(200).body(roles);
  }
}
