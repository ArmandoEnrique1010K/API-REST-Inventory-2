package com.pe.inventoryapp.backend.start.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
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

  // @Autowired
  // private UserRepository userRepository;

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

  // Configuracion de CSRF (OBLIGATORIO)
  @GetMapping("/csrf")
public ResponseEntity<?> csrf(CsrfToken token) {
    return ResponseEntity.ok(Map.of(
        "token", token.getToken(),
        "header", token.getHeaderName(),
        "parameter", token.getParameterName()
    ));
}

  // PRUEBA DE STACK OVERFLOW (NO ACTIVAR ESTE ENDPOINT)
  // Encontrar al usuario y traer la entidad relacionada como respuesta

  // @GetMapping("/stackoverflow/{id}")
  // public ResponseEntity<?> stackOverflow(@PathVariable Long id) {

  //   if (id == null) {
  //     return ResponseEntity.ok("No se encuentra el usuario");
  //   }

  //   User user = userRepository.findById(id).orElseThrow(
  //     () -> new RuntimeException("No se encuentra el usuario")
  //   );

  //   if (user != null) {
  //     System.out.println("El usuario " + user.getFirstname()+ " se encuentra");
  //     System.out.println(user);
  //     System.out.println(user.getTokens());
  //     return ResponseEntity.ok(user);
  //   } else {
  //     return ResponseEntity.ok("No se encuentra el usuario");
  //   }
  // }
}
