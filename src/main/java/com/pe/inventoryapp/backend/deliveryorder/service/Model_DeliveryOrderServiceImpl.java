package com.pe.inventoryapp.backend.deliveryorder.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.mapper.Model_DeliveryOrderMapper;
import com.pe.inventoryapp.backend.deliveryorder.model.response.Model_DeliveryOrderResponse;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Model_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;

@Service
public class Model_DeliveryOrderServiceImpl implements Model_DeliveryOrderService {

    private final ModelRepository modelRepository;
    private final DeliveryOrderRepository deliveryOrderRepository;
    private final Model_DeliveryOrderRepository model_DeliveryOrderRepository;

    public Model_DeliveryOrderServiceImpl (
        ModelRepository modelRepository,
        DeliveryOrderRepository deliveryOrderRepository,
        Model_DeliveryOrderRepository model_DeliveryOrderRepository
    ){
        this.modelRepository = modelRepository;
        this.deliveryOrderRepository = deliveryOrderRepository;
        this.model_DeliveryOrderRepository = model_DeliveryOrderRepository;
    }

    @Override
    @Transactional
    public void saveRelationModelInDeliveryOrder(Long idProduct, Long idDeliveryOrder) {
        if (idProduct == null || idDeliveryOrder == null) {
            // throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
            throw new BusinessException(ResponseStatus.BAD_REQUEST, "Datos invalidos");
        }

        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(idDeliveryOrder).orElseThrow(
                () -> new BusinessException(ResponseStatus.NOT_FOUND,
                        "La orden de entrega no existe en el sistema"));

        if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
            throw new BusinessException(ResponseStatus.CONFLICT,
                    "La orden de entrega ha sido cancelada");
        }

        if (deliveryOrder.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException(ResponseStatus.CONFLICT,
                    "La orden de entrega ha sido entregada");
        }

        Model model = modelRepository.findById(
                idProduct)
                .orElseThrow(() -> new BusinessException(
                        ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

        // Validar no exista la misma relación
        if (model_DeliveryOrderRepository.existsByDeliveryOrderIdAndModelId(idDeliveryOrder, idProduct)) {
            throw new BusinessException(ResponseStatus.CONFLICT,
                    "La relacion de modelo del producto y orden de entrega ya existe en el sistema");
        }
        
        if (!model.isStatus()) {
            throw new BusinessException(
                    ResponseStatus.CONFLICT,
                    "El modelo del producto se encuentra desactivado");
        }

        Model_DeliveryOrder model_DeliveryOrder = new Model_DeliveryOrder();
        model_DeliveryOrder.setRequiredQuantityTotal(0);
        model_DeliveryOrder.setStatus(true);
        model_DeliveryOrder.setDeliveryOrder(deliveryOrder);
        model_DeliveryOrder.setModel(model);

        model_DeliveryOrderRepository.save(model_DeliveryOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Model_DeliveryOrderResponse> findAllByDeliveryOrderId(Long idDeliveryOrder) {

        List<Model_DeliveryOrder> product_DeliveryOrders = model_DeliveryOrderRepository
                .findAllByDeliveryOrderId(idDeliveryOrder);

        return product_DeliveryOrders
                .stream().map(deliveryOrder -> Model_DeliveryOrderMapper.builder()
                        .setModel_DeliveryOrder(deliveryOrder).buildModel_DeliveryOrderListResponse())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRelationModelDeliveryOrder(Long id) {
        if (id == null) {
            throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
        Model_DeliveryOrder model_DeliveryOrder = model_DeliveryOrderRepository.findById(
                id).orElseThrow(
                        () -> new BusinessException(ResponseStatus.NOT_FOUND,
                                "La relacion de modelo del producto y orden de entrega no existe en el sistema"));
        if (model_DeliveryOrder == null) {
            throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }

        Long idDeliveryOrder = model_DeliveryOrder.getDeliveryOrder().getId();

        if (idDeliveryOrder == null) {
            throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }

        DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(idDeliveryOrder).orElseThrow(
                () -> new BusinessException(ResponseStatus.NOT_FOUND,
                        "La orden de entrega no existe en el sistema"));
        if (deliveryOrder == null) {
            throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }

        // Solamente si hay al menos una linea de entrega asociada a una relacion de model_DeliveryOrder, ya no se podra eliminar
        if (model_DeliveryOrder.getDeliveryLines().size() > 0 ) {
            throw new BusinessException(
                    ResponseStatus.CONFLICT,
                    "La relacion de modelo del producto y orden de entrega no puede ser eliminada porque hay cantidades requeridas pendientes");
        };

        if (model_DeliveryOrder.isStatus() == false) {
            throw new BusinessException(
                    ResponseStatus.CONFLICT,
                    "La relacion de modelo del producto y orden de entrega no puede ser eliminada porque la relación esta desactivada");
        }  

        model_DeliveryOrder.setStatus(false);
        model_DeliveryOrderRepository.save(model_DeliveryOrder);
    }
}
