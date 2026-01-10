package com.pe.inventoryapp.backend.stocklot.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.request.MovementTransferRequest;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.model.mapper.StockLotMapper;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotAdjustmentRequest;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotReceiveRequest;
import com.pe.inventoryapp.backend.stocklot.model.request.StockLotTransferRequest;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotDetailsResponse;
import com.pe.inventoryapp.backend.stocklot.model.response.StockLotListResponse;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;
import com.pe.inventoryapp.backend.stocklot.repository.StockLotRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class StockLotServiceImpl implements StockLotService{

  @Autowired
  private StockLotRepository stockLotRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MovementRepository movementRepository;

  // REGISTRA UN NUEVO LOTE DE STOCK
  @Override
  public void saveStockLot(StockLotReceiveRequest stockLotReceiveRequest, Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = stockLotReceiveRequest.getQuantity();

    // TODO: VERIFICAR SI HACE FALTA VALIDAR LA CANTIDAD
    // if (quantity <= 0) {
    //   throw new BusinessException(ResponseStatus.CONFLICT, "La cantidad debe ser mayor a 0");
    // }

    Long id_product = stockLotReceiveRequest.getIdProduct();

    if (id_product == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(id_product)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));

    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "El producto se encuentra desactivado");
    }

    Long id_company = stockLotReceiveRequest.getIdCompany();

    if (id_company == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Company company = companyRepository.findById(id_company)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El nombre de la empresa no existe"));

    // Obtiene la fecha de hoy por partes
    LocalDateTime now = LocalDateTime.now();
    String date = now.getDayOfMonth() + "/" + now.getMonthValue() + "/" + now.getYear();
    String time = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();

    // Genera automaticamente el batch
    String batch = "LOT-" + product.getName().replace(" ", "-") + "-" + date + "-" + time;

    // Guarda el nuevo lote de stock
    StockLot stockLot = new StockLot();
    stockLot.setBatch(batch);
    stockLot.setQuantityReceived(quantity);
    stockLot.setQuantityAvailable(quantity);
    // El total entregado es 0 porque aun no se ha entregado stock
    stockLot.setQuantityDelivered(0);
    // Indica si el stock es cero
    stockLot.setZeroStock(false);
    stockLot.setProduct(product);
    stockLot.setCompany(company);
    stockLotRepository.save(stockLot);

    // Actualiza las cantidades del stock
    Integer sumatoryStockQuantityReceived = stockLotRepository.sumQuantityReceivedByProductId(id_product);
    Integer sumatoryStockQuantityAvailable = stockLotRepository.sumQuantityAvailableByProductId(id_product);

    // TODO: VERIFICAR SI ESTA CAMPO ES NECESARIO
    Integer sumatoryStockQuantityDelivered = stockLotRepository.sumQuantityDeliveredByProductId(id_product);

    product.setTotalQuantityReceived(sumatoryStockQuantityReceived);
    product.setTotalQuantityAvailable(sumatoryStockQuantityAvailable);

    product.setTotalQuantityDelivered(sumatoryStockQuantityDelivered);

    productRepository.save(product);

    // MOVIMIENTO DE AGREGAR STOCK A UN PRODUCTO EXISTENTE EN EL ALMACEN
    Movement movement = new Movement();

    movement.setQuantity(quantity);
    movement.setComment(stockLotReceiveRequest.getComment());
    movement.setMovementType(MovementType.RECEIVE);

    // Solamente guarda el ID de stockLot receptor
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(stockLot);

    // No se guarda el ID de deliveryLine porque no se trata de una linea entrega
    movement.setDeliveryLine(null);
    movement.setProduct(product);
    movement.setUser(user);

    movementRepository.save(movement);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<StockLotListResponse> searchAllStockLotsByParams(
      Integer minQuantityAvailable,
      Integer maxQuantityAvailable,
      Integer minQuantityReceived,
      Integer maxQuantityReceived,
      Integer minDeliveredTotal,
      Integer maxDeliveredTotal,
      LocalDateTime minCreatedAt,
      LocalDateTime maxCreatedAt,
      String productName,
      Boolean zeroStock,
      Long companyId,
      Pageable pageable    
    ) {
        Page<StockLot> stockLots = stockLotRepository.findAllByParams(minQuantityAvailable, maxQuantityAvailable, minQuantityReceived, maxQuantityReceived, minDeliveredTotal, maxDeliveredTotal, minCreatedAt, maxCreatedAt, productName, zeroStock, companyId, pageable);

        List<StockLotListResponse> stockLotListResponse = stockLots.getContent().stream().map(stockLot -> StockLotMapper.builder().setStockLot(stockLot).buildStockLotListResponse()).toList();

        PageResponse<StockLotListResponse> pageResponse = new PageResponse<>(
          stockLotListResponse,
          stockLots.getNumber(),
          stockLots.getSize(),
          stockLots.getTotalElements(),
          stockLots.getTotalPages(),
          stockLots.isFirst(),
          stockLots.isLast()
        );

        return pageResponse;
      }


  @Override
  @Transactional(readOnly = true)
  public PageResponse<StockLotListResponse> searchAllStockLotsByParamsAndProductId(Integer minQuantityAvailable,
      Integer maxQuantityAvailable, Integer minQuantityReceived, Integer maxQuantityReceived,
      Integer minDeliveredTotal,
      Integer maxDeliveredTotal, LocalDateTime minCreatedAt, LocalDateTime maxCreatedAt, Boolean zeroStock,
      Long companyId, Long productId, Pageable pageable) {
    Page<StockLot> stockLots = stockLotRepository.findAllByParamsAndProductId(minQuantityAvailable, maxQuantityAvailable,
        minQuantityReceived, maxQuantityReceived, minDeliveredTotal, maxDeliveredTotal, minCreatedAt, maxCreatedAt,
        zeroStock, companyId, productId, pageable);

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

  @Override
  @Transactional(readOnly = true)
  public PageResponse<StockLotListResponse> searchAllStockLotsByNotZeroStockAndParams(Integer minQuantityAvailable,
      Integer maxQuantityAvailable, LocalDateTime minCreatedAt, LocalDateTime maxCreatedAt, Long companyId,
      String productName, Pageable pageable) {
    Page<StockLot> stockLots = stockLotRepository.findAllByNotZeroStock(minQuantityAvailable, maxQuantityAvailable, minCreatedAt, maxCreatedAt,
        productName, companyId, pageable);

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

  @Override
  @Transactional(readOnly = true)
  public PageResponse<StockLotListResponse> searchAllStockLotsByNotZeroStockAndParamsAndProductId(
      Integer minQuantityAvailable, Integer maxQuantityAvailable, LocalDateTime minCreatedAt,
      LocalDateTime maxCreatedAt, Long companyId, Long productId, Pageable pageable) {
    Page<StockLot> stockLots = stockLotRepository.findAllByProductIdAndNotZeroStock(minQuantityAvailable, maxQuantityAvailable,
        minCreatedAt, maxCreatedAt,
        productId, companyId, pageable);

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


  @Override
  @Transactional(readOnly = true)
  public StockLotDetailsResponse findStockLotById(Long stockLotId) {
    if (stockLotId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    StockLot stockLot = stockLotRepository.findById(stockLotId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock no existe en el sistema"));

    return StockLotMapper.builder().setStockLot(stockLot).buildStockLotDetailsResponse();
  }



  @Override
  public void increaseStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user) {
    if (id_user == null || idStockLot == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = stockLotAdjustmentRequest.getQuantity();

    // TODO: VERIFICAR SI LA CANTIDAD ES MENOR O IGUAL A CERO DESDE EL DTO DE TIPO REQUEST
    // if (quantity <= 0) {
    //   throw new BusinessException(ResponseStatus.CONFLICT, "La cantidad debe ser mayor a 0");
    // }


    // Encontrar el id del stockLot
    StockLot stockLot = stockLotRepository.findById(
        idStockLot).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock no existe"));

    // Obtener el id del producto desde el stockLot
    Long idProduct = stockLot.getProduct().getId();

    if (idProduct == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(idProduct).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe"));

    // Se debe pasar la cantidad en la que se quiere incrementar el stock
    int newQuantityReceived = stockLot.getQuantityReceived() + quantity;
    int newQuantityAvailable = stockLot.getQuantityAvailable() + quantity;

    stockLot.setQuantityReceived(newQuantityReceived);
    stockLot.setQuantityAvailable(newQuantityAvailable);
    stockLotRepository.save(stockLot);

    // Actualiza las cantidades del stock
    Integer sumatoryStockQuantityReceived = stockLotRepository.sumQuantityReceivedByProductId(idProduct);
    Integer sumatoryStockQuantityAvailable = stockLotRepository.sumQuantityAvailableByProductId(idProduct);

    product.setTotalQuantityReceived(sumatoryStockQuantityReceived);
    product.setTotalQuantityAvailable(sumatoryStockQuantityAvailable);

    productRepository.save(product);
    
    // GUARDAR EL MOVIMIENTO DE TIPO ADD
    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(stockLotAdjustmentRequest.getComment());
    movement.setMovementType(MovementType.ADD);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(stockLot);
    movement.setDeliveryLine(null);
    movement.setUser(user);
    movement.setProduct(product);

    movementRepository.save(movement);
  }


  @Override
  public void decreaseStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user) {
    if (id_user == null || idStockLot == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = stockLotAdjustmentRequest.getQuantity();

    // TODO: VALIDAR ESTO
    // if (quantity <= 0) {
    //   throw new BusinessException(ResponseStatus.CONFLICT, "La cantidad debe ser mayor a 0");
    // }

    StockLot stockLot = stockLotRepository.findById(
        idStockLot).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock no existe"));

    // La nueva cantidad de stock se resta
    int newQuantityAvailable = stockLot.getQuantityAvailable() - quantity;

    if (newQuantityAvailable < 0) {
      throw new BusinessException(ResponseStatus.CONFLICT, "Stock insuficiente");
    }

    // La cantidad disponible se actualiza
    stockLot.setQuantityAvailable(newQuantityAvailable);

    if (newQuantityAvailable == 0) {
      stockLot.setZeroStock(true);
    }

    stockLotRepository.save(stockLot);

    // Obtener el producto del stockLot
    Long idProduct = stockLot.getProduct().getId();

    if (idProduct == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(idProduct).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe"));

    // Actualiza las cantidades del stock
    Integer sumatoryStockQuantityAvailable = stockLotRepository.sumQuantityAvailableByProductId(idProduct);

    product.setTotalQuantityAvailable(sumatoryStockQuantityAvailable);

    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(stockLotAdjustmentRequest.getComment());
    movement.setMovementType(MovementType.LOSS);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(stockLot);
    movement.setDeliveryLine(null);
    movement.setUser(user);
    movement.setProduct(product);
    movementRepository.save(movement);
  }


  @Override
  public void recoveryStockLot(Long idStockLot, StockLotAdjustmentRequest stockLotAdjustmentRequest, Long id_user) {
    if (id_user == null || idStockLot == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = stockLotAdjustmentRequest.getQuantity();

    // TODO: VALIDAR ESTO
    // if (quantity <= 0) {
    //   throw new BusinessException(ResponseStatus.CONFLICT, "La cantidad debe ser mayor a 0");
    // }


    StockLot stockLot = stockLotRepository.findById(
        idStockLot).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock emisor no existe"));

    // IMPLEMENTAR UNA LOGICA PARA OBTENER EL ULTIMO MOVIMIENTO DE TIPO LOSS DE UN
    // PRODUCTO

    // 1° DEBE OBTENER TODOS LOS MOVIMIENTOS DE TIPO LOSS DEL PRODUCTO POR SU ID
    // 2° CALCULAR LA SUMATORIA DEL CAMPO QUANTITY DE LOS MOVIMIENTOS DE TIPO LOSS
    // DE ESE PRODUCTO
    // 3° ESE VALOR RESULTANTE DEBE SER MENOR O IGUAL QUE LA CANTIDAD INTRODUCIDA
    // PARA EL MOVIMIENTO DE TIPO RECOVERY

    Integer totalQuantityLoss = movementRepository.sumQuantityByProductAndType(
        stockLot.getProduct().getId(),
        MovementType.LOSS);

    Integer totalQuantityRecovery = movementRepository.sumQuantityByProductAndType(
        stockLot.getProduct().getId(),
        MovementType.RECOVERY);

    if (totalQuantityLoss == 0) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "No existen pérdidas registradas para este producto");
    }

    int maxRecoverable = totalQuantityLoss - totalQuantityRecovery;

    if (quantity > maxRecoverable) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "No se puede recuperar más stock del que ha sido reportado como pérdida");
    }

    // La nueva cantidad de stock se aumenta
    int newQuantityAvailable = stockLot.getQuantityAvailable() + quantity;

    // La cantidad disponible se actualiza
    stockLot.setQuantityAvailable(newQuantityAvailable);

    if (newQuantityAvailable != 0) {
      stockLot.setZeroStock(false);
    }

    stockLotRepository.save(stockLot);

    Long idProduct = stockLot.getProduct().getId();

    if (idProduct == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(idProduct).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe"));

    Integer sumatoryStockQuantityAvailable = stockLotRepository.sumQuantityAvailableByProductId(idProduct);
    product.setTotalQuantityAvailable(sumatoryStockQuantityAvailable);
    productRepository.save(product);

    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(stockLotAdjustmentRequest.getComment());
    movement.setMovementType(MovementType.RECOVERY);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(stockLot);
    movement.setDeliveryLine(null);
    movement.setProduct(product);
    movement.setUser(user);
    movementRepository.save(movement);
  }


  @Override
  public void transferStockLot(Long idStockLotEmitter, StockLotTransferRequest stockLotTransferRequest, Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    // TODO: VALIDAR ESTO
    Integer quantity = stockLotTransferRequest.getQuantity();

    // if (quantity <= 0) {
    //   throw new BusinessException(ResponseStatus.CONFLICT, "La cantidad debe ser mayor a 0");
    // }

    Long id_stock_lot_receiver = stockLotTransferRequest.getIdStockLotReceiver();

    if (idStockLotEmitter == null || id_stock_lot_receiver == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Encontrar el producto por id de stockLot
    StockLot stockLotEmitter = stockLotRepository.findById(
        idStockLotEmitter).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock emisor no existe"));
    StockLot stockLotReceiver = stockLotRepository.findById(id_stock_lot_receiver).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock receptor no existe"));

    int newAvailableEmitter = stockLotEmitter.getQuantityAvailable() - quantity;
    int newAvailableReceiver = stockLotReceiver.getQuantityAvailable() + quantity;

    if (newAvailableEmitter < 0) {
      throw new BusinessException(ResponseStatus.CONFLICT, "Stock insuficiente del lote de stock emisor");
    }

    Long id_product_emitter = stockLotEmitter.getProduct().getId();
    Long id_product_receiver = stockLotReceiver.getProduct().getId();

    if (id_product_emitter == null || id_product_receiver == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Verificar que ambos lotes correspondan al mismo producto
    if (!id_product_emitter.equals(id_product_receiver)) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "Los lotes de stock deben pertenecer al mismo producto");
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

    Product productReceiver = productRepository.findById(id_product_receiver).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto receptor no existe"));

    // La sumatoria de la cantidad disponible de un producto no se actualiza, porque no se actualiza el total de stock y solamente se hace una transferencia entre 2 lotes de stocks que pertenecen al mismo producto

    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(stockLotTransferRequest.getComment());
    movement.setMovementType(MovementType.TRANSFER);
    movement.setStockLotEmitter(stockLotEmitter);
    movement.setStockLotReceiver(stockLotReceiver);
    movement.setDeliveryLine(null);
    movement.setProduct(productReceiver);
    movement.setUser(user);
    movementRepository.save(movement);
  }
}
