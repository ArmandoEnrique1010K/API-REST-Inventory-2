package com.pe.inventoryapp.backend.deliveryorder.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.user.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ordenes_de_entrega")
public class DeliveryOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String batch;

  // TODO: ESTE CAMPO PODRIA NO SER NECESARIO, PORQUE SOLAMENTE SE PODRIA REQUERIR LA FECHA LIMITE PRIORITARIA
  // Fecha limite de toda la orden de entrega
  private LocalDateTime limitDate;

  // Fecha limite prioritaria (se genera automaticamente)
  private LocalDateTime priorityDate;

  // Estado
  private boolean status;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;


  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @ManyToOne
  @JoinColumn(name = "user_creator_id")
  @NotNull
  private User userCreator;

  @ManyToOne
  @JoinColumn(name = "user_updater_id")
  @NotNull
  private User userUpdater;

  @ManyToOne
  @JoinColumn(name = "user_client_id")
  @NotNull
  private User userClient;

  @OneToMany(mappedBy = "deliveryOrder")
  private List<DeliveryLine> deliveryLines;

  @OneToMany(mappedBy = "deliveryOrder")
  private List<Product_DeliveryOrder> product_DeliveryOrders;
}
