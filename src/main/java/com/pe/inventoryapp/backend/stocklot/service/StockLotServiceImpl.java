package com.pe.inventoryapp.backend.stocklot.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.movement.service.MovementDomainService;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.model.mapper.StockLotMapper;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotAdjustmentRequest;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotReceiveRequest;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotTransferRequest;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotDetailsResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotListResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotSameProductListResponse;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;
import com.pe.inventoryapp.backend.stocklot.repository.StockLotRepository;
import com.pe.inventoryapp.backend.stocklot.repository.specifications.StockLotSpecifications;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class StockLotServiceImpl implements StockLotService {

  private final StockLotRepository stockLotRepository;
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final MovementRepository movementRepository;
  private final ModelRepository modelRepository;
  private final StockLotDomainService stockLotDomainService;
  private final MovementDomainService movementDomainService;

  public StockLotServiceImpl(
      StockLotRepository stockLotRepository,
      CompanyRepository companyRepository,
      UserRepository userRepository,
      MovementRepository movementRepository,
      ModelRepository modelRepository,
      StockLotDomainService stockLotDomainService,
      MovementDomainService movementDomainService) {
    this.stockLotRepository = stockLotRepository;
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
    this.movementRepository = movementRepository;
    this.modelRepository = modelRepository;
    this.stockLotDomainService = stockLotDomainService;
    this.movementDomainService = movementDomainService;
  }

  // REGISTRA UN NUEVO LOTE DE STOCK
  @Override
  public void saveStockLot(StockLotReceiveRequest stockLotReceiveRequest, Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = stockLotReceiveRequest.getQuantity();

    Long modelId = stockLotReceiveRequest.getModelId();

    if (modelId == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Model model = modelRepository.findById(
        modelId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo no existe"));

    if (model.isStatus() == false) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "El modelo del producto se encuentra desactivado");
    }

    Long id_company = stockLotReceiveRequest.getCompanyId();

    if (id_company == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Company company = companyRepository.findById(id_company)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El nombre de la empresa no existe"));

    // Guarda el nuevo lote de stock
    StockLot stockLot = new StockLot();
    stockLot
        .setBatch(stockLotDomainService.resolveBatch(model.getProduct().getName(), model.getName(), company.getName()));
    stockLot.setQuantityReceived(quantity);
    stockLot.setQuantityAvailable(quantity);

    // El total entregado es 0 porque aun no se ha entregado stock
    stockLot.setQuantityDelivered(0);
    stockLot.setQuantityLost(0);
    stockLot.setQuantityRecovered(0);

    stockLot.setTemporary(false);
    // Indica si el stock es cero
    stockLot.setZeroStock(false);
    stockLot.setModel(model);
    stockLot.setCompany(company);
    stockLotRepository.save(stockLot);

    // Actualiza las cantidades del stock
    // Integer sumatoryStockQuantityReceived =
    // stockLotRepository.sumQuantityReceivedByModelId(modelId);
    // Integer sumatoryStockQuantityAvailable =
    // stockLotRepository.sumQuantityAvailableByModelId(modelId);

    // model.setTotalQuantityReceived(sumatoryStockQuantityReceived);
    // model.setTotalQuantityAvailable(sumatoryStockQuantityAvailable);

    model.setTotalQuantityReceived(model.getTotalQuantityReceived() + quantity);
    model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() + quantity);

    modelRepository.save(model);

    // MOVIMIENTO DE AGREGAR STOCK A UN MODELO DE PRODUCTO EXISTENTE EN EL ALMACEN
    // (TIPO
    // MOVEMENT_STOCK_RECEIVE)
    Movement movement = new Movement();

    movement.setQuantity(quantity);
    movement.setComment(stockLotReceiveRequest.getComment());
    movement.setMovementType(MovementType.MOVEMENT_STOCK_RECEIVE);

    movement.setUser(user);

    // Solamente guarda el ID de stockLot receptor
    movement.setStockLotReceiver(stockLot);
    movement.setStockLotEmitter(null);

    // No se guarda el ID de deliveryLine porque no se trata de una linea entrega
    movement.setDeliveryLine(null);
    movement.setModel(model);

    movementRepository.save(movement);
    movementDomainService.deleteLastestMovement();
    // TODO: VERIFICAR SI ES CORRECTO QUE HAYA 7 QUERIES
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<StockLotListResponse> searchAllStockLotsByParams(
      Integer minQuantityReceived,
      Integer maxQuantityReceived,
      Integer minQuantityAvailable,
      Integer maxQuantityAvailable,
      LocalDateTime minCreatedAt,
      LocalDateTime maxCreatedAt,
      String keyword,
      Long companyId,
      Long categoryId,
      Long typeId,
      Long modelId,
      Pageable pageable) {
    // Page<StockLot> stockLots =
    // stockLotRepository.findAllByParams(minQuantityReceived,
    // maxQuantityReceived,
    // minQuantityAvailable, maxQuantityAvailable, minCreatedAt, maxCreatedAt,
    // keyword,
    // companyId, categoryId, typeId, modelId,pageable);

    Specification<StockLot> spec = Specification.unrestricted();

    // 1. FILTROS (SIEMPRE PRIMERO)
    spec = spec.and(StockLotSpecifications.quantityReceivedBeetween(minQuantityReceived, maxQuantityReceived));
    spec = spec.and(StockLotSpecifications.quantityAvailableBeetween(minQuantityAvailable, maxQuantityAvailable));
    spec = spec.and(StockLotSpecifications.createdAtBetween(minCreatedAt, maxCreatedAt));
    spec = spec.and(StockLotSpecifications.keywordContains(keyword));
    spec = spec.and(StockLotSpecifications.hasCompany(companyId));
    spec = spec.and(StockLotSpecifications.hasCategory(categoryId));
    spec = spec.and(StockLotSpecifications.hasType(typeId));
    spec = spec.and(StockLotSpecifications.hasModel(modelId));
    spec = spec.and(StockLotSpecifications.isNotZeroStock());

    /*
     * SIEMPRE AL FINAL
     *
     * ¿Por qué?
     * - fetch() modifica el query
     * - si lo pones antes, otros specs pueden meter joins duplicados
     * - al final, garantiza estructura limpia
     */
    spec = spec.and(StockLotSpecifications.fetchRelations());

    // Ordenar los elementos de acuerdo al campo de createdAt de forma descendente
    Pageable sortedPageable = PageRequest.of(
    pageable.getPageNumber(),
    pageable.getPageSize(),
    Sort.by("createdAt").descending()
);
    Page<StockLot> stockLots = stockLotRepository.findAll(spec, sortedPageable);

    List<StockLotListResponse> stockLotListResponse = stockLots.getContent().stream()
        .map(stockLot -> StockLotMapper.builder().setStockLot(stockLot).buildStockLotListResponse()).toList();

    PageResponse<StockLotListResponse> pageResponse = new PageResponse<>(
        stockLotListResponse,
        stockLots.getNumber(),
        stockLots.getSize(),
        stockLots.getTotalElements(),
        stockLots.getTotalPages(),
        stockLots.isFirst(),
        stockLots.isLast());

    return pageResponse;
  }

  // ESTE METODO SE UTILIZA EN LA LISTA DE LOTES DE STOCK QUE PERTENEZCAN A UN
  // MISMO PRODUCTO, EXCEPTUANDO EL MISMO PRODUCTO
  @Override
  @Transactional(readOnly = true)
  public List<StockLotSameProductListResponse> findAllStockLotsExceptOneStockLotByModelId(Long modelId, Long companyId,
      Long stockLotId) {
    List<StockLot> stockLots = stockLotRepository
        .findAllByModelIdAndCompanyIdAndExcludeOneStockLotByIdAndZeroStockIsFalse(
            modelId,
            companyId,
            stockLotId);
    return stockLots.stream()
        .map(stockLot -> StockLotMapper.builder().setStockLot(stockLot).buildStockLotSameProductListResponse())
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public StockLotDetailsResponse findStockLotById(Long stockLotId) {
    if (stockLotId == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    StockLot stockLot = stockLotRepository.findByIdAndJoins(stockLotId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock no existe en el sistema"));

    return StockLotMapper.builder().setStockLot(stockLot).buildStockLotDetailsResponse();
  }

  // TODO: CONTINUAR AQUI
  // Método para incrementar la cantidad del lote de stock
  @Override
  @Transactional
  public void increaseStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user) {
    if (id_user == null || idStockLot == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = stockLotAdjustmentRequest.getQuantity();

    // Encontrar el id del stockLot
    StockLot stockLot = stockLotRepository.findByIdAndJoins(
        idStockLot).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock no existe"));

    // Obtener el id del producto desde el stockLot
    Long modelId = stockLot.getModel().getId();

    if (modelId == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Model model = modelRepository.findById(
        modelId).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

    // Se debe pasar la cantidad en la que se quiere incrementar el stock
    int newQuantityReceived = stockLot.getQuantityReceived() + quantity;
    int newQuantityAvailable = stockLot.getQuantityAvailable() + quantity;

    stockLot.setQuantityReceived(newQuantityReceived);
    stockLot.setQuantityAvailable(newQuantityAvailable);
    stockLot.setZeroStock(false);

    stockLotRepository.save(stockLot);

    // Actualiza las cantidades del stock
    // Integer sumatoryStockQuantityReceived =
    // stockLotRepository.sumQuantityReceivedByModelId(modelId);
    // Integer sumatoryStockQuantityAvailable =
    // stockLotRepository.sumQuantityAvailableByModelId(modelId);

    // model.setTotalQuantityReceived(sumatoryStockQuantityReceived);
    // model.setTotalQuantityAvailable(sumatoryStockQuantityAvailable);

    model.setTotalQuantityReceived(model.getTotalQuantityReceived() + quantity);
    model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() + quantity);

    modelRepository.save(model);

    // GUARDAR EL MOVIMIENTO DE TIPO MOVEMENT_STOCK_INCREASE
    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(stockLotAdjustmentRequest.getComment());
    movement.setMovementType(MovementType.MOVEMENT_STOCK_INCREASE);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(stockLot);
    movement.setDeliveryLine(null);
    movement.setUser(user);
    movement.setModel(model);

    movementRepository.save(movement);
    movementDomainService.deleteLastestMovement();
  }

  @Override
  public void decreaseStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user) {
    if (id_user == null || idStockLot == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = stockLotAdjustmentRequest.getQuantity();

    StockLot stockLot = stockLotRepository.findByIdAndJoins(
        idStockLot).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock no existe"));

    // La nueva cantidad de stock se resta
    int newQuantityAvailable = stockLot.getQuantityAvailable() - quantity;
    int newQuantityLost = stockLot.getQuantityLost() + quantity;

    if (newQuantityAvailable < 0) {
      throw new BusinessException(ResponseStatus.CONFLICT, "Stock insuficiente");
    }

    // La cantidad disponible se actualiza
    stockLot.setQuantityAvailable(newQuantityAvailable);
    stockLot.setQuantityLost(newQuantityLost);

    if (newQuantityAvailable == 0) {
      stockLot.setZeroStock(true);
    }

    stockLotRepository.save(stockLot);

    // Obtener el producto del stockLot
    Long idModel = stockLot.getModel().getId();

    if (idModel == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Model model = modelRepository.findById(
        idModel).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

    // Actualiza las cantidades del stock
    // Integer sumatoryStockQuantityAvailable =
    // stockLotRepository.sumQuantityAvailableByModelId(idModel);
    // model.setTotalQuantityAvailable(sumatoryStockQuantityAvailable);

    model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() + quantity);
    modelRepository.save(model);

    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(stockLotAdjustmentRequest.getComment());
    movement.setMovementType(MovementType.MOVEMENT_STOCK_DECREASE);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(stockLot);
    movement.setDeliveryLine(null);
    movement.setUser(user);
    movement.setModel(model);
    movementRepository.save(movement);
    movementDomainService.deleteLastestMovement();
  }

  // Metodo para recuperar lote de stock que fue reportado como perdida
  @Override
  public void recoveryStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user) {
    if (id_user == null || idStockLot == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = stockLotAdjustmentRequest.getQuantity();

    StockLot stockLot = stockLotRepository.findByIdAndJoins(
        idStockLot).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock no existe"));

    Long idModel = stockLot.getModel().getId();

    if (idModel == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    // IMPLEMENTAR UNA LOGICA PARA OBTENER EL ULTIMO MOVIMIENTO DE TIPO LOSS DE UN
    // PRODUCTO

    Integer quantityLost = stockLot.getQuantityLost();
    Integer quantityAvailable = stockLot.getQuantityAvailable();
    Integer quantityRecovered = stockLot.getQuantityRecovered();

    // La nueva cantidad de stock se aumenta
    int newQuantityAvailable = quantityAvailable + quantity;
    int newQuantityLost = quantityLost - quantity;
    int newQuantityRecovered = quantityRecovered + quantity;

    if (quantityLost == 0) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "No existen pérdidas registradas para este lote de stock");
    }

    int maxRecoverable = quantityLost;

    if (quantity > maxRecoverable) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "No se puede recuperar más cantidad del que ha sido reportado como pérdida");
    }

    // La cantidad disponible se actualiza
    stockLot.setQuantityAvailable(newQuantityAvailable);
    stockLot.setQuantityLost(newQuantityLost);
    stockLot.setQuantityRecovered(newQuantityRecovered);

    if (newQuantityAvailable != 0) {
      stockLot.setZeroStock(false);
    }

    stockLotRepository.save(stockLot);

    Model model = modelRepository.findById(
        idModel).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

    // Integer sumatoryStockQuantityAvailable =
    // stockLotRepository.sumQuantityAvailableByModelId(idModel);
    // model.setTotalQuantityAvailable(sumatoryStockQuantityAvailable);

    model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() + quantity);
    modelRepository.save(model);

    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(stockLotAdjustmentRequest.getComment());
    movement.setMovementType(MovementType.MOVEMENT_STOCK_RECOVERY);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(stockLot);
    movement.setDeliveryLine(null);
    movement.setModel(model);
    movement.setUser(user);
    movementRepository.save(movement);
    movementDomainService.deleteLastestMovement();
  }

  // Método para hacer una transferencia entre 2 lotes de stock
  @Override
  public void transferStockLot(Long idStockLotEmitter, StockLotTransferRequest stockLotTransferRequest, Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = stockLotTransferRequest.getQuantity();

    Long id_stock_lot_receiver = stockLotTransferRequest.getStockLotReceiverId();

    if (idStockLotEmitter == null || id_stock_lot_receiver == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    if (idStockLotEmitter.equals(id_stock_lot_receiver)) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "El lote de stock emisor y el lote de stock receptor deben ser diferentes");
    }

    // Encontrar el modelo por id de stockLot
    StockLot stockLotEmitter = stockLotRepository.findByIdAndJoins(
        idStockLotEmitter).orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock emisor no existe"));
    StockLot stockLotReceiver = stockLotRepository.findByIdAndJoins(id_stock_lot_receiver).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock receptor no existe"));

    int newAvailableEmitter = stockLotEmitter.getQuantityAvailable() - quantity;
    int newAvailableReceiver = stockLotReceiver.getQuantityAvailable() + quantity;

    Long id_model_emitter = stockLotEmitter.getModel().getId();
    Long id_model_receiver = stockLotReceiver.getModel().getId();

    if (id_model_emitter == null || id_model_receiver == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    if (!stockLotEmitter.getCompany().getId().equals(stockLotReceiver.getCompany().getId())) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "Los lotes de stock deben pertenecer a la misma empresa");
    }

    // Verificar que ambos lotes correspondan al mismo producto
    if (!id_model_emitter.equals(id_model_receiver)) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "Los lotes de stock deben pertenecer al mismo modelo");
    }

    if (newAvailableEmitter < 0) {
      throw new BusinessException(ResponseStatus.CONFLICT, "Cantidad insuficiente del lote de stock emisor");
    }

    stockLotEmitter.setQuantityAvailable(newAvailableEmitter);
    stockLotReceiver.setQuantityAvailable(newAvailableReceiver);

    // Solamente si la cantidad del stock disponible es 0, se cambia el valor en
    // zeroStock a true
    stockLotEmitter.setZeroStock(stockLotEmitter.getQuantityAvailable() == 0);
    stockLotReceiver.setZeroStock(stockLotReceiver.getQuantityAvailable() == 0);

    // Guarda los cambios en la base de datos
    stockLotRepository.save(stockLotEmitter);
    stockLotRepository.save(stockLotReceiver);

    Model modelReceiver = modelRepository.findById(id_model_receiver).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto receptor no existe"));

    // La sumatoria de la cantidad disponible de un producto no se actualiza, porque
    // no se actualiza el total de stock y solamente se hace una transferencia entre
    // 2 lotes de stocks que pertenecen al mismo producto

    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(stockLotTransferRequest.getComment());
    movement.setMovementType(MovementType.MOVEMENT_STOCK_TRANSFER);
    movement.setStockLotEmitter(stockLotEmitter);
    movement.setStockLotReceiver(stockLotReceiver);
    movement.setDeliveryLine(null);
    movement.setModel(modelReceiver);
    movement.setUser(user);
    movementRepository.save(movement);
    movementDomainService.deleteLastestMovement();
  }

  @Override
  public List<StockLotSameProductListResponse> findAllActivesStockLotsByModelId(Long modelId) {
    List<StockLot> stockLots = stockLotRepository.findAllActivesByModelId(modelId);
    return stockLots.stream()
        .map(stockLot -> StockLotMapper.builder().setStockLot(stockLot).buildStockLotSameProductListResponse())
        .toList();
  }
}
