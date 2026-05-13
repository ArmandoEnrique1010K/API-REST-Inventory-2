package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.user.model.data.RoleName;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.mapper.UserMapper;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
import com.pe.inventoryapp.backend.user.model.response.ListUsersByRoleUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;
import com.pe.inventoryapp.backend.user.model.response.RolesByUserResponse;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import com.pe.inventoryapp.backend.user.repository.specifications.UserSpecifications;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserDomainService userDomainService;

  public UserServiceImpl(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      UserDomainService userDomainService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userDomainService = userDomainService;
  }

  @Transactional
  @Override
  public void registerUser(RegisterRequest registerRequest) {

    userDomainService.verifyUserEmailExists(registerRequest.getEmail());
    userDomainService.validateBannedUserEmail(registerRequest.getEmail(), "El correo electronico es invalido");

    User user = new User();
    user.setFirstname(registerRequest.getFirstname().trim());
    user.setLastname(registerRequest.getLastname().trim());
    user.setEmail(registerRequest.getEmail().trim());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setDni(registerRequest.getDni());

    // Asigna los roles al usuario en base a las opciones marcadas
    user.setRole(
      registerRequest.getRole());
      // userDomainService.getRoles(
            // registerRequest.getUser(),
            // registerRequest.getAdmin(),
            // registerRequest.getSecretary(),
            // registerRequest.getOperator()                  
          // ));

    user.setActive(true);
    userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ListUsersResponse> findAllUsersByParams(
      String keyword, RoleName role, Pageable pageable) {
    Page<User> users = null;

    /**
     * IMPORTANTE:
     * - NO usamos fetchRelations()
     * - Dejamos que Hibernate cargue roles con @BatchSize
     * - Así mantenemos paginación real en DB
     */

    // if (roleIds == null || roleIds.isEmpty()) {

    //   System.out.println("SIN ROLES");

    //   Specification<User> spec = (UserSpecifications.keywordContains(keyword));

    //   Pageable sortedPageable = PageRequest.of(
    //       pageable.getPageNumber(),
    //       pageable.getPageSize(),
    //       Sort.by("id").descending());

    //   users = userRepository.findAll(spec, sortedPageable);
    // } else {
    //   /**
    //    * Aquí entra hasExactRoles
    //    * - NO usa JOIN en la query principal
    //    * - Usa subqueries
    //    * - Compatible con paginación
    //    */
    //   Specification<User> spec = (UserSpecifications.keywordContains(keyword))
    //       .and(UserSpecifications.hasExactRoles(roleIds));

    //   Pageable sortedPageable = PageRequest.of(
    //       pageable.getPageNumber(),
    //       pageable.getPageSize(),
    //       Sort.by("id").descending());

    //   users = userRepository.findAll(spec, sortedPageable);
    // }

    Specification<User> spec = (UserSpecifications.keywordContains(keyword))
    //.and(UserSpecifications.hasExactRoles(roleIds));
    .and(UserSpecifications.hasRole(role));

    // Pageable sortedPageable = PageRequest.of(
    // pageable.getPageNumber(),
    // pageable.getPageSize());
    // Sort.by("id").descending());

    users = userRepository.findAll(spec, pageable);

    /**
     * Aquí se disparará la carga de roles
     * - Hibernate hará 1 query adicional con IN (...)
     * - Gracias a @BatchSize
     */
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
        users.isLast());

    return pageResponse;
  }

  @Override
  public List<ListUsersByRoleUserResponse> findFirstTenUsersByName(String keyword) {

    Pageable pageable = PageRequest.of(
        0,
        10,
        Sort.by("id").descending());

    Specification<User> spec = (UserSpecifications.keywordContains(keyword))
        .and(UserSpecifications.isActive());

    List<User> users = userRepository.findAll(spec, pageable).getContent();

    return users.stream().map(user -> UserMapper.builder().setUser(user).buildListUsersByRoleUserResponse())
        .collect(Collectors.toList());
  }

  @Override
  public RolesByUserResponse getRolesByUser(Long idUser) {
    if (idUser == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    User user = userRepository.findByIdWithRoles(idUser)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "El usuario no existe"));

    return UserMapper.builder()
        .setUser(user)
        .buildRolesByUserResponse();
  }

  @Override
  public void updateUserRolesById(Long id, RolesRequest rolesRequest) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    // Primero verifica si existe otro usuario con el rol de administrador para no
    // dejar el sistema sin administradores
    // userDomainService.verifyUserByRoleAdminExist(rolesRequest.getAdmin(), id);

    userDomainService.verifyUserByRoleAdminExist(id);
    User user = userRepository.findByIdWithRoles(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "El usuario no existe"));

    userDomainService.validateBannedUserEmail(user.getEmail(), "No puedes alterar los roles de este usuario");
    
    // Verifica que el usuario este activo para que pueda cambiar los roles
    if (user.isActive() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El usuario debe estar activo para cambiar sus roles");
    }

    // List<Role> roles = userDomainService
    //     .getRoles(
    //         rolesRequest.getUser(),
    //         rolesRequest.getAdmin(),
    //         rolesRequest.getSecretary(),
    //         rolesRequest.getOperator());
    // * En la consola he notado que se hace una query adicional para borrar la
    // relacion anterior del usuario con los roles, recordar que se hace 4 queries
    // de tipo insert

    // user.setRoles(roles);

    user.setRole(rolesRequest.getRole());
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void changeStatusUserById(Long id_user, Long id_authenticated_user) {
    if (id_user == null || id_authenticated_user == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
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

    userDomainService.validateBannedUserEmail(user.getEmail(), "Este usuario no se puede bloquear del sistema");


    User userLogged = userRepository.findById(
        id_authenticated_user).orElseThrow(
            () -> new BusinessException(
                ResponseStatus.NOT_FOUND,
                "El usuario no existe"));

    // userDomainService
    //     .verifyUserByRoleAdminExist(user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")),
    //         id_user);
    userDomainService.verifyUserByRoleAdminExist(id_user);

    if (user.equals(userLogged)) {
      throw new BusinessException(
          ResponseStatus.CONFLICT, "No puedes desactivar tu propia cuenta");
    }

    user.setActive(!user.isActive());
    userRepository.save(user);
  }

}