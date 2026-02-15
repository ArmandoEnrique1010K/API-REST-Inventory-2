package com.pe.inventoryapp.backend.movement.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.user.model.entity.User;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "movimientos")
public class Movement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private Integer quantity;

  private String comment;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @Enumerated(EnumType.STRING)
  private MovementType movementType;

  // Relaciones
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // Primera relacion hacia stockLot, almacena un StockLot receptor
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_lot_receiver_id")
  @Nullable
  private StockLot stockLotReceiver;

  // Segunda relación hacia StockLot, almacena un StockLot emisor que se crea cuando se hace una transferencia
  // NOTA: ESTO ES OPCIONAL
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_lot_emitter_id")
  @Nullable
  private StockLot stockLotEmitter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_line_id")
  @Nullable
  private DeliveryLine deliveryLine;

  @ManyToOne
  @JoinColumn(name = "model_id")
  @NotNull
  private Model model;
  
  @OneToMany(mappedBy = "movement", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Movement_StockLot> stockLotDetails;
}
