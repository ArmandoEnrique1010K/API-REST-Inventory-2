package com.pe.inventoryapp.backend.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.auth.model.request.ChangePasswordRequest;
import com.pe.inventoryapp.backend.auth.model.request.ForgotPasswordRequest;
import com.pe.inventoryapp.backend.auth.model.request.ValidateTokenRequest;
import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
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
  public ResponseEntity<?> forgotUserPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    authService.processUserForgotPassword(forgotPasswordRequest.getEmail());

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se le ha enviado un código de recuperación a su correo"));
  }

  @PostMapping("/validate-token")
  public ResponseEntity<?> validateUserToken(@Valid @RequestBody ValidateTokenRequest validateTokenRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    authService.validateAndActivateResetToken(validateTokenRequest.getValue());

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "El token es válido, puede cambiar su contraseña"));
  }

  // SI EL USUARIO QUIERE CAMBIAR DE CONTRASEÑA
  // REQUIERE QUE EL TOKEN SEA VALIDADO
  @PutMapping("/change-password/{token}")
  public ResponseEntity<?> updateUserPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
      BindingResult result,
      @PathVariable String token) {

    validationService.validateFieldsAndThrowResponse(result);
    authService.updateUserPassword(token, changePasswordRequest);

    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "La contraseña fue cambiada correctamente"));
  }

  // TODO: CREAR UN ENDPOINT PARA CERRAR SESIÓN Y BORRAR EL TOKEN DE LAS COOKIES
}
// SI EL USUARIO QUIERE CAMBIAR DE CONTRASEÑA
// 1. Envia un correo al email del usuario con un token
// 2. El usuario debe ingresar el token
// 3. El usuario debe ingresar su nueva contraseña
// 4. El usuario debe iniciar sesión con la nueva contraseña

// Nuevo comentario