package com.pe.inventoryapp.backend.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.repository.RoleRepository;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class UserDomainService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserDomainService(UserRepository userRepository, RoleRepository roleRepository) {
      this.userRepository = userRepository;
      this.roleRepository = roleRepository;
    }

  // MÉTODOS AUXILIARES
  // Agregar los roles al usuario
  public List<Role> getRoles(boolean isAdmin, boolean isSecretary, boolean isOperator) {
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
  public Role getRoleOrThrow(String roleName, String message) {
    return roleRepository.findByName(roleName)
        .orElseThrow(() -> new BusinessException(ResponseStatus.BAD_REQUEST, message));
  }

  // Verifica si el email del usuario ya existe, de lo contrario lanza una excepcion
  public void verifyUserEmailExists(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new FieldValidation("email", "Este correo ya está en uso");
    }
  }

  // Verificar que haya al menos un usuario con el rol de administrador, de lo contrario lanza una excepcion
  public void verifyUserByRoleAdminExist(boolean admin, Long id) {
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
