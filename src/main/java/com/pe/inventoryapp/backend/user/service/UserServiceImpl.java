package com.pe.inventoryapp.backend.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    verifyUserEmailExists(registerRequest.getEmail());

    User user = new User();
    user.setFirstname(registerRequest.getFirstname().trim());
    user.setLastname(registerRequest.getLastname().trim());
    user.setEmail(registerRequest.getEmail().trim());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setDni(registerRequest.getDni());

    // Asigna los roles al usuario en base a las opciones marcadas
    user.setRoles(getRoles(registerRequest.isAdmin(), registerRequest.isSecretary(), registerRequest.isOperator()));

    userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ListUsersResponse> findAllUsersByParams(
    String name, 
      List<Long> roleIds, Pageable pageable) {
    Page<User> users = null;

    // Si existe alguna busqueda por roles, debe asegurarse que busque por los roles seleccionados
    if (roleIds == null || roleIds.isEmpty()) {
      users = userRepository.findAllByName(name, pageable);
    } else {
      users = userRepository.findAllByParamsAndHavingRoles(
          name,
          roleIds,
          roleIds.size(),
          pageable);
    }

    return users.map(user -> UserMapper.builder()
        .setUser(user)
        .buildListUserResponse());
  }

  // Busca un usuario por su ID
  @Override
  @Transactional(readOnly = true)
  public DetailUserResponse findUserById(Long id) {
    if (id == null) {
      throw new BusinessException(
          ResponseStatusCodes.COMMON_ERROR);
    }

    User user = userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatusCodes.ENTITY_NOT_FOUND,
            "El usuario no existe en el sistema"));

    return UserMapper.builder()
        .setUser(user)
        .buildDetailUserResponse();
  }

  // Actualiza el perfil del usuario
  @Override
  @Transactional
  public void updateUserProfileById(Long id, ProfileRequest profileRequest) {
    if (id == null) {
      throw new BusinessException(
          ResponseStatusCodes.COMMON_ERROR);
    }

    User user = userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatusCodes.ENTITY_NOT_FOUND,
            "El usuario no existe en el sistema"));

    // Obtener el correo del usuario actual y el nuevo
    String currentEmail = user.getEmail();
    String newEmail = profileRequest.getEmail().trim();

    // Verificar que el usuario haya modificado su email
    if (!currentEmail.equals(newEmail)) {
      verifyUserEmailExists(newEmail);
    }

    user.setFirstname(profileRequest.getFirstname().trim());
    user.setLastname(profileRequest.getLastname().trim());
    user.setEmail(newEmail);
    user.setDni(profileRequest.getDni());

    userRepository.save(user);
  }

  // Cambiar los roles del usuario
  @Override
  public void updateUserRolesById(Long id, RolesRequest rolesRequest) {
    if (id == null) {
      throw new BusinessException(
          ResponseStatusCodes.COMMON_ERROR);
    }

    verifyUserByRoleAdminExist(rolesRequest.isAdmin(), id);

    User user = userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatusCodes.ENTITY_NOT_FOUND,
            "El usuario no existe en el sistema"));

    List<Role> roles = getRoles(rolesRequest.isAdmin(), rolesRequest.isSecretary(), rolesRequest.isOperator());
    user.setRoles(roles);
    userRepository.save(user);
  }

  // Elimina un usuario del sistema
  @Override
  @Transactional
  public void deleteUserById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    if (id == 1L) {
      throw new BusinessException(
          ResponseStatusCodes.DEFAULT_RESOURCE,
          "Este usuario no se puede eliminar del sistema");
    }


    User user = userRepository.findById(id).orElseThrow(
        () -> new BusinessException(
            ResponseStatusCodes.ENTITY_NOT_FOUND,
            "El usuario no existe en el sistema"));

    if (user == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    } else {
      verifyUserByRoleAdminExist(user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")), id);
      userRepository.delete(user);
    }
  }

  // MÉTODOS AUXILIARES
  // Agregar los roles al usuario
  private List<Role> getRoles(boolean isAdmin, boolean isSecretary, boolean isOperator) {
    List<Role> roles = new ArrayList<>();

    // Rol base obligatorio
    roles.add(getRoleOrThrow("ROLE_USER", "El rol de usuario no existe en el sistema"));

    if (isOperator) {
      roles.add(getRoleOrThrow("ROLE_OPERATOR", "El rol de operador no existe en el sistema"));
    }

    if (isSecretary) {
      roles.add(getRoleOrThrow("ROLE_SECRETARY", "El rol de secretario no existe en el sistema"));
    }

    if (isAdmin) {
      roles.add(getRoleOrThrow("ROLE_ADMIN", "El rol de administrador no existe en el sistema"));
    }

    return roles;
  }

  // Busca un rol por su nombre, de lo contrario lanza una excepcion
  private Role getRoleOrThrow(String roleName, String message) {
    return roleRepository.findByName(roleName)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.VALIDATION_ERROR, message));
  }

  // Verifica si el email del usuario ya existe, de lo contrario lanza una excepcion
  private void verifyUserEmailExists(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new FieldValidation("email", "El usuario con ese email ya existe");
    }
  }

  // Verificar que haya al menos un usuario con el rol de administrador en el sistema, de lo contrario lanza una excepcion
  private void verifyUserByRoleAdminExist(boolean admin, Long id) {
    if (!roleRepository.existsByName("ROLE_ADMIN")) {
      throw new BusinessException(
          ResponseStatusCodes.ENTITY_NOT_FOUND,
          "El rol de administrador no existe en el sistema");
    }

    boolean existsAnotherAdmin = userRepository.existsByRoleNameAndIdNot("ROLE_ADMIN", id);

    if (!existsAnotherAdmin && !admin) {
      throw new BusinessException(
          ResponseStatusCodes.DEFAULT_RESOURCE,
          "Debe existir al menos un administrador distinto a este usuario");
    }
  }
}