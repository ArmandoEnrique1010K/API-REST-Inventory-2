package com.pe.inventoryapp.backend.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.response.ErrorResponse;
import com.pe.inventoryapp.backend.common.response.SuccessfulResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.user.model.request.PasswordRequest;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.service.UserService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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

    // String token = header.replace("Bearer ", "");

    // Claims claims = Jwts.parser()
    // .verifyWith((SecretKey) SECRET_KEY)
    // .build()
    // .parseSignedClaims(token)
    // .getPayload();

    // Long id = claims.get("id", Long.class);
    // System.out.println("ID recibido en getProfile: " + id);

    Long id = authService.extracIdFromClaims(header);
    Optional<DetailUserResponse> user = userService.findById(id);
    return ResponseEntity.ok(user);
  }

  @PutMapping("profile")
  public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String header,
      @Valid @RequestBody ProfileRequest profileRequest, BindingResult result) {

    Long id = authService.extracIdFromClaims(header);

    // Validar campos
    validationService.validateFieldsAndThrow(result);

    // Verificar que el usuario no haya modificado su email
    // Primero debe obtener el email del usuario actual
    if (userService.findById(id).get().getEmail() != profileRequest.getEmail()) {
      registerService.verifyUserEmailExists(profileRequest.getEmail());
    } else {
      return ResponseEntity.status(400)
          .body(responseService.generateErrorResponse("error", "El usuario ya existe", null));

    }

    if (userService.findById(id).isEmpty()) {
      return ResponseEntity.status(400)
          .body(responseService.generateErrorResponse("error", "El usuario no existe", null));
    }

    if (result.hasErrors()) {
      return ResponseEntity.status(400)
          .body(responseService.generateErrorResponse("error", "Complete los campos si o si", null));
    }

    String message = userService.updateProfile(id, profileRequest);
    return ResponseEntity.status(200).body(responseService.generateSuccessfulResponse("success", message));
  }

  @PutMapping("update-password")
  public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String header,
      @Valid @RequestBody PasswordRequest passwordRequest, BindingResult result) {

    Long id = authService.extracIdFromClaims(header);
    validationService.validateFieldsAndThrow(result);

    if (userService.findById(id).isEmpty()) {
      return ResponseEntity.status(400)
          .body(responseService.generateErrorResponse("error", "El usuario no existe", null));
    }

    if (result.hasErrors()) {
      return ResponseEntity.status(400)
          .body(responseService.generateErrorResponse("error", "Complete los campos si o si", null));
    }

    if (userService.validatePassword(id, passwordRequest)) {

      // TODO : CORREGIR AQUI, LAS CONTRASEÑAS NO COINCIDEN
      if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmPassword())) {
        System.out.println(passwordRequest.getNewPassword());
        System.out.println(passwordRequest.getConfirmPassword());
        return ResponseEntity.status(400)
            .body(responseService.generateErrorResponse("error", "Confirma tu contraseña, parece que no es la misma",
                null));
      } else {
        String message = userService.updatePassword(id, passwordRequest);
        return ResponseEntity.status(200).body(responseService.generateSuccessfulResponse("success", message));

      }

    } else {
      return ResponseEntity.status(400)
          .body(responseService.generateErrorResponse("error", "Intente de nuevo, no es la contraseña anterior", null));
    }

  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {

    if (userService.findById(id).isEmpty()) {
      return ResponseEntity.status(400)
          .body(responseService.generateErrorResponse("error", "El usuario no existe", null));
    }

    if (id == 1) {
      return ResponseEntity.status(400)
          .body(responseService.generateErrorResponse("error", "Este usuario no se puede eliminar", null));

    }

    userService.remove(id);
    // return ResponseEntity.ok("Se ha eliminado el usuario");

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateSuccessfulResponse("success", "Usuario eliminado"));

  }

}
