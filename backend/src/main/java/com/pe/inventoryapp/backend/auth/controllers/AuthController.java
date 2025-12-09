package com.pe.inventoryapp.backend.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.auth.models.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.auth.models.request.ForgotPasswordRequest;
import com.pe.inventoryapp.backend.auth.models.request.ValidateTokenRequest;
import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.auth.service.MailerSendService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.security.config.PasswordEncoderConfig;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.service.UserService;
import com.pe.inventoryapp.backend.user.service.UserTokenService;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*")
public class AuthController {

  @Autowired
  private AuthService authService;

  @Autowired
  private ResponseService responseService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private UserTokenService userTokenService;

  @Autowired
  private UserService userService;

  @Autowired
  private MailerSendService mailerSendService;
  @Autowired
  private PasswordEncoderConfig passwordEncoderConfig;

  // Nota: El endpoint "/" ya esta siendo manejado por Spring Security

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);

    // Verifica que el correo del usuario exista
    Boolean existUser = authService.existsUserByEmail(forgotPasswordRequest.getEmail());

    if (existUser == false) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(responseService.generateCommonResponse("error", "El correo no existe"));
    }

    // Crea un token para el usuario, lo envia al correo y lo guarda en la base de
    // datos
    String userToken = userTokenService.generateTokenForUserByEmail(forgotPasswordRequest.getEmail());
    mailerSendService.sendResetPasswordToken(forgotPasswordRequest.getEmail(), userToken);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success",
            "Se le ha enviado el token de recuperación a su correo: " + userToken));
  }

  @PostMapping("/validate-token")
  public ResponseEntity<?> validateToken(@Valid @RequestBody ValidateTokenRequest validateTokenRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    Boolean existToken = userTokenService.isTokenValid(validateTokenRequest.getValue());

    // CORREGIR AQUI, NO DEBE MOSTRAR UN ERRROR 500
    if (existToken == false || existToken == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(responseService.generateCommonResponse("error", "El token no existe"));
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", "El token es valido, puede cambiar su contraseña"));
  }

  // SI EL USUARIO QUIERE CAMBIAR DE CONTRASEÑA
  // REQUIERE QUE EL TOKEN SEA VALIDADO
  @PostMapping("/change-password/{value}")
  public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
      BindingResult result,
      @PathVariable String value) {
    validationService.validateFieldsAndThrowResponse(result);

    // BUSCA AL USUARIO POR EL TOKEN ENVIADO
    Long userId = userTokenService.findUserIdByUserToken(value);

    // ENCUENTRA AL USUARIO ACTUAL
    User optionalUser = userService.findUserById(userId);

    // Verifica que la nueva contraseña del usuario no sea la misma

    // Si el usuario ya no existe
    if (optionalUser == null) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "El usuario no existe"));
    }

    // Si el usuario no ha cambiado de contraseña
    if (passwordEncoderConfig.passwordEncoder().matches(changePasswordRequest.getNewPassword(),
        optionalUser.getPassword())) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "No has cambiado de contraseña"));
    }

    // Si las nuevas contraseñas no coinciden
    if (!changePasswordRequest.getNewPassword().trim().equals(changePasswordRequest.getConfirmNewPassword().trim())) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "Confirma tu contraseña, parece que no es la misma"));
    }

    // Cambia la contraseña del usuario
    authService.changePassword(changePasswordRequest.getNewPassword(), userId);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success",
            "Ha cambiado su contraseña, puede iniciar sesión con la nueva  contraseña"));
  }

  @GetMapping("/test-dotenv")
  public String getEnvironmentVariable() {
    Dotenv dotenv = Dotenv.load();
    String valor = dotenv.get("MY_TEST_ENV_VAR"); // Guardas el valor
    return valor;
  }
}
// SI EL USUARIO QUIERE CAMBIAR DE CONTRASEÑA
// 1. Envia un correo al email del usuario con un token
// 2. El usuario debe ingresar el token
// 3. El usuario debe ingresar su nueva contraseña
// 4. El usuario debe iniciar sesión con la nueva contraseña
