package com.pe.inventoryapp.backend.product.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;

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

// TODO: NUNCA ALTERAR LOS REGISTROS DESDE LA BASE DE DATOS, SIEMPRE HACERLO DESDE EL BACKEND PARA QUE SE EJECUTEN LOS CALCULOS Y ACTUALIZACIONES CORRESPONDIENTES EN LOS CAMPOS DE LA ENTIDAD PRODUCT (TOTALQUANTITYAVAILABLE, TOTALQUANTITYRECEIVED, TOTALQUANTITYDELIVERED)
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

    private String imageUrl;

    // Fecha de entrada del producto
    private LocalDate entryDate;

    // Fecha de caducidad del producto
    private LocalDate caducityDate;

    // Sumatoria y calculos de totales
    private Integer totalQuantityAvailable;
    private Integer totalQuantityReceived;
    private Integer totalQuantityDelivered;

    private boolean status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotNull
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<Product_DeliveryOrder> productDeliveryOrders;

    @OneToMany(mappedBy = "product")
    private List<Movement> movements;

    @OneToMany(mappedBy = "product")
    private List<DeliveryLine> deliveryLines;

    @OneToMany(mappedBy = "product")
    private List<StockLot> stockLots;
}
