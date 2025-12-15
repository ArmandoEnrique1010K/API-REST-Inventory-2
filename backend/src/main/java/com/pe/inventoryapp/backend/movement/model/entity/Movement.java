package com.pe.inventoryapp.backend.movement.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.data.Reason;
import com.pe.inventoryapp.backend.stock.model.entity.StockLot;
import com.pe.inventoryapp.backend.user.model.entity.User;

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
@Table(name = "movimientos")
public class Movement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Integer quantity;

  @CreationTimestamp
  private LocalDateTime createdAt;

  // Guardar el nombre del usuario que realizó el movimiento
  private String username_snapshot;

  // Comentario adicional
  private String comment;

  // Relaciones
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "stock_lot_id")
  private StockLot stockLot;

  @ManyToOne
  @JoinColumn(name = "delivery_line_id")
  private DeliveryLine deliveryLine;

  @Enumerated
  private Reason reason;

  @Enumerated
  private MovementType movementType;
}
