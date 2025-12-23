package com.pe.inventoryapp.backend.delivery.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.delivery.model.mapper.DeliveryOrderMapper;
import com.pe.inventoryapp.backend.delivery.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.delivery.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import com.pe.inventoryapp.backend.user.service.UserService;

@Service
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

  @Autowired
  private DeliveryOrderRepository deliveryOrderRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Override
  public String save(DeliveryOrderRequest deliveryOrderRequest, Long id_user) {
    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    Optional<DetailUserResponse> user = userService.findUserById(id_user);
    String username = user.get().getFirstname() + " " + user.get().getLastname();

    DeliveryOrder deliveryOrder = new DeliveryOrder();
    deliveryOrder.setBatch(deliveryOrderRequest.getBatch());
    deliveryOrder.setCreatedByUser(username);
    deliveryOrder.setUpdatedByUser(username);
    deliveryOrder.setLimitDate(null);
    deliveryOrder.setQuantityTotal(0);
    deliveryOrder.setPreparationStatus(PreparationStatus.INPROGRESS);

    // BUSCAR AL USUARIO POR SU ID

    Optional<User> userEmail = userRepository.findByEmail(user.get().getEmail());
    User userEntity = userEmail.orElseThrow(() -> new RuntimeException("El usuario no existe"));

    deliveryOrder.setUser(userEntity);

    deliveryOrderRepository.save(deliveryOrder);

    // Guardar el pedido de entrega en la base de datos
    return "Pedido de entrega guardado correctamente";

  }

  // @Override
  // public List<DeliveryOrderDetailsResponse> findAll() {
  // List<DeliveryOrder> deliveryOrders = (List<DeliveryOrder>)
  // deliveryOrderRepository.findAll();

  // return deliveryOrders.stream()
  // .map(
  // deliveryOrder ->
  // DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder).buildDeliveryOrderResponse())
  // .collect(Collectors.toList());
  // }

  // @Override
  // public List<DeliveryOrderDetailsResponse>
  // findAllByPreparationStatus(PreparationStatus status) {

  // List<DeliveryOrder> deliveryOrders = (List<DeliveryOrder>)
  // deliveryOrderRepository.findByPreparationStatus(status);

  // return deliveryOrders.stream()
  // .map(
  // deliveryOrder ->
  // DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder).buildDeliveryOrderResponse())
  // .collect(Collectors.toList());

  // }

  @Override
  public Page<DeliveryOrderListResponse> findAllDeliveryOrdersByParams(Pageable pageable,
      PreparationStatus status,
      String createdByUser, String batch, Integer minQuantity,
      Integer maxQuantity, LocalDateTime startDate, LocalDateTime endDate) {
    Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllByParams(pageable, status,
        createdByUser, batch, minQuantity, maxQuantity, startDate, endDate);

    return deliveryOrders
        .map(
            deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
                .buildDeliveryOrderListResponse());

  }

  @Override
  public Optional<DeliveryOrderDetailsResponse> findById(Long id) {
    return deliveryOrderRepository.findById(id).map(
        deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
            .buildDeliveryOrderDetailsResponse());
  }

  @Override
  public String update(Long id, DeliveryOrderRequest deliveryOrderRequest) {
    Optional<DeliveryOrder> deliveryOrderById = deliveryOrderRepository.findById(id);

    if (deliveryOrderById.isPresent()) {
      DeliveryOrder deliveryOrderData = deliveryOrderById.orElseThrow();
      deliveryOrderData.setBatch(deliveryOrderRequest.getBatch());
      // TODO: TAMBIEN DEBE ACTUALIZAR EL USUARIO QUE HA ACTUALIZADO LA ORDEN (EL QUE
      // HA INICIADO SESION)

      deliveryOrderRepository.save(deliveryOrderData);
      return "Pedido de entrega actualizado correctamente";
    } else {
      return "Pedido de entrega no encontrado";
    }
  }

  @Override
  public void changePreparationStatus(Long id, PreparationStatus status) {
    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow();
    deliveryOrder.setPreparationStatus(status);
    deliveryOrderRepository.save(deliveryOrder);
  }

  @Override
  public void verifyBatchExist(String batch) {
    if (deliveryOrderRepository.findByBatch(batch).isPresent()) {
      throw new FieldValidation("batch", "El lote ya existe, introduzca otro lote");
    }
  }

}
