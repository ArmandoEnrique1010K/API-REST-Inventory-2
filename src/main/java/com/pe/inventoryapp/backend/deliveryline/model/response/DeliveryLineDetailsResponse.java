package com.pe.inventoryapp.backend.deliveryline.model.response;

import java.time.LocalDateTime;

import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLineDetailsResponse {
  // Detalles de una linea de entrega
  private Long id;
  private Integer requiredQuantity;
  private Integer deliveredQuantity;
  private Integer pendingQuantity;
  private LocalDateTime limitDate;
  private LocalDateTime updatedAt;
  private LineStatus lineStatus;

  private String userUpdaterName;

  private Long locationId;
  private String locationName;

  private Long subregionId;
  private String subregionName;

  private Long regionId;
  private String regionName;

  // TODO: ¿ES POSIBLE OBTENER LA LISTA?
  // private Object[] stockLot_DeliveryLines;

  private Long modelId;
  private String modelName;
  private String modelImageUrl;

  private Long productId;
  private String productName;

  private Long categoryId;
  private String categoryName;

  private Long typeId;
  private String typeName;

  private Long deliveryOrderId;
  private String deliveryOrderBatch;
  private LocalDateTime deliveryOrderLimitDate;
}
