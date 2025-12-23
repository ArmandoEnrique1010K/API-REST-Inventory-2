package com.pe.inventoryapp.backend.start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.service.UserService;

import io.github.cdimascio.dotenv.Dotenv;

@RestController
@RequestMapping("/api")
public class WelcomeController {
  @Autowired
  private UserService userService;

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
}
