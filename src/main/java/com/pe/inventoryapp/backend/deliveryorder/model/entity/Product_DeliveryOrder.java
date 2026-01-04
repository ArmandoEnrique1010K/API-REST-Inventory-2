package com.pe.inventoryapp.backend.deliveryorder.model.entity;

import java.util.List;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.product.model.entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "productos_ordenes_de_entrega")
public class Product_DeliveryOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer requiredQuantityTotal;

  @ManyToOne
  @JoinColumn(name = "delivery_order_id")
  private DeliveryOrder deliveryOrder;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @OneToMany(mappedBy = "product_DeliveryOrder")
  private List<DeliveryLine> deliveryLines;

  @OneToMany(mappedBy = "product_DeliveryOrder")
  private List<Product_DeliveryOrder_Region> product_DeliveryOrder_Regions;

}
