package com.pe.inventoryapp.backend.deliveryline.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.product.model.entity.Model;
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
@Table(name = "lineas_de_entrega")
public class DeliveryLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer originalQuantity;

  private Integer requiredQuantity;

  private Integer deliveredQuantity;

  private Integer pendingQuantity;

  private LocalDateTime limitDate;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  private LineStatus lineStatus;

  @ManyToOne
  @JoinColumn(name = "location_id")
  @NotNull
  private Location location;

  @ManyToOne
  @JoinColumn(name = "user_creator_id")
  @NotNull
  private User userCreator;

  @ManyToOne
  @JoinColumn(name = "user_updater_id")
  @NotNull
  private User userUpdater;

  @OneToMany(mappedBy = "deliveryLine")
  private List<StockLot_DeliveryLine> stockLotDeliveryLines;

  @OneToMany(mappedBy = "deliveryLine")
  private List<Movement> movements;

  @ManyToOne
  @JoinColumn(name = "model_id")
  @NotNull
  private Model model;

  @ManyToOne
  @JoinColumn(name = "model_delivery_order_id")
  @NotNull
  private Model_DeliveryOrder model_DeliveryOrder;

  @ManyToOne
  @JoinColumn(name = "delivery_order_id")
  @NotNull
  private DeliveryOrder deliveryOrder;
}
