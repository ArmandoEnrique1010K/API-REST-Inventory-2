package com.pe.inventoryapp.backend.deliveryline.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineAllocateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineAlterRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineUpdateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineListResponse;

public interface DeliveryLineService {

  // Guardar una linea de entrega
  void saveDeliveryLine(DeliveryLineRequest deliveryLineRequest, Long deliveryOrderId, Long id_user);

  // Busca todas las lineas de una orden de entrega
  PageResponse<DeliveryLineListResponse> findAllDeliveryLinesByDeliveryOrderIdPageable(
    Long deliveryOrderId, 
    Integer minRequiredQuantity,
    Integer maxRequiredQuantity,
    LocalDateTime minLimitDate, 
    LocalDateTime maxLimitDate, 
    LineStatus lineStatus, 
    String location, 
    Long subregionId,
    Long regionId,
    Long modelId,
    Pageable pageable);

  // Busca una linea por id
  DeliveryLineDetailsResponse findDeliveryLineById(Long id);

  //
  void updateDeliveryLineById(Long id, DeliveryLineUpdateRequest deliveryLineUpdateRequest, Long id_user);

  void cancelDeliveryLineById(Long id, Long id_user_authenticated); //

  void sendDeliveryLineById(Long id, Long id_user_authenticated); //

  void lostDeliveryLineById(Long id, DeliveryLineAlterRequest deliveryLineAlterRequest, Long id_user_authenticated); //

  void returnDeliveryLineById(Long id, DeliveryLineAlterRequest deliveryLineAlterRequest, Long id_user_authenticated); //

  void allocateDeliveryLineById(Long id, DeliveryLineAllocateRequest deliveryLineAllocateRequest, Long id_user_authenticated); //

  void missingDeliveryLineById(Long id, Long id_user_authenticated);

  // TODO: EN UNA FUTURA ACTUALIZACION INVENTAR UN METODO QUE PERMITA AÑADIR VARIAS LINEAS A UNA MISMA ORDEN COMO IMPORTAR DESDE UN PDF O EXCEL
  // void saveAll(DeliveryLineRequest deliveryLineRequest, Long id_user);

}
