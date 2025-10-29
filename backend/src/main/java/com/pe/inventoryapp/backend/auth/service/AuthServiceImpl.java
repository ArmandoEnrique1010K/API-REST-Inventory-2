package com.pe.inventoryapp.backend.auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.auth.models.request.RegisterRequest;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.security.config.PasswordEncoderConfig;
import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.RoleRepository;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoderConfig passwordEncoderConfig;

  @Transactional
  @Override
  public String register(RegisterRequest registerRequest) {
    Optional<Role> rol = roleRepository.findByName("ROLE_USER");

    if (!rol.isPresent()) {
      throw new IllegalStateException("El rol 'usuario' no existe en el sistema");
    }

    List<Role> roles = new ArrayList<>();

    if (rol.isPresent()) {
      roles.add(rol.orElseThrow());
    }

    User user = new User();
    user.setFirstname(registerRequest.getFirstname());
    user.setLastname(registerRequest.getLastname());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoderConfig.passwordEncoder().encode(registerRequest.getPassword()));
    user.setDni(registerRequest.getDni());

    if (user.isOperator()) {
      Optional<Role> optionalAdmin = roleRepository.findByName("ROLE_OPERATOR");
      if (optionalAdmin.isPresent()) {
        roles.add(optionalAdmin.orElseThrow());
      }
    }

    if (user.isAdmin()) {
      Optional<Role> optionalManager = roleRepository.findByName("ROLE_ADMIN");
      if (optionalManager.isPresent()) {
        roles.add(optionalManager.orElseThrow());
      }
    }

    user.setRoles(roles);

    userRepository.save(user);

    return "Usuario registrado";
  }

  @Override
  public void verifyUserEmailExists(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new FieldValidation("email", "Ese usuario ya existe, pruebe con otro email");
    }
  }

  @Override
  public Long findIdByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(User::getId)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
  }

}
