package com.pe.inventoryapp.backend.start.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import com.pe.inventoryapp.backend.user.service.UserService;

import io.github.cdimascio.dotenv.Dotenv;

@RestController
@RequestMapping("/api")
public class WelcomeController {
  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;
  @GetMapping
  public String welcome() {
    return "Se ha inicializado el backend";
  }

  @GetMapping("/dotenv")
  public String getEnv() {
    Dotenv dotenv = Dotenv.load();
    String valor = dotenv.get("MY_TEST_ENV_VAR"); // Guardas el valor
    return "El valor de la variable de entorno es: " + valor;
  }

  @GetMapping("/database")
  public String testDatabase() {
    // Encontrar al primer usuario por id
    DetailUserResponse user = userService.findUserById(1L);

    if (user != null) {
      return "La base de datos funciona correctamente";
    } else {
      return "La base de datos no funciona correctamente";
    }
  }

  // PRUEBA DE STACK OVERFLOW
  @GetMapping("/stackoverflow/{id}")
  public ResponseEntity<?> stackOverflow(@PathVariable Long id) {
    // Encontrar al usuario y traer la entidad relacionada como respuesta
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
      return ResponseEntity.ok(user.get());
    } else {
      return ResponseEntity.ok("No se encuentra el usuario");
    }


  }
}
