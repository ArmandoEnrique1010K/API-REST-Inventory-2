package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.security.config.PasswordEncoderConfig;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.mapper.UserMapper;
import com.pe.inventoryapp.backend.user.model.request.PasswordRequest;
import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoderConfig passwordEncoderConfig;

  @Override
  @Transactional(readOnly = true)
  public List<ListUsersResponse> findAll() {
    List<User> users = (List<User>) userRepository.findAll();
    return users.stream().map(user -> UserMapper.builder().setUser(user).buildListUserResponse())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<DetailUserResponse> findById(Long id) {
    return userRepository.findById(id).map(user -> UserMapper.builder().setUser(user).buildDetailUserResponse());
  }

  @Override
  public void verifyUser(String name) {
    if (userRepository.findByEmail(name).isPresent()) {
      throw new FieldValidation("name", "El usuario con correo '" + name + "' ya existe");
    }
  }

  @Override
  public Optional<DetailUserResponse> findByEmail(String email) {
    return userRepository.findByEmail(email).map(user -> UserMapper.builder().setUser(user).buildDetailUserResponse());
  }

  @Override
  public void remove(Long id) {
    userRepository.deleteById(id);
  }

  // Servicio para actualizar el perfil del usuario
  @Override
  public String updateProfile(Long id, ProfileRequest profileRequest) {

    // Buscar el usuario existente
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

    // Actualizar los campos básicos

    user.setFirstname(profileRequest.getFirstname());
    user.setLastname(profileRequest.getLastname());
    user.setEmail(profileRequest.getEmail());
    user.setDni(profileRequest.getDni());

    userRepository.save(user);

    return "Su perfil ha sido actualizado";

  }

  // Actualiza la constraseña del usuario
  @Override
  public String updatePassword(Long id, PasswordRequest passwordRequest) {

    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

    String encodedPassword = passwordEncoderConfig.passwordEncoder().encode(passwordRequest.getNewPassword());

    user.setPassword(encodedPassword);

    userRepository.save(user);
    return "Contraseña del usuario actualizada";
  }

  // Debe validar que la contraseña que el usuario ha ingresado, debe ser la misma
  // que la constraseña actual
  @Override
  public Boolean validatePassword(Long id, PasswordRequest passwordRequest) {
    Optional<User> idUser = userRepository.findById(id);

    if (idUser.isPresent()
        && passwordEncoderConfig.passwordEncoder().matches(passwordRequest.getCurrentPassword(),
            idUser.get().getPassword())) {
      return true;

    } else {
      return false;
    }

  }

}
