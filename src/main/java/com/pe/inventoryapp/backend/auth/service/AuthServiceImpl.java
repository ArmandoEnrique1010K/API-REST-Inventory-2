package com.pe.inventoryapp.backend.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.entity.UserToken;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import com.pe.inventoryapp.backend.user.repository.UserTokenRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserTokenRepository userTokenRepository;
  private final MailerSendService mailerSendService;
  
  public AuthServiceImpl(
      UserRepository userRepository, 
      PasswordEncoder passwordEncoder, 
      UserTokenRepository userTokenRepository,
      MailerSendService mailerSendService
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userTokenRepository = userTokenRepository;
    this.mailerSendService = mailerSendService;
  }

  // Obtiene el id del usuario por su email
  @Override
  @Transactional(readOnly = true)
  public Long findUserIdByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(User::getId)
        .orElseThrow(() -> new UsernameNotFoundException(
          // "No se encuentra un usuario con email: " + email + " en el sistema"
          "Ha ocurrido un error desconocido"
        ));
  }

  // Envia un correo al usuario con un token de 6 digitos
  @Override
  @Transactional
  public void processUserForgotPassword(String email) {

    // DEVUELVE UN ERROR FALSO, SIMULA QUE EL USUARIO EXISTE EN EL SISTEMA A PESAR DE QUE NO EXISTA, PARA EVITAR FILTRAR USUARIOS POR SU EMAIL
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(ResponseStatus.CREATED, "Si el correo existe, se le enviará un código de verificación al correo"));

    // Eliminar tokens previos
    userTokenRepository.deleteByUser(user);

    String token = createResetToken(user);
    mailerSendService.sendResetPasswordToken(user.getEmail(), token);
  }

  // Verifica si el token de 6 digitos es valido
  @Transactional
  public void validateAndActivateResetToken(String token) {
    UserToken userToken = userTokenRepository.findByToken(token)
        .orElseThrow(() -> new BusinessException(ResponseStatus.UNAUTHORIZED, "El token de 6 digitos es inválido o ha expirado, vuelva a solicitar un nuevo token"));

    if (userToken.getExpirationTime().isBefore(LocalDateTime.now())) {
      throw new BusinessException(ResponseStatus.UNAUTHORIZED, "El token de 6 digitos es inválido o ha expirado, vuelva a solicitar un nuevo token");
    }

    // Da un error si ya está activo el token
    if (userToken.isActive()) { 
      throw new BusinessException(ResponseStatus.CONFLICT, "Este token de 6 digitos ya fue activado");
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
      throw new BusinessException(ResponseStatus.UNAUTHORIZED, "El token de 6 digitos es inválido o ha expirado, vuelva a solicitar un nuevo token");
    }

    if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST, "Las contraseñas introducidas no coinciden");
    }

    if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
      throw new BusinessException(ResponseStatus.CONFLICT, "No puedes utilizar esta contraseña");
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
    // Este token expira en 10 minutos
    userToken.setExpirationTime(LocalDateTime.now().plusMinutes(10));

    userTokenRepository.save(userToken);
    return token;
  }

  // Verifica si el token de 6 digitos es valido y si no ha expirado
  private UserToken getValidUserTokenOrThrow(String token) {
    UserToken userToken = userTokenRepository.findByToken(token)
        .orElseThrow(() -> new BusinessException(ResponseStatus.UNAUTHORIZED, "El token de 6 digitos es inválido o ha expirado, vuelva a solicitar un nuevo token"));

    if (userToken.getExpirationTime().isBefore(LocalDateTime.now())) {
      throw new BusinessException(ResponseStatus.UNAUTHORIZED, "El token de 6 digitos es inválido o ha expirado, vuelva a solicitar un nuevo token");
    }

    userToken.setActive(true);
    userTokenRepository.save(userToken);
    return userToken;
  }

  @Override
  public void logout(HttpServletResponse response) {
    Cookie cookie = new Cookie("ACCESS_TOKEN", null);
    cookie.setHttpOnly(true);
    cookie.setSecure(true); // true en producción HTTPS
    cookie.setPath("/");
    cookie.setMaxAge(0); // elimina la cookie

    response.addCookie(cookie);
  }
}
