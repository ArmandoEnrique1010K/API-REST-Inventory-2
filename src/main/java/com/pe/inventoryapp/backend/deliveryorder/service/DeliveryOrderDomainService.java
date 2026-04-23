package com.pe.inventoryapp.backend.deliveryorder.service;

import com.pe.inventoryapp.backend.deliveryorder.model.data.OnTimeStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;

@Service
public class DeliveryOrderDomainService {

  private final DeliveryOrderRepository deliveryOrderRepository;
  private final DeliveryLineRepository deliveryLineRepository;

  public DeliveryOrderDomainService(DeliveryLineRepository deliveryLineRepository,
      DeliveryOrderRepository deliveryOrderRepository) {
    this.deliveryLineRepository = deliveryLineRepository;
    this.deliveryOrderRepository = deliveryOrderRepository;
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
          "No puedes entregar esta orden de entrega, hay lineas de entrega que estan pendientes");
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

  public Double calculateDeliveryOrderPercentage(Long idDeliveryOrder) {
    Double percentage = deliveryOrderRepository.percentageByDeliveryOrder(idDeliveryOrder);

    percentage = BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP).doubleValue();

    return percentage;
  }

  public void updateDeliveryOrderDeliveredAt(DeliveryOrder deliveryOrder){
    if (deliveryLineRepository.allLinesCanceled(deliveryOrder.getId())){
      deliveryOrder.setDeliveredAt(null);
      return;
    }

    if (deliveryLineRepository.allLinesClosed(deliveryOrder.getId())){
      deliveryOrder.setDeliveredAt(LocalDateTime.now());
      return;
    }

    // Si no hay ninguna linea de entrega
    deliveryOrder.setDeliveredAt(null);
  }



  public void updateOnTimeStatus(
      DeliveryOrder order) {
    // VERIFICA QUE TODAS LAS LINEAS DE ENTREGA ASOCIADAS A DELIVERYORDER TENGAN EL
    // ESTADO
    // LINE_DELIVERED, // Entregado
    // LINE_CANCELED, // Cancelado
    // LINE_MISSING // Perdido luego de ser entregado

    if (!deliveryLineRepository.allLinesClosed(order.getId())) {
      order.setOnTimeStatus(OnTimeStatus.UNKNOWN);
      return;
    }

    // boolean allCanceled = deliveryLineRepository.countByDeliveryOrderId(order
    //     .getId()) > 0 &&
    //     !deliveryLineRepository.existsByDeliveryOrderIdAndLineStatusNot(order.getId(), LineStatus.LINE_CANCELED);

    if (deliveryLineRepository.allLinesCanceled(order.getId())){
      order.setOnTimeStatus(OnTimeStatus.UNKNOWN);
      return;
    }

    if (order.getDeliveredAt() == null) {
      order.setOnTimeStatus(OnTimeStatus.UNKNOWN);
      return;
    }

    order.setOnTimeStatus(
        order.getDeliveredAt().isAfter(
            order.getLimitDate())
            ? OnTimeStatus.LATE
            : OnTimeStatus.ON_TIME);
  }
}
