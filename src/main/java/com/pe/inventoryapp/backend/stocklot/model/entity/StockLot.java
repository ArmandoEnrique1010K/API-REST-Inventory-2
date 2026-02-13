package com.pe.inventoryapp.backend.stocklot.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pe.inventoryapp.backend.deliveryline.model.entity.StockLot_DeliveryLine;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.product.model.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "lotes_de_stock")
public class StockLot {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String batch;

  private Integer quantityReceived;

  private Integer quantityAvailable;

  private Integer quantityDelivered;

  private Integer quantityLost;

  private Integer quantityRecovered;

  private boolean zeroStock;

  // TODO: CORREGIR LOS DEMÁS SERVICIOS EN CUANTO AL CAMPO TEMPORALY
  // Representa si el lote de stock es temporal, es decir, si se ha creado para consolidar una devolución de una linea de entrega cancelada, y se asignara a otra linea de entrega que lo requiera, pero solamente si hay cantidad entregada, y se eliminara cuando se asigne a otra linea de entrega o cuando hayan pasado 24 horas desde su creación
  private boolean temporary;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "product_id")
  @NotNull
  private Product product;

  @OneToMany(mappedBy = "stockLotReceiver")
  private List<Movement> movements;

  @OneToMany(mappedBy = "stockLot")
  private List<StockLot_DeliveryLine> stockLotDeliveryLines;

  @ManyToOne
  @JoinColumn(name = "company_id")
  @NotNull
  private Company company;
}
