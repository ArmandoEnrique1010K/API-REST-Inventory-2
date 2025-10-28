package com.pe.inventoryapp.backend.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pe.inventoryapp.backend.common.dto.SuccessfulResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.common.service.ValidationService;
import com.pe.inventoryapp.backend.user.model.request.RegisterRequest;
import com.pe.inventoryapp.backend.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private ValidationService validationService;
  @Autowired
  private ResponseService responseService;

  @GetMapping
  public String welcome() {
    return "Bienvenido al sistema de usuarios";
  }

  // Registrar al usuario
  @PostMapping("/register")
  public ResponseEntity<SuccessfulResponse> register(@Valid @RequestBody RegisterRequest registerRequest,
      BindingResult result) {
    validationService.validateFieldsAndThrow(result);
    userService.verifyUser(registerRequest.getEmail());
    var user = userService.register(registerRequest);

    if (user == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al registrar el usuario");
    }

    // Registrar al usuario
    // return ResponseEntity
    // .status(HttpStatus.CREATED)
    // .body("Se ha registrado el usuario en la base de datos");

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.writeAResponse("success", user));
  }

  @PostMapping("/login")
  public String login() {
    return "usuario logeado";
  }
}
