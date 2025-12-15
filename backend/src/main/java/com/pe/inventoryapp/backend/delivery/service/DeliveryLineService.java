package com.pe.inventoryapp.backend.delivery.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pe.inventoryapp.backend.delivery.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineResponse;

public interface DeliveryLineService {

  // Guardar una linea
  void save(DeliveryLineRequest deliveryLineRequest);

  // Busca todas las lineas de una orden de entrega
  Page<DeliveryLineResponse> findAllByIdDeliveryOrderPageable(Long idDeliveryOrder, Pageable pageable);

  // Busca una linea por id
  Optional<DeliveryLineResponse> findById(Long id);

  // INVENTAR UN METODO QUE PERMITA AÑADIR VARIAS LINEAS A UNA MISMA ORDEN
  // void saveAll(DeliveryLineRequest deliveryLineRequest, Long id_user);

}
