package com.pe.inventoryapp.backend.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    Optional<User> userByEmail = userRepository.findByEmail(email);
    if (!userByEmail.isPresent()) {
      throw new UsernameNotFoundException("El usuario con el correo " + email + " no existe en el sistema");
    }

    User user = userByEmail.orElseThrow();

    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());

    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), true,
        true,
        true,
        true,
        authorities);
  }
}
