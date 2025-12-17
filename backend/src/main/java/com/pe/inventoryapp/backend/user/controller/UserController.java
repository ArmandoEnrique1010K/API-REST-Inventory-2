package com.pe.inventoryapp.backend.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pe.inventoryapp.backend.common.service.AuthenticationService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.service.UserService;

import jakarta.validation.Valid;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private ResponseService responseService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private AuthenticationService authenticationService;

  // TODO: DEBE SER UNA PAGINA DE USUARIOS
  @GetMapping
  public List<?> listAll() {
    return userService.findAll();
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    userService.verifyUserEmailExists(registerRequest.getEmail());
    userService.registerUser(registerRequest);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", "Usuario registrado"));
  }

  // TODO: ESTE ES UN MÉTODO DE PRUEBA PARA OBTENER EL ID DESDE LOS HEADERS

  @GetMapping("/{id}")
  public ResponseEntity<?> findById(@PathVariable Long id, Authentication authentication) {
    Long userId = authenticationService.extractUserIdFromAuthentication(authentication);
    DetailUserResponse user = userService.findUserById(userId);
    return ResponseEntity.ok(user);
  }

  // @GetMapping("/profile")
  // public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String
  // header) {
  // Long id = jwtService.extractUserIdFromClaims(header);
  // DetailUserResponse user = userService.findUserById(id);
  // return ResponseEntity.ok(user);
  // }

  @GetMapping("/profile")
  public ResponseEntity<?> getProfile(Authentication authentication) {
    Long username = authenticationService.extractUserIdFromAuthentication(authentication);
    DetailUserResponse user = userService.findUserById(username);
    return ResponseEntity.ok(user);
  }

  @PutMapping("/profile")
  public ResponseEntity<?> updateProfile(Authentication authentication,
      @Valid @RequestBody ProfileRequest profileRequest, BindingResult result) {

    Long userId = authenticationService.extractUserIdFromAuthentication(authentication);

    validationService.validateFieldsAndThrowResponse(result);

    userService.updateUserProfile(userId, profileRequest);
    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", "Su perfil ha sido actualizado"));
  }

  // TODO: NO BORRAR ESTE ENDPOINT
  // @PutMapping("update-password")
  // public ResponseEntity<?> updatePassword(@RequestHeader("Authorization")
  // String header,
  // @Valid @RequestBody PasswordRequest passwordRequest, BindingResult result) {

  // Long id = jwtService.extractUserIdFromClaims(header);
  // validationService.validateFieldsAndThrowResponse(result);

  // Optional<DetailUserResponse> optionalUser = userService.findUserById(id);

  // // Si el usuario ya no existe
  // if (optionalUser.isEmpty()) {
  // return ResponseEntity.status(400)
  // .body(responseService.generateCommonResponse("error", "El usuario no
  // existe"));
  // }

  // // Si el usuario no ha cambiado de contraseña
  // if
  // (passwordRequest.getCurrentPassword().equals(passwordRequest.getNewPassword()))
  // {
  // return ResponseEntity.status(400)
  // .body(responseService.generateCommonResponse("error", "No has cambiado de
  // contraseña"));
  // }

  // // Si las nuevas contraseñas no coinciden
  // if
  // (!passwordRequest.getNewPassword().trim().equals(passwordRequest.getConfirmPassword().trim()))
  // {
  // return ResponseEntity.status(400)
  // .body(responseService.generateCommonResponse("error", "Confirma tu
  // contraseña, parece que no es la misma"));
  // }

  // String message = userService.updateUserPassword(id, passwordRequest);
  // return
  // ResponseEntity.status(200).body(responseService.generateCommonResponse("success",
  // message));
  // }

  @PutMapping("/roles")
  public ResponseEntity<?> updateRoles(Authentication authentication,
      @Valid @RequestBody RolesRequest rolesRequest, BindingResult result) {

    Long id = authenticationService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    userService.updateUserRoles(id, rolesRequest);
    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", "Se ha cambiado los roles del usuario"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {

    userService.deleteUser(id);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateCommonResponse("success", "Usuario eliminado"));
  }

}
