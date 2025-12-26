package com.pe.inventoryapp.backend.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.stock.repository.StockLotRepository;

@Service
public class StockLotServiceImpl implements StockLotService{

  @Autowired
  private StockLotRepository stockLotRepository;

  @Autowired
  private ProductRepository productRepository;

  // TODO: ESTE MÉTODO SE PODRIA UTILIZAR PARA ACTUALIZAR EL STOCK
  @Override
  public void sumAvailableQuantityByProductId(Long productId) {

    Product product = productRepository.findById(productId).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El producto no existe"));

    int totalStock = stockLotRepository.sumAvailableByProductId(productId);
    product.setStock(totalStock);
    productRepository.save(product);

  }
  
}
