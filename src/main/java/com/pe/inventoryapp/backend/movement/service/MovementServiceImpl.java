package com.pe.inventoryapp.backend.movement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.request.MovementAdjustmentRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementAllocateRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementLossRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementSendRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementTransferRequest;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.stock.model.entity.Company;
import com.pe.inventoryapp.backend.stock.model.entity.StockLot;
import com.pe.inventoryapp.backend.stock.repository.CompanyRepository;
import com.pe.inventoryapp.backend.stock.repository.StockLotRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

// TODO: AUTOMATIZAR EL MOVIMIENTO
@Service
public class MovementServiceImpl implements MovementService {

  @Autowired
  private DeliveryLineRepository deliveryLineRepository;

  @Autowired
  private MovementRepository movementRepository;

  @Autowired
  private StockLotRepository stockLotRepository;

  @Autowired
  private DeliveryOrderRepository deliveryOrderRepository;

  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private UserRepository userRepository;


  @Autowired
  private CompanyRepository companyRepository;

  // MOVIMIENTO DE AGREGAR AL STOCK UN PRODUCTO
  @Override
  public void saveMovementSend(MovementSendRequest movementSendRequest, Long id_user) {

    // TODO: VERIFICAR SI ESTO CONVIENE USARLO  O NO
    // DetailUserResponse detailsUserResponse = userService.findUserById(id_user);

    // User user = userRepository.findById(id_user).orElseThrow(
    //     () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe")
    // );


    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));

    String username = user.getFirstname() + " " + user.getLastname();

    Long id_product = movementSendRequest.getIdProduct();

    if (id_product == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }
    Product product = productRepository.findById(id_product)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La ubicación no existe"));

    // TODO: UNA OPERACIÓN PARA CALCULAR EL TOTAL DE STOCK SUMANDO LOS STOCKS DE LOS PRODUCTOS
    product.setStock(stockLotRepository.sumAvailableByProductId(id_product));

    productRepository.save(product);

    Long id_company = movementSendRequest.getIdCompany();
    if (id_company == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Company company = companyRepository.findById(id_company)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El nombre de la empresa no existe"));

    StockLot stockLot = new StockLot();
    stockLot.setBatch(movementSendRequest.getBatch());
    stockLot.setQuantityReceived(movementSendRequest.getQuantity());
    stockLot.setQuantityAvailable(movementSendRequest.getQuantity());
    stockLot.setDeliveredTotal(0);
    stockLot.setProduct(product);
    stockLot.setCaducityDate(movementSendRequest.getCaducityDate());
    stockLot.setCompany(company);

    stockLotRepository.save(stockLot);

    Movement movement = new Movement();

    movement.setQuantity(movementSendRequest.getQuantity());
    movement.setUsername_snapshot(username);
    movement.setComment(movementSendRequest.getComment());
    movement.setProduct(product);
    movement.setStockLot(stockLot);
    movement.setUser(user);
    movement.setMovementType(MovementType.SEND);

    movementRepository.save(movement);
  }

  @Override
  public void saveMovementAdjustment(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user) {

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    Long id_stock_lot = movementAdjustmentRequest.getIdStockLot();

    // Encontrar el producto por id de stockLot
    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock no existe")
    );

    int newAvailable = stockLot.getQuantityAvailable() + movementAdjustmentRequest.getQuantity();


    Product product = stockLot.getProduct();

    // UNA OPERACIÓN PARA CALCULAR EL TOTAL DE STOCK SUMANDO LOS STOCKS DE LOS PRODUCTOS
    product.setStock(stockLotRepository.sumAvailableByProductId(product.getId()));

    productRepository.save(product);


    // Se debe pasar la cantidad en la que se quiere incrementar o decrementar el stock
    if (newAvailable < 0)
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,"Stock insuficiente");

    stockLot.setQuantityAvailable(newAvailable);

    if (movementAdjustmentRequest.isAlterQuantityReceived()) {
      stockLot.setQuantityReceived(stockLot.getQuantityReceived() + movementAdjustmentRequest.getQuantity());
    }
    stockLot.setCaducityDate(movementAdjustmentRequest.getCaducityDate());
    stockLotRepository.save(stockLot);

    Movement movement = new Movement();
    movement.setQuantity(movementAdjustmentRequest.getQuantity());
    movement.setUsername_snapshot(username);
    movement.setComment(movementAdjustmentRequest.getComment());
    movement.setUser(user);
    movement.setProduct(product);

    // ESTE CAMPO SE PODRIA MODIFICAR SI SE TRATA DE ALTERAR LA CANTIDAD RECIBIDA
    if (movementAdjustmentRequest.isAlterQuantityReceived()) {
      movement.setMovementType(MovementType.ADJUSTMENT_QUANTITY_RECEIVED);
    } else {
      movement.setMovementType(MovementType.ADJUSTMENT_QUANTITY_AVAILABLE);
    }


    movement.setStockLot(stockLot);
    movementRepository.save(movement);
  }
  // // Obtener la linea de entrega por id
  // DeliveryLine deliveryLine =
  // deliveryLineRepository.findById(movementRequest.getIdDeliveryLine())
  // .orElseThrow(() -> new RuntimeException("DeliveryLine no existe"));

  @Override
  public void saveMovementTransfer(MovementTransferRequest movementTransferRequest, Long id_user) {
    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    Long id_stock_lot_emitter = movementTransferRequest.getIdStockLotEmitter();
    Long id_stock_lot_receiver = movementTransferRequest.getIdStockLotReceiver();

    // Encontrar el producto por id de stockLot
    StockLot stockLotEmitter = stockLotRepository.findById(id_stock_lot_emitter).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock emisor no existe")
    );
    StockLot stockLotReceiver = stockLotRepository.findById(id_stock_lot_receiver).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock receptor no existe")
    );

    int newAvailableEmitter = stockLotEmitter.getQuantityAvailable() - movementTransferRequest.getQuantity();
    int newAvailableReceiver = stockLotReceiver.getQuantityAvailable() + movementTransferRequest.getQuantity();

    // Verificar que ambos lotes correspondan al mismo producto
    if (stockLotEmitter.getProduct().getId() != stockLotReceiver.getProduct().getId())
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,"Los lotes deben pertenecer al mismo producto");

    if (newAvailableEmitter < 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,"Stock insuficiente");
    }

    stockLotEmitter.setQuantityAvailable(newAvailableEmitter);
    stockLotReceiver.setQuantityAvailable(newAvailableReceiver);

    // NOTA: NO SE RECALCULA EL TOTAL DE STOCK PORQUE ES UNA TRANSFERENCIA DE STOCK

    stockLotRepository.save(stockLotEmitter);
    stockLotRepository.save(stockLotReceiver);

    // NOTA: SE TOMA EL PRODUCTO DEL EMISOR, PORQUE ES EL MISMO QUE EL RECEPTOR
    Product product = stockLotEmitter.getProduct();

    Movement movement = new Movement();
    movement.setUsername_snapshot(username);
    movement.setComment(movementTransferRequest.getComment());
    movement.setProduct(product);
    movement.setUser(user);
    movement.setMovementType(MovementType.TRANSFER);
    // NOTA: SE TOMA EL STOCKLOT DEL RECEPTOR
    movement.setStockLot(stockLotReceiver);
    movement.setQuantity(movementTransferRequest.getQuantity());
    movementRepository.save(movement);
  }

  // Movimiento de perdida del almacen
  @Override
  public void saveMovementLoss(MovementLossRequest movementLossRequest, Long id_user) {
    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    Long id_stock_lot = movementLossRequest.getIdStockLot();
    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock emisor no existe"));
    int newAvailableEmitter = stockLot.getQuantityAvailable() - movementLossRequest.getQuantity();

    if (newAvailableEmitter < 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "Stock insuficiente");
    }

    stockLot.setQuantityAvailable(newAvailableEmitter);
    stockLotRepository.save(stockLot);

    Product product = stockLot.getProduct();

    // UNA OPERACIÓN PARA CALCULAR EL TOTAL DE STOCK SUMANDO LOS STOCKS DE LOS
    // PRODUCTOS
    product.setStock(stockLotRepository.sumAvailableByProductId(product.getId()));

    productRepository.save(product);

    Movement movement = new Movement();
    movement.setUsername_snapshot(username);
    movement.setComment(movementLossRequest.getComment());
    movement.setProduct(product);
    movement.setUser(user);
    movement.setMovementType(MovementType.LOSS);
    movement.setStockLot(stockLot);
    movement.setQuantity(movementLossRequest.getQuantity());
    movementRepository.save(movement);

  }

  @Transactional
  @Override
  public void saveMovementAllocate(MovementAllocateRequest movementAllocateRequest, Long id_user) {

    if (movementAllocateRequest.getQuantity() <= 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La cantidad debe ser mayor a 0");
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    // LINEA DE ENTREGA
    Long id_delivery_line = movementAllocateRequest.getIdDeliveryLine();
    DeliveryLine deliveryLine = deliveryLineRepository.findById(id_delivery_line).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La linea de entrega no existe"));

    // DEBE VERIFICARSE QUE LA LINEA DE ENTREGA ESTE PENDIENTE
    if (deliveryLine.getPreparationStatus() != PreparationStatus.INPROGRESS) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,
          "La linea de entrega no tiene el estado 'pendiente' de entrega");
    }

    // Calculo de la nueva cantidad pendiente
    int newPending = deliveryLine.getPendingQuantity() - movementAllocateRequest.getQuantity();
    if (newPending < 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,
          "No se puede entregar esa cantidad porque excede a la cantidad requerida");
    }

    // SI SE HA COMPLETADO LA CANTIDAD REQUERIDA DE UNA LINEA DE ENTREGA, DEBE
    // CAMBIAR SU STATUS
    deliveryLine.setPendingQuantity(newPending);
    deliveryLine.setDeliveredQuantity(deliveryLine.getDeliveredQuantity() + movementAllocateRequest.getQuantity());

    if (deliveryLine.getPendingQuantity() == 0) {
      deliveryLine.setPreparationStatus(PreparationStatus.READY);
    }

    deliveryLineRepository.save(deliveryLine);

    // LOTE DE STOCK
    Long id_stock_lot = movementAllocateRequest.getIdStockLot();
    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock emisor no existe"));

    // Calcular la cantidad disponible
    int newAvailableEmitter = stockLot.getQuantityAvailable() - movementAllocateRequest.getQuantity();
    if (newAvailableEmitter < 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "Stock insuficiente");
    }
    stockLot.setQuantityAvailable(newAvailableEmitter);
    stockLotRepository.save(stockLot);


    // PRODUCTO DESDE EL STOCKLOT
    Product product = stockLot.getProduct();

    // UNA OPERACIÓN PARA CALCULAR EL NUEVO TOTAL DE STOCK SUMANDO LOS STOCKS DE LOS
    // PRODUCTOS
    product.setStock(stockLotRepository.sumAvailableByProductId(product.getId()));

    productRepository.save(product);

    // MOVIMIENTO
    Movement movement = new Movement();
    movement.setUsername_snapshot(username);
    movement.setComment(movementAllocateRequest.getComment());
    movement.setProduct(product);
    movement.setUser(user);
    movement.setMovementType(MovementType.ALLOCATE);
    movement.setStockLot(stockLot);
    movement.setQuantity(movementAllocateRequest.getQuantity());
    movement.setDeliveryLine(deliveryLine);
    movementRepository.save(movement);

  }

}

// DEFINIR METODOS PRIVADOS PARA
// 1. EXTRAER EL NOMBRE DEL USAURIO QUE HA INICIADO SESION
// 2. RECALCULAR EL TOTAL DEL STOCK
// 3. 