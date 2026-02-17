package com.pe.inventoryapp.backend.summary.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Subregion;
import com.pe.inventoryapp.backend.summary.model.mapper.Model_DeliveryOrder_SubregionMapper;
import com.pe.inventoryapp.backend.summary.model.response.Model_DeliveryOrder_SubregionResponse;
import com.pe.inventoryapp.backend.summary.repository.Model_DeliveryOrder_SubregionRepository;

@Service
public class Model_DeliveryOrder_SubregionServiceImpl implements Model_DeliveryOrder_SubregionService {
  private final Model_DeliveryOrder_SubregionRepository model_DeliveryOrder_SubegionRepository;

  public Model_DeliveryOrder_SubregionServiceImpl(
      Model_DeliveryOrder_SubregionRepository model_DeliveryOrder_SubegionRepository) {
    this.model_DeliveryOrder_SubegionRepository = model_DeliveryOrder_SubegionRepository;
  }

  @Override
  public List<Model_DeliveryOrder_SubregionResponse> findAllByDeliveryOrderId(Long deliveryOrderId) {
    List<Model_DeliveryOrder_Subregion> product_DeliveryOrder_Subregions = (List<Model_DeliveryOrder_Subregion>) model_DeliveryOrder_SubegionRepository
        .findAllModel_DeliveryOrder_SubregionsByModel_DeliveryOrderId(deliveryOrderId);

    return product_DeliveryOrder_Subregions.stream()
        .map(pdr -> Model_DeliveryOrder_SubregionMapper.builder().setProduct_DeliveryOrder_Subregion(
            pdr).buildModel_DeliveryOrder_SubregionResponse())
        .collect(Collectors.toList());
  }
}
