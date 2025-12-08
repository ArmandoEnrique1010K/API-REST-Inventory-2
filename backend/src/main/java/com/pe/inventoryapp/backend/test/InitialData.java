package com.pe.inventoryapp.backend.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.RoleRepository;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Configuration
public class InitialData {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostConstruct
  public void init() {

    Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
      Role newRole = new Role();
      newRole.setName("ROLE_USER");
      return roleRepository.save(newRole);
    });

    roleRepository.findByName("ROLE_OPERATOR").orElseGet(() -> {
      Role newRole = new Role();
      newRole.setName("ROLE_OPERATOR");
      return roleRepository.save(newRole);
    });

    Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
      Role newRole = new Role();
      newRole.setName("ROLE_ADMIN");
      return roleRepository.save(newRole);
    });

    if (userRepository.findByEmail("correo@example.com").isEmpty()) {
      List<Role> roles = List.of(roleUser, roleAdmin);

      User user = new User();
      user.setFirstname("Administrador");
      user.setLastname("sin apellidos");
      user.setDni(12345678);
      user.setEmail("correo@example.com");
      user.setPassword(passwordEncoder.encode("12345"));
      user.setRoles(roles);

      userRepository.save(user);
    }
  }
}
