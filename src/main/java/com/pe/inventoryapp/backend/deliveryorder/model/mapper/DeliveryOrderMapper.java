package com.pe.inventoryapp.backend.deliveryorder.model.mapper;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientListResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderSummaryResponse;

public class DeliveryOrderMapper {
  private DeliveryOrder deliveryOrder;

  private DeliveryOrderMapper() {
  }

  public static DeliveryOrderMapper builder() {
    return new DeliveryOrderMapper();
  }

  public DeliveryOrderMapper setDeliveryOrder(DeliveryOrder deliveryOrder) {
    this.deliveryOrder = deliveryOrder;
    return this;
  }

  public DeliveryOrderListResponse buildDeliveryOrderListResponse() {
    if (deliveryOrder == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryOrder");
    } else {
      return new DeliveryOrderListResponse(
          deliveryOrder.getId(),
          deliveryOrder.getBatch(),
          deliveryOrder.getLimitDate(),
          deliveryOrder.getPriorityDate(),
          deliveryOrder.getUserClient().getFirstname() + " " + deliveryOrder.getUserClient().getLastname(),
          deliveryOrder.getOrderStatus());
    }
  }

  public DeliveryOrderDetailsResponse buildDeliveryOrderDetailsResponse() {
    if (deliveryOrder == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryOrder");
    } else {
      return new DeliveryOrderDetailsResponse(
          deliveryOrder.getId(),
          deliveryOrder.getBatch(),
          deliveryOrder.getLimitDate(),
          deliveryOrder.getPriorityDate(),
          deliveryOrder.getUserCreator().getFirstname() + " " + deliveryOrder.getUserCreator().getLastname(),
          deliveryOrder.getUserUpdater().getFirstname() + " " + deliveryOrder.getUserUpdater().getLastname(),
          deliveryOrder.getUserClient().getFirstname() + " " + deliveryOrder.getUserClient().getLastname(),
          deliveryOrder.getCreatedAt(),
          deliveryOrder.getUpdatedAt(),
          deliveryOrder.getOrderStatus(),
          deliveryOrder.getPercentage(),
          deliveryOrder.getDeliveredAt(),
          deliveryOrder.getOnTimeStatus());
    }
  }

  public DeliveryOrderClientListResponse buildDeliveryOrderClientListResponse() {
    if (deliveryOrder == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryOrder");
    } else {
      return new DeliveryOrderClientListResponse(
          deliveryOrder.getId(),
          deliveryOrder.getBatch(),
          deliveryOrder.getLimitDate(),
          deliveryOrder.getPriorityDate(),
          deliveryOrder.getOrderStatus());
    }

  }

  public DeliveryOrderClientDetailsResponse buildDeliveryOrderClientDetailsResponse() {
    if (deliveryOrder == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryOrder");
    } else {
      return new DeliveryOrderClientDetailsResponse(
          deliveryOrder.getId(),
          deliveryOrder.getBatch(),
          deliveryOrder.getLimitDate(),
          deliveryOrder.getUserClient().getFirstname() + " " + deliveryOrder.getUserClient().getLastname(),
          deliveryOrder.getOrderStatus(),
          deliveryOrder.getPercentage());
    }

  }

  public DeliveryOrderSummaryResponse buildDeliveryOrderSummaryResponse() {
        if (deliveryOrder == null) {
      throw new RuntimeException("Debe pasar la entidad DeliveryOrder");
    } else {
      return new DeliveryOrderSummaryResponse(
          deliveryOrder.getId(),
          deliveryOrder.getBatch(),
          deliveryOrder.getPriorityDate(),
          deliveryOrder.getPercentage());
    }
  }
}
