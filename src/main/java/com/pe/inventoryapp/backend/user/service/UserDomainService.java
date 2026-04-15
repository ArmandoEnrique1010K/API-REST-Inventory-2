package com.pe.inventoryapp.backend.user.service;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.user.model.data.RoleName;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class UserDomainService {
  private final UserRepository userRepository;

  public UserDomainService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // MÉTODOS AUXILIARES
  // Agregar los roles al usuario
  // public List<Role> getRoles(boolean isUser, boolean isAdmin, boolean isSecretary, boolean isOperator) {

  //   List<String> roleNames = new ArrayList<>();
  //   if (isUser)
  //     roleNames.add("ROLE_USER");
  //   if (isOperator)
  //     roleNames.add("ROLE_OPERATOR");
  //   if (isSecretary)
  //     roleNames.add("ROLE_SECRETARY");
  //   if (isAdmin)
  //     roleNames.add("ROLE_ADMIN");

  //   List<Role> roles = roleRepository.findByNameIn(roleNames);

  //   if (roles.size() != roleNames.size()) {
  //     throw new BusinessException(ResponseStatus.BAD_REQUEST, "Uno o más roles no existen");
  //   }

  //   return roles;
  // }

  // Busca un rol por su nombre, de lo contrario lanza una excepcion
  // public Role getRoleOrThrow(String roleName, String message) {
  //   return roleRepository.findByName(roleName)
  //       .orElseThrow(() -> new BusinessException(ResponseStatus.BAD_REQUEST, message));
  // }

  // Verifica si el email del usuario ya existe, de lo contrario lanza una
  // excepcion
  public void verifyUserEmailExists(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new FieldValidation("email", "Este correo ya está en uso");
    }
  }

  // Verificar que haya al menos un usuario con el rol de administrador, de lo
  // contrario lanza una excepcion
  public void verifyUserByRoleAdminExist(Long id) {
    // if (!roleRepository.existsByName("ROLE_ADMIN")) {
    // throw new BusinessException(
    // ResponseStatus.NOT_FOUND,
    // " El rol de administrador no existe");
    // }

    boolean existsAnotherAdmin = userRepository.existsByRoleAndIdNot(RoleName.ROLE_ADMIN, id);

    // if (!existsAnotherAdmin && !admin) {
if(!existsAnotherAdmin){
    throw new BusinessException(
          ResponseStatus.CONFLICT,
          "Debe existir al menos un administrador distinto a este usuario");
    }
  }
}
