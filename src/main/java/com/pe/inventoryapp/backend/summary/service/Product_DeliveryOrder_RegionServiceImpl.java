package com.pe.inventoryapp.backend.summary.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Region;
import com.pe.inventoryapp.backend.summary.model.mapper.Model_DeliveryOrder_RegionMapper;
import com.pe.inventoryapp.backend.summary.model.response.Model_DeliveryOrder_RegionResponse;
import com.pe.inventoryapp.backend.summary.repository.Model_DeliveryOrder_RegionRepository;

@Service
public class Product_DeliveryOrder_RegionServiceImpl implements Product_DeliveryOrder_RegionService {

  @Autowired
  private Model_DeliveryOrder_RegionRepository product_DeliveryOrder_RegionRepository;

  @Override
  public List<Model_DeliveryOrder_RegionResponse> findAllByDeliveryOrderId(Long deliveryOrderId){
   List<Model_DeliveryOrder_Region> product_DeliveryOrder_Regions = (List<Model_DeliveryOrder_Region>)  product_DeliveryOrder_RegionRepository.findAllByModel_DeliveryOrderIdAndRequiredTotalQuantityGreaterThanZero(deliveryOrderId);

    return product_DeliveryOrder_Regions.stream()
        .map(pdr -> Model_DeliveryOrder_RegionMapper.builder().setProduct_DeliveryOrder_Region(
            pdr).buildProduct_DeliveryOrder_RegionResponse())
        .collect(Collectors.toList());
  }
  
}
