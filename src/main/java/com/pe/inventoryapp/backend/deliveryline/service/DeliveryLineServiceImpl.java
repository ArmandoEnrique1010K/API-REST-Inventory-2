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
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
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

  // ORDEN CORRECTO (regla de oro)
  // 1. Validaciones básicas
  // 2. Carga de entidades
  // 3. Validaciones de estado
  // 4. Lógica principal
  // 5. Persistencia principal
  // 6. Recalculos derivados
  // 7. Auditoría / movimientos
  @Override
  public void saveDeliveryLine(DeliveryLineRequest deliveryLineRequest, Long id_product_deliveryOrder, Long id_user) {
    validateSaveRequest(deliveryLineRequest, id_product_deliveryOrder, id_user);

    User user = getUser(id_user);
    Location location = getLocation(deliveryLineRequest.getIdLocation());
    Product_DeliveryOrder pdo = getProduct_DeliveryOrder(id_product_deliveryOrder);

    validateNoDuplicateLine(pdo, location);

    DeliveryLine deliveryLine = buildDeliveryLine(deliveryLineRequest, user, location, pdo);

    if (deliveryLine == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    deliveryLineRepository.save(deliveryLine);
    recalculateAfterDeliveryLineChange(pdo, location);
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

    DeliveryLine deliveryLine = getDeliveryLine(id);

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

    User user = getUser(id_user);

    DeliveryLine deliveryLine = getDeliveryLine(id);

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
        deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder_id));

    product_DeliveryOrderRepository.save(product_DeliveryOrder);

    // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
    deliveryOrder.setLimitDate(getClosestLimitDate(deliveryOrder_id));
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

  // Método para eliminar una linea de entrega (solamente si no hay cantidad
  // entregada o si nunca hubo una relacion con StockLot_DeliveryLine)
  @Override
  public void cancelDeliveryLineById(Long id, Long id_user_authenticated) {
    if (id == null || id_user_authenticated == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = getUser(id_user_authenticated);

    DeliveryLine deliveryLine = getDeliveryLine(id);

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

    Product_DeliveryOrder product_DeliveryOrder = getProduct_DeliveryOrder(id_product_deliveryOrder);

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

      // Recordar que si hay una relación con StockLot_DeliveryLine no se puede
      // eliminar
      if (deliveryLine.getStockLotDeliveryLines().size() > 0) {
        throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
            "No se puede eliminar porque ya hay una relacion con StockLot_DeliveryLine");
      }

      if (deliveryLine.getLineStatus() == LineStatus.CANCELED || deliveryLine.getLineStatus() == LineStatus.DELIVERED) {
        throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La linea de entrega ya ha sido cancelada");
      }

      deliveryLine.setLineStatus(LineStatus.CANCELED);
      deliveryLine.setUserUpdater(user);
      deliveryLineRepository.save(deliveryLine);

      // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
      product_DeliveryOrder.setRequiredQuantityTotal(
          deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder.getId()));

      product_DeliveryOrderRepository.save(product_DeliveryOrder);

      // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
      deliveryOrder.setLimitDate(getClosestLimitDate(deliveryOrder.getId()));

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
    User user = getUser(id_user_authenticated);

    DeliveryLine deliveryLine = getDeliveryLine(id);

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

  // ESTO ES UN MOVIMIENTO DE CANCELACIÓN
  @Override
  public void lostDeliveryLineById(Long id, DeliveryLineAlterRequest deliveryLineAlterRequest, Long id_user) {
    if (id == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = getUser(id_user);
    DeliveryLine deliveryLine = getDeliveryLine(id);

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
    Product_DeliveryOrder product_DeliveryOrder = getProduct_DeliveryOrder(
        deliveryLine.getProduct_DeliveryOrder().getId());

    // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
    product_DeliveryOrder.setRequiredQuantityTotal(
        deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder.getId()));

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

  // Servicio para retornar cantidad entregada de una linea de entrega
  @Override
  public void returnDeliveryLineById(Long id, DeliveryLineAlterRequest deliveryLineAlterRequest,
      Long id_user_authenticated) {

    if (id == null || id_user_authenticated == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = getUser(id_user_authenticated);

    DeliveryLine deliveryLine = getDeliveryLine(id);

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

    Product_DeliveryOrder product_DeliveryOrder = getProduct_DeliveryOrder(
        deliveryLine.getProduct_DeliveryOrder().getId());
    if (product_DeliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // TODO: NECESITO LA SUMATORIA DE LOS LOTES DE STOCK ENTREGADOS, ASOCIADOS A
    // ESTA LINEA DE ENTREGA, PORQUE AL RETORNAR UNA CANTIDAD, SE DEBE ALMANCENAR EN
    // UN NUEVO STOCK LOT

    deliveryLine.setLineStatus(LineStatus.CANCELED);
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
    product_DeliveryOrder.setRequiredQuantityTotal(
        deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder.getId()));

    product_DeliveryOrderRepository.save(product_DeliveryOrder);

    // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
    deliveryOrder.setLimitDate(getClosestLimitDate(deliveryOrder.getId()));

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
    User user = getUser(id_user_authenticated);

    // ===== Línea de entrega =====
    DeliveryLine deliveryLine = getDeliveryLine(id);

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

    Product_DeliveryOrder product_DeliveryOrder = getProduct_DeliveryOrder(id_product_deliveryOrder);

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

    // ===== Asignar stock por lotes =====
    int remaining = quantity;

    for (StockLot stockLot : stockLots) {

      if (remaining == 0)
        break;

      int available = stockLot.getQuantityAvailable();

      if (available <= 0)
        continue;

      int used = Math.min(available, remaining);

      stockLot.setQuantityAvailable(available - used);
      stockLotRepository.save(stockLot);

      StockLot_DeliveryLine relation = new StockLot_DeliveryLine();
      relation.setStockLot(stockLot);
      relation.setDeliveryLine(deliveryLine);
      relation.setQuantityUsed(used);

      stockLot_DeliveryLineRepository.save(relation);

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
                product_DeliveryOrder.getId()));

    product_DeliveryOrderRepository.save(product_DeliveryOrder);

    // ===== Recalcular orden =====
    deliveryOrder.setLimitDate(
        getClosestLimitDate(deliveryOrder.getId()));

    if (deliveryLineRepository.allLinesAreReady(deliveryOrder.getId())) {
      deliveryOrder.setOrderStatus(OrderStatus.READY);
    } else {
      deliveryOrder.setOrderStatus(OrderStatus.PENDING);
    }

    deliveryOrderRepository.save(deliveryOrder);

    // ===== Recalcular regiones =====
    recalculateProductDeliveryOrderRegions(deliveryOrder.getId());

    // ==== Movimiento =====
    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment("Entrega...");
    movement.setDeliveryLine(deliveryLine);
    movement.setMovementType(MovementType.ALLOCATE);
    movement.setStockLots(stockLots);
    movement.setUser(user);

    movementRepository.save(movement);
  }

  // DEFINICIÓN DE MÉTODOS PRIVADOS

  // Encontrar al usuario por id
  private User getUser(Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    return userRepository.findById(
        id_user)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));
  }

  private Product_DeliveryOrder getProduct_DeliveryOrder(Long id_product_delivery_order) {

    if (id_product_delivery_order == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    return product_DeliveryOrderRepository.findById(
        id_product_delivery_order)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND,
            "La relación de producto y orden de entrega no existe"));
  }

  private DeliveryLine getDeliveryLine(Long id_delivery_line) {

    if (id_delivery_line == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    return deliveryLineRepository.findById(
        id_delivery_line)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La linea de entrega no existe"));

  }

  private DeliveryOrder getDeliveryOrder(Long id_delivery_order) {

    if (id_delivery_order == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    return deliveryOrderRepository.findById(
        id_delivery_order)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "La orden de entrega no existe"));
  }

  // Actualizar el estado de la linea de entrega
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

  private void validateSaveRequest(
      DeliveryLineRequest request,
      Long productDeliveryOrderId,
      Long userId) {

    if (request == null ||
        request.getIdLocation() == null ||
        productDeliveryOrderId == null ||
        userId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private void validateNoDuplicateLine(
      Product_DeliveryOrder pdo,
      Location location) {

    // Regla: no permitir duplicados por ubicación, excepto de las lineas de entrega
    // con estado CANCELED
    boolean exists = deliveryLineRepository.existsDuplicate(
        pdo.getDeliveryOrder().getId(),
        pdo.getProduct().getId(),
        location.getId());

    if (exists) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "Ya existe una línea de entrega para este producto en esta ubicación");
    }
  }

  private Location getLocation(Long id_location) {
    if (id_location == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    return locationRepository.findById(
        id_location)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));
  }

  private  DeliveryLine buildDeliveryLine(
      DeliveryLineRequest request,
      User user,
      Location location,
      Product_DeliveryOrder pdo) {
    DeliveryLine dl = new DeliveryLine();

    Integer qty = request.getRequiredQuantity();

    dl.setOriginalQuantity(qty);
    dl.setRequiredQuantity(qty);
    dl.setDeliveredQuantity(0);
    dl.setPendingQuantity(qty);
    dl.setLimitDate(request.getLimitDate());
    dl.setLineStatus(LineStatus.PENDING);

    dl.setUserCreator(user);
    dl.setUserUpdater(user);

    dl.setLocation(location);
    dl.setProduct_DeliveryOrder(pdo);
    dl.setProduct(pdo.getProduct());
    dl.setDeliveryOrder(pdo.getDeliveryOrder());

    return dl;
  }

  private void updateDeliveryOrder(DeliveryOrder order) {
    order.setPriorityDate(getClosestLimitDate(order.getId()));
    order.setOrderStatus(OrderStatus.PENDING);
    deliveryOrderRepository.save(order);
  }

  private void recalculateAfterDeliveryLineChange(
      Product_DeliveryOrder pdo,
      Location location) {

    updateDeliveryOrder(pdo.getDeliveryOrder());
    updateProductDeliveryOrder(pdo);
    updateProductDeliveryOrderRegion(pdo, location);
  }

  private void updateProductDeliveryOrder(Product_DeliveryOrder pdo) {
    Integer totalRequired = deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(pdo.getId());

    pdo.setRequiredQuantityTotal(totalRequired);
    product_DeliveryOrderRepository.save(pdo);
  }

  private void updateProductDeliveryOrderRegion(
      Product_DeliveryOrder pdo,
      Location location) {

    Integer regionTotal = deliveryLineRepository.sumRequiredByProductDeliveryOrderAndRegion(
        pdo.getId(),
        location.getRegion().getId());

    Product_DeliveryOrder_Region entity = product_DeliveryOrder_RegionRepository
        .findByProduct_DeliveryOrderIdAndRegionId(
            pdo.getId(),
            location.getRegion().getId())
        .orElseGet(() -> {
          Product_DeliveryOrder_Region e = new Product_DeliveryOrder_Region();
          e.setProduct_DeliveryOrder(pdo);
          e.setRegion(location.getRegion());
          return e;
        });

    entity.setRequiredTotalQuantity(regionTotal);
    product_DeliveryOrder_RegionRepository.save(entity);
  }

}
