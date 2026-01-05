package com.pe.inventoryapp.backend.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.entity.UserPrincipal;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(
            "El usuario con el correo " + email + " no existe en el sistema"));

    return new UserPrincipal(user);

    // return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isActive(),
    //     user.isAccountNonExpired(),
    //     user.isCredentialsNonExpired(),
    //     user.isAccountNonLocked(),
    //     user.getAuthorities());
  }
}
