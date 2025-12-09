package com.pe.inventoryapp.backend.user.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

  @Override
  public boolean isExpired(String value) {
    UserToken token = userTokenRepository.findByValue(value);
    return token.getExpirationTime().isBefore(LocalDateTime.now());
  }

  @Override
  public UserToken createTokenForUserByEmail(String email) {
    int number = (int) (Math.random() * 900000) + 100000;
    String stringtoken = String.valueOf(number);

    // Devolver usuario por email
    User user = userRepository.findUserByEmail(email);

    UserToken token = new UserToken();
    token.setUser(user);
    token.setValue(stringtoken);
    token.setExpirationTime(LocalDateTime.now().plusMinutes(1)); // 1 minuto
    return userTokenRepository.save(token);

  }

  @Override
  public boolean isTokenValid(String value) {
    UserToken token = userTokenRepository.findByValue(value);
    return token != null || this.isExpired(value);
  }

  @Override
  public User findUserByToken(String token) {
    UserToken userToken = userTokenRepository.findByValue(token);
    return userToken.getUser();
  }

}
