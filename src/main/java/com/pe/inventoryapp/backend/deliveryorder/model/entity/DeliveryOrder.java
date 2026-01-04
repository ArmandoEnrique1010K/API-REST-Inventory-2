package com.pe.inventoryapp.backend.deliveryorder.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pe.inventoryapp.backend.deliveryline.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.user.model.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "ordenes_de_entrega")
public class DeliveryOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String batch;


  private LocalDateTime limitDate;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private String createdByUser;
  private String updatedByUser;

  @Enumerated(EnumType.STRING)
  private PreparationStatus preparationStatus;

  @OneToMany(mappedBy = "deliveryOrder")
  private List<DeliveryLine> deliveryLines;

  @OneToMany(mappedBy = "deliveryOrder")
  private List<Product_DeliveryOrder> product_DeliveryOrders;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
