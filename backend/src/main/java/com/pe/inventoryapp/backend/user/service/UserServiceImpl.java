package com.pe.inventoryapp.backend.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ErrorCode;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.security.config.PasswordEncoderConfig;
import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.mapper.UserMapper;
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
    user.setRoles(getRoles(registerRequest.isAdmin(), registerRequest.isSecretary(), registerRequest.isOperator()));

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
  public DetailUserResponse findUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ErrorCode.ENTITY_NOT_FOUND,
            "El usuario no existe"));

    return UserMapper.builder()
        .setUser(user)
        .buildDetailUserResponse();
  }

  // Actualiza el perfil del usuario
  @Override
  @Transactional
  public void updateUserProfile(Long id, ProfileRequest profileRequest) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

    user.setFirstname(profileRequest.getFirstname());
    user.setLastname(profileRequest.getLastname());
    user.setEmail(profileRequest.getEmail());
    user.setDni(profileRequest.getDni());

    userRepository.save(user);
  }

  // TODO: NO BORRAR ESTE ENDPOINT
  // Actualiza la contraseña del usuario (si se acuerda su contraseña anterior)
  // @Override
  // @Transactional
  // public String updateUserPassword(Long id, PasswordRequest passwordRequest) {
  // User user = userRepository.findById(id)
  // .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID:
  // " + id));

  // String encodedPassword =
  // passwordEncoderConfig.passwordEncoder().encode(passwordRequest.getNewPassword());
  // user.setPassword(encodedPassword);

  // userRepository.save(user);
  // return "Contraseña del usuario actualizada";
  // }

  // Elimina un usuario del sistema
  @Override
  @Transactional
  public void remove(Long id) {
    Optional<User> optionalUser = userRepository.findById(id);
    // El primer usuario jamas podra ser eliminado
    if (id == 1) {
      throw new BusinessException(ErrorCode.INTERNAL_ERROR, "El primer usuario no se puede eliminar");
    }

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

  // Alterar los roles del usuario
  @Override
  public void alterRoles(Long id, RolesRequest rolesRequest) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

    List<Role> roles = getRoles(rolesRequest.isAdmin(), rolesRequest.isSecretary(), rolesRequest.isOperator());
    user.setRoles(roles);
    userRepository.save(user);
  }

  // MÉTODOS AUXILIARES

  // Agregar los roles al usuario
  private List<Role> getRoles(boolean isAdmin, boolean isSecretary, boolean isOperator) {
    List<Role> roles = new ArrayList<>();

    // Rol base obligatorio
    roles.add(getRoleOrThrow("ROLE_USER", "El rol usuario no existe"));

    if (isOperator) {
      roles.add(getRoleOrThrow("ROLE_OPERATOR", "El rol operador no existe"));
    }

    if (isSecretary) {
      roles.add(getRoleOrThrow("ROLE_SECRETARY", "El rol secretario no existe"));
    }

    if (isAdmin) {
      roles.add(getRoleOrThrow("ROLE_ADMIN", "El rol administrador no existe"));
    }

    return roles;
  }

  private Role getRoleOrThrow(String roleName, String message) {
    return roleRepository.findByName(roleName)
        .orElseThrow(() -> new BusinessException(ErrorCode.VALIDATION_ERROR, message));
  }

}
