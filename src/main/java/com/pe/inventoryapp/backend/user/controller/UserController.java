package com.pe.inventoryapp.backend.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.user.model.data.RoleName;
import com.pe.inventoryapp.backend.user.model.entity.UserPrincipal;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.model.request.RolesRequest;
import com.pe.inventoryapp.backend.user.model.response.ListUsersByRoleUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;
import com.pe.inventoryapp.backend.user.model.response.RolesByUserResponse;
import com.pe.inventoryapp.backend.user.service.UserService;

import jakarta.validation.Valid;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;
  private final ResponseService responseService;
  private final ValidationService validationService;

  public UserController(
      UserService userService,
      ResponseService responseService,
      ValidationService validationService) {
    this.userService = userService;
    this.responseService = responseService;
    this.validationService = validationService;
  }

  @PostMapping("/register")
  public ResponseEntity<CommonResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    userService.registerUser(registerRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se registro el usuario");
    return ResponseEntity.status(response.status()).body(response);
  }

  @GetMapping
  public ResponseEntity<?> listAllUsers(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(required = false) String name,
      // @RequestParam(required = false) List<Long> idRoles
      @RequestParam(required = false) RoleName role,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "desc") String direction
    ) {
      Sort sort = buildSort(sortBy, direction);
    Pageable pageable = PageRequest.of(page, 20, sort);

    PageResponse<ListUsersResponse> users = userService.findAllUsersByParams(name, role, pageable);
    DataResponse<PageResponse<ListUsersResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, users);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/role/user")
  public ResponseEntity<?> listFirstTenUsersByKeyword(@RequestParam(required = true) String name) {
    List<ListUsersByRoleUserResponse> users = userService.findFirstTenUsersByName(name);
    DataResponse<List<ListUsersByRoleUserResponse>> dataResponse = responseService
        .generateDataResponse(ResponseStatus.SUCCESS, users);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @GetMapping("/{id}/roles")
  public ResponseEntity<?> getUserRoles(@PathVariable Long id) {
    RolesByUserResponse user = userService.getRolesByUser(id);

    DataResponse<RolesByUserResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS, user);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping("/{id}/roles")
  public ResponseEntity<CommonResponse> updateUserRoles(@PathVariable Long id,
      @Valid @RequestBody RolesRequest rolesRequest, BindingResult result) {
    validationService.validateFieldsAndThrowResponse(result);
    userService.updateUserRolesById(id, rolesRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se han modificado los roles del usuario");
    return ResponseEntity.status(response.status()).body(response);
  }

  // Nota: no usar un código de estado 204, porque no se puede devolver un body
  @PatchMapping("/{id}/status")
  public ResponseEntity<CommonResponse> changeStatusUser(Authentication authentication, @PathVariable Long id) {

    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    // Long id_authenticated_user =
    // authenticationContextService.extractUserIdFromAuthentication(authentication);
    userService.changeStatusUserById(id, userPrincipal.getId());

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha cambiado el estado del usuario");
    return ResponseEntity.status(response.status()).body(response);
  }

  private Sort buildSort(String sortBy, String direction) {

    String field = switch (sortBy) {
      case "id" -> "id";
      case "firstname" -> "firstname";
      case "lastname" -> "lastname";
      case "dni" -> "dni";
      case "role" -> "role";
      default -> "id";
    };

    Sort.Direction dir = direction.equalsIgnoreCase("asc")
        ? Sort.Direction.ASC
        : Sort.Direction.DESC;

    return Sort.by(dir, field);
  }

}
