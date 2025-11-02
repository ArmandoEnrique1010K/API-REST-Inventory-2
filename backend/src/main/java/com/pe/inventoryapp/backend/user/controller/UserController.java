package com.pe.inventoryapp.backend.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.user.model.request.PasswordRequest;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.service.UserService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserController {

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

  @GetMapping
  public List<?> listAll() {
    return userService.findAll();
  }

  @GetMapping("/profile")
  public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String header) {
    Long id = authService.extracIdFromClaims(header);
    Optional<DetailUserResponse> user = userService.findById(id);
    return ResponseEntity.ok(user);
  }

  @PutMapping("/profile")
  public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String header,
      @Valid @RequestBody ProfileRequest profileRequest, BindingResult result) {

    Long id = authService.extracIdFromClaims(header);

    // Validar campos
    validationService.validateFieldsAndThrowResponse(result);

    String currentEmail = userService.findById(id).get().getEmail();
    String newEmail = profileRequest.getEmail();

    // Verificar que el usuario no haya modificado su email
    // Primero debe obtener el email del usuario actual

    // No usar el operador !=, en su lugar utiliza el metodo equals
    if (!currentEmail.equals(newEmail)) {
      // System.out.println(userService.findById(id).get().getEmail());
      // System.out.println(profileRequest.getEmail());
      registerService.verifyUserEmailExists(profileRequest.getEmail());
    }

    if (userService.findById(id).isEmpty()) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "El usuario no existe"));
    }

    String message = userService.updateProfile(id, profileRequest);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success", message));
  }

  @PutMapping("update-password")
  public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String header,
      @Valid @RequestBody PasswordRequest passwordRequest, BindingResult result) {

    Long id = authService.extracIdFromClaims(header);
    validationService.validateFieldsAndThrowResponse(result);

    Optional<DetailUserResponse> optionalUser = userService.findById(id);

    // Si el usuario ya no existe
    if (optionalUser.isEmpty()) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "El usuario no existe"));
    }

    // Si el usuario no ha cambiado de contraseña
    if (passwordRequest.getCurrentPassword().equals(passwordRequest.getNewPassword())) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "No has cambiado de contraseña"));
    }

    // Si las nuevas contraseñas no coinciden
    if (!passwordRequest.getNewPassword().trim().equals(passwordRequest.getConfirmPassword().trim())) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "Confirma tu contraseña, parece que no es la misma"));
    }

    String message = userService.updatePassword(id, passwordRequest);
    return ResponseEntity.status(200).body(responseService.generateCommonResponse("success", message));

  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {

    if (userService.findById(id).isEmpty()) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "El usuario no existe"));
    }

    // El primer usuario jamas podra ser eliminado
    if (id == 1) {
      return ResponseEntity.status(400)
          .body(responseService.generateCommonResponse("error", "Este usuario no se puede eliminar"));
    }

    userService.remove(id);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", "Usuario eliminado"));
  }

}
