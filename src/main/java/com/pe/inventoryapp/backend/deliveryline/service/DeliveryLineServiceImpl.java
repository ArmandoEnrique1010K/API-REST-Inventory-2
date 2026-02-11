package com.pe.inventoryapp.backend.deliveryline.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.model.entity.StockLot_DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.model.mapper.DeliveryLineMapper;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineAllocateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineAlterRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineUpdateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineListResponse;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryline.repository.StockLot_DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder_Region;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Product_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Product_DeliveryOrder_RegionRepository;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.entity.Movement_StockLot;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.movement.repository.Movement_StockLotRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.repository.StockLotRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;

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
  private Product_DeliveryOrderRepository product_DeliveryOrderRepository;

  @Autowired
  private Product_DeliveryOrder_RegionRepository product_DeliveryOrder_RegionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StockLotRepository stockLotRepository;

  @Autowired
  private StockLot_DeliveryLineRepository stockLot_DeliveryLineRepository;

  @Autowired
  private Movement_StockLotRepository movement_StockLotRepository;

  @Override
  public void saveDeliveryLine(DeliveryLineRequest deliveryLineRequest, Long id_product_deliveryOrder, Long id_user) {

    Long id_location = deliveryLineRequest.getIdLocation();

    if (id_location == null || id_product_deliveryOrder == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Location location = locationRepository.findById(
        id_location).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));

    // Obtener el producto y orden de entrega desde Product_DeliveryOrder
    Product_DeliveryOrder product_DeliveryOrder = product_DeliveryOrderRepository.findById(id_product_deliveryOrder)
        .orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND,
                "La relación de producto y orden de entrega no existe"));

    // if (!product_DeliveryOrderRepository
    // .existsByIdAndDeliveryOrderId(
    // id_product_deliveryOrder,
    // product_DeliveryOrder.getDeliveryOrder().getId())) {
    // throw new BusinessException(
    // ResponseStatus.CONFLICT,
    // "El producto no pertenece a la orden de entrega");
    // }

    Long id_deliveryOrder = product_DeliveryOrder.getDeliveryOrder().getId();
    Long id_product = product_DeliveryOrder.getProduct().getId();

    if (id_deliveryOrder == null || id_product == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Regla: no permitir duplicados por ubicación, excepto de las lineas de entrega
    // con estado CANCELED
    boolean exists = deliveryLineRepository
        .existsDuplicate(id_deliveryOrder, id_product, id_location);

    if (exists) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "Ya existe una línea de entrega para este producto en esta ubicación");
    }

    // Crear la linea de entrega
    DeliveryLine deliveryLine = new DeliveryLine();

    deliveryLine.setOriginalQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setRequiredQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setDeliveredQuantity(0);
    deliveryLine.setPendingQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setLimitDate(deliveryLineRequest.getLimitDate());
    // La fecha de actualización se genera automaticamente
    deliveryLine.setLineStatus(LineStatus.PENDING);

    // Actualizar los usuarios creador y actualizador
    deliveryLine.setUserCreator(user);
    deliveryLine.setUserUpdater(user);

    deliveryLine.setLocation(location);
    deliveryLine.setProduct_DeliveryOrder(product_DeliveryOrder);
    deliveryLine.setProduct(product_DeliveryOrder.getProduct());
    deliveryLine.setDeliveryOrder(product_DeliveryOrder.getDeliveryOrder());

    deliveryLineRepository.save(deliveryLine);

    // Actualizar la orden de entrega
    DeliveryOrder deliveryOrder = product_DeliveryOrder.getDeliveryOrder();
    // 1° actualizar la fecha limite de deliveryOrder comparando todas las lineas de
    // entrega y tomar el valor con la fecha más cercana que no haya sido entregada
    deliveryOrder.setPriorityDate(getClosestLimitDate(deliveryOrder.getId()));

    // 2° actualizar el estado a PENDING cada vez que se guarde una nueva linea de
    // entrega
    deliveryOrder.setOrderStatus(OrderStatus.PENDING);
    deliveryOrderRepository.save(deliveryOrder);

    // 3° CALCULAR LA SUMATORIA DE LAS CANTIDADES REQUERIDAS DE TODAS LAS LINEAS DE
    // ENTREGA POR ORDEN DE ENTREGA
    Integer totalRequired = deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(id_product_deliveryOrder, id_product);

    product_DeliveryOrder.setRequiredQuantityTotal(totalRequired);

    product_DeliveryOrderRepository.save(product_DeliveryOrder);

    // Agregar un registro en la entidad Product_DeliveryOrder_Region
    Integer regionTotal = deliveryLineRepository.sumRequiredByProductDeliveryOrderAndRegion(
        product_DeliveryOrder.getId(),
        location.getRegion().getId());

    Product_DeliveryOrder_Region entity = product_DeliveryOrder_RegionRepository
        .findByProduct_DeliveryOrderIdAndRegionId(
            product_DeliveryOrder.getId(),
            location.getRegion().getId())
        .orElseGet(() -> {
          Product_DeliveryOrder_Region e = new Product_DeliveryOrder_Region();
          e.setProduct_DeliveryOrder(product_DeliveryOrder);
          e.setRegion(location.getRegion());
          return e;
        });

    entity.setRequiredTotalQuantity(regionTotal);
    product_DeliveryOrder_RegionRepository.save(entity);
  }

  @Override
  public PageResponse<DeliveryLineListResponse> findAllDeliveryLinesByDeliveryOrderIdPageable(
      Long deliveryOrderId,
      Integer minRequiredQuantity,
      Integer maxRequiredQuantity,
      LocalDateTime minLimitDate,
      LocalDateTime maxLimitDate,
      LineStatus lineStatus,
      String location,
      Pageable pageable) {
    if (deliveryOrderId != null && !deliveryOrderRepository.existsById(deliveryOrderId)) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          "La orden de entrega no existe");
    }

    Page<DeliveryLine> deliveryLines = deliveryLineRepository.searchAllByDeliveryOrderIdAndParams(
        deliveryOrderId, minRequiredQuantity, maxRequiredQuantity, minLimitDate,
        maxLimitDate, lineStatus, location, pageable);

    List<DeliveryLineListResponse> result = deliveryLines.getContent().stream().map(
        deliveryLine -> DeliveryLineMapper.builder()
            .setDeliveryLine(deliveryLine).buildDeliveryLineListResponse())
        .toList();

    PageResponse<DeliveryLineListResponse> pageResponse = new PageResponse<>(
        result,
        deliveryLines.getNumber(),
        deliveryLines.getSize(),
        deliveryLines.getTotalElements(),
        deliveryLines.getTotalPages(),
        deliveryLines.isFirst(),
        deliveryLines.isLast());

    return pageResponse;
  }

  @Override
  public DeliveryLineDetailsResponse findDeliveryLineById(Long id) {

    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La linea de entrega no existe"));

    if (deliveryLine == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    return DeliveryLineMapper.builder().setDeliveryLine(deliveryLine).buildDeliveryLineDetailsResponse();
  }

  // ESTE MÉTODO SIRVE PARA CAMBIAR LA CANTIDAD REQUERIDA Y LA FECHA LIMITE
  @Override
  public void updateDeliveryLineById(Long id, DeliveryLineUpdateRequest deliveryLineUpdateRequest, Long id_user) {
    if (id == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La linea de entrega no existe"));

    Long deliveryLine_id = deliveryLine.getId();

    if (deliveryLine_id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Long deliveryOrder_id = deliveryLine.getDeliveryOrder().getId();

    if (deliveryOrder_id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // LOGICA QUE VERIFIQUE QUE EL ESTADO DE LA LINEA DE ENTREGA NO TENGA EL ESTADO
    // DELIVERED NI CANCELED
    if (deliveryLine.getLineStatus() == LineStatus.DELIVERED || deliveryLine.getLineStatus() == LineStatus.CANCELED) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
          "La linea de entrega no puede ser modificada");
    }

    // Verifica se que haya modificado uno de los datos originales como minimo, 2
    // como maximo
    Integer oldRequired = deliveryLine.getRequiredQuantity();
    Integer newRequired = deliveryLineUpdateRequest.getRequiredQuantity();

    LocalDateTime oldLimitDate = deliveryLine.getLimitDate();
    LocalDateTime newLimitDate = deliveryLineUpdateRequest.getLimitDate();

    if (Objects.equals(oldRequired, newRequired) && Objects.equals(oldLimitDate, newLimitDate)) {
      throw new BusinessException(ResponseStatus.CONFLICT,
          "No ha realizado algún cambio");
    }

    // Balance entre la cantidad original anterior y la nueva cantidad (puede ser un
    // numero positivo o negativo)
    Integer quantityBalance = newRequired - oldRequired;

    deliveryLine.setRequiredQuantity(newRequired);
    deliveryLine.setLimitDate(deliveryLineUpdateRequest.getLimitDate());
    deliveryLine.setUserUpdater(user);

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryOrder_id)
        .orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "El pedido de entrega no existe"));

    // DEBE BUSCAR LA RELACION PRODUCTOS - ORDENES DE ENTREGA PARA ACTUALIZAR LA
    // SUMATORIA DE LA CANTIDAD REQUERIDA
    Product_DeliveryOrder product_DeliveryOrder = deliveryLine.getProduct_DeliveryOrder();
    if (product_DeliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Long product_DeliveryOrder_id = product_DeliveryOrder.getId();

    if (product_DeliveryOrder_id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = product_DeliveryOrder.getProduct();

    if (product == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Llamar al método auxiliar para actualizar la linea de entrega
    updateLineStatus(deliveryLine);
    deliveryLineRepository.save(deliveryLine);

    // Recalcular la suma de las cantidades requeridas de las lineas de entrega

    // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
    product_DeliveryOrder.setRequiredQuantityTotal(
        deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder_id, product.getId()));

    product_DeliveryOrderRepository.save(product_DeliveryOrder);

    // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
    deliveryOrder.setPriorityDate(getClosestLimitDate(deliveryOrder_id));
    deliveryOrderRepository.save(deliveryOrder);

    // RECALCULAR LA SUMATORIA DE CANTIDADES POR REGION
    recalculateProductDeliveryOrderRegions(deliveryLine.getDeliveryOrder().getId());

    Movement movement = new Movement();
    movement.setQuantity(quantityBalance);
    // Si el balance es un número positivo se considera un ALTER, si es negativo se
    // considera un CHANGE
    movement.setMovementType(quantityBalance > 0 ? MovementType.ALTER : MovementType.CHANGE);
    movement.setComment(deliveryLineUpdateRequest.getComment());
    movement.setProduct(product);
    movement.setUser(user);
    movement.setStockLotReceiver(null);
    movement.setStockLotEmitter(null);
    movement.setDeliveryLine(deliveryLine);

    movementRepository.save(movement);
  }

  private void updateLineStatus(DeliveryLine line) {

    int required = line.getRequiredQuantity();
    int delivered = line.getDeliveredQuantity();

    if (required > delivered) {
      line.setPendingQuantity(required - delivered);
      line.setLineStatus(LineStatus.PENDING);
      return;
    }

    if (required == delivered) {
      line.setPendingQuantity(0);
      line.setLineStatus(LineStatus.READY);
      return;
    }

    // required < delivered
    line.setPendingQuantity(required - delivered);
    line.setLineStatus(LineStatus.EXCEEDED);
  }

  // Método para eliminar una linea de entrega (solamente si no hay cantidad
  // entregada o si nunca hubo una relacion con StockLot_DeliveryLine)
  @Override
  public void cancelDeliveryLineById(Long id, Long id_user_authenticated) {
    if (id == null || id_user_authenticated == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user_authenticated)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La linea de entrega no existe"));

    if (deliveryLine == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Long deliveryOrderId = deliveryLine.getDeliveryOrder().getId();

    if (deliveryOrderId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryOrderId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Long id_product_deliveryOrder = deliveryLine.getProduct_DeliveryOrder().getId();

    if (id_product_deliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product_DeliveryOrder product_DeliveryOrder = product_DeliveryOrderRepository.findById(
        id_product_deliveryOrder).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion producto-orden de entrega no existe"));

    if (product_DeliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    } else {
      // TODO: CORREGIR ESTA PARTE, DEBE ELIMINAR LA LINEA DE ENTREGA
      // TODO: DEBE CREAR UN NUEVO LOTE DE STOCK CON LA SUMATORIA DE LAS CANTIDADES
      // ENTREGADAS DE LAS LINEAS DE ENTREGA QUE ESTAN EN MODO READY, PENDING Y
      // DELIVERED
      // SI HAY CANTIDAD ENTREGADA, ENTONCES YA NO SE PODRA ELIMINAR ESTE CAMPO
      if (deliveryLine.getDeliveredQuantity() > 0) {
        throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
            "No se puede cancelar esta línea porque ya hay una cantidad a entregar");
      }



      if (deliveryLine.getLineStatus() == LineStatus.CANCELED) {
        throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La linea de entrega ya ha sido cancelada");
      }

      deliveryLine.setLineStatus(LineStatus.CANCELED);
      deliveryLine.setUserUpdater(user);
      deliveryLineRepository.save(deliveryLine);

      // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
      product_DeliveryOrder.setRequiredQuantityTotal(
          deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder.getId(), product_DeliveryOrder.getProduct().getId()));

      product_DeliveryOrder.setStatus(false);
      product_DeliveryOrderRepository.save(product_DeliveryOrder);

      // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
      deliveryOrder.setPriorityDate(getClosestLimitDate(deliveryOrder.getId()));

      // Operacion para verificar si todas las lineas de entrega de una orden de
      // entrega han sido entregadas, es decir si todas tiene el estado READY
      if (deliveryLineRepository.allLinesAreReady(deliveryOrderId)) {
        deliveryOrder.setOrderStatus(OrderStatus.READY);
      } else {
        deliveryOrder.setOrderStatus(OrderStatus.PENDING);
      }

      deliveryOrderRepository.save(deliveryOrder);

      // RECALCULAR LA SUMATORIA DE CANTIDADES POR REGION
      recalculateProductDeliveryOrderRegions(deliveryLine.getDeliveryOrder().getId());

      Movement movement = new Movement();
      movement.setQuantity(deliveryLine.getDeliveredQuantity());
      movement.setMovementType(MovementType.CANCELED);
      movement.setComment("Cancelacion de linea de entrega: " + deliveryLine.getId());
      movement.setProduct(deliveryLine.getProduct());
      movement.setUser(user);
      movement.setStockLotReceiver(null);
      movement.setStockLotEmitter(null);
      movement.setDeliveryLine(deliveryLine);

      movementRepository.save(movement);

    }
  }

  @Override
  public void sendDeliveryLineById(Long id, Long id_user_authenticated) {

    if (id == null || id_user_authenticated == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }
    User user = userRepository.findById(
        id_user_authenticated)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryLine == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Solamente podra declarar entregada si la linea de entrega se encuentra lista
    if (deliveryLine.getLineStatus() != LineStatus.READY) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La linea de entrega no puede ser entregada");
    }

    deliveryLine.setLineStatus(LineStatus.DELIVERED);
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    Movement movement = new Movement();
    movement.setQuantity(deliveryLine.getDeliveredQuantity());
    movement.setMovementType(MovementType.DELIVERED);
    movement.setComment("Entrega de linea de entrega: " + deliveryLine.getId());
    movement.setProduct(deliveryLine.getProduct());
    movement.setUser(user);
    movement.setStockLotReceiver(null);
    movement.setStockLotEmitter(null);
    movement.setDeliveryLine(deliveryLine);

    movementRepository.save(movement);

  }

  // Tomar la fecha mas cercana que no haya sido entregada
  private LocalDateTime getClosestLimitDate(Long idDeliveryOrder) {
    // 1° encontrar todas las lineas de entrega correspondientes a la orden de
    // entrega
    // 2° tomar las fechas limites de cada linea de entrega cuyo estado sea
    // INPROGRESS
    // 3° devolver la fecha más cercana que no haya sido entregada

    return deliveryLineRepository
        .findClosestLimitDate(idDeliveryOrder)
        .orElse(null); // o lanza excepción
  }

  // TODO: PROBAR ESTE MÉTODO EN POSTMAN PARA REPORTAR UNA PARTE DE UNA LINEA DE ENTREGA COMO PERDIDA

  // ESTO ES UN MOVIMIENTO DE CANCELACIÓN
  @Override
  public void lostDeliveryLineById(Long id, DeliveryLineAlterRequest deliveryLineAlterRequest, Long id_user) {
    if (id == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));
    if (deliveryLine == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }
    Long deliveryOrderId = deliveryLine.getDeliveryOrder().getId();

    if (deliveryOrderId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }
    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryOrderId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }
    // No importa el estado de la linea de entrega

    // TODO: IMPLEMENTAR UNA LOGICA PARA REALIZAR ALGO CON LA CANTIDAD REQUERIDA

    // Si una linea de entrega fue reportada como perdida

    Integer lostQuantity = deliveryLineAlterRequest.getQuantity();

    // TODO: VERIFICAR LAS CANTIDADES ALTERADAS
    // 1° debe descontar la cantidad entregada de la cantidad requerida
    Integer deliveredQuantity = deliveryLine.getDeliveredQuantity();

    if (deliveredQuantity - lostQuantity < 0) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
          "La cantidad perdida es mayor a la cantidad entregada");
    }

    deliveryLine.setDeliveredQuantity(deliveredQuantity - lostQuantity);
    deliveryLine.setPendingQuantity(deliveryLine.getPendingQuantity() + lostQuantity);

    // Actualizar el estado de la linea de entrega
    if (deliveryLine.getPendingQuantity() == 0) {
      deliveryLine.setLineStatus(LineStatus.READY);
    } else {
      deliveryLine.setLineStatus(LineStatus.PENDING);
    }

    deliveryLine.setUserUpdater(user);

    // TODO: Probar esta logica para ver si se actualiza el total de la cantidad
    // requerida
    Product_DeliveryOrder product_DeliveryOrder = product_DeliveryOrderRepository.findById(
        deliveryOrderId).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion producto-orden de entrega no existe"));

    // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
    product_DeliveryOrder.setRequiredQuantityTotal(
        deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder.getId(), product_DeliveryOrder.getProduct().getId()));

    product_DeliveryOrderRepository.save(product_DeliveryOrder);

    // 3° REGISTRARLO COMO MOVIMIENTO
    Movement movement = new Movement();
    movement.setQuantity(lostQuantity);
    movement.setMovementType(MovementType.MISSING);
    movement.setComment(deliveryLineAlterRequest.getComment());
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(null);
    movement.setUser(user);
    movement.setProduct(deliveryLine.getProduct());
    movement.setDeliveryLine(deliveryLine);
    movementRepository.save(movement);

  }


  // ESTRATEGIA DE ACTUALIZACIÓN
  // MÉTODO AUXILIAR DE REPARACIÓN
  private void recalculateProductDeliveryOrderRegions(Long productDeliveryOrderId) {
    List<Product_DeliveryOrder_Region> regions = product_DeliveryOrder_RegionRepository
        .findAllByProduct_DeliveryOrderId(productDeliveryOrderId);

    for (Product_DeliveryOrder_Region entity : regions) {

      // Solamente hay un campo para la cantidad total requerida
      Integer requiredTotal = deliveryLineRepository.sumRequiredByProductDeliveryOrderAndRegion(
          productDeliveryOrderId,
          entity.getRegion().getId());

      entity.setRequiredTotalQuantity(requiredTotal);
    }

    // TODO: ¿FALTA ACTUALIZAR LA CANTIDAD ENTREGADA?
    if (regions == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR,
          "No se encontraron regiones para el product_delivery_order");
    }

    product_DeliveryOrder_RegionRepository.saveAll(regions);

  }

  // Servicio para retornar cantidad entregada de una linea de entrega
  @Override
  public void returnDeliveryLineById(Long id, DeliveryLineAlterRequest deliveryLineAlterRequest,
      Long id_user_authenticated) {

    if (id == null || id_user_authenticated == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user_authenticated)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La linea de entrega no existe"));

    if (deliveryLine == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Long deliveryOrderId = deliveryLine.getDeliveryOrder().getId();

    if (deliveryOrderId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Integer returnedQuantity = deliveryLineAlterRequest.getQuantity();

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryOrderId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product_DeliveryOrder product_DeliveryOrder = product_DeliveryOrderRepository.findById(
        deliveryOrderId).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion producto-orden de entrega no existe"));
    if (product_DeliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // TODO: NECESITO LA SUMATORIA DE LOS LOTES DE STOCK ENTREGADOS, ASOCIADOS A
    // ESTA LINEA DE ENTREGA, PORQUE AL RETORNAR UNA CANTIDAD, SE DEBE ALMANCENAR EN
    // UN NUEVO STOCK LOT

    // AQUI NO DEBE CAMBIAR EL ESTADO A CANCELED, SINO QUE DEBE COMPARAR SI
    // ESTA LINEA DE ENTREGA TIENE CANTIDAD ENTREGADA
    if (deliveryLine.getDeliveredQuantity() - returnedQuantity == deliveryLine.getRequiredQuantity()) {
      deliveryLine.setLineStatus(LineStatus.READY);
    }

    if (deliveryLine.getDeliveredQuantity() - returnedQuantity > deliveryLine.getRequiredQuantity()) {
      deliveryLine.setLineStatus(LineStatus.EXCEEDED);
    }

    if (deliveryLine.getDeliveredQuantity() - returnedQuantity < deliveryLine.getRequiredQuantity()) {
      deliveryLine.setLineStatus(LineStatus.PENDING);
    }

    deliveryLine.setDeliveredQuantity(deliveryLine.getDeliveredQuantity() - returnedQuantity);
    deliveryLine.setPendingQuantity(deliveryLine.getPendingQuantity() + returnedQuantity);
    


    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
    product_DeliveryOrder.setRequiredQuantityTotal(
        deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder.getId(), product_DeliveryOrder.getProduct().getId()));

    product_DeliveryOrderRepository.save(product_DeliveryOrder);

    // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
    deliveryOrder.setPriorityDate(getClosestLimitDate(deliveryOrder.getId()));

    // Operacion para verificar si todas las lineas de entrega de una orden de
    // entrega han sido entregadas, es decir si todas tiene el estado READY
    if (deliveryLineRepository.allLinesAreReady(deliveryOrderId)) {
      deliveryOrder.setOrderStatus(OrderStatus.READY);
    } else {
      deliveryOrder.setOrderStatus(OrderStatus.PENDING);
    }

    deliveryOrderRepository.save(deliveryOrder);

    // RECALCULAR LA SUMATORIA DE CANTIDADES POR REGION
    recalculateProductDeliveryOrderRegions(deliveryLine.getDeliveryOrder().getId());

    Movement movement = new Movement();
    movement.setQuantity(deliveryLine.getDeliveredQuantity());
    movement.setMovementType(MovementType.CANCELED);
    movement.setComment("Cancelacion de linea de entrega: " + deliveryLine.getId());
    movement.setProduct(deliveryLine.getProduct());
    movement.setUser(user);
    movement.setStockLotReceiver(null);
    movement.setStockLotEmitter(null);
    movement.setDeliveryLine(deliveryLine);

    movementRepository.save(movement);
  }

  // Servicio para entregar una cantidad a una linea de entrega
  @Transactional
  @Override
  public void allocateDeliveryLineById(Long id, DeliveryLineAllocateRequest deliveryLineAllocateRequest,
      Long id_user_authenticated) {
        
    if (id == null || id_user_authenticated == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // ===== Usuario =====
    User user = userRepository.findById(id_user_authenticated)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    // ===== Línea de entrega =====
    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La linea de entrega no existe"));
    
    // ===== Orden de entrega =====
    Long deliveryOrderId = deliveryLine.getDeliveryOrder().getId();

    if (deliveryOrderId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryOrderId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    // ===== Relación producto-orden =====
    Long id_product_deliveryOrder = deliveryLine.getProduct_DeliveryOrder().getId();

    if (id_product_deliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product_DeliveryOrder product_DeliveryOrder = product_DeliveryOrderRepository.findById(
        id_product_deliveryOrder).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion producto-orden de entrega no existe"));

    // ===== Cantidad =====
    Integer quantity = deliveryLineAllocateRequest.getQuantity();

    if (quantity == null || quantity <= 0) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST, "Cantidad inválida");
    }

    if (quantity > deliveryLine.getPendingQuantity()) {
      throw new BusinessException(
          ResponseStatus.BAD_REQUEST,
          "La cantidad supera lo pendiente de la línea");
    }

    // ===== Lotes =====
    List<Long> stockLotIds = deliveryLineAllocateRequest.getIdStockLots();

    if (stockLotIds == null || stockLotIds.isEmpty()) {
      throw new BusinessException(
          ResponseStatus.BAD_REQUEST,
          "Debe seleccionar al menos un lote");
    }

    List<StockLot> stockLots = stockLotRepository.findAllById(stockLotIds);

    if (stockLots.size() != stockLotIds.size()) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          "Uno o más lotes no existen");
    }

    // ===== Validar producto =====
    Long productId = deliveryLine.getProduct().getId();

    for (StockLot stockLot : stockLots) {
      if (!stockLot.getProduct().getId().equals(productId)) {
        throw new BusinessException(
            ResponseStatus.BAD_REQUEST,
            "El lote no pertenece al producto de la línea");
      }
    }

    // ===== Validar stock total =====
    int totalAvailable = stockLots.stream()
            .mapToInt(StockLot::getQuantityAvailable)
            .sum();

    if (totalAvailable < quantity) {
        throw new BusinessException(
                ResponseStatus.BAD_REQUEST,
                "Stock insuficiente para la cantidad solicitada");
    }
    // ==== Movimiento =====
    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment("Entrega de stock");
    movement.setDeliveryLine(deliveryLine);
    movement.setMovementType(MovementType.ALLOCATE);
    // movement.setStockLots(stockLots);
    movement.setProduct(deliveryLine.getProduct());
    movement.setUser(user);

    movementRepository.save(movement);

    // ===== Asignar stock por lotes =====
    int remaining = quantity;

    for (StockLot stockLot : stockLots) {

      if (remaining == 0)
        break;

      int available = stockLot.getQuantityAvailable();

      if (available <= 0)
        continue;

      int used = Math.min(available, remaining);

      // 1. Actualizar stock
      stockLot.setQuantityAvailable(available - used);
      stockLotRepository.save(stockLot);

      // 2. Relación DeliveryLine ↔ StockLot
      StockLot_DeliveryLine dlRelation = new StockLot_DeliveryLine();
      dlRelation.setStockLot(stockLot);
      dlRelation.setDeliveryLine(deliveryLine);
      dlRelation.setQuantityUsed(used);
      stockLot_DeliveryLineRepository.save(dlRelation);

      // 3. Relación Movement ↔ StockLot
      Movement_StockLot mRelation = new Movement_StockLot();
      mRelation.setMovement(movement);
      mRelation.setStockLot(stockLot);
      mRelation.setQuantityTaken(used);
      movement_StockLotRepository.save(mRelation);

      remaining -= used;
    }    


    // ===== Actualizar línea =====
    deliveryLine.setDeliveredQuantity(
            deliveryLine.getDeliveredQuantity() + quantity);
    deliveryLine.setPendingQuantity(
            deliveryLine.getPendingQuantity() - quantity);

    if (deliveryLine.getPendingQuantity() == 0) {
        deliveryLine.setLineStatus(LineStatus.READY);
    }

    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    // ===== Recalcular producto-orden =====
    product_DeliveryOrder.setRequiredQuantityTotal(
        deliveryLineRepository
            .sumRequiredQuantityByProduct_DeliveryOrder(
                product_DeliveryOrder.getId(), productId));

    product_DeliveryOrderRepository.save(product_DeliveryOrder);    

    // ===== Recalcular orden =====
    deliveryOrder.setPriorityDate(
        getClosestLimitDate(deliveryOrder.getId()));

    // TODO: Verificar si todas las lineas de entrega de una orden de
    // entrega han sido entregadas, es decir si todas tiene el estado READY
    if (deliveryLineRepository.allLinesAreReady(deliveryOrder.getId())) {
      deliveryOrder.setOrderStatus(OrderStatus.READY);
    } else {
      deliveryOrder.setOrderStatus(OrderStatus.PENDING);
    }

    deliveryOrderRepository.save(deliveryOrder);

    // ===== Recalcular regiones =====
    recalculateProductDeliveryOrderRegions(deliveryOrder.getId());

  }
}