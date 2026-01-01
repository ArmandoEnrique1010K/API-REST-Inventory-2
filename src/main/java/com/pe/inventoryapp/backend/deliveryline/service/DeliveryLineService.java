package com.pe.inventoryapp.backend.deliveryline.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.deliveryline.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineUpdateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineListResponse;

public interface DeliveryLineService {

  // Guardar una linea de entrega
  void saveDeliveryLine(DeliveryLineRequest deliveryLineRequest, Long id_product_deliveryOrder, Long id_user);

  // Busca todas las lineas de una orden de entrega
  Page<DeliveryLineListResponse> findAllDeliveryLinesByDeliveryOrderIdPageable(
    Long deliveryOrderId, 
    Integer minRequiredQuantity,
    Integer maxRequiredQuantity,
    LocalDateTime minLimitDate, 
      LocalDateTime maxLimitDate, 
    PreparationStatus preparationStatus, 
    String location, 
    Pageable pageable);

  // Busca una linea por id
  DeliveryLineDetailsResponse findDeliveryLineById(Long id);

  void updateDeliveryLineById(Long id, DeliveryLineUpdateRequest deliveryLineUpdateRequest, Long id_user);

  void deleteDeliveryLineById(Long id);

  void changeDeliveredStatusDeliveryLineById(Long id, Long id_user);

  void changeCanceledStatusDeliveryLineById(Long id, Long id_user);
  void changeMissingStatusDeliveryLineById(Long id, Long id_user);

  // void changePreparationStatusDeliveryLineById(Long id, PreparationStatus preparationStatus, Long id_user);

  // TODO: INVENTAR UN METODO QUE PERMITA AÑADIR VARIAS LINEAS A UNA MISMA ORDEN
  // void saveAll(DeliveryLineRequest deliveryLineRequest, Long id_user);

}
