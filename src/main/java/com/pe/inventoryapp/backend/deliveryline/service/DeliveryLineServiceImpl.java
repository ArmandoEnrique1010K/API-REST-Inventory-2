package com.pe.inventoryapp.backend.deliveryline.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.deliveryline.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.model.mapper.DeliveryLineMapper;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineUpdateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineListResponse;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Product_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import com.pe.inventoryapp.backend.user.service.UserService;

@Service
public class DeliveryLineServiceImpl implements DeliveryLineService {
  @Autowired
  private DeliveryLineRepository deliveryLineRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private DeliveryOrderRepository deliveryOrderRepository;

  @Autowired
  private MovementRepository movementRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private Product_DeliveryOrderRepository product_deliveryOrderRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Override
  public void saveDeliveryLine(DeliveryLineRequest deliveryLineRequest, Long id_product_deliveryOrder, Long id_user) {

    // SI SE HA GUARDADO EL PEDIDO DE ENTREGA, CUYA UBICACIÓN YA EXISTE EN LA
    // MISMA ORDEN DE ENTREGA, NO SE TIENE QUE AGREGAR LA LINEA DE ENTREGA
    existDeliveryLineByProduct_DeliveryOrder(deliveryLineRequest.getIdLocation(), id_product_deliveryOrder);
    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
    String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

    if (id_product_deliveryOrder == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    // Obtener el producto y orden de entrega desde Product_DeliveryOrder
    Product_DeliveryOrder product_DeliveryOrder = product_deliveryOrderRepository.findById(id_product_deliveryOrder).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La orden de entrega no existe"));


    DeliveryLine deliveryLine = new DeliveryLine();

    deliveryLine.setOriginalQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setRequiredQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setDeliveredQuantity(0);
    deliveryLine.setPendingQuantity(deliveryLineRequest.getRequiredQuantity());
    // Automaticamente se actualiza la fecha de actualización porque hay una anotacion en la entidad
    deliveryLine.setLimitDate(deliveryLineRequest.getLimitDate());
    deliveryLine.setUpdatedByUser(username);
    deliveryLine.setPreparationStatus(PreparationStatus.INPROGRESS);

    // Buscar el id de la ubicación y orden de entrega
    Long idLocation = deliveryLineRequest.getIdLocation();
    Long idDeliveryOrder = product_DeliveryOrder.getDeliveryOrder().getId();
    Long idProduct = product_DeliveryOrder.getProduct().getId();

    if (idLocation == null || idDeliveryOrder == null || idProduct == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Location location = locationRepository.findById(idLocation)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La ubicación no existe en el sistema"));

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(idDeliveryOrder)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El pedido de entrega no existe en el sistema"));

    Product product = productRepository.findById(idProduct).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El producto no existe en el sistema"));

    deliveryLine.setLocation(location);
    deliveryLine.setDeliveryOrder(deliveryOrder);
    deliveryLine.setProduct(product);
    deliveryLine.setProductDeliveryOrder(product_DeliveryOrder);
    deliveryLineRepository.save(deliveryLine);

    // TODO: EN EL MODULO DE MOVEMENT, SI TODAS LAS ORDENES DE ENTREGA YA ESTAN MARCADAS COMO READY, ENTONCES LA ORDEN DE ENTRRGA DEBE MARCARSE COMO READY
    
    // OPERACIONES CON LA ORDEN DE ENTREGA (DELIVERY ORDER)

    // 1° actualizar la fecha limite de deliveryOrder comparando todas las lineas de entrega y tomar el valor con la fecha más cercana que no haya sido entregada
    deliveryOrder.setLimitDate(getClosestLimitDate(idDeliveryOrder));


    // 2° CALCULAR LA SUMATORIA DE LAS CANTIDADES REQUERIDAS DE TODAS LAS LINEAS DE ENTREGA POR ORDEN DE ENTREGA
    product_DeliveryOrder.setRequiredQuantityTotal(deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(id_product_deliveryOrder));

    // 3° actualizar el estado a INPROGRESS cada vez que se guarde una nueva linea de entrega
    deliveryOrder.setPreparationStatus(PreparationStatus.INPROGRESS);

    deliveryOrderRepository.save(deliveryOrder);
  }


  // Repository → devuelve Optional
  // Service → trabaja con entidades reales
  // Nunca uses .get()
  // Nunca propagues Optional fuera del repository

  @Override
  public Page<DeliveryLineListResponse> findAllDeliveryLinesByDeliveryOrderIdPageable(
      Long deliveryOrderId,
      Integer minRequiredQuantity,
      Integer maxRequiredQuantity,
      LocalDateTime minLimitDate,
      LocalDateTime maxLimitDate,
      PreparationStatus preparationStatus,
      String location,
      Pageable pageable) {
    if (deliveryOrderId != null && !deliveryOrderRepository.existsById(deliveryOrderId)) {
      throw new BusinessException(
          ResponseStatusCodes.ENTITY_NOT_FOUND,
          "La orden de entrega no existe");
    }

    Page<DeliveryLine> deliveryLines = deliveryLineRepository.searchAllByDeliveryOrderIdAndParams(
        deliveryOrderId, minRequiredQuantity, maxRequiredQuantity, minLimitDate, 
        maxLimitDate, preparationStatus, location, pageable);

    return deliveryLines.map(deliveryLine -> DeliveryLineMapper.builder().setDeliveryLine(deliveryLine).buildDeliveryLineListResponse());
  }

  @Override
  public DeliveryLineDetailsResponse findDeliveryLineById(Long id) {

    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La linea de entrega no existe"));

    return  DeliveryLineMapper.builder().setDeliveryLine(deliveryLine).buildDeliveryLineDetailsResponse();
  }


  // ESTE MÉTODO SIRVE PARA CAMBIAR LA CANTIDAD REQUERIDA Y LA FECHA LIMITE
  @Override
  public void updateDeliveryLineById(Long id, DeliveryLineUpdateRequest deliveryLineUpdateRequest, Long id_user) {
    // DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
    // String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

    if (id_user == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    User user = userRepository.findById(id_user)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.COMMON_ERROR));

    String username = user.getFirstname() + " " + user.getLastname();

    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La linea de entrega no existe en el sistema"));

    Long deliveryLine_id = deliveryLine.getId();

    if (deliveryLine_id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Long deliveryOrder_id = deliveryLine.getDeliveryOrder().getId();

    if (deliveryOrder_id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryOrder_id)
        .orElseThrow(
            () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El pedido de entrega no existe"));


    // DEBE BUSCAR LA RELACION PRODUCTOS - ORDENES DE ENTREGA PARA ACTUALIZAR LA SUMATORIA DE LA CANTIDAD REQUERIDA
    Product_DeliveryOrder product_DeliveryOrder = deliveryLine.getProductDeliveryOrder();
    if (product_DeliveryOrder == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Long product_DeliveryOrder_id = deliveryLine.getProductDeliveryOrder().getId();

    if (product_DeliveryOrder_id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Product product = product_DeliveryOrder.getProduct();
    if (product == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }





    // NO SE ACTUALIZA LA CANTIDAD ORIGINAL
    deliveryLine.setRequiredQuantity(deliveryLineUpdateRequest.getRequiredQuantity());  
    deliveryLine.setLimitDate(deliveryLineUpdateRequest.getLimitDate());
    deliveryLine.setUpdatedByUser(username);

    // SI SE ACTUALIZA UNA LINEA DE ENTREGA

    // 1° actualizar la fecha limite de deliveryOrder comparando todas las lineas de
    // entrega y tomar el valor con la fecha más cercana que no haya sido entregada

    
    // 2° CASOS ESPECIALES

    // SI LA CANTIDAD REQUERIDA CAMBIA Y LA CANTIDAD ENTREGADA ES MENOR QUE LA CANTIDAD REQUERIDA
    Integer requiredQuantity = deliveryLine.getRequiredQuantity();
    Integer deliveredQuantity = deliveryLine.getDeliveredQuantity();
    

    if (requiredQuantity > deliveredQuantity) {
      // Calcular el nuevo total que hace falta entregar
      deliveryLine.setPendingQuantity(requiredQuantity - deliveredQuantity);
      deliveryLine.setPreparationStatus(PreparationStatus.INPROGRESS);
    }

    // SI LA CANTIDAD REQUERIDA CAMBIA Y LA CANTIDAD ENTREGADA ES MENOR QUE LA
    // CANTIDAD REQUERIDA
    if (deliveryLine.getRequiredQuantity() < deliveryLine.getDeliveredQuantity()) {
      // ESTO SERIA UN EXCESO DE CANTIDAD (NUMERO NEGATIVO RESULTANTE), QUEDA
      // PENDIENTE EL MANEJO DE CANTIDAD EXCESIVA

      // Se tendria un numero negativo como cantidad pendiente
      deliveryLine.setPendingQuantity(requiredQuantity - deliveredQuantity);
      deliveryLine.setPreparationStatus(PreparationStatus.INPROGRESS);
    }

    // SI LA CANTIDAD REQUERIDA CAMBIA Y LA CANTIDAD ENTREGADA SON IGUALES
    if (requiredQuantity == deliveredQuantity){
      deliveryLine.setPendingQuantity(0);
      deliveryLine.setPreparationStatus(PreparationStatus.READY);
    }
    deliveryLineRepository.save(deliveryLine);

    // MANEJO DE LA FECHA LIMITE
    deliveryOrder.setLimitDate(getClosestLimitDate(deliveryOrder_id));
    deliveryOrderRepository.save(deliveryOrder);

    // Debe actualizar la sumatoria de las cantidades requeridas
    product_DeliveryOrder.setRequiredQuantityTotal(
        deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder_id));

    product_deliveryOrderRepository.save(product_DeliveryOrder);


    // ESTO REPRESENTA UN NUEVO MOVIMIENTO DE CANTIDAD
    Movement  movement = new Movement();
    movement.setQuantity(deliveryLineUpdateRequest.getRequiredQuantity());
    movement.setUsername_snapshot(username);
    movement.setMovementType(MovementType.ALTER);
    movement.setComment("Se actualizo la cantidad de la linea de entrega con ID: " + deliveryLine_id);

    movement.setDeliveryLine(deliveryLine);
    movement.setProduct(product);
    movement.setStockLot(null);
    movement.setUser(user);
    movement.setStockLotEmitter(null);

    movementRepository.save(movement);

  }

  @Override
  public void deleteDeliveryLineById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La linea de entrega no existe"));

    if (deliveryLine == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Long deliveryLineId = deliveryLine.getId();

    if (deliveryLineId == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryLineId).orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La orden de entrega no existe"));
    if (deliveryOrder == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }
    Long deliveryOrderId = deliveryOrder.getId();

    if (deliveryOrderId == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Product_DeliveryOrder product_DeliveryOrder = product_deliveryOrderRepository.findById(
        deliveryOrderId).orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La relacion producto-orden de entrega no existe"));

    if (deliveryLine == null || product_DeliveryOrder == null || deliveryOrder == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    } else {
      // SI HAY CANTIDAD ENTREGADA, ENTONCES YA NO SE PODRA ELIMINAR ESTE CAMPO
      if (deliveryLine.getDeliveredQuantity() > 0) {
        throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "No se puede eliminar porque ya hay una cantidad a entregar");
      }
      deliveryLineRepository.delete(deliveryLine);
      // RECALCULAR EL TOTAL DE CANTIDAD PENDIENTE
      // 1° actualizar la fecha limite de deliveryOrder comparando todas las lineas de
      // entrega y tomar el valor con la fecha más cercana que no haya sido entregada
      deliveryOrder.setLimitDate(getClosestLimitDate(deliveryOrder.getId()));

      // 2° CALCULAR LA SUMATORIA DE LAS CANTIDADES REQUERIDAS DE TODAS LAS LINEAS DE
      // ENTREGA POR ORDEN DE ENTREGA
      product_DeliveryOrder.setRequiredQuantityTotal(
          deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder.getId()));

      // 3° actualizar el estado a...
      
      // Operacion para verificar si todas las lineas de entrega de una orden de entrega han sido entregadas, es decir si todas tiene el estado READY
      if (deliveryLineRepository.allLinesAreReady(deliveryOrderId)) {
        deliveryOrder.setPreparationStatus(PreparationStatus.READY);
      } else {
        deliveryOrder.setPreparationStatus(PreparationStatus.INPROGRESS);
      }

      deliveryOrderRepository.save(deliveryOrder);
    }
  }

  // @Override
  // public void changePreparationStatusDeliveryLineById(Long id, PreparationStatus preparationStatus, Long id_user){
  //   if (id == null) {
  //     throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
  //   }


  //   DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
  //       () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La orden de entrega no existe"));

  //   // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
  //   DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
  //   String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

  //   deliveryLine.setPreparationStatus(preparationStatus);
  //   deliveryLine.setUpdatedByUser(username);
  //   deliveryLineRepository.save(deliveryLine);
  // }

  @Override
  public void changeDeliveredStatusDeliveryLineById(Long id, Long id_user) {
    
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La orden de entrega no existe"));


    if (deliveryLine.getPreparationStatus() != PreparationStatus.READY) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La linea de entrega no puede ser entregada porque tiene el estado " + deliveryLine.getPreparationStatus());
    }
    
    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
    String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

    deliveryLine.setPreparationStatus(PreparationStatus.DELIVERED);
    deliveryLine.setUpdatedByUser(username);
    deliveryLineRepository.save(deliveryLine);
  }


  // Metodo auxiliar
  // Busca si existe una linea de entrega que pertenezca a esa ubicación y tambien a esa misma orden de entrega
  private void existDeliveryLineByProduct_DeliveryOrder(Long idLocation, Long idProduct_DeliveryOrder) {

    // VERIFICA SI EL MISMO PRODUCTO EXISTE EN ESA MISMA UBICACION
    // SE PUEDE TENER MÁS DE UN PRODUCTO EN ESA MISMA UBICACION
    if (deliveryLineRepository
        .existsByLocationIdAndProductDeliveryOrderId(idLocation, idProduct_DeliveryOrder)) {

      throw new FieldValidation(
          "idLocation",
          "La línea de entrega para esa ubicación ya existe en esta orden");
    }
  }

  // Tomar la fecha mas cercana que no haya sido entregada
  private LocalDateTime getClosestLimitDate(Long idDeliveryOrder) {
    // 1° encontrar todas las lineas de entrega correspondientes a la orden de entrega
    // 2° tomar las fechas limites de cada linea de entrega cuyo estado sea INPROGRESS
    // 3° devolver la fecha más cercana que no haya sido entregada

    return deliveryLineRepository
        .findClosestLimitDate(idDeliveryOrder)
        .orElse(null); // o lanza excepción
    }


  @Override
  public void changeCanceledStatusDeliveryLineById(Long id, Long id_user) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryLine.getPreparationStatus() != PreparationStatus.INPROGRESS || deliveryLine.getPreparationStatus() != PreparationStatus.READY) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,
          "La linea de entrega no puede ser cancelada porque tiene el estado " + deliveryLine.getPreparationStatus());
    }

    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
    String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

    // TODO: FALTA IMPLEMENTAR UNA LOGICA PARA REALIZAR ALGO CON LA CANTIDAD REQUERIDA

    deliveryLine.setPreparationStatus(PreparationStatus.CANCELED);
    deliveryLine.setUpdatedByUser(username);
    deliveryLineRepository.save(deliveryLine);
  }


  @Override
  public void changeMissingStatusDeliveryLineById(Long id, Long id_user) {
    if (id == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La orden de entrega no existe"));

    // Solamente podra declarar perdida si la linea de entrega se encuentra entregada
    if (deliveryLine.getPreparationStatus() != PreparationStatus.DELIVERED) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,
          "La linea de entrega no puede ser cancelada porque tiene el estado " + deliveryLine.getPreparationStatus());
    }

    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
    String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

    // TODO: FALTA IMPLEMENTAR UNA LOGICA PARA REALIZAR ALGO CON LA CANTIDAD
    // REQUERIDA

    deliveryLine.setPreparationStatus(PreparationStatus.MISSING);
    deliveryLine.setUpdatedByUser(username);
    deliveryLineRepository.save(deliveryLine);
  }
}
