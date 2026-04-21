package com.pe.inventoryapp.backend.user.model.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {
  private Long id;
  private String email;
  private String password;
  private boolean active;

  // ROL DEL USUARIO
  private String role;

  // private Collection<? extends GrantedAuthority> authorities;

  // private final User user;

  public UserPrincipal(
      Long id,
      String email,
      String password,
      boolean active,
      String role,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.active = active;
    this.role = role;
    // this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // return authorities;
    return List.of(new SimpleGrantedAuthority(role));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isEnabled() {
    return active;
  }

  @Override
  public boolean isAccountNonLocked() {
    return active;
  }

  @Override
  public boolean isAccountNonExpired() {
    return active;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return active;
  }

  public Long getId() {
    return id;
  }

  public String getRole() {
    return role;
  }

}
