package com.pe.inventoryapp.backend.deliveryorder.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.mapper.DeliveryOrderMapper;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientListResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

  @Autowired
  private DeliveryOrderRepository deliveryOrderRepository;

  @Autowired
  private UserRepository userRepository;

  private static final long BATCH_START = 10000L;
  @Override
  public void saveDeliveryOrder(DeliveryOrderRequest deliveryOrderRequest, Long id_user) {

    Long id_client = deliveryOrderRequest.getIdClient();

    if (id_user == null || id_client == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
      () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    User userClient = userRepository.findById(id_client).orElseThrow(
      () -> new BusinessException(ResponseStatus.NOT_FOUND, "El cliente no existe")); 


    // TODO: ESTO ES IMPOSIBLE, SE SABE QUE CADA NUEVO USUARIO CREADO SIEMPRE VA A TENER EL ROL DE USER
    // Verificar que el usuario seleccionado tenga el rol de USER (Cliente)
    // System.out.println(userClient.getRoles().stream().anyMatch(r -> "ROLE_USER".equals(r.getName())));

    // if (!userClient.getRoles().stream().anyMatch(r -> "ROLE_USER".equals(r.getName()))) {
    //   throw new BusinessException(ResponseStatus.CONFLICT, "El usuario seleccionado no es un cliente");
    // }

    DeliveryOrder deliveryOrder = new DeliveryOrder();

    deliveryOrder.setLimitDate(deliveryOrderRequest.getLimitDate());
    // La fecha limite prioritaria se establece en null porque aun no hay una fecha de entrega de una linea de entrega
    deliveryOrder.setPriorityDate(null);
    deliveryOrder.setOrderStatus(OrderStatus.PENDING);
    deliveryOrder.setUserCreator(user);
    deliveryOrder.setUserUpdater(user);
    deliveryOrder.setUserClient(userClient);

    DeliveryOrder saved = deliveryOrderRepository.save(deliveryOrder);

    // Este numero debe ser generado automaticamente
    long newBatch = BATCH_START + saved.getId();
    String newBatchString = String.valueOf(newBatch);

    saved.setBatch(newBatchString);
    deliveryOrderRepository.save(saved);
  }

  @Override
  public PageResponse<DeliveryOrderListResponse> findAllDeliveryOrdersByParams(
      String batch,
      LocalDateTime startDate,
      LocalDateTime endDate,
      String userClientName,
      OrderStatus status,
      Pageable pageable) {  
    Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllByParams(batch, startDate, endDate, status, userClientName, pageable);

    List<DeliveryOrderListResponse> result = deliveryOrders.getContent().stream().map(
      deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder).buildDeliveryOrderListResponse()
    ).toList();

    PageResponse<DeliveryOrderListResponse> pageResponse = new PageResponse<>(
      result,
      deliveryOrders.getNumber(),
      deliveryOrders.getSize(),
      deliveryOrders.getTotalElements(),
      deliveryOrders.getTotalPages(),
      deliveryOrders.hasNext(),
      deliveryOrders.hasPrevious()
    );

    return pageResponse;
  }

  @Override
  public PageResponse<DeliveryOrderListResponse> findAllActiveDeliveryOrdersByParams(
      String batch,
      LocalDateTime startDate,
      LocalDateTime endDate,
      String userClientName,
      Pageable pageable) {
    Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllActiveByParams(batch, startDate, endDate, 
        userClientName, pageable);

    List<DeliveryOrderListResponse> result = deliveryOrders.getContent().stream().map(
        deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder).buildDeliveryOrderListResponse())
        .toList();

    PageResponse<DeliveryOrderListResponse> pageResponse = new PageResponse<>(
        result,
        deliveryOrders.getNumber(),
        deliveryOrders.getSize(),
        deliveryOrders.getTotalElements(),
        deliveryOrders.getTotalPages(),
        deliveryOrders.hasNext(),
        deliveryOrders.hasPrevious());

    return pageResponse;
  }

  @Override
  public PageResponse<DeliveryOrderClientListResponse> findAllDeliveryOrdesByClientId(Long id, String batch,
      LocalDateTime startDate, LocalDateTime endDate, OrderStatus status, Pageable pageable) {

    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllByUserClientId(id, batch, startDate, endDate, 
        status, pageable);

    List<DeliveryOrderClientListResponse> result = deliveryOrders.getContent().stream().map(
        deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder).buildDeliveryOrderClientListResponse())
        .toList();

    PageResponse<DeliveryOrderClientListResponse> pageResponse = new PageResponse<>(
        result,
        deliveryOrders.getNumber(),
        deliveryOrders.getSize(),
        deliveryOrders.getTotalElements(),
        deliveryOrders.getTotalPages(),
        deliveryOrders.hasNext(),
        deliveryOrders.hasPrevious());

    return pageResponse;

  }

  @Override
  public DeliveryOrderDetailsResponse findDeliveryOrderById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
      throw new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega ha sido cancelada");
    }



    return DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
        .buildDeliveryOrderDetailsResponse();
  }

  @Override
  public DeliveryOrderClientDetailsResponse findDeliveryOrderByIdAndValidateUserClient(Long id, Long id_user) {
    if (id == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
      throw new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega ha sido cancelada");
    }

    User user = userRepository.findById(id_user)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    boolean isOnlyUserRole = user.getRoles().size() == 1 &&
        user.getRoles().stream()
            .anyMatch(r -> "ROLE_USER".equals(r.getName()));

    // System.out.println(isOnlyUserRole);
    // System.out.println(user.getRoles());
    // System.out.println(user.getRoles().size());

    // System.out.println(user.getId());
    // System.out.println(deliveryOrder.getUserClient().getId());
    // System.out.println(deliveryOrder.getUserClient().getId().equals(user.getId()));
    
    // Si el usuario tiene solamente el rol de USER, entonces solamente podra ver
    // una orden cuyo userClient sea el mismo usuario que ha iniciado sesión
    if (isOnlyUserRole) {
      if (deliveryOrder.getUserClient().getId().equals(user.getId())) {
        return DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
            .buildDeliveryOrderClientDetailsResponse();
      } else {
        throw new BusinessException(
            ResponseStatus.CONFLICT,
            "El usuario no es el cliente de la orden de entrega");
      }
    } else {
        throw new BusinessException(
            ResponseStatus.CONFLICT,
            "El usuario no tiene el rol de cliente"
        );
      }

  }

  @Override
  public void changeLimitDate(Long id, LocalDateTime limitDate, Long id_user) {

    if (id == null || limitDate == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    deliveryOrder.setLimitDate(limitDate);
    deliveryOrder.setUserUpdater(user);

    deliveryOrderRepository.save(deliveryOrder);
  }

  // TODO: PENDIENTE IMPLEMENTAR UNA LOGICA PARA CAMBIAR EL ESTADO DE LA ORDEN
  // SOLAMENTE SI TODAS LAS LINEAS DE ENTREGAS TIENEN EL ESTADO READY

  // @Override
  // public void changeStatusOrderToCanceledById(Long id, Long id_user) {
  //   if (id == null || id_user == null) {
  //     throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
  //   }

  //   User user = userRepository.findById(id_user)
  //       .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

  //   DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
  //       () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

  //       if (deliveryOrder.getOrderStatus() != OrderStatus.PENDING
  //       && deliveryOrder.getOrderStatus() != OrderStatus.READY && deliveryOrder.getOrderStatus() != OrderStatus.CANCELED) {
  //     throw new BusinessException(ResponseStatus.CONFLICT,
  //         "La orden de entrega no puede ser cancelada");
  //   }

  //   deliveryOrder.setOrderStatus(OrderStatus.CANCELED);
  //   deliveryOrder.setUserUpdater(user);
  //   deliveryOrderRepository.save(deliveryOrder);
  // }

  @Override
  public void cancelDeliveryOrderById(Long id, Long id_user) {
    if (id == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

     User user = userRepository.findById(id_user)
     .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    // TODO: ESTE MÉTODO DEBE "BORRAR" UNA ORDEN DE ENTREGA BAJO CIERTAS CONDICIONES
    // SI LO BORRA, DEBE CREAR UN NUEVO LOTE DE STOCK CON LA SUMATORIA DE LAS CANTIDADES ENTREGADAS DE LAS LINEAS DE ENTREGA QUE ESTAN EN MODO READY, PENDING Y DELIVERED

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
      () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    deliveryOrder.setOrderStatus(OrderStatus.CANCELED);
    deliveryOrder.setUserUpdater(user);
    deliveryOrderRepository.save(deliveryOrder);
  }

  @Override
  public void sendDeliveryOrderById(Long id, Long id_user) {
    if (id == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }
    User user = userRepository.findById(id_user)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    // TODO: DEBE VERIFICAR QUE TODAS LAS LINEAS DE ENTREGA ASOCIADAS A ESTA ORDEN DE ENTREGA TENGAN EL ESTADO READY, ESTO TAMBIEN CAMBIARA EL ESTADO DE TODAS LAS LINEAS DE ENTREGA A DELIVERED

    // TODO: CONSTRUIR UN METODO PARA ACTUALIZAR EL ESTADO DE UNA ORDEN DE ENTREGA DE FORMA AUTOMATICA CUANDO TODAS LAS LINEAS DE ENTREGA TENGAN EL ESTADO DELIVERED, EN DELIVERYLINESERVICE
    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    deliveryOrder.setOrderStatus(OrderStatus.DELIVERED);
    deliveryOrder.setUserUpdater(user);
    deliveryOrderRepository.save(deliveryOrder);
  }
}
