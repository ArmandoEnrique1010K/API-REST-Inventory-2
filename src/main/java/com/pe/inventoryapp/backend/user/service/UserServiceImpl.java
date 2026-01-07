package com.pe.inventoryapp.backend.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
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
    user.setRoles(getRoles(registerRequest.getAdmin(), registerRequest.getSecretary(), registerRequest.getOperator()));

    user.setActive(true);
    userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ListUsersResponse> findAllUsersByParams(
    String name, List<Long> roleIds, Pageable pageable) {
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

    List<ListUsersResponse> result = users.getContent().stream()
            .map(user -> UserMapper.builder()
                .setUser(user)
                .buildListUserResponse())
            .toList();

    PageResponse<ListUsersResponse> pageResponse = new PageResponse<>(
        result,
        users.getNumber(),
        users.getSize(),
        users.getTotalElements(),
        users.getTotalPages(),
        users.isFirst(),
        users.isLast()
    );

    return pageResponse;
  }

  @Override
  @Transactional(readOnly = true)
  public DetailUserResponse findUserById(Long id) {
    if (id == null) {
      throw new BusinessException(
          ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "El usuario no existe"));

    return UserMapper.builder()
        .setUser(user)
        .buildDetailUserResponse();
  }

  @Override
  @Transactional
  public void updateUserProfileById(Long id, ProfileRequest profileRequest) {
    if (id == null) {
      throw new BusinessException(
          ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "El usuario no existe"));

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

  @Override
  public void updateUserRolesById(Long id, RolesRequest rolesRequest) {
    if (id == null) {
      throw new BusinessException(
          ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Primero verifica si existe otro usuario con el rol de administrador para no
    // dejar el sistema sin administradores
    verifyUserByRoleAdminExist(rolesRequest.getAdmin(), id);

    User user = userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "El usuario no existe"));

    // Verifica que el usuario este activo para que pueda cambiar los roles
    if (user.isActive() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El usuario debe estar activo para cambiar sus roles");
    }

    List<Role> roles = getRoles(rolesRequest.getAdmin(), rolesRequest.getSecretary(), rolesRequest.getOperator());
    user.setRoles(roles);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void changeStatusUserById(Long id_user, Long id_authenticated_user) {
    if (id_user == null || id_authenticated_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    if (id_user == 1L) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "Este usuario no se puede bloquear del sistema");
    }

    User user = userRepository.findById(
        id_user).orElseThrow(
        () -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "El usuario no existe"));

    User userLogged = userRepository.findById(
        id_authenticated_user).orElseThrow(
        () -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "El usuario no existe"));
    
    verifyUserByRoleAdminExist(user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")), id_user);

    if (user.equals(userLogged)) {
      throw new BusinessException(
          ResponseStatus.CONFLICT, "No puedes desactivar tu propia cuenta");
    }

    user.setActive(!user.isActive());
    userRepository.save(user);
  }


  // MÉTODOS AUXILIARES
  // Agregar los roles al usuario
  private List<Role> getRoles(boolean isAdmin, boolean isSecretary, boolean isOperator) {
    List<Role> roles = new ArrayList<>();
    // Rol base obligatorio
    roles.add(getRoleOrThrow("ROLE_USER", "El rol de usuario no existe"));

    if (isOperator) {
      roles.add(getRoleOrThrow("ROLE_OPERATOR", "El rol de operador no existe"));
    }

    if (isSecretary) {
      roles.add(getRoleOrThrow("ROLE_SECRETARY", "El rol de secretario no existe"));
    }

    if (isAdmin) {
      roles.add(getRoleOrThrow("ROLE_ADMIN", "El rol de administrador no existe"));
    }

    return roles;
  }

  // Busca un rol por su nombre, de lo contrario lanza una excepcion
  private Role getRoleOrThrow(String roleName, String message) {
    return roleRepository.findByName(roleName)
        .orElseThrow(() -> new BusinessException(ResponseStatus.BAD_REQUEST, message));
  }

  // Verifica si el email del usuario ya existe, de lo contrario lanza una excepcion
  private void verifyUserEmailExists(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new FieldValidation("email", "Este correo ya está en uso");
    }
  }

  // Verificar que haya al menos un usuario con el rol de administrador, de lo contrario lanza una excepcion
  private void verifyUserByRoleAdminExist(boolean admin, Long id) {
    if (!roleRepository.existsByName("ROLE_ADMIN")) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          " El rol de administrador no existe");
    }

    boolean existsAnotherAdmin = userRepository.existsByRoleNameAndIdNot("ROLE_ADMIN", id);

    if (!existsAnotherAdmin && !admin) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "Debe existir al menos un administrador distinto a este usuario");
    }
  }
}