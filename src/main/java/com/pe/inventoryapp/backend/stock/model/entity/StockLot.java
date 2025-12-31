package com.pe.inventoryapp.backend.stock.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.product.model.entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

  private String batch;

  private Integer quantityReceived;
  private Integer quantityAvailable;

  // TODO: Este campo se podria eliminar porque la fecha de caducidad de un stock es equivalente a la de un producto
  private LocalDate caducityDate;

  private Integer deliveredTotal;

  // Este campo representa si ya no hay stock en el lote
  private boolean zeroStock;

  @CreationTimestamp
  private LocalDateTime createdAt;
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @OneToMany(mappedBy = "stockLot")
  private List<Movement> movements;

  @ManyToOne
  @JoinColumn(name = "company_id")
  private Company company;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "stockLots_deliveryLines", 
  joinColumns = @JoinColumn(name = "stock_lot_id"), 
  inverseJoinColumns = @JoinColumn(name = "delivery_line_id"), 
  uniqueConstraints = {
      @UniqueConstraint(columnNames = { "stock_lot_id", "delivery_line_id" })
  })
  private List<DeliveryLine> deliveryLines;
}
