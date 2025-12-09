package com.pe.inventoryapp.backend.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.security.config.PasswordEncoderConfig;
import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.mapper.UserMapper;
import com.pe.inventoryapp.backend.user.model.request.PasswordRequest;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;
import com.pe.inventoryapp.backend.user.repository.RoleRepository;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoderConfig passwordEncoderConfig;

  // Registra un nuevo usuario en el sistema
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

  // Lista todos los usuarios
  @Override
  @Transactional(readOnly = true)
  public List<ListUsersResponse> findAll() {
    List<User> users = (List<User>) userRepository.findAll();
    return users.stream().map(user -> UserMapper.builder().setUser(user).buildListUserResponse())
        .collect(Collectors.toList());
  }

  // Busca un usuario por su ID
  @Override
  @Transactional(readOnly = true)
  public Optional<DetailUserResponse> findById(Long id) {
    return userRepository.findById(id).map(user -> UserMapper.builder().setUser(user).buildDetailUserResponse());
  }

  // Actualiza el perfil del usuario
  @Override
  @Transactional
  public String updateProfile(Long id, ProfileRequest profileRequest) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

    user.setFirstname(profileRequest.getFirstname());
    user.setLastname(profileRequest.getLastname());
    user.setEmail(profileRequest.getEmail());
    user.setDni(profileRequest.getDni());

    userRepository.save(user);
    return "Su perfil ha sido actualizado";
  }

  // Actualiza la contraseña del usuario (si se acuerda su contraseña anterior)
  @Override
  @Transactional
  public String updatePassword(Long id, PasswordRequest passwordRequest) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

    String encodedPassword = passwordEncoderConfig.passwordEncoder().encode(passwordRequest.getNewPassword());
    user.setPassword(encodedPassword);

    userRepository.save(user);
    return "Contraseña del usuario actualizada";
  }

  // Elimina un usuario del sistema
  @Override
  @Transactional
  public void remove(Long id) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isPresent()) {
      userRepository.deleteById(id);
    } else {
      throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
    }
  }

  // Verifica si el email del usuario ya existe, de lo contrario lanza una
  // excepcion
  @Override
  @Transactional(readOnly = true)
  public void verifyUserEmailExists(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new FieldValidation("email", "El usuario con ese email ya existe, introduzca otro email");
    }
  }
}
