package com.pe.inventoryapp.backend.deliveryline.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Model_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.service.DeliveryOrderDomainService;
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

import jakarta.transaction.Transactional;

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
      MovementDomainService movementDomainService) {
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
  }

  // TODO: EXAMINAR MINUCIOSAMENTE EL FUNCIONAMIENTO DE CADA UNO DE LOS SERVICIOS
  @Override
  public void saveDeliveryLine(DeliveryLineRequest deliveryLineRequest, Long id_model_deliveryOrder, Long id_user) {

    Long id_location = deliveryLineRequest.getLocationId();

    if (id_location == null || id_model_deliveryOrder == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Location location = locationRepository.findById(
        id_location).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));

    // Obtener el producto y orden de entrega desde Product_DeliveryOrder
    Model_DeliveryOrder model_DeliveryOrder = model_DeliveryOrderRepository.findById(id_model_deliveryOrder)
        .orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND,
                "La relación de producto y orden de entrega no existe"));

    Long id_deliveryOrder = model_DeliveryOrder.getDeliveryOrder().getId();
    Long id_model = model_DeliveryOrder.getModel().getId();

    if (id_deliveryOrder == null || id_model == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Regla: no permitir duplicados por ubicación, excepto de las lineas de entrega
    // con estado MOVEMENT_LINE_CANCELED
    boolean exists = deliveryLineRepository
        .existsDuplicate(id_deliveryOrder, id_model, id_location);

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
    deliveryLine.setModel(model_DeliveryOrder.getModel());
    deliveryLine.setModel_DeliveryOrder(model_DeliveryOrder);
    deliveryLine.setDeliveryOrder(model_DeliveryOrder.getDeliveryOrder());

    deliveryLineRepository.save(deliveryLine);

    // Actualizar la orden de entrega
    DeliveryOrder deliveryOrder = model_DeliveryOrder.getDeliveryOrder();
    // 1° actualizar la fecha limite de deliveryOrder comparando todas las lineas de
    // entrega y tomar el valor con la fecha más cercana que no haya sido entregada
    deliveryOrder.setPriorityDate(deliveryOrderDomainService.getClosestLimitDate(deliveryOrder.getId()));

    // 2° actualizar el estado a PENDING cada vez que se guarde una nueva linea de
    // entrega
    deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
    deliveryOrderRepository.save(deliveryOrder);

    // 3° CALCULAR LA SUMATORIA DE LAS CANTIDADES REQUERIDAS DE TODAS LAS LINEAS DE
    // ENTREGA POR ORDEN DE ENTREGA Y PRODUCTO
    deliveryOrderDomainService.recalculateSummaries(deliveryOrder);

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

    // TODO: ES PROBABLE QUE SI LA LINEA DE ENTREGA TIENE EL ESTADO DE MOVEMENT_LINE_CANCELED,
    // DEVUELVA UN THROW
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
    if (deliveryLine.getLineStatus() == LineStatus.LINE_DELIVERED || deliveryLine.getLineStatus() == LineStatus.LINE_CANCELED) {
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
    deliveryOrderDomainService.recalculateSummaries(deliveryOrder);

    // recalculateProductDeliveryOrderRegions(model_DeliveryOrder.getId());

    Movement movement = new Movement();
    movement.setQuantity(quantityBalance);
    movement.setComment(movementDomainService.generateComment(deliveryLineUpdateRequest.getMovementComment(),
        "Un usuario altero los datos de la linea de entrega"));

    // Si el balance es un número positivo se considera un MOVEMENT_LINE_ALTER, si es negativo se
    // considera un MOVEMENT_LINE_CHANGE
    movement.setMovementType(quantityBalance > 0 ? MovementType.MOVEMENT_LINE_ALTER : MovementType.MOVEMENT_LINE_CHANGE);

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

    // La cantidad que esta preparada se le considera como cancelada
    Integer quantityCanceled = deliveryLine.getDeliveredQuantity();

    // CREAR UN NUEVO LOTE DE STOCK CON LA CANTIDAD ENTREGADA DE LA LINEA DE ENTREGA
    // QUE SE ESTA CANCELANDO, PERO SOLAMENTE SI HAY CANTIDAD ENTREGADA
    if (quantityCanceled > 0) {
      // throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
      // "No se puede cancelar esta línea porque ya hay una cantidad a entregar");
      // TODO: VERIFICAR EL FUNCIONAMIENTO DEL METODO
      storageTempStockLot(deliveryLine);
    }

    deliveryLine.setPendingQuantity(0);
    deliveryLine.setDeliveredQuantity(0);
    deliveryLine.setLimitDate(null);
    deliveryLine.setLineStatus(LineStatus.LINE_CANCELED);
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    // TODO: VERIFICAR ESTO
    //* Debe eliminar la cantidad que fue tomada
    Model model = model_DeliveryOrder.getModel();
    model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() + deliveryLine.getDeliveredQuantity());
    model.setTotalQuantityTaken(model.getTotalQuantityTaken() - deliveryLine.getDeliveredQuantity());
    modelRepository.save(model);


    // TODO: EL PROBLEMA ESTA RELACIONADO CON EL CAMPO QUANTITYDELIVERED
    // NOTA: RECUERDA QUE TOTALQUANTITYDELIVERED REPRESENTA LA CANTIDAD QUE HA SIDO
    // ENTREGADA AL CLIENTE

    // Model model = deliveryLine.getModel();

    // model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() +
    // quantityCanceled);
    // model.setTotalQuantityDelivered(model.getTotalQuantityDelivered() -
    // quantityCanceled);

    // modelRepository.save(model);

    // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
    deliveryOrderDomainService.recalculateSummaries(deliveryOrder);

    // model_DeliveryOrder.setRequiredQuantityTotal(
    // deliveryLineRepository.sumRequiredQuantityByDeliveryOrder_Product(deliveryOrder.getId(),
    // model_DeliveryOrder.getModel().getId()));

    // model_DeliveryOrderRepository.save(model_DeliveryOrder);

    // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
    deliveryOrder.setPriorityDate(deliveryOrderDomainService.getClosestLimitDate(deliveryOrder.getId()));

    // Operacion para verificar si todas las lineas de entrega de una orden de
    // entrega han sido entregadas, es decir si todas tiene el estado READY

    if (deliveryLineRepository.allLinesAreCanceled(deliveryOrderId)) {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_CANCELED);
    } else {
      if (deliveryLineRepository.allLinesAreReady(deliveryOrderId)) {
        deliveryOrder.setOrderStatus(OrderStatus.ORDER_READY);
      } else {
        deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
      }
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

  // Método auxiliar para almacenar un lote de stock temporal con la cantidad
  // entregada de la linea de entrega que se esta cancelando, para luego ser
  // asignado a otra linea de entrega que lo requiera, pero solamente si hay
  // cantidad entregada

  // Se tiene en cuenta que si vuelve a cancelar otra linea de entrega, se
  // almacenara en este mismo lote de stock antes de las 24 horas de su fecha de
  // creación, para luego ser asignado a otra linea de entrega que lo requiera
  private void storageTempStockLot(DeliveryLine deliveryLine) {
    Integer deliveredQuantity = deliveryLine.getDeliveredQuantity();

    // 1️⃣ No hacer nada si no hay cantidad entregada
    if (deliveredQuantity == null || deliveredQuantity <= 0) {
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
      stockLot.setQuantityReceived(stockLot.getQuantityReceived() + deliveredQuantity);
      stockLot.setQuantityAvailable(stockLot.getQuantityAvailable() + deliveredQuantity);
    } else {
      // 4️⃣ Si no existe → crear nuevo lote temporal
      stockLot = new StockLot();

      stockLot.setBatch(stockLotDomainService.resolveBatch(deliveryLine.getModel().getProduct().getName(),
          deliveryLine.getModel().getName(), company.getName()));
      stockLot.setQuantityReceived(deliveredQuantity);
      stockLot.setQuantityAvailable(deliveredQuantity);
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
    stockLot_DeliveryLine.setQuantityUsed(deliveredQuantity);

    stockLot_DeliveryLineRepository.save(stockLot_DeliveryLine);
  }

  // MÉTODO PARA ENTREGAR UNA LINEA DE ENTREGA POR ID
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
    if (deliveryLine.getLineStatus() != LineStatus.LINE_READY) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La linea de entrega no puede ser entregada");
    }

    deliveryLine.setLineStatus(LineStatus.LINE_DELIVERED);
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    // TODO: VERIFICAR SI AL ENTREGAR UNA LINEA DE ENTREGA, SE ALTERARA LA CANTIDAD
    // ENTREGADA DEL PRODUCTO
    Model model = modelRepository.findById(deliveryLine.getModel().getId()).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

    // Actualizara los campos porque se ha entregado el modelo del producto
    model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() - deliveryLine.getDeliveredQuantity());

    // La cantidad tomada ahora pasa a ser la cantidad entregada
    model.setTotalQuantityTaken(model.getTotalQuantityTaken() - deliveryLine.getDeliveredQuantity());
    model.setTotalQuantityDelivered(model.getTotalQuantityDelivered() + deliveryLine.getDeliveredQuantity());
    modelRepository.save(model);

    Movement movement = new Movement();
    movement.setQuantity(deliveryLine.getDeliveredQuantity());
    movement.setComment("Se entrego la linea de entrega con el ID: " + deliveryLine.getId());
    movement.setMovementType(MovementType.MOVEMENT_LINE_DELIVERED);
    movement.setUser(user);
    movement.setStockLotReceiver(null);
    movement.setStockLotEmitter(null);
    movement.setDeliveryLine(deliveryLine);
    movement.setModel(deliveryLine.getModel());
    movementRepository.save(movement);

  }

  // ESTO ES UN MOVIMIENTO EXTRAER UNA CANTIDAD Y REPORTARLA COMO PERDIDA DE UNA
  // LINEA DE
  // ENTREGA
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

    // Si una linea de entrega fue reportada como perdida
    if (deliveryLine.getLineStatus() == LineStatus.LINE_DELIVERED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_CANCELED ||
        deliveryLine.getLineStatus() == LineStatus.LINE_MISSING) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
          "La linea de entrega no puede ser reportada como perdida");
    }

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
      deliveryLine.setLineStatus(LineStatus.LINE_READY);
    } else {
      deliveryLine.setLineStatus(LineStatus.LINE_PENDING);
    }

    deliveryLine.setUserUpdater(user);

    // TODO: Probar esta logica para ver si se actualiza el total de la cantidad
    // requerida
    Model_DeliveryOrder model_DeliveryOrder = model_DeliveryOrderRepository.findById(
        deliveryOrderId).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion producto-orden de entrega no existe"));

    // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
    // TODO: NO SE VAN A RECALCULAR PORQUE NO SE ALTERA LA CANTIDAD REQUERIDA DE LA
    // LINEA DE ENTREGA
    // model_DeliveryOrder.setRequiredQuantityTotal(
    // deliveryLineRepository.sumRequiredQuantityByDeliveryOrderIdAndModelId(deliveryOrder.getId(),
    // model_DeliveryOrder.getModel().getId()));

    // model_DeliveryOrderRepository.save(model_DeliveryOrder);

    Long modelId = model_DeliveryOrder.getModel().getId();

    if (modelId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // TODO: VERIFICAR SI ALTERA EL PRODUCTO, PORQUE SI SE REPORTA UNA CANTIDAD COMO
    // PERDIDA, ENTONCES DEBE DESCONTAR ESA CANTIDAD DE LA CANTIDAD ENTREGADA DEL
    // PRODUCTO, PERO NO DE LA CANTIDAD DISPONIBLE, PORQUE ESA CANTIDAD YA FUE
    // DESCONTADA CUANDO SE ENTREGÓ LA LINEA DE ENTREGA

    // TODO: NO ALTERARA EL CAMPO DEL MODELO PORQUE NO SE HA ENTREGADO EL MODELO DEL
    // PRODUCTO
    // Model model = modelRepository.findById(
    // modelId)
    // .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El
    // producto no existe"));

    // model.setTotalQuantityDelivered(model.getTotalQuantityDelivered() -
    // lostQuantity);
    // productRepository.save(model);

    Model model = modelRepository.findById(modelId).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

    // Al reportar una cantidad como perdida, se debe descontar esa cantidad de la cantidad tomada del producto, porque esa cantidad ya no sera entregada al cliente
    model.setTotalQuantityTaken(model.getTotalQuantityTaken() - lostQuantity);
    modelRepository.save(model);

    // 3° REGISTRARLO COMO MOVIMIENTO
    Movement movement = new Movement();
    movement.setQuantity(lostQuantity);
    movement.setComment(movementDomainService.generateComment(deliveryLineAlterRequest.getMovementComment(),
        "Se ha descontado una cantidad de la linea de entrega con ID: " + deliveryLine.getId()));
    movement.setMovementType(MovementType.MOVEMENT_LINE_LOST);

    movement.setUser(user);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(null);
    movement.setDeliveryLine(deliveryLine);
    movement.setModel(deliveryLine.getModel());
    movementRepository.save(movement);

  }

  // ESTRATEGIA DE ACTUALIZACIÓN
  // MÉTODO AUXILIAR DE REPARACIÓN

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

    Model_DeliveryOrder model_DeliveryOrder = model_DeliveryOrderRepository.findById(
        deliveryOrderId).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion producto-orden de entrega no existe"));
    if (model_DeliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // TODO: CALCULAR LA SUMATORIA DE LOS LOTES DE STOCK ENTREGADOS, ASOCIADOS A
    // ESTA LINEA DE ENTREGA, PORQUE AL RETORNAR UNA CANTIDAD, SE DEBE ALMANCENAR EN
    // UN NUEVO STOCK LOT

    // AQUI NO DEBE CAMBIAR EL ESTADO A MOVEMENT_LINE_CANCELED, SINO QUE DEBE COMPARAR SI
    // ESTA LINEA DE ENTREGA TIENE CANTIDAD ENTREGADA
    if (deliveryLine.getDeliveredQuantity() - returnedQuantity == deliveryLine.getRequiredQuantity()) {
      deliveryLine.setLineStatus(LineStatus.LINE_READY);
    }

    if (deliveryLine.getDeliveredQuantity() - returnedQuantity > deliveryLine.getRequiredQuantity()) {
      deliveryLine.setLineStatus(LineStatus.LINE_EXCEEDED);
    }

    if (deliveryLine.getDeliveredQuantity() - returnedQuantity < deliveryLine.getRequiredQuantity()) {
      deliveryLine.setLineStatus(LineStatus.LINE_PENDING);
    }

    deliveryLine.setDeliveredQuantity(deliveryLine.getDeliveredQuantity() - returnedQuantity);
    deliveryLine.setPendingQuantity(deliveryLine.getPendingQuantity() + returnedQuantity);

    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
    model_DeliveryOrder.setRequiredQuantityTotal(
        deliveryLineRepository.sumRequiredQuantityByDeliveryOrderIdAndModelId(deliveryOrder.getId(),
            model_DeliveryOrder.getModel().getId()));

    model_DeliveryOrderRepository.save(model_DeliveryOrder);

    // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
    deliveryOrder.setPriorityDate(deliveryOrderDomainService.getClosestLimitDate(deliveryOrder.getId()));

    // Operacion para verificar si todas las lineas de entrega de una orden de
    // entrega han sido entregadas, es decir si todas tiene el estado READY
    if (deliveryLineRepository.allLinesAreReady(deliveryOrderId)) {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_READY);
    } else {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
    }

    deliveryOrderRepository.save(deliveryOrder);

    // RECALCULAR LA SUMATORIA DE CANTIDADES POR REGION
    deliveryOrderDomainService.recalculateSummaries(deliveryOrder);

    Model model = modelRepository.findById(model_DeliveryOrder.getModel().getId()).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

    model.setTotalQuantityTaken(model.getTotalQuantityTaken() - returnedQuantity);
      modelRepository.save(model);


    // TODO: DEBE CREAR UN NUEVO LOTE DE STOCK TEMPORAL CON LA CANTIDAD DEVUELTA
    

    Movement movement = new Movement();
    movement.setQuantity(deliveryLine.getDeliveredQuantity());
    movement.setMovementType(MovementType.MOVEMENT_LINE_RETURN);
    movement.setComment("Se ha devuelto una cantidad de linea de entrega: " + deliveryLine.getId());
    movement.setModel(deliveryLine.getModel());
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

    // ==== Modelo =====
    Long modelId = deliveryLine.getModel().getId();

    if (modelId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Model model = modelRepository.findById(modelId).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

    // ===== Relación producto-orden =====
    Long id_model_deliveryOrder = deliveryLine.getModel_DeliveryOrder().getId();

    if (id_model_deliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Model_DeliveryOrder model_DeliveryOrder = model_DeliveryOrderRepository.findById(
        id_model_deliveryOrder).orElseThrow(
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
    List<Long> stockLotIds = deliveryLineAllocateRequest.getStockLotsIds();

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

    for (StockLot stockLot : stockLots) {
      if (!stockLot.getModel().getId().equals(modelId)) {
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
    movement.setMovementType(MovementType.MOVEMENT_LINE_ALLOCATE);
    // movement.setStockLots(stockLots);
    movement.setModel(deliveryLine.getModel());
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
      deliveryLine.setLineStatus(LineStatus.LINE_READY);
    }
    // ===== Alterar campos del producto =====
    // model.setTotalQuantityDelivered(model.getTotalQuantityDelivered() + quantity);
    model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() - quantity);
    model.setTotalQuantityTaken(model.getTotalQuantityTaken() + quantity);
    modelRepository.save(model);

    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);

    // ===== Recalcular producto-orden =====
    Integer totalQuantitySumatory = deliveryLineRepository.sumRequiredQuantityByDeliveryOrderIdAndModelId(
        deliveryOrder.getId(), modelId);

    System.out.println("Total quantity sumatory: " + totalQuantitySumatory);

    model_DeliveryOrder.setRequiredQuantityTotal(
        totalQuantitySumatory);

    model_DeliveryOrderRepository.save(model_DeliveryOrder);

    // ===== Recalcular orden =====
    deliveryOrder.setPriorityDate(
        deliveryOrderDomainService.getClosestLimitDate(deliveryOrder.getId()));

    if (deliveryLineRepository.allLinesAreReady(deliveryOrder.getId())) {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_READY);
    } else {
      deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
    }

    deliveryOrderRepository.save(deliveryOrder);

    // ===== Recalcular regiones =====
    deliveryOrderDomainService.recalculateSummaries(deliveryOrder);

  }

  @Override
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
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
          "La linea de entrega no puede ser reportada como perdida luego de la entrega");
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