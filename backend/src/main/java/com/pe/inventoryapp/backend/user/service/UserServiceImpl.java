package com.pe.inventoryapp.backend.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
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

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  // Registra un nuevo usuario en el sistema
  @Transactional
  @Override
  public void registerUser(RegisterRequest registerRequest) {
    User user = new User();
    user.setFirstname(registerRequest.getFirstname());
    user.setLastname(registerRequest.getLastname());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
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
            ResponseStatusCodes.ENTITY_NOT_FOUND,
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
        .orElseThrow(() -> new BusinessException(
            ResponseStatusCodes.ENTITY_NOT_FOUND,
            "El usuario no existe"));

    // Obtener el correo del usuario actual y el nuevo
    String currentEmail = user.getEmail();
    String newEmail = profileRequest.getEmail();

    // Verificar que el usuario haya modificado su email
    if (!currentEmail.equals(newEmail)) {
      verifyUserEmailExists(newEmail);
    }

    user.setFirstname(profileRequest.getFirstname());
    user.setLastname(profileRequest.getLastname());
    user.setEmail(profileRequest.getEmail());
    user.setDni(profileRequest.getDni());

    userRepository.save(user);
  }

  // Actualizar los roles del usuario
  @Override
  public void updateUserRoles(Long id, RolesRequest rolesRequest) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatusCodes.ENTITY_NOT_FOUND,
            "El usuario no existe"));

    List<Role> roles = getRoles(rolesRequest.isAdmin(), rolesRequest.isSecretary(), rolesRequest.isOperator());
    user.setRoles(roles);
    userRepository.save(user);
  }

  // Verifica si el email del usuario ya existe, de lo contrario lanza una
  // excepcion
  @Override
  @Transactional(readOnly = true)
  public void verifyUserEmailExists(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new FieldValidation("email", "El usuario con ese email ya existe");
    }
  }

  // Elimina un usuario del sistema
  @Override
  @Transactional
  public void deleteUser(Long id) {
    if (id == 1) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "Este usuario no se puede eliminar");
    }

    User user = userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatusCodes.ENTITY_NOT_FOUND,
            "El usuario no existe"));

    userRepository.delete(user);
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

  // Busca un rol por su nombre, de lo contrario lanza una excepcion
  private Role getRoleOrThrow(String roleName, String message) {
    return roleRepository.findByName(roleName)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.VALIDATION_ERROR, message));
  }
}
