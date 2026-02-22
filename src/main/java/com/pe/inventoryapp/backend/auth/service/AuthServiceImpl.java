package com.pe.inventoryapp.backend.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.auth.model.entity.PasswordResetToken;
import com.pe.inventoryapp.backend.auth.model.entity.UserPasswordOtp;
import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.auth.model.request.ValidateTokenRequest;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.PasswordResetTokenRepository;
import com.pe.inventoryapp.backend.user.repository.UserPasswordOtpRepository;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final MailerSendService mailerSendService;
  private final UserPasswordOtpRepository userPasswordOtpRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  
  public AuthServiceImpl(
      UserRepository userRepository, 
      PasswordEncoder passwordEncoder, 
      MailerSendService mailerSendService,
      UserPasswordOtpRepository userPasswordOtpRepository,
      PasswordResetTokenRepository passwordResetTokenRepository
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.mailerSendService = mailerSendService;
    this.userPasswordOtpRepository = userPasswordOtpRepository;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
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
  public String processUserForgotPasswordAndReturnRequestId(String email) {

    // DEVUELVE UN ERROR FALSO, SIMULA QUE EL USUARIO EXISTE EN EL SISTEMA A PESAR DE QUE NO EXISTA, PARA EVITAR FILTRAR USUARIOS POR SU EMAIL
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(ResponseStatus.CREATED, "Si el correo existe, se le enviará un código de verificación al correo"));

    // Genera el token de 6 digitos
    String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    String requestId = UUID.randomUUID().toString();
    UserPasswordOtp entity = new UserPasswordOtp();
    entity.setUser(user);
    entity.setRequestId(requestId);
    entity.setOtpHash(sha256(otp)); // Lo hashea y lo guarda en BD
    entity.setAttempts(0);
    entity.setVerified(false);
    entity.setExpiresAt(LocalDateTime.now().plusMinutes(5));

    userPasswordOtpRepository.save(entity);
    mailerSendService.sendResetPasswordToken(user.getEmail(), otp);

    return requestId;
  }

  // Verifica si el token de 6 digitos es valido
  @Transactional
  public String validateAndActivateResetToken(ValidateTokenRequest validateTokenRequest) {

    String token = validateTokenRequest.getValue();
    String requestId = validateTokenRequest.getRequestId();

    // Si alguien llegara a quebrantar el requestId
    UserPasswordOtp entity = userPasswordOtpRepository.findByRequestId(requestId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.CREATED,
            "Ha ocurrido un grave error, vuelva a solicitar un token"));

    // Da un error si ya está activo el token
    if (entity.isVerified()) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El token ya fue usado");
    }

    if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new BusinessException(ResponseStatus.UNAUTHORIZED, "El token ha expirado");
    }

    // Solamente el usuario tendra 3 intentos
    if (entity.getAttempts() >= 3) {
      throw new BusinessException(ResponseStatus.UNAUTHORIZED, "Demasiados intentos fallidos");
    }

    entity.setAttempts(entity.getAttempts() + 1);

    if (!entity.getOtpHash().equals(sha256(token))) {
      userPasswordOtpRepository.save(entity);
      throw new BusinessException(ResponseStatus.UNAUTHORIZED, "Token incorrecto, vuelva a intentarlo");
    }

    entity.setVerified(true);
    userPasswordOtpRepository.save(entity);

    // Si todo sale bien, entonces ¿deberia generar el token largo?
    String resetToken = UUID.randomUUID().toString() + UUID.randomUUID();

    PasswordResetToken passwordResetToken = new PasswordResetToken();
    passwordResetToken.setUser(entity.getUser());
    passwordResetToken.setTokenHash(sha256(resetToken));
    passwordResetToken.setExpiresAt(LocalDateTime.now().plusMinutes(5));
    passwordResetToken.setUsed(false);

    passwordResetTokenRepository.save(passwordResetToken);
    return resetToken;
  }

  // Cambia la contraseña actual del usuario y lo guarda (si el usuario no se
  // acuerda de su contraseña anterior)
  @Override
  @Transactional
  public void updateUserPassword(ChangePasswordRequest changePasswordRequest) {
    // CAMPOS
    String resetToken = changePasswordRequest.getResetToken();

    // Si alguien llegara a quebrantar el requestId
    PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByTokenHash(sha256(
        resetToken))
        .orElseThrow(() -> new BusinessException(ResponseStatus.CREATED,
            "Ha ocurrido un grave error, vuelva a solicitar un token"));

    User user = passwordResetToken.getUser();


    // Si el token ya fue usado
    if (passwordResetToken.isUsed()) {
      throw new BusinessException(ResponseStatus.UNAUTHORIZED, "El token de reseteo ya ha sido usado, vuelva a solicitar un nuevo token");
    }

    // Si el token ya ha expirado
    if (passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new BusinessException(ResponseStatus.UNAUTHORIZED, "El token de reseteo ha expirado, vuelva a solicitar un nuevo token");
    }

    // Si las contraseñas no coinciden
    if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST, "Las contraseñas introducidas no coinciden");
    }

    // Si no ha cambiado su contraseña
    if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
      throw new BusinessException(ResponseStatus.CONFLICT, "No puedes utilizar esta contraseña");
    }

    passwordResetToken.setUsed(true);
    passwordResetTokenRepository.save(passwordResetToken);

      
    user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    userRepository.save(user);

    // invalidar token
    // passwordResetTokenRepository.save(passwordResetToken);
  }

  // =======================
  // PRIVATE HELPERS
  // =======================

  // UTILIDAD DE HASH
  public static String sha256(String value) {
      try {
          MessageDigest md = MessageDigest.getInstance("SHA-256");
          byte[] hash = md.digest(value.getBytes(StandardCharsets.UTF_8));
          return HexFormat.of().formatHex(hash);
      } catch (Exception e) {
          throw new IllegalStateException("Hash error");
      }
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
