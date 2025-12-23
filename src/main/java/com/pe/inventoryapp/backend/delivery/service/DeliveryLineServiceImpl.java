package com.pe.inventoryapp.backend.delivery.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.delivery.model.mapper.DeliveryLineMapper;
import com.pe.inventoryapp.backend.delivery.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineResponse;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;
import com.pe.inventoryapp.backend.product.model.mapper.ProductMapper;

@Service
public class DeliveryLineServiceImpl implements DeliveryLineService {
  @Autowired
  private DeliveryLineRepository deliveryLineRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private DeliveryOrderRepository deliveryOrderRepository;

  @Override
  public void save(DeliveryLineRequest deliveryLineRequest) {
    DeliveryLine deliveryLine = new DeliveryLine();

    deliveryLine.setRequiredQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setDeliveredQuantity(0);
    deliveryLine.setPendingQuantity(deliveryLineRequest.getRequiredQuantity());

    // Automaticamente se actualiza porque hay una anotacion en la entidad
    // deliveryLine.setUpdatedAt(LocalDateTime.now());

    deliveryLine.setPreparationStatus(PreparationStatus.INPROGRESS);

    Long idLocation = deliveryLineRequest.getIdLocation();
    Long idDeliveryOrder = deliveryLineRequest.getIdDeliveryOrder();

    Location location = locationRepository.findById(idLocation)
        .orElseThrow(() -> new RuntimeException("La ubicación no existe"));

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(idDeliveryOrder)
        .orElseThrow(() -> new RuntimeException("El pedido de entrega no existe"));

    deliveryLine.setLocation(location);
    deliveryLine.setDeliveryOrder(deliveryOrder);
    // Repository → devuelve Optional
    // Service → trabaja con entidades reales
    // Nunca uses .get()
    // Nunca propagues Optional fuera del repository
    deliveryLineRepository.save(deliveryLine);
  }

  @Override
  public Page<DeliveryLineResponse> findAllByIdDeliveryOrderPageable(Long idDeliveryOrder, Pageable pageable) {
    DeliveryOrder order = deliveryOrderRepository.findById(idDeliveryOrder)
        .orElseThrow(() -> new RuntimeException("DeliveryOrder no existe"));

    Page<DeliveryLine> deliveryLines = deliveryLineRepository.findAllByDeliveryOrder(pageable, order);
    return deliveryLines
        .map(
            deliveryLine -> DeliveryLineMapper.builder().setDeliveryLine(
                deliveryLine)
                .buildDeliveryLineResponse());

  }

  @Override
  public Optional<DeliveryLineResponse> findById(Long id) {
    return deliveryLineRepository.findById(id)
        .map(deliveryLine -> DeliveryLineMapper.builder().setDeliveryLine(deliveryLine).buildDeliveryLineResponse());
  }

  // TODO: BUSCAR POR PARAMETROS

}
