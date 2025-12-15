package com.pe.inventoryapp.backend.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.auth.model.request.ForgotPasswordRequest;
import com.pe.inventoryapp.backend.auth.model.request.ValidateTokenRequest;
import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  // Nota: El endpoint POST "/" ya esta siendo manejado por Spring Security

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);

    authService.processForgotPassword(forgotPasswordRequest.getEmail());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse(
            "success",
            "Se le ha enviado un código de recuperación a su correo"));
  }

  @PostMapping("/validate-token")
  public ResponseEntity<?> validateToken(@Valid @RequestBody ValidateTokenRequest validateTokenRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    authService.validateResetToken(validateTokenRequest.getValue());

    return ResponseEntity.ok(
        responseService.generateCommonResponse(
            "success",
            "El token es válido"));
  }

  // SI EL USUARIO QUIERE CAMBIAR DE CONTRASEÑA
  // REQUIERE QUE EL TOKEN SEA VALIDADO
  @PutMapping("/change-password/{token}")
  public ResponseEntity<?> changeUserPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
      BindingResult result,
      @PathVariable String token) {

    validationService.validateFieldsAndThrowResponse(result);
    authService.changeUserPassword(token, changePasswordRequest);

    return ResponseEntity.ok(
        responseService.generateCommonResponse(
            "SUCCESS",
            "La contraseña fue cambiada correctamente"));
  }

}
// SI EL USUARIO QUIERE CAMBIAR DE CONTRASEÑA
// 1. Envia un correo al email del usuario con un token
// 2. El usuario debe ingresar el token
// 3. El usuario debe ingresar su nueva contraseña
// 4. El usuario debe iniciar sesión con la nueva contraseña
