package com.pe.inventoryapp.backend.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.auth.models.request.ForgotPasswordRequest;
import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.service.UserService;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*")
public class AuthController {

  @Autowired
  private UserService userService;

  @Autowired
  private AuthService authService;

  @Autowired
  private ResponseService responseService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private AuthService registerService;

  // Nota: El endpoint "/" ya esta siendo manejado por Spring Security

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    Boolean existUser = authService.existsUserByEmail(forgotPasswordRequest.getEmail());

    if (existUser == false) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(responseService.generateCommonResponse("error", "El correo no existe"));
    }

    // Generar un token de 6 digitos
    String token = authService.generateToken();
    // Enviar el token al correo del usuario
    // authService.sendTokenToEmail(forgotPasswordRequest.getEmail(), token);

    authService.sendResetPasswordToken(forgotPasswordRequest.getEmail(), token);

    // Guardar ese token en la base de datos de tokens

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success",
            "Se le ha enviado el token de recuperación a su correo: " + token));
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
