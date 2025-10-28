package com.pe.inventoryapp.backend.user.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Introduzca un correo")
  @Email(message = "No se reconoce el correo ingresado")
  @Column(unique = true)
  private String email;

  @NotBlank(message = "Introduzca una contraseña")
  private String password;

  @NotBlank(message = "Introduzca un nombre")
  private String firstname;

  @NotBlank(message = "Introduzca un apellido")
  private String lastname;

  @NotNull(message = "Introduzca su DNI")
  @Min(10000000)
  @Max(99999999)
  private Integer dni;

  @ManyToMany
  @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false), uniqueConstraints = {
      @UniqueConstraint(columnNames = { "user_id", "role_id" }) })
  private List<Role> roles;

  @Transient
  private boolean isOperator;

  @Transient
  private boolean isAdmin;
}
