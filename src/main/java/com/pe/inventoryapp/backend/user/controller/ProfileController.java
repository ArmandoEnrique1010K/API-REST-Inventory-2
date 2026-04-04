package com.pe.inventoryapp.backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.security.service.AuthenticationContextService;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.service.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
  private final ProfileService profileService;
  private final ResponseService responseService;
  private final ValidationService validationService;
  private final AuthenticationContextService authenticationContextService;

  public ProfileController(
      ProfileService profileService,
      ResponseService responseService,
      ValidationService validationService,
      AuthenticationContextService authenticationContextService) {
    this.profileService = profileService;
    this.responseService = responseService;
    this.validationService = validationService;
    this.authenticationContextService = authenticationContextService;
  }

  @GetMapping
  public ResponseEntity<?> getUserProfile(Authentication authentication) {
    Long username = authenticationContextService.extractUserIdFromAuthentication(authentication);
    DetailUserResponse user = profileService.findUserById(username);

    DataResponse<DetailUserResponse> response = responseService.generateDataResponse(ResponseStatus.SUCCESS, user);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PutMapping
  public ResponseEntity<CommonResponse> updateUserProfile(Authentication authentication,
      @Valid @RequestBody ProfileRequest profileRequest, BindingResult result) {
    Long id_authenticated_user = authenticationContextService.extractUserIdFromAuthentication(authentication);
    validationService.validateFieldsAndThrowResponse(result);
    profileService.updateUserProfileById(id_authenticated_user, profileRequest);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Su perfil ha sido actualizado correctamente");
    return ResponseEntity.status(response.status()).body(response);
  }
}
