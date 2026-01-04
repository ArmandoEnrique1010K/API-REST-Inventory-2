package com.pe.inventoryapp.backend.deliveryorder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.mapper.Product_DeliveryOrderMapper;
import com.pe.inventoryapp.backend.deliveryorder.model.request.Product_DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.ProductDeliveryOrderResponse;
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

  // Método para relacionar varios productos con una orden de entrega
  @Transactional
  @Override
  public void saveProduct_DeliveryOrder(Product_DeliveryOrderRequest product_DeliveryOrderRequest, Long idDeliveryOrder) {

    List<Long> idProducts = product_DeliveryOrderRequest.getIdProducts();

    if (idProducts == null || idProducts.isEmpty() || idDeliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_ERROR);
    }
    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(idDeliveryOrder).orElseThrow(
        () -> new BusinessException(ResponseStatus.ENTITY_NOT_FOUND,
            "La orden de entrega no existe en el sistema"));

    List<Product_DeliveryOrder> relations = new ArrayList<>();
    // Buscar producto y orden de entrega
    for (Long productId : idProducts) {

        if (productId == null) {
            throw new BusinessException(ResponseStatus.INTERNAL_ERROR);
        }

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new BusinessException(
                ResponseStatus.ENTITY_NOT_FOUND,
                "El producto con el id " + productId + " no existe en el sistema"
            ));

        if (!product.isStatus()) {
            throw new BusinessException(
                ResponseStatus.DEFAULT_RESOURCE,
                "El producto con el id " + productId + " se encuentra desactivado"
            );
        }

        Product_DeliveryOrder pdo = new Product_DeliveryOrder();
        pdo.setProduct(product);
        pdo.setDeliveryOrder(deliveryOrder);
        pdo.setRequiredQuantityTotal(0);

        relations.add(pdo);
        // product_DeliveryOrderRepository.save(pdo);
    }

    // NOTA: SOLO SE EJECUTA SI CADA UNO DE LOS REGISTROS FUE VALIDADO
    product_DeliveryOrderRepository.saveAll(relations);
  }

  @Override
  public List<ProductDeliveryOrderResponse> findAllByDeliveryOrderId(Long idDeliveryOrder) {

    List<Product_DeliveryOrder> product_DeliveryOrders = product_DeliveryOrderRepository.findAllByDeliveryOrderId(idDeliveryOrder);

    return product_DeliveryOrders.stream().map(deliveryOrder -> Product_DeliveryOrderMapper.builder().setProduct_DeliveryOrder(deliveryOrder).buildProduct_DeliveryOrderListResponse()).collect(Collectors.toList());
  }  
}
