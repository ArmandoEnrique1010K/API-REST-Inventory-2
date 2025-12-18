package com.pe.inventoryapp.backend.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.ResourceNotFound;
import com.pe.inventoryapp.backend.security.config.PasswordEncoderConfig;
import com.pe.inventoryapp.backend.user.model.entity.Role;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import com.pe.inventoryapp.backend.user.service.UserTokenService;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoderConfig passwordEncoderConfig;

  @Autowired
  private UserTokenService userTokenService;

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
      throw new BusinessException(ResponseStatusCodes.VALIDATION_PASSWORD_MISMATCH);
    }

    // 2° verificar si la nueva contraseña es igual a la anterior
    if (passwordEncoderConfig.passwordEncoder().matches(newPassword, user.getPassword())) {
      throw new BusinessException(ResponseStatusCodes.PASSWORD_REUSE_NOT_ALLOWED);
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
    if (!this.existsUserByEmail(email)) {
      throw new ResourceNotFound("El correo no existe");
    }

    String token = userTokenService.generateTokenForUserByEmail(email);
    sendResetPasswordToken(email, token);
  }

  @Override
  @Transactional(readOnly = true)
  public void validateResetToken(String token) {

    if (!userTokenService.isTokenValid(token)) {
      throw new BusinessException(ResponseStatusCodes.AUTH_TOKEN_EXPIRED);
    }
  }

  // Metodos auxiliares

  private void sendResetPasswordToken(String toEmail, String token) {
    // Configuración de MailerSend
    MailerSend ms = new MailerSend();

    Dotenv dotenv = Dotenv.load();
    String apiKey = dotenv.get("MAILERSEND_API_TOKEN");
    String testDomain = dotenv.get("MAILERSEND_TEST_DOMAIN");

    System.out.println(apiKey);
    System.out.println(testDomain);

    ms.setToken(apiKey);

    Email email = new Email();
    email.setFrom("Inventory App 2",
        testDomain);
    email.addRecipient("", toEmail);
    email.setSubject("Recuperar contraseña");

    String text = "Tu código para restablecer contraseña es: " + token;
    String html = "<p>Tu código de 6 digitos para restablecer contraseña es: <strong>" + token
        + "</strong>. Recuerda que tienes 5 minutos para restablecer tu contraseña antes que el código expire</p>";

    email.setPlain(text);
    email.setHtml(html);

    try {
      MailerSendResponse resp = ms.emails().send(email);
      System.out.println("Email enviado, messageId: " + resp.messageId);
    } catch (MailerSendException e) {
      e.printStackTrace();
    }

  }

  @Override
  public boolean existsUserByEmail(String email) {
    if (userRepository.findByEmail(email).get() != null) {
      return true;
    }
    return false;
  }

}
