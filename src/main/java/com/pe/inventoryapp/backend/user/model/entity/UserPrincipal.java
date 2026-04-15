package com.pe.inventoryapp.backend.user.model.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {
  private Long id;
  private String email;
  private String password;
  private boolean active;
  private Collection<? extends GrantedAuthority> authorities;

  // private final User user;

  public UserPrincipal(
      Long id,
      String email,
      String password,
      boolean active,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.active = active;
    this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
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
}
