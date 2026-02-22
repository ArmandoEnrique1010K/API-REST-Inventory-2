package com.pe.inventoryapp.backend.auth.controller;

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
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.CommonResponseWithSecretField;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*")
public class AuthController {
  private final AuthService authService;
  private final ResponseService responseService;
  private final ValidationService validationService;

  public AuthController (AuthService authService, ResponseService responseService, ValidationService validationService) {
    this.authService = authService;
    this.responseService = responseService;
    this.validationService = validationService; 
  }

  // Nota: El endpoint POST "/" ya esta siendo manejado por Spring Security

  @PostMapping("/forgot-password")
  public ResponseEntity<CommonResponseWithSecretField> forgotUserPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    String requestId = authService.processUserForgotPasswordAndReturnRequestId(forgotPasswordRequest.getEmail());

    //* En el campo secreto pasa el requestId
    CommonResponseWithSecretField response = responseService.generateSucessfullResponseWithSecretField(ResponseStatus.SUCCESS, 
      "Si el correo existe, se le enviará un código de verificación al correo", requestId);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PostMapping("/validate-token")
  public ResponseEntity<CommonResponseWithSecretField> validateUserToken(@Valid @RequestBody ValidateTokenRequest validateTokenRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    String resetToken = authService.validateAndActivateResetToken(validateTokenRequest);

    CommonResponseWithSecretField response = responseService.generateSucessfullResponseWithSecretField(ResponseStatus.SUCCESS,
        "El token es válido, puede cambiar su contraseña", resetToken);
    return ResponseEntity.status(response.status()).body(response);
  }

  // SI EL USUARIO QUIERE CAMBIAR DE CONTRASEÑA
  // REQUIERE QUE EL TOKEN SEA VALIDADO
  @PutMapping("/change-password")
  public ResponseEntity<CommonResponse>updateUserPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
      BindingResult result) {

    validationService.validateFieldsAndThrowResponse(result);
    authService.updateUserPassword(changePasswordRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Su contraseña ha sido modificada correctamente");
    return ResponseEntity.status(response.status()).body(response);
  }

  // CERRAR SESION, BORRA LAS COOKIES EN EL CUAL ESTA ALMACENADO EL JWT
  @PostMapping("/logout")
  public ResponseEntity<CommonResponse> logout(HttpServletResponse httpServletResponse) {
    authService.logout(httpServletResponse);
    
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Ha cerrado sesión correctamente");
    return ResponseEntity.status(response.status()).body(response);
  }
}

// SI EL USUARIO QUIERE CAMBIAR DE CONTRASEÑA
// 1. El usuario debe escribir su correo electronico
// 2. El sistema enviara un token de 6 digitos al correo del usuario
// 3. El usuario debe ingresar el token para activarlo
// 4. El usuario debe ingresar su nueva contraseña 
// 5. El usuario podra iniciar sesión con la nueva contraseña