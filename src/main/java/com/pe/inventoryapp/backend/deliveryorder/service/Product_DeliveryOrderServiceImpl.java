package com.pe.inventoryapp.backend.deliveryorder.service;

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
import com.pe.inventoryapp.backend.deliveryorder.model.response.ProductDeliveryOrderResponse;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Product_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;

@Service
public class Product_DeliveryOrderServiceImpl implements Product_DeliveryOrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private Product_DeliveryOrderRepository product_DeliveryOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDeliveryOrderResponse> findAllByDeliveryOrderId(Long idDeliveryOrder) {

        List<Product_DeliveryOrder> product_DeliveryOrders = product_DeliveryOrderRepository
                .findAllByDeliveryOrderId(idDeliveryOrder);

        return product_DeliveryOrders
                .stream().map(deliveryOrder -> Product_DeliveryOrderMapper.builder()
                        .setProduct_DeliveryOrder(deliveryOrder).buildProduct_DeliveryOrderListResponse())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveRelationProductInDeliveryOrder(Long idDeliveryOrder, Long idProduct) {
        if (idProduct == null || idDeliveryOrder == null) {
            throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }

        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(idDeliveryOrder).orElseThrow(
                () -> new BusinessException(ResponseStatus.NOT_FOUND,
                        "La orden de entrega no existe en el sistema"));

        Product product = productRepository.findById(
                idProduct)
                .orElseThrow(() -> new BusinessException(
                        ResponseStatus.NOT_FOUND, "El producto no existe"));

        // Validar no exista la misma relación
        if (product_DeliveryOrderRepository.existsByDeliveryOrderIdAndProductId(idDeliveryOrder, idProduct)) {
            throw new BusinessException(ResponseStatus.CONFLICT,
                    "La relacion de producto y orden de entrega ya existe en el sistema");
        }
        
        if (!product.isStatus()) {
            throw new BusinessException(
                    ResponseStatus.CONFLICT,
                    "El producto se encuentra desactivado");
        }

        Product_DeliveryOrder product_DeliveryOrder = new Product_DeliveryOrder();
        product_DeliveryOrder.setProduct(product);
        product_DeliveryOrder.setDeliveryOrder(deliveryOrder);
        product_DeliveryOrder.setRequiredQuantityTotal(0);

        product_DeliveryOrderRepository.save(product_DeliveryOrder);
    }

    @Override
    @Transactional
    public void deleteRelationProductDeliveryOrder(Long idDeliveryOrder) {
        if (idDeliveryOrder == null) {
            throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }

        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(idDeliveryOrder).orElseThrow(
                () -> new BusinessException(ResponseStatus.NOT_FOUND,
                        "La orden de entrega no existe en el sistema"));
        if (deliveryOrder == null) {
            throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }

        Product_DeliveryOrder product_DeliveryOrder = product_DeliveryOrderRepository.findById(idDeliveryOrder).orElseThrow(
                () -> new BusinessException(ResponseStatus.NOT_FOUND,
                        "La relacion de producto y orden de entrega no existe en el sistema")
        );

        if (product_DeliveryOrder == null) {
            throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }

        // Si la sumatoria de las cantidades requeridas es mayor a 0, no se puede eliminar
        // Porque si hubiera cantidades pendientes de entrega, habria una rleacion con DeliveryLine
        if (product_DeliveryOrder.getRequiredQuantityTotal() > 0) {
            throw new BusinessException(
                    ResponseStatus.CONFLICT,
                    "La relacion de producto y orden de entrega no puede ser eliminada porque hay cantidades requeridas pendientes");
        };

        product_DeliveryOrderRepository.delete(product_DeliveryOrder);
    }
}
