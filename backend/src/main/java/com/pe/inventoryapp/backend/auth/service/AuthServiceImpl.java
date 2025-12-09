package com.pe.inventoryapp.backend.auth.service;

import static com.pe.inventoryapp.backend.security.config.TokenJwtConfig.SECRET_KEY;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.security.config.PasswordEncoderConfig;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoderConfig passwordEncoderConfig;

  // Obtiene el id del usuario por su email
  @Override
  @Transactional(readOnly = true)
  public Long findIdByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(User::getId)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
  }

  // Verifica si el email del usuario ya existe, de lo contrario lanza una
  // excepcion
  @Override
  @Transactional(readOnly = true)
  public void verifyUserEmailExists(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new FieldValidation("email", "El usuario con ese email ya existe, introduzca otro email");
    }
  }

  // Extrae el id del usuario desde el JWT del header
  @Override
  @Transactional(readOnly = true)
  public Long extracIdFromClaims(String header) {
    String token = header.replace("Bearer ", "");

    Claims claims = Jwts.parser()
        .verifyWith((SecretKey) SECRET_KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    Long id = claims.get("id", Long.class);

    return id;
  }

  // Verifica si el email del usuario ya existe
  @Override
  @Transactional(readOnly = true)
  public boolean existsUserByEmail(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      return true;
    }
    return false;
  }

  // Cambia el password del usuario y lo guarda en la base de datos
  @Override
  @Transactional
  public void changePassword(String password, Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    user.setPassword(passwordEncoderConfig.passwordEncoder().encode(password));
    // No llamas a save(): el contexto de persistencia hace flush automáticamente
    // userRepository.save(user);
  }
}
