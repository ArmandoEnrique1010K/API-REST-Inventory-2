package com.pe.inventoryapp.backend.product.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "productos")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  private BigDecimal length;

  private BigDecimal width;

  private BigDecimal height;

  @NotNull
  private boolean status;

  private Integer quantityModels;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "category_id")
  @NotNull
  private Category category;

  @OneToMany(mappedBy = "product")
  private List<Model> models;

  @ManyToOne
  @JoinColumn(name = "type_id")
  @NotNull
  private Type type;
}
