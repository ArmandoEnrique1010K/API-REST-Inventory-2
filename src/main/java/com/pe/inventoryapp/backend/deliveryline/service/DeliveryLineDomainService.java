package com.pe.inventoryapp.backend.deliveryline.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryline.repository.specifications.DeliveryLineSpecifications;

@Service
public class DeliveryLineDomainService {

  private final DeliveryLineRepository deliveryLineRepository;

  public DeliveryLineDomainService(DeliveryLineRepository deliveryLineRepository) {
    this.deliveryLineRepository = deliveryLineRepository;
  }

  // En este caso se ha optado por usar los specifications de DeliveryOrder en una clase de dominio porque este método se reutiliza en 2 servicios de DeliveryLine
  public List<DeliveryLine> findAllByDeliveryOrderId(Long deliveryOrderId) {

    Specification<DeliveryLine> spec = (DeliveryLineSpecifications.fetchAllRelations())
        .and(DeliveryLineSpecifications.hasDeliveryOrder(
            deliveryOrderId))
        .and(DeliveryLineSpecifications.isNotCanceled());

    List<DeliveryLine> result = deliveryLineRepository.findAll(spec);
    return result;
  }

  // List<DeliveryLine> deliveryLines =
  // deliveryLineRepository.findAllByDeliveryOrderId(id);

}
