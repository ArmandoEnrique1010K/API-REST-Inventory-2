package com.pe.inventoryapp.backend.summary.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.location.model.entity.Subregion;

import jakarta.persistence.Entity;
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
@Table(name = "modelos_ordenes_de_entrega_subregion")
public class Model_DeliveryOrder_Subregion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer requiredTotalQuantity;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "subregion_id")
  @NotNull
  private Subregion subregion;

  @ManyToOne
  @JoinColumn(name= "model_delivery_order_id")
  @NotNull
  private Model_DeliveryOrder model_DeliveryOrder;
}
