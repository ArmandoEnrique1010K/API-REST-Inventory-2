package com.pe.inventoryapp.backend.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.auth.models.UserPrincipal;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.SECRET_KEY;

import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private ResponseService responseService;

  @GetMapping
  public List<?> listAll() {
    return userService.findAll();
  }

  @GetMapping("/profile")
  public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String header) {

    String token = header.replace("Bearer ", "");

    Claims claims = Jwts.parser()
        .verifyWith((SecretKey) SECRET_KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    Long id = claims.get("id", Long.class);
    System.out.println("ID recibido en getProfile: " + id);

    Optional<DetailUserResponse> user = userService.findById(id);
    return ResponseEntity.ok(user);
  }
}
