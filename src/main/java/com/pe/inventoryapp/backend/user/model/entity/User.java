package com.pe.inventoryapp.backend.user.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.JoinTable;
// import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
// import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// import org.hibernate.annotations.BatchSize;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.user.model.data.RoleName;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
@Data
public class User  {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  private String password;

  private String firstname;

  private String lastname;

  private Integer dni;

  private boolean active;

  private RoleName role;

  // @ManyToMany(fetch = FetchType.LAZY)
  // @JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), uniqueConstraints = {
  //     @UniqueConstraint(columnNames = { "user_id", "role_id" })
  // })

  /**
   * @BatchSize
   *
   * Soluciona el problema N+1 en relaciones MANY TO MANY sin usar JOIN FETCH.
   *
   * ¿Cómo funciona?
   *
   * Supongamos que traes 10 usuarios:
   *
   * Sin @BatchSize:
   * - 1 query para usuarios
   * - 10 queries para roles (1 por usuario)
   * → N+1 problem
   *
   * Con @BatchSize(size = 20):
   * - 1 query para usuarios
   * - 1 query para roles:
   * SELECT * FROM roles WHERE user_id IN (...)
   *
   * IMPORTANTE:
   * - "20" no es cantidad de usuarios
   * - Es el tamaño del lote que Hibernate agrupa para cargar relaciones
   *
   * Beneficios:
   * - Evita N+1
   * - Compatible con paginación
   * - No rompe SQL ni genera duplicados
   */

  // @BatchSize(size = 20)
  // private List<Role> roles;
  
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  private List<Movement> movements;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "userCreator")
  private List<DeliveryOrder> deliveryOrders;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "userCreator")
  private List<DeliveryLine> deliveryLines;
}
