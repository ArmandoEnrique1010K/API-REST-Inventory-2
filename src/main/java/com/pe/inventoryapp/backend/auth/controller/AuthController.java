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
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;

import jakarta.servlet.http.HttpServletResponse;
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
  public ResponseEntity<CommonResponse> forgotUserPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    authService.processUserForgotPassword(forgotPasswordRequest.getEmail());

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS, 
      "Si el correo existe, se le enviará un código de verificación al correo");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  @PostMapping("/validate-token")
  public ResponseEntity<CommonResponse> validateUserToken(@Valid @RequestBody ValidateTokenRequest validateTokenRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    authService.validateAndActivateResetToken(validateTokenRequest.getValue());

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "El token es válido, puede cambiar su contraseña");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  // SI EL USUARIO QUIERE CAMBIAR DE CONTRASEÑA
  // REQUIERE QUE EL TOKEN SEA VALIDADO
  @PutMapping("/change-password/{token}")
  public ResponseEntity<CommonResponse>updateUserPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
      BindingResult result,
      @PathVariable String token) {

    validationService.validateFieldsAndThrowResponse(result);
    authService.updateUserPassword(token, changePasswordRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Su contraseña ha sido modificada correctamente");
    return ResponseEntity.status(response.getStatus()).body(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<CommonResponse> logoutUser(HttpServletResponse httpServletResponse) {

    authService.logout(httpServletResponse);
    
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Ha cerrado sesión correctamente");
    return ResponseEntity.status(response.getStatus()).body(response);
  }
  

  // TODO: CREAR UN ENDPOINT PARA CERRAR SESIÓN Y BORRAR EL TOKEN DE LAS COOKIES
}
// SI EL USUARIO QUIERE CAMBIAR DE CONTRASEÑA
// 1. Envia un correo al email del usuario con un token
// 2. El usuario debe ingresar el token
// 3. El usuario debe ingresar su nueva contraseña
// 4. El usuario debe iniciar sesión con la nueva contraseña

// Nuevo comentario