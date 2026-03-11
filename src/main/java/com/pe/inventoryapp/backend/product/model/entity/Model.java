package com.pe.inventoryapp.backend.product.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "modelos")
public class Model {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  // TODO: EN ALGUN FUTURO CREAR UNA ENTIDAD PARA RELACIONAR VARIAS IMAGENES HACIA UN MODELO
  private String imageUrl;
  private String publicImageId;

  // Fecha de entrada del producto
  private LocalDate entryDate;

  // Fecha de caducidad del producto
  private LocalDate caducityDate;

  // Sumatoria y calculos de totales
  @NotNull
  private Integer totalQuantityAvailable;
  @NotNull
  private Integer totalQuantityReceived;
  
  @NotNull
  private Integer totalQuantityTaken;

  @NotNull
  private Integer totalQuantityDelivered;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @NotNull
  private boolean status;

  @OneToMany(mappedBy = "model")
  private List<Model_DeliveryOrder> productDeliveryOrders;

  @OneToMany(mappedBy = "model")
  private List<Movement> movements;

  @OneToMany(mappedBy = "model")
  private List<DeliveryLine> deliveryLines;

  @OneToMany(mappedBy = "model")
  private List<StockLot> stockLots;

  @ManyToOne
  @JoinColumn(name = "product_id")
  @NotNull
  private Product product;
}
