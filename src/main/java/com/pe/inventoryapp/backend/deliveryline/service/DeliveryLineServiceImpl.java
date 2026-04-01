package com.pe.inventoryapp.backend.deliveryline.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Model_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.service.DeliveryOrderDomainService;
import com.pe.inventoryapp.backend.deliveryorder.service.Model_DeliveryOrderDomainService;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.entity.Movement_StockLot;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.movement.repository.Movement_StockLotRepository;
import com.pe.inventoryapp.backend.movement.service.MovementDomainService;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;
import com.pe.inventoryapp.backend.stocklot.repository.StockLotRepository;
import com.pe.inventoryapp.backend.stocklot.service.StockLotDomainService;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class DeliveryLineServiceImpl implements DeliveryLineService {

  private final DeliveryLineRepository deliveryLineRepository;
  private final ModelRepository modelRepository;
  private final LocationRepository locationRepository;
  private final DeliveryOrderRepository deliveryOrderRepository;
  private final MovementRepository movementRepository;
  private final Model_DeliveryOrderRepository model_DeliveryOrderRepository;
  private final UserRepository userRepository;
  private final StockLotRepository stockLotRepository;
  private final StockLot_DeliveryLineRepository stockLot_DeliveryLineRepository;
  private final Movement_StockLotRepository movement_StockLotRepository;
  private final CompanyRepository companyRepository;
  private final DeliveryOrderDomainService deliveryOrderDomainService;
  private final StockLotDomainService stockLotDomainService;
  private final MovementDomainService movementDomainService;
  private final Model_DeliveryOrderDomainService model_DeliveryOrderDomainService;

  public DeliveryLineServiceImpl(
      DeliveryLineRepository deliveryLineRepository,
      ModelRepository modelRepository,
      LocationRepository locationRepository,
      DeliveryOrderRepository deliveryOrderRepository,
      MovementRepository movementRepository,
      Model_DeliveryOrderRepository model_DeliveryOrderRepository,
      UserRepository userRepository,
      StockLotRepository stockLotRepository,
      StockLot_DeliveryLineRepository stockLot_DeliveryLineRepository,
      Movement_StockLotRepository movement_StockLotRepository,
      CompanyRepository companyRepository,
      DeliveryOrderDomainService deliveryOrderDomainService,
      StockLotDomainService stockLotDomainService,
      MovementDomainService movementDomainService,
    Model_DeliveryOrderDomainService model_DeliveryOrderDomainService) {
    this.deliveryLineRepository = deliveryLineRepository;
    this.modelRepository = modelRepository;
    this.locationRepository = locationRepository;
    this.deliveryOrderRepository = deliveryOrderRepository;
    this.movementRepository = movementRepository;
    this.model_DeliveryOrderRepository = model_DeliveryOrderRepository;
    this.userRepository = userRepository;
    this.stockLotRepository = stockLotRepository;
    this.stockLot_DeliveryLineRepository = stockLot_DeliveryLineRepository;
    this.movement_StockLotRepository = movement_StockLotRepository;
    this.companyRepository = companyRepository;
    this.deliveryOrderDomainService = deliveryOrderDomainService;
    this.stockLotDomainService = stockLotDomainService;
    this.movementDomainService = movementDomainService;
    this.model_DeliveryOrderDomainService = model_DeliveryOrderDomainService;
  }

  @Override
  @Transactional
  public void saveDeliveryLine(DeliveryLineRequest deliveryLineRequest, Long deliveryOrderId, Long id_user) {

    Long id_location = deliveryLineRequest.getLocationId();

    Long id_model = deliveryLineRequest.getModelId();

    if (id_location == null || deliveryOrderId == null || id_user == null || id_model == null) {
      // TODO: APLICAR EN TODOS LOS SERVICIOS QUE EN LUGAR DE RETORNAR
      // INTERNAL_SERVER_ERROR, UN BAD_REQUEST
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Model model = modelRepository.findById(id_model).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo no existe"));

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Location location = locationRepository.findById(
        id_location).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(deliveryOrderId).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));


    // NO PUEDES AGREGAR NUEVAS LINEAS DE ENTREGA A UNA ORDEN DE ENTREGA QUE FUE ENTREGADA O CANCELADA
    if (deliveryOrder.getOrderStatus().equals(OrderStatus.ORDER_DELIVERED) || 
    deliveryOrder.getOrderStatus().equals(OrderStatus.ORDER_CANCELED) || deliveryOrder.getOrderStatus().equals(OrderStatus.ORDER_PARTIALLY_DELIVERED)) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "No puedes agregar líneas a una orden que ya fue procesada o cerrada");
    }

    // Verificar si existe la relación, de lo contrario, lanzar una excepción
    Model_DeliveryOrder modelDeliveryOrder = model_DeliveryOrderRepository
        .findByModelIdAndDeliveryOrderId(id_model, deliveryOrderId)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.CONFLICT,
            "La relación entre orden de entrega y modelo no existe"));

    // Obtener el producto y orden de entrega desde Product_DeliveryOrder
    // Model_DeliveryOrder model_DeliveryOrder =
    // model_DeliveryOrderRepository.findById(id_model_deliveryOrder)
    // .orElseThrow(
    // () -> new BusinessException(ResponseStatus.NOT_FOUND,
    // "La relación de producto y orden de entrega no existe"));

    // Long id_deliveryOrder = model_DeliveryOrder.getDeliveryOrder().getId();
    // Long id_model = model_DeliveryOrder.getModel().getId();

    // if (id_deliveryOrder == null || id_model == null) {
    // throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    // }

    // Regla: no permitir duplicados por ubicación, excepto de las lineas de entrega
    // con estado MOVEMENT_LINE_CANCELED
    boolean exists = deliveryLineRepository
        .existsDuplicate(deliveryOrderId, id_model, id_location);

    if (exists) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "Ya existe una línea de entrega para este modelo del producto en esta ubicación");
    }

    // Crear la linea de entrega
    DeliveryLine deliveryLine = new DeliveryLine();

    deliveryLine.setOriginalQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setRequiredQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setDeliveredQuantity(0);
    deliveryLine.setPendingQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setLimitDate(deliveryLineRequest.getLimitDate());
    // La fecha de actualización se genera automaticamente
    deliveryLine.setLineStatus(LineStatus.LINE_PENDING);

    deliveryLine.setLocation(location);

    // Actualizar los usuarios creador y actualizador
    deliveryLine.setUserCreator(user);
    deliveryLine.setUserUpdater(user);
    deliveryLine.setModel(model);
    deliveryLine.setModel_DeliveryOrder(modelDeliveryOrder);

    deliveryLine.setDeliveryOrder(deliveryOrder);

    deliveryLineRepository.save(deliveryLine);

    // 1° actualizar la fecha limite de deliveryOrder comparando todas las lineas de
    // entrega y tomar el valor con la fecha más cercana que no haya sido entregada
    deliveryOrder.setPriorityDate(deliveryOrderDomainService.getClosestLimitDate(deliveryOrder.getId()));

    // 2° actualizar el estado a PENDING cada vez que se guarde una nueva linea de
    // entrega
    deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
    deliveryOrderRepository.save(deliveryOrder);

    // 3° CALCULAR LA SUMATORIA DE LAS CANTIDADES REQUERIDAS DE TODAS LAS LINEAS DE
    // ENTREGA POR ORDEN DE ENTREGA Y PRODUCTO
    model_DeliveryOrderDomainService.recalculateSummaries(deliveryOrder.getId(), model.getId());

    // Integer totalRequired =
    // deliveryLineRepository.sumRequiredQuantityByDeliveryOrder_Model(id_deliveryOrder,
    // id_model);

    // model_DeliveryOrder.setRequiredQuantityTotal(totalRequired);

    // model_DeliveryOrderRepository.save(model_DeliveryOrder);

    // // Agregar un registro en la entidad Product_DeliveryOrder_Region
    // Integer regionTotal =
    // deliveryLineRepository.sumRequiredByProductDeliveryOrderAndRegion(
    // model_DeliveryOrder.getId(),
    // location.getRegion().getId());

    // Model_DeliveryOrder_Region entity = product_DeliveryOrder_RegionRepository
    // .findByProduct_DeliveryOrderIdAndRegionId(
    // model_DeliveryOrder.getId(),
    // location.getRegion().getId())
    // .orElseGet(() -> {
    // Model_DeliveryOrder_Region e = new Model_DeliveryOrder_Region();
    // e.setProduct_DeliveryOrder(model_DeliveryOrder);
    // e.setRegion(location.getRegion());
    // return e;
    // });

    // entity.setRequiredTotalQuantity(regionTotal);
    // product_DeliveryOrder_RegionRepository.save(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<DeliveryLineListResponse> findAllDeliveryLinesByDeliveryOrderIdPageable(
      Long deliveryOrderId,
      Integer minRequiredQuantity,
      Integer maxRequiredQuantity,
      LocalDateTime minLimitDate,
      LocalDateTime maxLimitDate,
      LineStatus lineStatus,
      String location,
      Long subregionId,
      Long regionId,
      Long modelId,
      Pageable pageable) {
    if (deliveryOrderId != null && !deliveryOrderRepository.existsById(deliveryOrderId)) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          "La orden de entrega no existe");
    }

    Page<DeliveryLine> deliveryLines = deliveryLineRepository.searchAllByDeliveryOrderIdAndParams(
        deliveryOrderId, minRequiredQuantity, maxRequiredQuantity, minLimitDate,
        maxLimitDate, lineStatus, location, subregionId, regionId, modelId, pageable);

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

    // SI LA LINEA DE ENTREGA TIENE EL ESTADO DE MOVEMENT_LINE_CANCELED,  DEBE DEVOLVER UNA EXCEPCION
    if (deliveryLine.getLineStatus() == LineStatus.LINE_CANCELED) {
    throw new BusinessException(
    ResponseStatus.CONFLICT,
    "La linea de entrega se encuentra cancelada");
    }

    return DeliveryLineMapper.builder().setDeliveryLine(deliveryLine).buildDeliveryLineDetailsResponse();
  }

  // ESTE MÉTODO SIRVE PARA CAMBIAR LA CANTIDAD REQUERIDA Y LA FECHA LIMITE,
  // ADEMÁS DE AÑADIR UN BREVE COMENTARIO
  @Override
  @Transactional
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
    // MOVEMENT_LINE_DELIVERED NI MOVEMENT_LINE_CANCELED
    if (deliveryLine.getLineStatus() == LineStatus.LINE_DELIVERED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_CANCELED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_MISSING) {
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
    Model_DeliveryOrder model_DeliveryOrder = deliveryLine.getModel_DeliveryOrder();
    if (model_DeliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Long model_DeliveryOrder_id = model_DeliveryOrder.getId();

    if (model_DeliveryOrder_id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Model model = model_DeliveryOrder.getModel();

    if (model == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Llamar al método auxiliar para actualizar la linea de entrega
    updateLineStatus(deliveryLine, deliveryOrder);
    deliveryLineRepository.save(deliveryLine);

    // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
    deliveryOrder.setPriorityDate(deliveryOrderDomainService.getClosestLimitDate(deliveryOrder_id));
    deliveryOrderRepository.save(deliveryOrder);

    // Recalcular la suma de las cantidades requeridas de las lineas de entrega

    // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
    model_DeliveryOrder.setRequiredQuantityTotal(
        deliveryLineRepository.sumRequiredQuantityByDeliveryOrderIdAndModelId(deliveryOrder_id,
            model_DeliveryOrder_id));

    model_DeliveryOrderRepository.save(model_DeliveryOrder);

    // RECALCULAR LA SUMATORIA DE CANTIDADES POR REGION
    model_DeliveryOrderDomainService.recalculateSummaries(deliveryOrder.getId(), model.getId());

    // recalculateProductDeliveryOrderRegions(model_DeliveryOrder.getId());

    Movement movement = new Movement();
    movement.setQuantity(quantityBalance);
    movement.setComment(movementDomainService.generateComment(deliveryLineUpdateRequest.getMovementComment(),
        "Un usuario altero los datos de la linea de entrega"));

    // Si el balance es un número positivo se considera un MOVEMENT_LINE_ALTER, si
    // es negativo se
    // considera un MOVEMENT_LINE_CHANGE
    movement
        .setMovementType(quantityBalance > 0 ? MovementType.MOVEMENT_LINE_ALTER : MovementType.MOVEMENT_LINE_CHANGE);

    movement.setUser(user);
    movement.setStockLotReceiver(null);
    movement.setStockLotEmitter(null);
    movement.setDeliveryLine(deliveryLine);
    movement.setModel(model);

    movementRepository.save(movement);
  }

  // Nota: Tambien actualiza el estado de la orden de entrega dependiendo del
  // estado de las lineas de entrega
  private void updateLineStatus(DeliveryLine line, DeliveryOrder order) {

    int required = line.getRequiredQuantity();
    int delivered = line.getDeliveredQuantity();
    DeliveryOrder deliveryOrder = line.getDeliveryOrder();

    if (required > delivered) {
      line.setPendingQuantity(required - delivered);
      line.setLineStatus(LineStatus.LINE_PENDING);
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
      return;
    }

    if (required == delivered) {
      line.setPendingQuantity(0);
      line.setLineStatus(LineStatus.LINE_READY);
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_READY);
      return;
    }

    // required < delivered
    line.setPendingQuantity(required - delivered);
    line.setLineStatus(LineStatus.LINE_EXCEEDED);
    deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);

  }

  @Override
  @Transactional
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

    Long id_model_deliveryOrder = deliveryLine.getModel_DeliveryOrder().getId();

    if (id_model_deliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Model_DeliveryOrder model_DeliveryOrder = model_DeliveryOrderRepository.findById(
        id_model_deliveryOrder).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion producto-orden de entrega no existe"));

    if (model_DeliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }


    if (deliveryLine.getLineStatus() == LineStatus.LINE_CANCELED) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La linea de entrega ya ha sido cancelada");
    }

    if (deliveryLine.getLineStatus() == LineStatus.LINE_DELIVERED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_MISSING) {
      throw new BusinessException(
          ResponseStatus.DEFAULT_RESOURCE,
          "No se puede cancelar una línea finalizada");
    }


    // La cantidad que esta preparada se le considera como cancelada
    Integer quantityCanceled = deliveryLine.getDeliveredQuantity();

    // DEVOLVER A STOCK Y GUARDAR LA NUEVA CANTIDAD
    returnStockToInventory(deliveryLine, quantityCanceled);


    // deliveryLine.setPendingQuantity(0);
    // deliveryLine.setDeliveredQuantity(0);
    deliveryLine.setLimitDate(null);
    deliveryLine.setLineStatus(LineStatus.LINE_CANCELED);
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);


    // RECALCULAR LAS CANTIDADES TOTALES EN LA RELACION DE MODEL - DELIVERY ORDER
    model_DeliveryOrderDomainService.recalculateSummaries(deliveryOrder.getId(), deliveryLine.getModel().getId());

    // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
    deliveryOrder.setPriorityDate(deliveryOrderDomainService.getClosestLimitDate(deliveryOrder.getId()));

    // Operacion para verificar si todas las lineas de entrega de una orden de
    // entrega han sido entregadas, es decir si todas tiene el estado READY



    // if (deliveryLineRepository.allLinesAreCanceled(deliveryOrderId)) {
    //   deliveryOrder.setOrderStatus(OrderStatus.ORDER_CANCELED);
    // } else {
    //   if (deliveryLineRepository.allLinesAreReady(deliveryOrderId)) {
    //     deliveryOrder.setOrderStatus(OrderStatus.ORDER_READY);
    //   } else {
    //     deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
    //   }
    // }

    boolean allCanceled = deliveryLineRepository.allLinesAreCanceled(deliveryOrderId);
    boolean allReady = deliveryLineRepository.allLinesAreReady(deliveryOrderId);

    // Si existe al menos una linea entregada o perdida
    boolean anyDeliveredOrMissing = deliveryLineRepository.existsByDeliveryOrderIdAndLineStatusIn(
        deliveryOrderId,
        List.of(LineStatus.LINE_DELIVERED, LineStatus.LINE_MISSING));

    if (allCanceled) {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_CANCELED);
    } else if (anyDeliveredOrMissing) {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_PARTIALLY_DELIVERED);
    } else if (allReady) {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_READY);
    } else {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
    }
    


    deliveryOrderRepository.save(deliveryOrder);



    // NUEVO MOVIMIENTO DE CANCELACIÓN DE LINEA DE ENTREGA
    Movement movement = new Movement();
    movement.setQuantity(quantityCanceled);
    movement.setComment("Se cancelo la linea de entrega con el ID: " + deliveryLine.getId());
    movement.setMovementType(MovementType.MOVEMENT_LINE_CANCELED);
    movement.setUser(user);
    movement.setStockLotReceiver(null);
    movement.setStockLotEmitter(null);
    movement.setDeliveryLine(deliveryLine);
    movement.setModel(deliveryLine.getModel());
    movementRepository.save(movement);
  }


  // METODO AUXILIAR PARA DEVOLVER STOCK AL INVENTARIO
  private void returnStockToInventory(
      DeliveryLine deliveryLine,
      int quantity) {
    if (quantity <= 0)
      return;

    storageTempStockLot(deliveryLine, quantity);

    Model model = deliveryLine.getModel();

    model.setTotalQuantityAvailable(
        model.getTotalQuantityAvailable() + quantity);

    model.setTotalQuantityTaken(
        model.getTotalQuantityTaken() - quantity);

    modelRepository.save(model);
  }


  // Método auxiliar para almacenar un lote de stock temporal con la cantidad
  // entregada de la linea de entrega que se esta cancelando, para luego ser
  // asignado a otra linea de entrega que lo requiera, pero solamente si hay
  // cantidad entregada

  // Se tiene en cuenta que si vuelve a cancelar otra linea de entrega, se
  // almacenara en este mismo lote de stock antes de las 24 horas de su fecha de
  // creación, para luego ser asignado a otra linea de entrega que lo requiera
  private void storageTempStockLot(DeliveryLine deliveryLine, Integer quantity) {

    // 1️⃣ No hacer nada si no hay cantidad entregada
    if (quantity == null || quantity <= 0) {
      return;
    }

    Company company = companyRepository.findById(1L)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "La empresa no existe"));
    LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);

    // 2️⃣ Buscar lote temporal activo
    Optional<StockLot> optionalStockLot = stockLotRepository.findActiveTemporaryStockLot(
        1L,
        deliveryLine.getModel().getId(),
        last24Hours);

    StockLot stockLot;

    if (optionalStockLot.isPresent()) {
      // 3️⃣ Si existe → sumar cantidad
      stockLot = optionalStockLot.get();
      stockLot.setQuantityReceived(stockLot.getQuantityReceived() + quantity);
      stockLot.setQuantityAvailable(stockLot.getQuantityAvailable() + quantity);
    } else {
      // 4️⃣ Si no existe → crear nuevo lote temporal
      stockLot = new StockLot();

      stockLot.setBatch(stockLotDomainService.resolveBatch(deliveryLine.getModel().getProduct().getName(),
          deliveryLine.getModel().getName(), company.getName()));
      stockLot.setQuantityReceived(quantity);
      stockLot.setQuantityAvailable(quantity);
      stockLot.setQuantityDelivered(0);
      stockLot.setQuantityLost(0);
      stockLot.setQuantityRecovered(0);
      stockLot.setZeroStock(false);
      stockLot.setTemporary(true);
      stockLot.setModel(deliveryLine.getModel());
      stockLot.setCompany(company);
    }

    stockLotRepository.save(stockLot);

    // 5️⃣ Registrar relación
    StockLot_DeliveryLine stockLot_DeliveryLine = new StockLot_DeliveryLine();
    stockLot_DeliveryLine.setStockLot(stockLot);
    stockLot_DeliveryLine.setDeliveryLine(deliveryLine);
    stockLot_DeliveryLine.setQuantityUsed(quantity);

    stockLot_DeliveryLineRepository.save(stockLot_DeliveryLine);
  }

  // MÉTODO PARA ENTREGAR UNA LINEA DE ENTREGA POR ID
  @Override
  @Transactional
  public void sendDeliveryLineById(Long id, Long id_user_authenticated) {
    User user = getUser(id_user_authenticated);
    DeliveryLine deliveryLine = getDeliveryLine(id);

    // Solamente podra declarar entregada si la linea de entrega se encuentra lista
    if (deliveryLine.getLineStatus() != LineStatus.LINE_READY) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La linea de entrega no puede ser entregada");
    }

    deliveryLine.setLineStatus(LineStatus.LINE_DELIVERED);
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    Model model = deliveryLine.getModel();
    int deliveredQty = deliveryLine.getDeliveredQuantity();

    // SOLO transición lógica
    model.setTotalQuantityTaken(model.getTotalQuantityTaken() - deliveredQty);
    model.setTotalQuantityDelivered(model.getTotalQuantityDelivered() + deliveredQty);
    modelRepository.save(model);

    Movement movement = new Movement();
    movement.setQuantity(deliveryLine.getDeliveredQuantity());
    movement.setComment("Se entrego la linea de entrega con el ID: " + deliveryLine.getId());
    movement.setMovementType(MovementType.MOVEMENT_LINE_DELIVERED);
    movement.setUser(user);
    movement.setStockLotReceiver(null);
    movement.setStockLotEmitter(null);
    movement.setDeliveryLine(deliveryLine);
    movement.setModel(model);
    movementRepository.save(movement);
  }

  // ESTO ES UN MOVIMIENTO EXTRAER UNA CANTIDAD Y REPORTARLA COMO PERDIDA DE UNA
  // LINEA DE
  // ENTREGA
  @Override
  @Transactional
  public void lostDeliveryLineById(Long id, DeliveryLineAlterRequest deliveryLineAlterRequest, Long id_user) {

    // Validación básica de parámetros obligatorios
    if (id == null || id_user == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    // Obtener usuario que realiza la operación
    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    // Obtener la línea de entrega
    DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryLine == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    // Obtener ID de la orden de entrega asociada
    Long deliveryOrderId = deliveryLine.getDeliveryOrder().getId();

    if (deliveryOrderId == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    // Obtener la orden de entrega
    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryOrderId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryOrder == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    // Validar que la línea NO esté en estados que impiden marcar como perdida
    if (deliveryLine.getLineStatus() == LineStatus.LINE_DELIVERED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_CANCELED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_MISSING) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
          "La linea de entrega no puede tener cantidades reportadas como perdida");
    }

    // Cantidad que se quiere marcar como perdida
    Integer lostQuantity = deliveryLineAlterRequest.getQuantity();

    // Cantidad actualmente entregada
    Integer deliveredQuantity = deliveryLine.getDeliveredQuantity();

    // Validar que no se pierda más de lo entregado
    if (deliveredQuantity - lostQuantity < 0) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
          "La cantidad perdida es mayor a la cantidad preparada");
    }

    // Ajuste de cantidades en la línea:
    // - Se reduce lo entregado
    // - Se incrementa lo pendiente (porque ahora se debe reponer)
    deliveryLine.setDeliveredQuantity(deliveredQuantity - lostQuantity);
    deliveryLine.setPendingQuantity(deliveryLine.getPendingQuantity() + lostQuantity);

    // Recalcular estado de la línea según lo pendiente
    if (deliveryLine.getPendingQuantity() == 0) {
      deliveryLine.setLineStatus(LineStatus.LINE_READY);
    } else {
      deliveryLine.setLineStatus(LineStatus.LINE_PENDING);
    }

    // Registrar quién hizo el cambio
    deliveryLine.setUserUpdater(user);

    // Obtener relación modelo - orden de entrega
    Model_DeliveryOrder model_DeliveryOrder = model_DeliveryOrderRepository.findById(
        deliveryOrderId).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion producto-orden de entrega no existe"));

    Long modelId = model_DeliveryOrder.getModel().getId();

    if (modelId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // // Obtener modelo del producto
    // Model model = modelRepository.findById(modelId).orElseThrow(
    // () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto
    // no existe"));

    // // Lógica clave:
    // // Se descuenta del "taken" (cantidad retirada del stock)
    // // porque esa cantidad ya no será entregada al cliente
    // // PERO no se devuelve al stock (ya se considera perdida)
    // model.setTotalQuantityTaken(model.getTotalQuantityTaken() - lostQuantity);
    // modelRepository.save(model);

    // Registrar el movimiento de pérdida
    Movement movement = new Movement();
    movement.setQuantity(lostQuantity);

    // Generar comentario automático + comentario opcional del usuario
    movement.setComment(movementDomainService.generateComment(deliveryLineAlterRequest.getMovementComment(),
        "Se ha descontado una cantidad de la linea de entrega con ID: " + deliveryLine.getId()));

    // Tipo de movimiento: pérdida de línea
    movement.setMovementType(MovementType.MOVEMENT_LINE_LOST);

    // Relaciones del movimiento
    movement.setUser(user);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(null);
    movement.setDeliveryLine(deliveryLine);
    movement.setModel(deliveryLine.getModel());

    // Persistir movimiento
    movementRepository.save(movement);
  }

  // ESTRATEGIA DE ACTUALIZACIÓN
  // MÉTODO AUXILIAR DE REPARACIÓN

  // Servicio para DEVOLVER una cantidad desde una línea de entrega hacia stock
  // (No elimina la línea, solo revierte parcialmente lo entregado)
  @Override
  @Transactional
  public void returnDeliveryLineById(Long id, DeliveryLineAlterRequest deliveryLineAlterRequest,
      Long id_user_authenticated) {

    // Validación básica de parámetros
    if (id == null || id_user_authenticated == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Usuario que ejecuta la operación
    User user = userRepository.findById(id_user_authenticated)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    // Obtener la línea de entrega
    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La linea de entrega no existe"));

    if (deliveryLine == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Cantidad que se va a devolver al stock
    Integer returnedQuantity = deliveryLineAlterRequest.getQuantity();

    // Obtener orden de entrega
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

    // Obtener relación modelo - orden de entrega
    Model_DeliveryOrder model_DeliveryOrder = model_DeliveryOrderRepository.findById(
        deliveryOrderId).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion producto-orden de entrega no existe"));

    if (model_DeliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // ---------------------------
    // ACTUALIZACIÓN DE LA LÍNEA
    // ---------------------------

    // Validar que no tenga el estado CANCELED O DELIVERED
    if (deliveryLine.getLineStatus() == LineStatus.LINE_DELIVERED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_CANCELED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_MISSING) {
      throw new BusinessException(
          ResponseStatus.DEFAULT_RESOURCE,
          "La linea de entrega no puede ser devuelta");
    }

    // Validar que no haya cantidad negativa
    // Validar que no se intente devolver más de lo entregado
    if (returnedQuantity > deliveryLine.getDeliveredQuantity()) {
      throw new BusinessException(
          ResponseStatus.BAD_REQUEST,
          "La cantidad a devolver no puede ser mayor a la cantidad preparada");
    }

    // Recalcular estado en base a la nueva cantidad entregada
    // (lo que queda después de devolver)
    if (deliveryLine.getDeliveredQuantity() - returnedQuantity == deliveryLine.getRequiredQuantity()) {
      deliveryLine.setLineStatus(LineStatus.LINE_READY);
    }

    if (deliveryLine.getDeliveredQuantity() - returnedQuantity > deliveryLine.getRequiredQuantity()) {
      deliveryLine.setLineStatus(LineStatus.LINE_EXCEEDED);
    }

    if (deliveryLine.getDeliveredQuantity() - returnedQuantity < deliveryLine.getRequiredQuantity()) {
      deliveryLine.setLineStatus(LineStatus.LINE_PENDING);
    }

    // Ajuste de cantidades:
    // - Se reduce lo entregado
    // - Se incrementa lo pendiente (porque ahora se debe volver a entregar)
    deliveryLine.setDeliveredQuantity(deliveryLine.getDeliveredQuantity() - returnedQuantity);
    deliveryLine.setPendingQuantity(deliveryLine.getPendingQuantity() + returnedQuantity);

    // Registrar usuario que hizo la modificación
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    // ---------------------------
    // ACTUALIZACIÓN RELACIÓN MODELO - ORDEN
    // ---------------------------

    // Recalcular cantidad total requerida (aunque aquí no cambia directamente,
    // se recalcula por consistencia)
    // model_DeliveryOrder.setRequiredQuantityTotal(
    // deliveryLineRepository.sumRequiredQuantityByDeliveryOrderIdAndModelId(deliveryOrder.getId(),
    // model_DeliveryOrder.getModel().getId()));

    // model_DeliveryOrderRepository.save(model_DeliveryOrder);

    // ---------------------------
    // ACTUALIZACIÓN DE LA ORDEN
    // ---------------------------
    // Recalcular fecha prioritaria (la más próxima)
    deliveryOrder.setPriorityDate(deliveryOrderDomainService.getClosestLimitDate(deliveryOrder.getId()));

    // Verificar si todas las líneas están completas
    if (deliveryLineRepository.allLinesAreReady(deliveryOrderId)) {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_READY);
    } else {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
    }

    deliveryOrderRepository.save(deliveryOrder);


    // ---------------------------
    // AJUSTE DEL MODELO (INVENTARIO)
    // ---------------------------
    Model model = modelRepository.findById(model_DeliveryOrder.getModel().getId()).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

    // Se reduce la cantidad "taken" porque ahora regresa al stock
    // (a diferencia de pérdida, aquí sí vuelve al inventario)

    model.setTotalQuantityTaken(model.getTotalQuantityTaken() - returnedQuantity);
    model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() + returnedQuantity);

    modelRepository.save(model);

    // ---------------------------
    // REINGRESO A STOCK
    // ---------------------------

    // Se crea un NUEVO lote temporal con la cantidad devuelta
    // (no se reutiliza el lote original → trazabilidad)
    storageTempStockLot(deliveryLine, returnedQuantity);

    // ---------------------------
    // REGISTRO DE MOVIMIENTO
    // ---------------------------
    Movement movement = new Movement();
    movement.setQuantity(returnedQuantity);
    movement.setMovementType(MovementType.MOVEMENT_LINE_RETURN);
    movement.setComment("Se ha devuelto una cantidad de linea de entrega: " + deliveryLine.getId());
    movement.setModel(deliveryLine.getModel());
    movement.setUser(user);

    // No hay emisor/receptor directo porque es una reversión lógica
    movement.setStockLotReceiver(null);
    movement.setStockLotEmitter(null);

    movement.setDeliveryLine(deliveryLine);

    movementRepository.save(movement);
  }

  private User getUser(Long userId) {
    if (userId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "El usuario no existe"));
  }

  private DeliveryLine getDeliveryLine(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    return deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "La linea de entrega no existe"));
  }

  private int validateQuantity(
      DeliveryLine deliveryLine,
      DeliveryLineAllocateRequest request) {

    Integer quantity = request.getQuantity();

    if (quantity == null || quantity <= 0) {
      throw new BusinessException(
          ResponseStatus.BAD_REQUEST,
          "Cantidad inválida");
    }

    if (quantity > deliveryLine.getPendingQuantity()) {
      throw new BusinessException(
          ResponseStatus.BAD_REQUEST,
          "La cantidad a entregar supera a la cantidad pendiente");
    }

    // 🚨 Validación clave que te faltaba
    if (deliveryLine.getLineStatus() == LineStatus.LINE_CANCELED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_DELIVERED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_MISSING) {

      throw new BusinessException(
          ResponseStatus.DEFAULT_RESOURCE,
          "La línea no permite asignación de stock");
    }

    return quantity;
  }

  private List<StockLot> validateStockLots(
      DeliveryLineAllocateRequest request,
      Model model) {

    List<Long> stockLotIds = request.getStockLotsIds();

    if (stockLotIds == null || stockLotIds.isEmpty()) {
      throw new BusinessException(
          ResponseStatus.BAD_REQUEST,
          "Debe seleccionar al menos un lote");
    }

    List<StockLot> stockLots = stockLotRepository.findAllByIdForUpdate(stockLotIds);

    // Validar existencia completa
    if (stockLots.size() != stockLotIds.size()) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          "Uno o más lotes no existen");
    }

    // Validar que todos pertenezcan al modelo
    for (StockLot stockLot : stockLots) {

      if (!stockLot.getModel().getId().equals(model.getId())) {
        throw new BusinessException(
            ResponseStatus.BAD_REQUEST,
            "El lote no pertenece al modelo");
      }

      // 🚨 Validación clave: evitar usar lotes sin stock
      if (stockLot.getQuantityAvailable() <= 0) {
        throw new BusinessException(
            ResponseStatus.BAD_REQUEST,
            "Uno de los lotes no tiene stock disponible");
      }

      // 🚨 Validación avanzada (opcional pero recomendable)
      if (Boolean.TRUE.equals(stockLot.isZeroStock())) {
        throw new BusinessException(
            ResponseStatus.BAD_REQUEST,
            "Uno de los lotes está marcado como sin stock");
      }
    }

    return stockLots;
  }

  @Transactional
  public void allocateStock(
      Model model,
      DeliveryLine deliveryLine,
      List<StockLot> stockLots,
      int quantity,
      User user) {

    int remaining = quantity;
    int totalAvailable = stockLots.stream()
        .mapToInt(StockLot::getQuantityAvailable)
        .sum();

    if (totalAvailable < quantity) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST, "Stock inconsistente");
    }
    // ==== Movimiento =====
    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment("Entrega de stock");
    movement.setDeliveryLine(deliveryLine);
    movement.setMovementType(MovementType.MOVEMENT_LINE_ALLOCATE);
    // movement.setStockLots(stockLots);
    movement.setModel(deliveryLine.getModel());
    movement.setUser(user);

    movementRepository.save(movement);

    for (StockLot stockLot : stockLots) {

      if (remaining == 0)
        break;

      int available = stockLot.getQuantityAvailable();
      if (available <= 0)
        continue;

      int used = Math.min(available, remaining);

      // 🔴 1. ACTUALIZAR LOTE
      stockLot.setQuantityAvailable(available - used);
      stockLot.setQuantityDelivered(stockLot.getQuantityDelivered() + used);

      stockLotRepository.save(stockLot);

      // 🔴 2. RELACIÓN DELIVERY
      // saveDeliveryRelation(stockLot, deliveryLine, used);
      StockLot_DeliveryLine dlRelation = new StockLot_DeliveryLine();
      dlRelation.setStockLot(stockLot);
      dlRelation.setDeliveryLine(deliveryLine);
      dlRelation.setQuantityUsed(used);
      stockLot_DeliveryLineRepository.save(dlRelation);

      // 🔴 3. RELACIÓN MOVIMIENTO
      // saveMovementRelation(movement, stockLot, used);
      Movement_StockLot mRelation = new Movement_StockLot();
      mRelation.setMovement(movement);
      mRelation.setStockLot(stockLot);
      mRelation.setQuantityTaken(used);
      movement_StockLotRepository.save(mRelation);

      remaining -= used;
    }

    if (remaining > 0) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST, "Stock inconsistente");
    }

    // 🔴 4. ACTUALIZAR MODELO (UNA SOLA VEZ)
    model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() - quantity);
    model.setTotalQuantityTaken(model.getTotalQuantityTaken() + quantity);

    modelRepository.save(model);
  }

  // Servicio para entregar una cantidad a una linea de entrega
  @Transactional
  @Override
  public void allocateDeliveryLineById(Long id, DeliveryLineAllocateRequest deliveryLineAllocateRequest,
      Long id_user_authenticated) {

    // ===== VALIDACIONES =====
    User user = getUser(id_user_authenticated);
    DeliveryLine deliveryLine = getDeliveryLine(id);
    Model model = deliveryLine.getModel();

    int quantity = validateQuantity(deliveryLine, deliveryLineAllocateRequest);

    List<StockLot> stockLots = validateStockLots(deliveryLineAllocateRequest, model);

    // ===== Orden de entrega =====
    // Long deliveryOrderId = deliveryLine.getDeliveryOrder().getId();

    // if (deliveryOrderId == null) {
    // throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    // }

    // DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
    // deliveryOrderId)
    // .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden
    // de entrega no existe"));

    DeliveryOrder deliveryOrder = deliveryLine.getDeliveryOrder();

    // ===== Relación producto-orden =====
    // Long id_model_deliveryOrder = deliveryLine.getModel_DeliveryOrder().getId();

    // if (id_model_deliveryOrder == null) {
    // throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    // }

    // Model_DeliveryOrder model_DeliveryOrder =
    // model_DeliveryOrderRepository.findById(
    // id_model_deliveryOrder).orElseThrow(
    // () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion
    // producto-orden de entrega no existe"));

    // ===== Validar stock total =====
    // Disminuye la cantidad total disponible del lote del stock
    // int totalAvailable = stockLots.stream()
    // .mapToInt(StockLot::getQuantityAvailable)
    // .sum();

    // if (totalAvailable < quantity) {
    // throw new BusinessException(
    // ResponseStatus.BAD_REQUEST,
    // "Stock insuficiente para la cantidad solicitada");
    // }

    // ===== Asignar stock por lotes =====
    allocateStock(model, deliveryLine, stockLots, quantity, user);

    // ===== Actualizar línea =====
    deliveryLine.setDeliveredQuantity(
        deliveryLine.getDeliveredQuantity() + quantity);
    deliveryLine.setPendingQuantity(
        deliveryLine.getPendingQuantity() - quantity);

    if (deliveryLine.getPendingQuantity() == 0) {
      deliveryLine.setLineStatus(LineStatus.LINE_READY);
    } else {
      deliveryLine.setLineStatus(LineStatus.LINE_PENDING);
    }
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    // ===== Recalcular producto-orden =====
    // Integer totalQuantitySumatory =
    // deliveryLineRepository.sumRequiredQuantityByDeliveryOrderIdAndModelId(
    // deliveryOrder.getId(), model.getId());

    // System.out.println("Total quantity sumatory: " + totalQuantitySumatory);

    // NO SE VA A ACTUALIZAR LA CANTIDAD REQUERIDA TOTAL DE
    // UNA RELACION ORDEN - MODELO DE PRODUCTO CUANDO SE ENTREGA DESDE EL ALMACEN
    // model_DeliveryOrder.setRequiredQuantityTotal(
    // totalQuantitySumatory);

    // model_DeliveryOrderRepository.save(model_DeliveryOrder);

    // ===== Recalcular orden =====
    deliveryOrder.setPriorityDate(
        deliveryOrderDomainService.getClosestLimitDate(deliveryOrder.getId()));

    if (deliveryLineRepository.allLinesAreReady(deliveryOrder.getId())) {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_READY);
    } else {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
    }

    deliveryOrderRepository.save(deliveryOrder);

  }

  @Override
  @Transactional
  public void missingDeliveryLineById(Long id, Long id_user_authenticated) {
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
    if (deliveryLine.getLineStatus() != LineStatus.LINE_DELIVERED) {
      throw new BusinessException(
          ResponseStatus.DEFAULT_RESOURCE,
          "Solo se puede marcar como MISSING una línea entregada");
    }
    deliveryLine.setLineStatus(LineStatus.LINE_MISSING);
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    // AQUI NO SE ALTERAN LOS CAMPOS DE LAS CANTIDADES DEL MODELO DEL PRODUCTO

    Movement movement = new Movement();
    movement.setQuantity(deliveryLine.getDeliveredQuantity());
    movement.setMovementType(MovementType.MOVEMENT_LINE_MISSING);
    movement.setComment("Perdida durante la entrega de la linea de entrega: " + deliveryLine.getId());
    movement.setModel(deliveryLine.getModel());
    movement.setUser(user);
    movement.setStockLotReceiver(null);
    movement.setStockLotEmitter(null);
    movement.setDeliveryLine(deliveryLine);

    movementRepository.save(movement);
  }

}
