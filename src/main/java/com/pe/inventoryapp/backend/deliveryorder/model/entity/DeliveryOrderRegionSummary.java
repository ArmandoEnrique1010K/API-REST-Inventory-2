package com.pe.inventoryapp.backend.deliveryorder.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.pe.inventoryapp.backend.location.model.entity.Region;

import jakarta.persistence.Entity;
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
@Table(name = "resumen_de_ordenes_de_entrega_por_region")
public class DeliveryOrderRegionSummary {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer totalQuantity;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToOne
    @JoinColumn(name = "region_id")
  private Region region;

  @ManyToOne
  @JoinColumn(name= "delivery_order_id")
  private DeliveryOrder deliveryOrder;
}
