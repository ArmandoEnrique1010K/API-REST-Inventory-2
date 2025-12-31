package com.pe.inventoryapp.backend.delivery.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.stock.model.entity.StockLot;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private LocalDateTime limitDate;
  private String updatedByUser;

  @Enumerated(EnumType.STRING)
  private PreparationStatus preparationStatus;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @OneToMany(mappedBy = "deliveryLine")
  private List<Movement> movements;

  @ManyToOne
  @JoinColumn(name = "delivery_order_id")
  private DeliveryOrder deliveryOrder;

  @ManyToMany(mappedBy = "deliveryLines")
  private List<StockLot> stockLots;
}
