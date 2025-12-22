package com.pe.inventoryapp.backend.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.service.AuthenticationService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;
import com.pe.inventoryapp.backend.user.service.UserService;

import jakarta.validation.Valid;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
  public ResponseEntity<?> listAll() {
    List<ListUsersResponse> users = userService.findAll();
    return ResponseEntity.status(200).body(users);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    userService.registerUser(registerRequest);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha creado el usuario"));
  }

  @GetMapping("/profile")
  public ResponseEntity<?> getProfile(Authentication authentication) {
    Long username = authenticationService.extractUserIdFromAuthentication(authentication);
    DetailUserResponse user = userService.findUserById(username);

    return ResponseEntity.status(200).body(user);
  }

  @PutMapping("/profile")
  public ResponseEntity<?> updateProfile(Authentication authentication,
      @Valid @RequestBody ProfileRequest profileRequest, BindingResult result) {
    Long userId = authenticationService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    userService.updateUserProfile(userId, profileRequest);

    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Su perfil ha sido actualizado"));
  }

  @PutMapping("/roles")
  public ResponseEntity<?> updateRoles(Authentication authentication,
      @Valid @RequestBody RolesRequest rolesRequest, BindingResult result) {
    Long id = authenticationService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    userService.updateUserRoles(id, rolesRequest);

    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se han cambiado los roles del usuario"));
  }

  // Nota: no usar un código de estado 204, porque no se puede devolver un body
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);

    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Usuario eliminado"));
  }
}
