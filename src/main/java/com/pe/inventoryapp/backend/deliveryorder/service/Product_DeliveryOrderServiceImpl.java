package com.pe.inventoryapp.backend.deliveryorder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Product_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;

@Service
public class Product_DeliveryOrderServiceImpl implements Product_DeliveryOrderService{

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private DeliveryOrderRepository deliveryOrderRepository;

  @Autowired
  private Product_DeliveryOrderRepository product_DeliveryOrderRepository;

  // Método para relacionar un producto con una orden de entrega
  @Override
  public void saveProduct_DeliveryOrder(Long idProduct, Long idDeliveryOrder) {

    if (idProduct == null || idDeliveryOrder == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    // Buscar producto y orden de entrega
    Product product = productRepository.findById(idProduct).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El producto no existe en el sistema")
    );
    
    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(idDeliveryOrder).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La orden de entrega no existe en el sistema")
    );

    // Verificar si el producto esta activo
    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "El producto se encuentra desactivado");
    }

    // GUARDAR LOS CAMBIOS EN LA BASE DE DATOS
    Product_DeliveryOrder product_DeliveryOrder = new Product_DeliveryOrder();
    product_DeliveryOrder.setProduct(product);
    product_DeliveryOrder.setDeliveryOrder(deliveryOrder);
    product_DeliveryOrder.setQuantityTotal(0);

    product_DeliveryOrderRepository.save(product_DeliveryOrder);
  }
  
}
