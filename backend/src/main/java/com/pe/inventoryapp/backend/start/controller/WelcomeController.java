package com.pe.inventoryapp.backend.start.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.cdimascio.dotenv.Dotenv;

@RestController
@RequestMapping("/api")
public class WelcomeController {
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
}
