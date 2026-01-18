package com.pe.inventoryapp.backend.movement.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.user.model.entity.User;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
  @ManyToOne
  @JoinColumn(name = "user_id")
  @NotNull
  private User user;

  // Primera relacion hacia stockLot, almacena un StockLot receptor
  @ManyToOne
  @JoinColumn(name = "stock_lot_receiver_id")
  @Nullable
  private StockLot stockLotReceiver;

  // Segunda relación hacia StockLot, almacena un StockLot emisor que se crea cuando se hace una transferencia
  // NOTA: ESTO ES OPCIONAL
  @ManyToOne
  @JoinColumn(name = "stock_lot_emitter_id")
  @Nullable
  private StockLot stockLotEmitter;

  @ManyToOne
  @JoinColumn(name = "delivery_line_id")
  @Nullable
  private DeliveryLine deliveryLine;

  @ManyToOne
  @JoinColumn(name = "product_id")
  @NotNull
  private Product product;
  
  // TODO: OPCIONALMENTE SE VA A PROBAR CON UNA RELACION @ManyToMany a StockLot
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "movimientos_lotes_de_stock", joinColumns = @JoinColumn(name = "movement_id"), inverseJoinColumns = @JoinColumn(name = "stock_lot_id"), uniqueConstraints = {
      @UniqueConstraint(columnNames = { "movement_id", "stock_lot_id" })
  })
  private List<StockLot> stockLots;
}
