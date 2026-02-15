package com.pe.inventoryapp.backend.deliveryorder.model.entity;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Region;

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
@Table(name = "productos_ordenes_de_entrega")
public class Product_DeliveryOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer requiredQuantityTotal;

  private boolean status;

  @ManyToOne
  @JoinColumn(name = "delivery_order_id")
  @NotNull
  private DeliveryOrder deliveryOrder;

  @ManyToOne
  @JoinColumn(name = "product_id")
  @NotNull
  private Product product;

  @OneToMany(mappedBy = "product_DeliveryOrder")
  private List<DeliveryLine> deliveryLines;

  @OneToMany(mappedBy = "product_DeliveryOrder")
  private List<Model_DeliveryOrder_Region> product_DeliveryOrder_Regions;
}
