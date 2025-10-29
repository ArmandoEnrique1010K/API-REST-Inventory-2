package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.mapper.UserMapper;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.model.response.ListUsersResponse;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

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
  public void remove(Long id) {
    userRepository.deleteById(id);
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

}
