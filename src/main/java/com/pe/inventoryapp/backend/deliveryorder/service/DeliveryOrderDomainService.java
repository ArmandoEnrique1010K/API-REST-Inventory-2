package com.pe.inventoryapp.backend.deliveryorder.service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.repository.Model_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.summary.service.Model_DeliveryOrder_RegionDomainService;
import com.pe.inventoryapp.backend.summary.service.Model_DeliveryOrder_SubregionDomainService;

@Service
public class DeliveryOrderDomainService {

  private final DeliveryLineRepository deliveryLineRepository;
  private final Model_DeliveryOrderRepository model_DeliveryOrderRepository;
  private final Model_DeliveryOrder_RegionDomainService model_DeliveryOrder_RegionDomainService;
  private final Model_DeliveryOrder_SubregionDomainService model_DeliveryOrder_SubregionDomainService;

  public DeliveryOrderDomainService(DeliveryLineRepository deliveryLineRepository,
      Model_DeliveryOrderRepository model_DeliveryOrderRepository,
      Model_DeliveryOrder_RegionDomainService model_DeliveryOrder_RegionDomainService,
      Model_DeliveryOrder_SubregionDomainService model_DeliveryOrder_SubregionDomainService) {
    this.deliveryLineRepository = deliveryLineRepository;
    this.model_DeliveryOrderRepository = model_DeliveryOrderRepository;
    this.model_DeliveryOrder_RegionDomainService = model_DeliveryOrder_RegionDomainService;
    this.model_DeliveryOrder_SubregionDomainService = model_DeliveryOrder_SubregionDomainService;
  }

  // Verifica que todas las lineas de entrega que pertenecen a una orden de
  // entrega tenga el estado lineStatus, si lo tienen no debe devolver nada, de lo
  // contrario un mensaje de error
  public void assertAllLinesInAllowedStates(
      List<DeliveryLine> lines,
      EnumSet<LineStatus> allowedStates) {
    boolean invalid = lines.stream()
        .anyMatch(l -> !allowedStates.contains(l.getLineStatus()));

    if (invalid) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "No puedes entregar esta orden de entrega");
    }
  }

  // TODO: TRASLADAR ESTE MÉTODO EN MovementDomainService, ESTE MÉTODO ES
  // REUTILIZABLE PARA CUALQUIER TIPO DE MOVIMIENTO, NO SOLO PARA LAS ORDENES DE
  // ENTREGA
  // Genera un comentario para la cancelacion de una orden de entrega, si el
  // comentario es nulo o vacio, se asigna un comentario por defecto
  public String generateComment(String comment, String defaultComment) {
    if (comment == null || comment.trim().isEmpty()) {
      return defaultComment;
    }
    return comment;
  }

  // Tomar la fecha mas cercana que no haya sido entregada
  public LocalDateTime getClosestLimitDate(Long idDeliveryOrder) {

    // TODO: VERIFICAR SI LO QUE DICE EL COMENTARIO ES CIERTO
    // 1° encontrar todas las lineas de entrega correspondientes a la orden de
    // entrega
    // 2° tomar las fechas limites de cada linea de entrega cuyo estado sea
    // INPROGRESS
    // 3° devolver la fecha más cercana que no haya sido entregada

    return deliveryLineRepository
        .findClosestLimitDate(idDeliveryOrder)
        .orElse(null); // o lanza excepción
  }

  public void recalculateSummaries(DeliveryOrder deliveryOrder) {
    model_DeliveryOrderRepository
        .recalculateRequiredQuantities(deliveryOrder.getId());

    model_DeliveryOrder_RegionDomainService
        .recalculateSummatoryModel_DeliveryOrderRegionsByDeliveryOrder(deliveryOrder.getId());

    model_DeliveryOrder_SubregionDomainService
        .recalculateSummatoryModel_DeliveryOrderSubregionsByDeliveryOrder(deliveryOrder.getId());
  }

}
