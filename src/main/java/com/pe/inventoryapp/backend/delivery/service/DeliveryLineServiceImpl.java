package com.pe.inventoryapp.backend.delivery.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
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
    // SI SE HA GUARDADO EL PEDIDO DE ENTREGA, CUYA UBICACIÓN YA EXISTE EN LA
    // MISMA ORDEN DE ENTREGA, NO SE TIENE QUE AGREGAR LA LINEA DE ENTREGA
    existDeliveryLineByLocationId(deliveryLineRequest.getIdLocation(), deliveryLineRequest.getIdDeliveryOrder());
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

    // TODO: AQUI DEBE REALIZAR LAS OPERACIONES CON LA ORDEN DE ENTREGA (DELIVERY ORDER)

    // 1° actualizar la fecha limite de deliveryOrder comparando todas las lineas de entrega y tomar el valor con la fecha más cercana que no haya sido entregada
    deliveryOrder.setLimitDate(getClosestLimitDate(idDeliveryOrder));

    // 2° actualizar la cantidad de deliveryOrder sumando todos los totales lineas de entrega
    // deliveryOrder.setQuantityTotal(deliveryLineRepository.sumRequiredQuantityByDeliveryOrderId(idDeliveryOrder));
    // deliveryOrder.setQuantityTotal(1000);

    // 3° actualizar el estado a INPROGRESS cada vez que se guarde una nueva linea de entrega
    deliveryOrder.setPreparationStatus(PreparationStatus.INPROGRESS);

    deliveryOrderRepository.save(deliveryOrder);
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

  // TODO: ESTE MÉTODO SIRVE PARA CAMBIAR LA CANTIDAD REQUERIDA Y LA FECHA LIMITE
  @Override
  public void updateDeliveryLineById(Long id, DeliveryLineRequest deliveryLineRequest, Long id_user) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La linea de entrega no existe"));

    // TODO: SE IGNORAN LOS CAMPOS idLocation y idDeliveryOrder PORQUE NO SE PUEDEN CAMBIAR
    deliveryLine.setRequiredQuantity(deliveryLineRequest.getRequiredQuantity());  
    deliveryLine.setLimitDate(deliveryLineRequest.getLimitDate());

    Long deliveryLine_id = deliveryLine.getId();

    if (deliveryLine_id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }
    
    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryLine_id)
        .orElseThrow(
            () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El pedido de entrega no existe"));

    Long deliveryOrder_id = deliveryOrder.getId();
    // TODO: SI SE ACTUALIZA UNA LINEA DE ENTREGA

    // 1° actualizar la fecha limite de deliveryOrder comparando todas las lineas de
    // entrega y tomar el valor con la fecha más cercana que no haya sido entregada
    deliveryOrder.setLimitDate(getClosestLimitDate(deliveryOrder_id));

    // 2° actualizar la cantidad de deliveryOrder sumando todos los totales lineas
    // de entrega
    // Integer total = deliveryLineRepository
    //     .sumRequiredQuantityByDeliveryOrderId(deliveryOrder_id);

    // deliveryOrder.setQuantityTotal(total != null ? total : 0);
    
    // CASOS ESPECIALES

    // TODO: COMPLETAR EL 3° PASO, QUE ES VERIFICAR SI UNA ORDEN DE ENTREGA ESTA LISTA PARA SER ENTREGADA
    deliveryLineRepository.save(deliveryLine);
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

    // TODO: SI SE BORRA UNA LINEA DE ENTREGA, SE TIENE QUE ACTUALIZAR STOCKLOT (EL PEDIDO PASA A STOCK) Y DELIVERYORDER (EL TOTAL DE UNIDADES DEBE CAMBIAR)
  }


  // Metodo auxiliar
  // Busca si existe una linea de entrega que pertenezca a esa ubicación y tambien a esa misma orden de entrega
  private void existDeliveryLineByLocationId(Long idLocation, Long idDeliveryOrder) {

    if (deliveryLineRepository
        .existsByLocationIdAndDeliveryOrderId(idLocation, idDeliveryOrder)) {

      throw new FieldValidation(
          "idLocation",
          "La línea de entrega para esa ubicación ya existe en esta orden");
    }
  }

  // Tomar la fecha mas cercana que no haya sido entregada
  private LocalDateTime getClosestLimitDate(Long idDeliveryOrder) {
    // 1° encontrar todas las lineas de entrega correspondientes a la orden de entrega
    // 2° tomar las fechas limites de cada linea de entrega cuyo estado sea INPROGRESS
    // 3° devolver la fecha más cercana que no haya sido entregada

    return deliveryLineRepository
        .findClosestLimitDate(idDeliveryOrder)
        .orElse(null); // o lanza excepción
    }
}
