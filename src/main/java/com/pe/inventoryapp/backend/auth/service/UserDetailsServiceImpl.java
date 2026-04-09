package com.pe.inventoryapp.backend.auth.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmailWithRoles(email)
        .orElseThrow(() -> new UsernameNotFoundException(
            // "El usuario con el correo " + email + " no existe en el sistema"
            "Ha ocurrido un error desconocido"));
    var authorities = user.getRoles()
        .stream()
        .map(role -> new SimpleGrantedAuthority(role.getName()))
        .toList();

    return new UserPrincipal(user.getId(), user.getEmail(),
        user.getPassword(), user.isActive(), authorities);

    // return new
    // org.springframework.security.core.userdetails.User(user.getEmail(),
    // user.getPassword(), user.isActive(),
    // user.isAccountNonExpired(),
    // user.isCredentialsNonExpired(),
    // user.isAccountNonLocked(),
    // user.getAuthorities());
  }
}
