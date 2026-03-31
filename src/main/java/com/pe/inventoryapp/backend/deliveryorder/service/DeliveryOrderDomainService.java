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
import com.pe.inventoryapp.backend.product.model.entity.Model;

@Service
public class DeliveryOrderDomainService {

  private final DeliveryLineRepository deliveryLineRepository;
  private final Model_DeliveryOrderRepository model_DeliveryOrderRepository;
  // private final DeliveryOrderSummaryDomainService deliveryOrderSummaryDomainService;

  public DeliveryOrderDomainService(DeliveryLineRepository deliveryLineRepository,
      Model_DeliveryOrderRepository model_DeliveryOrderRepository
      // ,
      // DeliveryOrderSummaryDomainService deliveryOrderSummaryDomainService
    ) {
    this.deliveryLineRepository = deliveryLineRepository;
    this.model_DeliveryOrderRepository = model_DeliveryOrderRepository;
    // this.deliveryOrderSummaryDomainService = deliveryOrderSummaryDomainService;
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

  // Tomar la fecha mas cercana que no haya sido entregada
  public LocalDateTime getClosestLimitDate(Long idDeliveryOrder) {
    // 1° encontrar todas las lineas de entrega correspondientes a la orden de
    // entrega
    // 2° tomar las fechas limites de cada linea de entrega cuyo estado sea
    // LINE_PENDING O LINE_EXCEEDED
    // 3° devolver la fecha más cercana que no haya sido entregada
    return deliveryLineRepository
        .findClosestLimitDate(idDeliveryOrder)
        .orElse(null); // o lanza excepción
  }

  public void recalculateSummaries(DeliveryOrder deliveryOrder, Model model) {
    model_DeliveryOrderRepository
        .recalculateRequiredQuantities(deliveryOrder.getId());
  }

}
