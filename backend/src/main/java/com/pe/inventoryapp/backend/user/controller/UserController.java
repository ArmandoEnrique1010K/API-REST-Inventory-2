package com.pe.inventoryapp.backend.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.auth.models.UserPrincipal;
import com.pe.inventoryapp.backend.auth.service.AuthService;
import com.pe.inventoryapp.backend.common.response.Response;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.user.model.request.PasswordRequest;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.SECRET_KEY;

import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
  public ResponseEntity<Response> updateProfile(@RequestHeader("Authorization") String header,
      @Valid @RequestBody ProfileRequest profileRequest, BindingResult result) {
    Long id = authService.extracIdFromClaims(header);

    if (userService.findById(id).isEmpty()) {
      return ResponseEntity.status(400)
          .body(responseService.writeAResponse("error", "El usuario no existe"));
    }

    if (result.hasErrors()) {
      return ResponseEntity.status(400).body(responseService.writeAResponse("error", "Complete los campos si o si"));
    }

    String message = userService.updateProfile(id, profileRequest);
    return ResponseEntity.status(200).body(responseService.writeAResponse("success", message));
  }

  @PutMapping("update-password")
  public ResponseEntity<Response> updatePassword(@RequestHeader("Authorization") String header,
      @Valid @RequestBody PasswordRequest passwordRequest, BindingResult result) {
    Long id = authService.extracIdFromClaims(header);

    if (userService.findById(id).isEmpty()) {
      return ResponseEntity.status(400)
          .body(responseService.writeAResponse("error", "El usuario no existe"));
    }

    if (result.hasErrors()) {
      return ResponseEntity.status(400).body(responseService.writeAResponse("error", "Complete los campos si o si"));
    }

    if (userService.validatePassword(id, passwordRequest)) {

      // TODO : CORREGIR AQUI, LAS CONTRASEÑAS NO COINCIDEN
      if (passwordRequest.getNewPassword() != passwordRequest.getConfirmPassword()) {
        return ResponseEntity.status(400)
            .body(responseService.writeAResponse("error", "Confirma tu contraseña, parece que no es la misma"));
      } else {
        String message = userService.updatePassword(id, passwordRequest);
        return ResponseEntity.status(200).body(responseService.writeAResponse("success", message));

      }

    } else {
      return ResponseEntity.status(400)
          .body(responseService.writeAResponse("error", "Intente de nuevo, no es la contraseña anterior"));
    }

  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Response> deleteUser(@PathVariable Long id) {

    if (userService.findById(id).isEmpty()) {
      return ResponseEntity.status(400)
          .body(responseService.writeAResponse("error", "El usuario no existe"));
    }

    if (id == 1) {
      return ResponseEntity.status(400)
          .body(responseService.writeAResponse("error", "Este usuario no se puede eliminar"));

    }

    userService.remove(id);
    // return ResponseEntity.ok("Se ha eliminado el usuario");

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(responseService.writeAResponse("success", "Usuario eliminado"));

  }

}
