package com.pe.inventoryapp.backend.auth.service;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.SECRET_KEY;

import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.common.data.ErrorCode;
import com.pe.inventoryapp.backend.common.exception.InvalidPassword;
import com.pe.inventoryapp.backend.security.config.PasswordEncoderConfig;
import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import com.pe.inventoryapp.backend.user.service.UserTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoderConfig passwordEncoderConfig;

  @Autowired
  private UserTokenService userTokenService;

  // Extrae el id del usuario desde el JWT del header
  @Override
  @Transactional(readOnly = true)
  public Long extractIdUserFromClaims(String header) {
    String token = header.replace("Bearer ", "");

    Claims claims = Jwts.parser()
        .verifyWith((SecretKey) SECRET_KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    Long id = claims.get("id", Long.class);

    return id;
  }

  // Obtiene el usuario por su id
  @Override
  @Transactional(readOnly = true)
  public User findUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID:" + id));
  }

  // Obtiene el id del usuario por su email
  @Override
  @Transactional(readOnly = true)
  public Long findIdByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(User::getId)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
  }

  // Verifica si el email del usuario existe (devuelve true o false)
  @Override
  @Transactional(readOnly = true)
  public boolean existsUserByEmail(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      return true;
    }
    return false;
  }

  // Cambia la contraseña actual del usuario y lo guarda (si el usuario no se
  // acuerda de su contraseña anterior)
  @Override
  @Transactional
  public void changePassword(String token, ChangePasswordRequest changePasswordRequest) {

    Long userId = userTokenService.findUserIdByUserToken(token);

    User user = userRepository.findById(
        userId)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    if (passwordEncoderConfig.passwordEncoder().matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
      throw new InvalidPassword(ErrorCode.PASSWORD_REUSE_NOT_ALLOWED.getDefaultMessage());
    }

    user.setPassword(passwordEncoderConfig.passwordEncoder().encode(
        changePasswordRequest.getConfirmNewPassword()));
    userRepository.save(user);

    userTokenService.invalidateToken(token);
  }

  @Override
  public DetailUserResponse findById(Long id) {

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

}
