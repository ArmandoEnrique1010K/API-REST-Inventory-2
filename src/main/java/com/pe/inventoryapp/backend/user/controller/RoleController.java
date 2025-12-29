package com.pe.inventoryapp.backend.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.user.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
  @Autowired
  private RoleService roleService;
  
  @GetMapping
  public ResponseEntity<?> listAllRoles() {
    return ResponseEntity.status(200).body(roleService.findAllRoles());
  }
}
