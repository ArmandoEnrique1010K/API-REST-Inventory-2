package com.pe.inventoryapp.backend.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.response.CommonResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;
import com.pe.inventoryapp.backend.user.service.UserService;

import jakarta.validation.Valid;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
  private AuthenticationContextService authenticationContextService;

  @PostMapping("/register")
  public ResponseEntity<CommonResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    userService.registerUser(registerRequest);

    return ResponseEntity.status(201)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se ha registrado al usuario en el sistema"));
  }

  @GetMapping
  public ResponseEntity<?> listAllUsers(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) List<Long> idRoles) {
    Pageable pageable = PageRequest.of(page, 20);

    Page<ListUsersResponse> users = userService.findAllUsersByParams(name, idRoles, pageable);

    return ResponseEntity.status(200).body(users);
  }

  @GetMapping("/profile")
  public ResponseEntity<?> getUserProfile(Authentication authentication) {
    Long username = authenticationContextService.extractUserIdFromAuthentication(authentication);
    DetailUserResponse user = userService.findUserById(username);

    return ResponseEntity.status(200).body(user);
  }

  @PutMapping("/profile")
  public ResponseEntity<CommonResponse> updateUserProfile(Authentication authentication,
      @Valid @RequestBody ProfileRequest profileRequest, BindingResult result) {
    Long userId = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    userService.updateUserProfileById(userId, profileRequest);

    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Su perfil ha sido actualizado"));
  }

  @PutMapping("/roles")
  public ResponseEntity<CommonResponse> updateUserRoles(Authentication authentication,
      @Valid @RequestBody RolesRequest rolesRequest, BindingResult result) {
    Long id = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    userService.updateUserRolesById(id, rolesRequest);

    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se han cambiado los roles del usuario"));
  }

  // Nota: no usar un código de estado 204, porque no se puede devolver un body
  @PatchMapping("/{id}")
  public ResponseEntity<CommonResponse> changeStatusUser(@PathVariable Long id) {
    userService.changeStatusUserById(id);

    return ResponseEntity.status(200)
        .body(responseService.generateCommonResponse("success", ResponseStatusCodes.SUCCESS_RESPONSE,
            "Se cambio el estado del usuario"));
  }
}
