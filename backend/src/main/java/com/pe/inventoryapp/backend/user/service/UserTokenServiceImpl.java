package com.pe.inventoryapp.backend.user.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.entity.UserToken;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import com.pe.inventoryapp.backend.user.repository.UserTokenRepository;

@Service
public class UserTokenServiceImpl implements UserTokenService {

  @Autowired
  private UserTokenRepository userTokenRepository;

  @Autowired
  private UserRepository userRepository;

  // Verificar si el token de 6 digitos ha expirado
  @Override
  @Transactional(readOnly = true)
  public boolean isExpired(String token) {
    Optional<UserToken> userToken = userTokenRepository.findByToken(token);
    return userToken.isPresent() && userToken.get().getExpirationTime().isBefore(LocalDateTime.now());
  }

  // Crea un token de 6 digitos de "corta vida" para el usuario por su email
  @Override
  public String generateTokenForUserByEmail(String email) {
    int number = (int) (Math.random() * 1_000_000);
    String stringtoken = String.format("%06d", number);

    // Devolver usuario por email
    User user = userRepository.findUserByEmail(email);

    UserToken token = new UserToken();
    token.setUser(user);
    token.setToken(stringtoken);
    token.setExpirationTime(LocalDateTime.now().plusMinutes(5)); // 5 minutos
    userTokenRepository.save(token);
    return token.getToken();
  }

  // Verifica si el token de 6 digitos es valido y si no ha expirado
  @Override
  @Transactional(readOnly = true)
  public boolean isTokenValid(String token) {
    Optional<UserToken> userToken = userTokenRepository.findByToken(token);
    return userToken.isPresent() && !this.isExpired(token);
  }

  // Busca el id del usuario por el token de 6 digitos del usuario
  @Override
  public Long findUserIdByUserToken(String token) {
    Optional<UserToken> userToken = userTokenRepository.findByToken(token);
    return userToken.isPresent() ? userToken.get().getUser().getId() : null;
  }
}
