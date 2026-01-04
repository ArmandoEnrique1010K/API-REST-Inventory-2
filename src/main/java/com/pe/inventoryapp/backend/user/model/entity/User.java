package com.pe.inventoryapp.backend.user.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
@Data

// CON ELLO SE EVITA EL PROBLEMA DE RECURSIVIDAD INFINITA
// No utilizar @Data 
// En su lugar utilizar @Getter y @Setter y @ToString
// @Getter
// @Setter
// @ToString(exclude = { "deliveryOrders", "movements", "tokens", "roles" })
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  private String password;

  private String firstname;

  private String lastname;

  private Integer dni;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), uniqueConstraints = {
      @UniqueConstraint(columnNames = { "user_id", "role_id" })
  })
  private List<Role> roles;

  private boolean status;

  // NOTA: USAR @JsonIgnore evita tener un error de recursividad (StackOverflow) al imprimir la entidad
  
  // Pero  cuando se trata de editar datos en el servicio, no ocurre una sobrecarga porque no se esta serializando la entidad
  // @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserToken> tokens;
  
  // @JsonIgnore
  @OneToMany(mappedBy = "user")
  private List<Movement> movements;

  // @JsonIgnore
  @OneToMany(mappedBy = "user")
  private List<DeliveryOrder> deliveryOrders;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    List<GrantedAuthority> authorities = roles.stream()
        .map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());

    return authorities;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isEnabled() {
    return this.status;
  }

  @Override
  public boolean isAccountNonLocked() {
    return status;
  }

  @Override
  public boolean isAccountNonExpired() {
    return status;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return status;
  }
}
