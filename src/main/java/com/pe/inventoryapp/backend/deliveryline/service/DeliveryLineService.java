package com.pe.inventoryapp.backend.deliveryline.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineUpdateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineListResponse;

public interface DeliveryLineService {

  // Guardar una linea de entrega
  void saveDeliveryLine(DeliveryLineRequest deliveryLineRequest, Long id_product_deliveryOrder, Long id_user);

  // Busca todas las lineas de una orden de entrega
  PageResponse<DeliveryLineListResponse> findAllDeliveryLinesByDeliveryOrderIdPageable(
    Long deliveryOrderId, 
    Integer minRequiredQuantity,
    Integer maxRequiredQuantity,
    LocalDateTime minLimitDate, 
      LocalDateTime maxLimitDate, 
    LineStatus lineStatus, 
    String location, 
    Pageable pageable);

  // Busca una linea por id
  DeliveryLineDetailsResponse findDeliveryLineById(Long id);

  void updateDeliveryLineById(Long id, DeliveryLineUpdateRequest deliveryLineUpdateRequest, Long id_user);

  void cancelDeliveryLineById(Long id, Long id_user_authenticated);

  void changeDeliveredStatusDeliveryLineById(Long id, Long id_user);

  void changeCanceledStatusDeliveryLineById(Long id, Long id_user);
  void changeMissingStatusDeliveryLineById(Long id, Long id_user);

  // void changePreparationStatusDeliveryLineById(Long id, PreparationStatus preparationStatus, Long id_user);

  // TODO: INVENTAR UN METODO QUE PERMITA AÑADIR VARIAS LINEAS A UNA MISMA ORDEN
  // void saveAll(DeliveryLineRequest deliveryLineRequest, Long id_user);

}
