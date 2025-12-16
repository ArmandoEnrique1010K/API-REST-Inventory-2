package com.pe.inventoryapp.backend.user.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
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
  public void registerUser(RegisterRequest registerRequest) {
    User user = new User();
    user.setFirstname(registerRequest.getFirstname());
    user.setLastname(registerRequest.getLastname());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoderConfig.passwordEncoder().encode(registerRequest.getPassword()));
    user.setDni(registerRequest.getDni());

    // Asigna los roles al usuario
    user.setRoles(getRoles(registerRequest.getRolesRequest()));

    userRepository.save(user);
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
  public Optional<DetailUserResponse> findUserById(Long id) {
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

  // MÉTODOS AUXILIARES

  // Agregar los roles al usuario
  private List<Role> getRoles(RolesRequest rolesRequest) {
    List<Role> userRoles = new ArrayList<>();
    Optional<Role> roleUser = roleRepository.findByName("ROLE_USER");

    if (roleUser.isPresent()) {
      userRoles.add(roleUser.orElseThrow(
          () -> new RuntimeException("No se pudo obtener el rol ROLE_USER")));
    }

    if (rolesRequest.getIsOperator()) {
      Optional<Role> roleOperator = roleRepository.findByName("ROLE_OPERATOR");
      if (roleOperator.isPresent()) {
        userRoles.add(roleOperator.orElseThrow());
      }
    }

    if (rolesRequest.getIsSecretary()) {
      Optional<Role> roleSecretary = roleRepository.findByName("ROLE_SECRETARY");
      if (roleSecretary.isPresent()) {
        userRoles.add(roleSecretary.orElseThrow());
      }
    }

    if (rolesRequest.getIsAdmin()) {
      Optional<Role> roleAdmin = roleRepository.findByName("ROLE_ADMIN");
      if (roleAdmin.isPresent()) {
        userRoles.add(roleAdmin.orElseThrow());
      }
    }

    return userRoles;
  }
}
