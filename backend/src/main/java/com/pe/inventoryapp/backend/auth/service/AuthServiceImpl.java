package com.pe.inventoryapp.backend.auth.service;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.SECRET_KEY;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.common.data.ErrorCode;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.ResourceNotFound;
import com.pe.inventoryapp.backend.security.config.PasswordEncoderConfig;
import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import com.pe.inventoryapp.backend.user.service.UserTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoderConfig passwordEncoderConfig;

  @Autowired
  private UserTokenService userTokenService;

  @Autowired
  private MailerSendService mailerSendService;

  // Extrae el id del usuario desde el JWT del header
  @Override
  @Transactional(readOnly = true)
  public Long extractUserIdFromClaims(String header) {
    String token = header.replace("Bearer ", "");

    Claims claims = Jwts.parser()
        .verifyWith((SecretKey) SECRET_KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    Long id = claims.get("id", Long.class);

    return id;
  }

  // Obtiene el id del usuario por su email
  @Override
  @Transactional(readOnly = true)
  public Long findUserIdByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(User::getId)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
  }

  // Cambia la contraseña actual del usuario y lo guarda (si el usuario no se
  // acuerda de su contraseña anterior)
  @Override
  @Transactional
  public void changeUserPassword(String token, ChangePasswordRequest changePasswordRequest) {

    Long userId = userTokenService.findUserIdByUserToken(token);

    User user = userRepository.findById(
        userId)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    String newPassword = changePasswordRequest.getNewPassword();
    String confirmPassword = changePasswordRequest.getConfirmNewPassword();

    // 1° verificar si la nueva contraseña y la confirmación de la nueva contraseña
    // son iguales
    if (!newPassword.equals(confirmPassword)) {
      System.out.println("No son iguales");
      throw new BusinessException(ErrorCode.PASSWORD_MISMATCH, ErrorCode.PASSWORD_MISMATCH.getDefaultMessage());
    }

    // 2° verificar si la nueva contraseña es igual a la anterior
    if (passwordEncoderConfig.passwordEncoder().matches(newPassword, user.getPassword())) {
      System.out.println("La nueva contraseña es igual a la anterior");
      throw new BusinessException(ErrorCode.PASSWORD_REUSE_NOT_ALLOWED,
          ErrorCode.PASSWORD_REUSE_NOT_ALLOWED.getDefaultMessage());
    }

    user.setPassword(passwordEncoderConfig.passwordEncoder().encode(
        changePasswordRequest.getConfirmNewPassword()));

    userRepository.save(user);
    userTokenService.invalidateToken(token);
  }

  @Override
  public DetailUserResponse findUserById(Long id) {

    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException());

    return DetailUserResponse.builder()
        .firstname(user.getFirstname())
        .lastname(user.getLastname())
        .email(user.getEmail())
        .dni(user.getDni())
        .roles(
            user.getRoles()
                .stream()
                .map(Role::getName)
                .toList())
        .build();
  }

  @Override
  @Transactional
  public void processForgotPassword(String email) {
    if (!userRepository.existsByEmail(email)) {
      throw new ResourceNotFound("El correo no existe");
    }

    String token = userTokenService.generateTokenForUserByEmail(email);
    mailerSendService.sendResetPasswordToken(email, token);
  }

  @Override
  @Transactional(readOnly = true)
  public void validateResetToken(String token) {

    if (!userTokenService.isTokenValid(token)) {
      throw new BusinessException(ErrorCode.TOKEN_EXPIRED, ErrorCode.TOKEN_EXPIRED.getDefaultMessage());
    }
  }

}
