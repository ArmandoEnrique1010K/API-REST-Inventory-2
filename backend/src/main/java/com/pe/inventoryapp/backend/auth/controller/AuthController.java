package com.pe.inventoryapp.backend.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pe.inventoryapp.backend.auth.models.request.RegisterRequest;
import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthService registerService;

  @Autowired
  private ValidationService validationService;

  @Autowired
  private ResponseService responseService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrow(result);
    registerService.verifyUserEmailExists(registerRequest.getEmail());
    var user = registerService.register(registerRequest);

    if (user == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar el usuario");
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.generateSuccessfulResponse("success", user));
  }

}
