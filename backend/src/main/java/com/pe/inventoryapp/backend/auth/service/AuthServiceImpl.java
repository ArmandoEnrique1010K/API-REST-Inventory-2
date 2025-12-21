package com.pe.inventoryapp.backend.auth.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.entity.UserToken;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import com.pe.inventoryapp.backend.user.repository.UserTokenRepository;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserTokenRepository userTokenRepository;

  @Autowired
  private MailerSendService mailerSendService;

  // Obtiene el id del usuario por su email
  @Override
  @Transactional(readOnly = true)
  public Long findUserIdByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(User::getId)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
  }

  // Envia un correo al usuario con un token de 6 digitos
  @Override
  @Transactional
  public void processForgotPassword(String email) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));

    // invalidar tokens previos
    userTokenRepository.deleteByUser(user);

    String token = createResetToken(user);
    mailerSendService.sendResetPasswordToken(user.getEmail(), token);
  }

  // Verifica si el token de 6 digitos es valido
  @Transactional
  public void validateAndActivateResetToken(String token) {

    UserToken userToken = userTokenRepository.findByToken(token)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.AUTH_TOKEN_EXPIRED));

    if (userToken.getExpirationTime().isBefore(LocalDateTime.now())) {
      throw new BusinessException(ResponseStatusCodes.AUTH_TOKEN_EXPIRED);
    }

    if (userToken.isActive()) {
      return; // idempotente, no hace nada si ya está activo
    }

    userToken.setActive(true);
    userTokenRepository.save(userToken);
  }

  // Cambia la contraseña actual del usuario y lo guarda (si el usuario no se
  // acuerda de su contraseña anterior)
  @Override
  @Transactional
  public void updateUserPassword(String token, ChangePasswordRequest changePasswordRequest) {
    UserToken userToken = getValidUserTokenOrThrow(token);
    User user = userToken.getUser();

    if (userToken.isActive() == false) {
      throw new BusinessException(ResponseStatusCodes.AUTH_TOKEN_EXPIRED, "El token de 6 digitos ha expirado");
    }

    if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
      throw new BusinessException(ResponseStatusCodes.VALIDATION_ERROR, "Las contraseñas no coinciden");
    }

    if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
      throw new BusinessException(ResponseStatusCodes.PASSWORD_REUSE_NOT_ALLOWED);
    }

    user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    userRepository.save(user);

    // invalidar token
    userTokenRepository.delete(userToken);

  }

  // =======================
  // PRIVATE HELPERS
  // =======================

  // Crea un token de 6 digitos de "corta vida" para el usuario por su email
  private String createResetToken(User user) {

    String token = String.format("%06d", (int) (Math.random() * 1_000_000));

    UserToken userToken = new UserToken();
    userToken.setUser(user);
    userToken.setToken(token);
    userToken.setActive(false);
    userToken.setExpirationTime(LocalDateTime.now().plusMinutes(5));

    userTokenRepository.save(userToken);
    return token;
  }

  // Verifica si el token de 6 digitos es valido y si no ha expirado
  private UserToken getValidUserTokenOrThrow(String token) {

    UserToken userToken = userTokenRepository.findByToken(token)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.AUTH_TOKEN_EXPIRED));

    if (userToken.getExpirationTime().isBefore(LocalDateTime.now())) {
      throw new BusinessException(ResponseStatusCodes.AUTH_TOKEN_EXPIRED);
    }

    userToken.setActive(true);
    userTokenRepository.save(userToken);

    return userToken;
  }
}
