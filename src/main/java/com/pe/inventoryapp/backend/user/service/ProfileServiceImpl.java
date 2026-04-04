package com.pe.inventoryapp.backend.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.mapper.UserMapper;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class ProfileServiceImpl implements ProfileService {

  private final UserRepository userRepository;
  private final UserDomainService userDomainService;

  public ProfileServiceImpl(
      UserRepository userRepository,
      UserDomainService userDomainService) {
    this.userRepository = userRepository;
    this.userDomainService = userDomainService;
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
      userDomainService.verifyUserEmailExists(newEmail);
    }

    user.setFirstname(profileRequest.getFirstname().trim());
    user.setLastname(profileRequest.getLastname().trim());
    user.setEmail(newEmail);
    user.setDni(profileRequest.getDni());

    userRepository.save(user);
  }
}
