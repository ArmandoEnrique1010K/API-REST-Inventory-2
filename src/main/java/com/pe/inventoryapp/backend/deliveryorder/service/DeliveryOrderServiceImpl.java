package com.pe.inventoryapp.backend.deliveryorder.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.deliveryline.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.mapper.DeliveryOrderMapper;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
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
  public void saveDeliveryOrder(DeliveryOrderRequest deliveryOrderRequest, Long id_user) {
    verifyBatchExist(deliveryOrderRequest.getBatch());
    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
    String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

    // TODO: SUGERENCIA DE QUE EL BATCH SE PUEDA GENERAR AUTOMATICAMENTE CON CADA ORDEN DE ENTREGA
    DeliveryOrder deliveryOrder = new DeliveryOrder();
    deliveryOrder.setBatch(deliveryOrderRequest.getBatch());
    deliveryOrder.setCreatedByUser(username);
    deliveryOrder.setUpdatedByUser(username);
    deliveryOrder.setLimitDate(null);
    deliveryOrder.setPreparationStatus(PreparationStatus.INPROGRESS);

    // BUSCAR AL USUARIO POR SU ID
    Optional<User> userEmail = userRepository.findByEmail(detailsUserResponse.getEmail());
    User userEntity = userEmail.orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND,
        "El usuario no existe"));

    deliveryOrder.setUser(userEntity);
    deliveryOrderRepository.save(deliveryOrder);
  }

  @Override
  public Page<DeliveryOrderListResponse> findAllDeliveryOrdersByParams(
      PreparationStatus status,
      String createdByUser,
      String batch,
      LocalDateTime startDate,
      LocalDateTime endDate,
          Pageable pageable
    ) {
    Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllByParams(pageable, status,
        createdByUser, batch, startDate, endDate);

    return deliveryOrders
        .map(deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
            .buildDeliveryOrderListResponse());
  }

  @Override
  public Page<DeliveryOrderListResponse> findAllActiveDeliveryOrdersByParams(
      String createdByUser,
      String batch,
      LocalDateTime startDate,
      LocalDateTime endDate,
          Pageable pageable) {
    Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllActiveByParams(pageable,
        createdByUser, batch,  startDate, endDate);

    return deliveryOrders
        .map(deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
            .buildDeliveryOrderListResponse());
  }

  @Override
  public DeliveryOrderDetailsResponse findDeliveryOrderById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id)
        .orElseThrow(
            () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La orden de entrega no existe"));

    return DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
        .buildDeliveryOrderDetailsResponse();
  }

  @Override
  public void updateDeliveryOrderById(Long id, DeliveryOrderRequest deliveryOrderRequest, Long id_user) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    // TAMBIEN DEBE ACTUALIZAR EL USUARIO QUE HA ACTUALIZADO LA ORDEN (EL QUE
    // HA INICIADO SESION)

    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
    String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

    // BUSCAR AL USUARIO POR SU ID
    // Optional<User> userEmail =
    // userRepository.findByEmail(detailsUserResponse.getEmail());
    // User userEntity = userEmail.orElseThrow(() -> new
    // BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND,
    // "El usuario no existe"));

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La orden de entrega no existe"));

    verifyBatchExist(deliveryOrderRequest.getBatch());

    deliveryOrder.setBatch(deliveryOrderRequest.getBatch());
    deliveryOrder.setUpdatedByUser(username);

    deliveryOrderRepository.save(deliveryOrder);
  }
  // return "Pedido de entrega actualizado correctamente";

  // @Override
  // public void changePreparationStatusDeliveryOrderById(Long id, PreparationStatus preparationStatus, Long id_user) {
  //   if (id == null) {
  //     throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
  //   }

  //   // TODO: LISTAR LOS ESTADOS NO PERMITIDOS
  //   // EVITAR QUE EL USUARIO HAYA INTRODUCIDO UN ESTADO NO PERMITIDO
  //   if (preparationStatus == PreparationStatus.INPROGRESS) {
  //     throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "El estado de la orden de entrega no puede ser INPROGRESS");
  //   }


  //   DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
  //       () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La orden de entrega no existe"));

  //   // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
  //   DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
  //   String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();


  //   deliveryOrder.setPreparationStatus(preparationStatus);
  //   deliveryOrder.setUpdatedByUser(username);
  //   deliveryOrderRepository.save(deliveryOrder);
  // }

  private void verifyBatchExist(String batch) {
    if (deliveryOrderRepository.findByBatch(batch).isPresent()) {
      throw new FieldValidation("batch", "El lote de entrega ya existe, introduzca otro lote");
    }
  }
}
