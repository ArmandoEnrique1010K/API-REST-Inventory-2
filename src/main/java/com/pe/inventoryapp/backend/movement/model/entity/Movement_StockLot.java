package com.pe.inventoryapp.backend.movement.model.entity;

import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "movimientos_lotes_de_entrega")
public class Movement_StockLot {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private Integer quantityTaken;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "movement_id", nullable = false)
  private Movement movement;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_lot_id", nullable = false)
  private StockLot stockLot;

}
