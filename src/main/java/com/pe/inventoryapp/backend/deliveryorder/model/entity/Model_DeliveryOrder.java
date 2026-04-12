package com.pe.inventoryapp.backend.deliveryorder.model.entity;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.product.model.entity.Model;

import jakarta.persistence.Entity;
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
@Table(name = "modelos_ordenes_de_entrega")
public class Model_DeliveryOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer requiredQuantityTotal;

  private boolean status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_order_id")
  @NotNull
  private DeliveryOrder deliveryOrder;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_id")
  @NotNull
  private Model model;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "model_DeliveryOrder")
  private List<DeliveryLine> deliveryLines;
}
