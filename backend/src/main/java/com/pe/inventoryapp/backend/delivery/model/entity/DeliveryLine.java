package com.pe.inventoryapp.backend.delivery.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.stock.models.entity.StockLot;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  private Integer requiredQuantity;

  private Integer deliveredQuantity;

  private Integer pendingQuantity;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Enumerated
  private PreparationStatus preparationStatus;

  @ManyToOne
  @JoinColumn(name = "delivery_order_id")
  private DeliveryOrder deliveryOrder;

  @ManyToOne
  @JoinColumn(name = "stock_lot_id")
  private StockLot stockLot;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;
}
