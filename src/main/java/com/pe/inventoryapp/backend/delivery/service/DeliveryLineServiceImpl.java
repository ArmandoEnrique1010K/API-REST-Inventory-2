package com.pe.inventoryapp.backend.delivery.service;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.delivery.model.mapper.DeliveryLineMapper;
import com.pe.inventoryapp.backend.delivery.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryLineListResponse;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.service.UserService;

@Service
public class DeliveryLineServiceImpl implements DeliveryLineService {
  @Autowired
  private DeliveryLineRepository deliveryLineRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private DeliveryOrderRepository deliveryOrderRepository;

  @Autowired
  private UserService userService;

  @Override
  public void saveDeliveryLine(DeliveryLineRequest deliveryLineRequest, Long id_user) {

    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
    String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

    DeliveryLine deliveryLine = new DeliveryLine();

    deliveryLine.setRequiredQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setDeliveredQuantity(0);
    deliveryLine.setPendingQuantity(deliveryLineRequest.getRequiredQuantity());
    // Automaticamente se actualiza la fecha de actualización porque hay una anotacion en la entidad
    deliveryLine.setLimitDate(deliveryLineRequest.getLimitDate());
    deliveryLine.setUpdatedByUser(username);
    deliveryLine.setPreparationStatus(PreparationStatus.INPROGRESS);

    // Buscar el id de la ubicación y orden de entrega
    Long idLocation = deliveryLineRequest.getIdLocation();
    Long idDeliveryOrder = deliveryLineRequest.getIdDeliveryOrder();

    if (idLocation == null){
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    if (idDeliveryOrder == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Location location = locationRepository.findById(idLocation)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La ubicación no existe"));

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(idDeliveryOrder)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El pedido de entrega no existe"));

    deliveryLine.setLocation(location);
    deliveryLine.setDeliveryOrder(deliveryOrder);

    deliveryLineRepository.save(deliveryLine);
  }
  // Repository → devuelve Optional
  // Service → trabaja con entidades reales
  // Nunca uses .get()
  // Nunca propagues Optional fuera del repository

  @Override
  public Page<DeliveryLineListResponse> findAllDeliveryLinesByDeliveryOrderIdPageable(
      Long deliveryOrderId,
      Integer minRequiredQuantity,
      Integer maxRequiredQuantity,
      LocalDate minLimitDate,
      LocalDate maxLimitDate,
      PreparationStatus preparationStatus,
      String location,
      Pageable pageable) {
    if (deliveryOrderId != null && !deliveryOrderRepository.existsById(deliveryOrderId)) {
      throw new BusinessException(
          ResponseStatusCodes.ENTITY_NOT_FOUND,
          "La orden de entrega no existe");
    }

    Page<DeliveryLine> deliveryLines = deliveryLineRepository.searchAllByDeliveryOrderIdAndParams(
        deliveryOrderId, minRequiredQuantity, maxRequiredQuantity, minLimitDate, 
        maxLimitDate, preparationStatus, location, pageable);

    return deliveryLines.map(deliveryLine -> DeliveryLineMapper.builder().setDeliveryLine(deliveryLine).buildDeliveryLineListResponse());
  }

  @Override
  public DeliveryLineDetailsResponse findDeliveryLineById(Long id) {

    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La linea de entrega no existe"));

    return  DeliveryLineMapper.builder().setDeliveryLine(deliveryLine).buildDeliveryLineDetailsResponse();
  }

  // TODO: MEJORAR ESTE MÉTODO PODRIA SERVIR PARA CAMBIAR LA CANTIDAD REQUERIDA
  @Override
  public void updateDeliveryLineById(Long id, DeliveryLineRequest deliveryLineRequest, Long id_user) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La linea de entrega no existe"));

    Long idDeliveryOrder = deliveryLineRequest.getIdDeliveryOrder();

    if (idDeliveryOrder == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    } 

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(idDeliveryOrder)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El pedido de entrega no existe"));

    // SE IGNORAN LOS CAMPOS idLocation y idDeliveryOrder PORQUE NO SE PUEDEN CAMBIAR
    deliveryLine.setRequiredQuantity(deliveryLineRequest.getRequiredQuantity());  
    deliveryLine.setLimitDate(deliveryLineRequest.getLimitDate());
    deliveryLine.setDeliveryOrder(deliveryOrder);
  }

  @Override
  public void deleteDeliveryLineById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La linea de entrega no existe"));

    if (deliveryLine == null){
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    } else {
      deliveryLineRepository.delete(deliveryLine);
    }
  }
}
