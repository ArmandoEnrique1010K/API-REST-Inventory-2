package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryline.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.request.MovementAdjustmentRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementAllocateRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReturnRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReceiveRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementTransferRequest;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;
import com.pe.inventoryapp.backend.stocklot.repository.StockLotRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class MovementServiceImpl implements MovementService {

  @Autowired
  private DeliveryLineRepository deliveryLineRepository;

  @Autowired
  private MovementRepository movementRepository;

  @Autowired
  private StockLotRepository stockLotRepository;

  // @Autowired
  // private DeliveryOrderRepository deliveryOrderRepository;

  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private UserRepository userRepository;


  @Autowired
  private CompanyRepository companyRepository;

  // MOVIMIENTO DE AGREGAR STOCK A UN PRODUCTO EXISTENTE EN EL ALMACEN
  @Override
  public void saveMovementReceive(MovementReceiveRequest movementReceiveRequest, Long id_user) {
    if (movementReceiveRequest.getQuantity() <= 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La cantidad debe ser mayor a 0");
    }

    if (id_user == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }


    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));

    String username = user.getFirstname() + " " + user.getLastname();

    Long id_product = movementReceiveRequest.getIdProduct();

    if (id_product == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }
    Product product = productRepository.findById(id_product)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La ubicación no existe"));


    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "El producto se encuentra desactivado");
    }


    productRepository.save(product);

    Long id_company = movementReceiveRequest.getIdCompany();
    if (id_company == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    Company company = companyRepository.findById(id_company)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El nombre de la empresa no existe"));

    StockLot stockLot = new StockLot();
    stockLot.setBatch(movementReceiveRequest.getBatch());
    stockLot.setQuantityReceived(movementReceiveRequest.getQuantity());
    stockLot.setQuantityAvailable(movementReceiveRequest.getQuantity());
    // Logicamente el total entregado es 0 porque todavia no se ha entregado algo del stock
    stockLot.setDeliveredTotal(0);

    // Este campo indica si el stock es cero
    stockLot.setZeroStock(false);
    
    stockLot.setProduct(product);
    stockLot.setCompany(company);

    stockLotRepository.save(stockLot);


    // NOTA: se vuelve a llamar al producto para actualizar el stock
    Product findedProduct = productRepository.findById(id_product).get();
    // UNA OPERACIÓN PARA CALCULAR EL TOTAL DE STOCK SUMANDO LOS STOCKS DE LOS
    // PRODUCTOS
    Integer sumatoryStock = stockLotRepository.sumAvailableByProductId(id_product);
    System.out.println(sumatoryStock);

    findedProduct.setStock(sumatoryStock);

    productRepository.save(findedProduct);


    Movement movement = new Movement();

    movement.setQuantity(movementReceiveRequest.getQuantity());
    movement.setUsername_snapshot(username);
    movement.setComment(movementReceiveRequest.getComment());
    movement.setProduct(product);
    movement.setStockLot(stockLot);
    movement.setUser(user);

    movement.setDeliveryLine(null);

    // No se guarda el ID de deliveryLine porque no se trata de una linea entrega
    movement.setMovementType(MovementType.RECEIVE);

    movementRepository.save(movement);
  }

  @Override
  public void saveMovementAdjustmentIncrease(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user) {
    if (movementAdjustmentRequest.getQuantity() <= 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La cantidad debe ser mayor a 0");
    }

    if (id_user == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    Long id_stock_lot = movementAdjustmentRequest.getIdStockLot();

    if (id_stock_lot == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    // Encontrar el producto por id de stockLot
    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock no existe"));

    // Se debe pasar la cantidad en la que se quiere incrementar el stock
    int newAvailable = stockLot.getQuantityAvailable() + movementAdjustmentRequest.getQuantity();

    stockLot.setQuantityAvailable(newAvailable);
    stockLot.setQuantityReceived(stockLot.getQuantityReceived() + movementAdjustmentRequest.getQuantity());
    stockLotRepository.save(stockLot);

    Product product = stockLot.getProduct();
    product.setStock(stockLotRepository.sumAvailableByProductId(product.getId()));
    productRepository.save(product);

    Movement movement = new Movement();
    movement.setQuantity(movementAdjustmentRequest.getQuantity());
    movement.setUsername_snapshot(username);
    movement.setComment(movementAdjustmentRequest.getComment());
    movement.setUser(user);
    movement.setProduct(product);
    movement.setMovementType(MovementType.ADD);

    movement.setStockLot(stockLot);
    movementRepository.save(movement);
  }

  @Override
  public void saveMovementAdjustmentLoss(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user) {
    if (movementAdjustmentRequest.getQuantity() <= 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La cantidad debe ser mayor a 0");
    }

    if (id_user == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    Long id_stock_lot = movementAdjustmentRequest.getIdStockLot();

    if (id_stock_lot == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock emisor no existe"));

    // La nueva cantidad de stock se resta
    int newStock = stockLot.getQuantityAvailable() - movementAdjustmentRequest.getQuantity();

    if (newStock < 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "Stock insuficiente");
    }

    // La cantidad disponible se actualiza
    stockLot.setQuantityAvailable(newStock);

    if (newStock == 0){
      stockLot.setZeroStock(true);
    }

    stockLotRepository.save(stockLot);

    Product product = stockLot.getProduct();

    // UNA OPERACIÓN PARA CALCULAR EL TOTAL DE STOCK SUMANDO LOS STOCKS DE LOS
    // PRODUCTOS
    product.setStock(stockLotRepository.sumAvailableByProductId(product.getId()));

    productRepository.save(product);

    Movement movement = new Movement();
    movement.setUsername_snapshot(username);
    movement.setComment(movementAdjustmentRequest.getComment());
    movement.setProduct(product);
    movement.setUser(user);
    movement.setMovementType(MovementType.LOSS);
    movement.setStockLot(stockLot);
    movement.setQuantity(movementAdjustmentRequest.getQuantity());
    movementRepository.save(movement);
  }

  @Override
  public void saveMovementAdjustmentRecovery(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user) {
    if (movementAdjustmentRequest.getQuantity() <= 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La cantidad debe ser mayor a 0");
    }

    if (id_user == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    Long id_stock_lot = movementAdjustmentRequest.getIdStockLot();

    if (id_stock_lot == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock emisor no existe"));

    // DEBE HACER UNA COMPARACION PARA SABER SI LA SUMA DE LA CANTIDAD DISPONIBLE Y EL TOTAL ENTREGADO NO ES IGUAL A LA CANTIDAD RECIBIDA
    if (stockLot.getQuantityAvailable() + movementAdjustmentRequest.getQuantity() != stockLot.getQuantityReceived()) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "No hubo una perdida en este lote de stock");
    }

    // La nueva cantidad de stock se aumenta
    int newStock = stockLot.getQuantityAvailable() + movementAdjustmentRequest.getQuantity();

    // La cantidad disponible se actualiza
    stockLot.setQuantityAvailable(newStock);

    if (newStock != 0) {
      stockLot.setZeroStock(false);
    }

    stockLotRepository.save(stockLot);

    Product product = stockLot.getProduct();

    // UNA OPERACIÓN PARA CALCULAR EL TOTAL DE STOCK SUMANDO LOS STOCKS DE LOS
    // PRODUCTOS
    product.setStock(stockLotRepository.sumAvailableByProductId(product.getId()));

    productRepository.save(product);

    Movement movement = new Movement();
    movement.setUsername_snapshot(username);
    movement.setComment(movementAdjustmentRequest.getComment());
    movement.setProduct(product);
    movement.setUser(user);
    movement.setMovementType(MovementType.RECOVERY);
    movement.setStockLot(stockLot);
    movement.setQuantity(movementAdjustmentRequest.getQuantity());
    movementRepository.save(movement);

  }


  // @Override
  // public void saveMovementAdjustment(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user) {
  //   if (id_user == null) {
  //     throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
  //   }

  //   User user = userRepository.findById(id_user).orElseThrow(
  //       () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
  //   String username = user.getFirstname() + " " + user.getLastname();

  //   Long id_stock_lot = movementAdjustmentRequest.getIdStockLot();

  //   if (id_stock_lot == null) {
  //     throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
  //   }

  //   // Encontrar el producto por id de stockLot
  //   StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
  //       () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock no existe")
  //   );
  //   if (movementAdjustmentRequest.getQuantity() == 0){
  //     throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La cantidad debe ser mayor a 0");
  //   }

    
  //   // Se debe pasar la cantidad en la que se quiere incrementar o decrementar el stock
  //   int newAvailable = stockLot.getQuantityAvailable() + movementAdjustmentRequest.getQuantity();
    
  //   if (newAvailable < 0)
  //     throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,"Stock insuficiente");

  //   stockLot.setQuantityAvailable(newAvailable);

  //   if (movementAdjustmentRequest.isAlterQuantityReceived()) {
  //     stockLot.setQuantityReceived(stockLot.getQuantityReceived() + movementAdjustmentRequest.getQuantity());
  //   }
    
    
  //   // No se va a alterar el total entregado
  //   stockLotRepository.save(stockLot);
    
  //  // NOTA: POSIBLE ERROR DE REFERENCIA CIRCULAR!!!
  
  //   Product product = stockLot.getProduct();

  //   // UNA OPERACIÓN PARA CALCULAR EL TOTAL DE STOCK SUMANDO LOS STOCKS DE LOS PRODUCTOS
  //   // Debe recalcular el total de la sumatoria de los stocks disponibles del producto
  //   product.setStock(stockLotRepository.sumAvailableByProductId(product.getId()));

  //   productRepository.save(product);
    
  //   Movement movement = new Movement();
  //   movement.setQuantity(movementAdjustmentRequest.getQuantity());
  //   movement.setUsername_snapshot(username);
  //   movement.setComment(movementAdjustmentRequest.getComment());
  //   movement.setUser(user);
  //   movement.setProduct(product);

  //   // ESTE CAMPO SE PODRIA MODIFICAR SI SE TRATA DE ALTERAR LA CANTIDAD RECIBIDA
  //   if (movementAdjustmentRequest.isAlterQuantityReceived()) {
  //     movement.setMovementType(MovementType.ADJUSTMENT_QUANTITY_RECEIVED);
  //   } else {
  //     movement.setMovementType(MovementType.ADJUSTMENT_QUANTITY_AVAILABLE);
  //   }


  //   movement.setStockLot(stockLot);
  //   movementRepository.save(movement);
  // }

  @Override
  public void saveMovementTransfer(MovementTransferRequest movementTransferRequest, Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    Long id_stock_lot_emitter = movementTransferRequest.getIdStockLotEmitter();
    Long id_stock_lot_receiver = movementTransferRequest.getIdStockLotReceiver();

    if (id_stock_lot_emitter == null || id_stock_lot_receiver == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

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
    if (!stockLotEmitter.getProduct().getId()
        .equals(stockLotReceiver.getProduct().getId())) {
      throw new BusinessException(
          ResponseStatusCodes.DEFAULT_RESOURCE,
          "Los lotes deben pertenecer al mismo producto");
    }

    if (newAvailableEmitter < 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,"Stock insuficiente del lote de stock emisor");
    }

    stockLotEmitter.setQuantityAvailable(newAvailableEmitter);
    stockLotReceiver.setQuantityAvailable(newAvailableReceiver);
    // if (stockLotEmitter.getQuantityAvailable() == 0) {
    //   stockLotEmitter.setZeroStock(true);
    // } 
    
    // if (stockLotEmitter.getQuantityAvailable() > 0){
    //   stockLotEmitter.setZeroStock(false);
    // }
    stockLotEmitter.setZeroStock(
        stockLotEmitter.getQuantityAvailable() == 0);

    // NOTA: NO SE RECALCULA EL TOTAL DE STOCK PORQUE ES UNA TRANSFERENCIA DE STOCK

    // Guarda los cambios en la base de datos
    stockLotRepository.save(stockLotEmitter);
    stockLotRepository.save(stockLotReceiver);

    // NOTA: SE TOMA EL PRODUCTO DEL EMISOR, PORQUE ES EL MISMO QUE EL RECEPTOR
    Product product = stockLotEmitter.getProduct();

    Movement movement = new Movement();
    movement.setUsername_snapshot(username);
    
    movement.setComment(movementTransferRequest.getComment());
    
    // movement.setComment(movementTransferRequest.getComment());
    movement.setProduct(product);
    movement.setUser(user);
    movement.setMovementType(MovementType.TRANSFER);
    // NOTA: SE TOMA EL STOCKLOT DEL RECEPTOR
    movement.setStockLot(stockLotReceiver);
    movement.setStockLotEmitter(stockLotEmitter);
    movement.setQuantity(movementTransferRequest.getQuantity());
    movementRepository.save(movement);
  }

  // Movimiento de perdida del almacen
  // @Override
  // public void saveMovementLoss(MovementLossRequest movementLossRequest, Long id_user) {
  //   if (id_user == null) {
  //     throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
  //   }

  //   User user = userRepository.findById(id_user).orElseThrow(
  //       () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
  //   String username = user.getFirstname() + " " + user.getLastname();

  //   Long id_stock_lot = movementLossRequest.getIdStockLot();

  //     if (id_stock_lot == null) {
  //       throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
  //     }

  //   StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
  //       () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock emisor no existe"));
  //   int newStock = stockLot.getQuantityAvailable() - movementLossRequest.getQuantity();

  //   if (newStock < 0) {
  //     throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "Stock insuficiente");
  //   }

  //   stockLot.setQuantityAvailable(newStock);
  //   stockLotRepository.save(stockLot);

  //   Product product = stockLot.getProduct();

  //   // UNA OPERACIÓN PARA CALCULAR EL TOTAL DE STOCK SUMANDO LOS STOCKS DE LOS
  //   // PRODUCTOS
  //   product.setStock(stockLotRepository.sumAvailableByProductId(product.getId()));

  //   productRepository.save(product);

  //   Movement movement = new Movement();
  //   movement.setUsername_snapshot(username);
  //   movement.setComment(movementLossRequest.getComment());
  //   movement.setProduct(product);
  //   movement.setUser(user);
  //   movement.setMovementType(MovementType.LOSS);
  //   movement.setStockLot(stockLot);
  //   movement.setQuantity(movementLossRequest.getQuantity());
  //   movementRepository.save(movement);

  // }


  // TODO: HAY UN PROBLEMA EN LA RELACION DE ENTIDADES, DEBERIA SER DE MUCHOS A MUCHOS ENTRE STOCKLOT Y DELIVERYLINE PARA GUARDAR LOS IDS DE LOS LOTES DE STOCK EN LA LINEA DE ENTREGA

  @Transactional
  @Override
  public void saveMovementAllocate(MovementAllocateRequest movementAllocateRequest, Long id_user) {

    // Verifica si se ha introducido una cantidad 0 o negativa
    if (movementAllocateRequest.getQuantity() <= 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La cantidad debe ser mayor a 0");
    }

    if (id_user == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    // Obtiene el lote de stock por id
    Long id_stock_lot = movementAllocateRequest.getIdStockLot();

    if (id_stock_lot == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El lote de stock emisor no existe"));
    // Obtiene la linea de entrega por ID

    Long id_delivery_line = movementAllocateRequest.getIdDeliveryLine();

    if (id_delivery_line == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id_delivery_line).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La linea de entrega no existe"));
    
        // Verificar que corresponda al mismo producto
        if (stockLot.getProduct().getId() != deliveryLine.getProduct().getId()) {
          System.out.println("Lote de stock: " + stockLot.getProduct().getId() + " Linea de entrega: " + deliveryLine
              .getProduct().getId());
          throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,"El lote de stock y la linea de entrega deben pertenecer al mismo producto");
        }


    // Calcular la cantidad disponible
    int newAvailable = stockLot.getQuantityAvailable() - movementAllocateRequest.getQuantity();
    if (newAvailable < 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "Stock insuficiente");
    }
    stockLot.setQuantityAvailable(newAvailable);

    // Sumar la cantidad entregada
    stockLot.setDeliveredTotal(stockLot.getDeliveredTotal() + movementAllocateRequest.getQuantity());

    // TODO: NO FUNCIONO
    // List<DeliveryLine> deliveryLines = new ArrayList<>();
    // deliveryLines.add(deliveryLine);
    // stockLot.setDeliveryLines(deliveryLines);

    stockLotRepository.save(stockLot);


    // DEBE VERIFICARSE QUE LA LINEA DE ENTREGA TENGA EL ESTADO INPROGRESS
    if (deliveryLine.getPreparationStatus() != PreparationStatus.INPROGRESS) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,
          "La linea de entrega no tiene el estado 'en progreso' de entrega");
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
    
    // Si la cantidad pendiente es mayor que 0, se sobreentiende que el PrepartionStatus queda en INPROGRESS

    // deliveryLine.setStockLot(stockLot);

    // List<StockLot> stockLots = new ArrayList<>();
    // stockLots.add(stockLot);
    // deliveryLine.setStockLots(stockLots);

    deliveryLineRepository.save(deliveryLine);



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

  // Movimiento de devolución de una linea de entrega con estado READY
  @Override
  @Transactional
  public void saveMovementReturn(MovementReturnRequest movementReturnRequest, Long id_user) {
    if (movementReturnRequest.getQuantity() <= 0) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "La cantidad debe ser mayor a 0");
    }
    if (id_user == null) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE, "El usuario no puede ser nulo");
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    // LINEA DE ENTREGA
    Long id_delivery_line = movementReturnRequest.getIdDeliveryLine();

    if (id_delivery_line == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id_delivery_line).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La linea de entrega no existe"));

    // DEBE VERIFICARSE QUE LA LINEA DE ENTREGA NO TENGA LOS ESTADOS MISSING NI CANCELED,
    // PUEDE TENER LOS ESTADOS READY, INPROGRESS O DELIVERED
    if (deliveryLine.getPreparationStatus() != PreparationStatus.READY
        && deliveryLine.getPreparationStatus() != PreparationStatus.INPROGRESS
        && deliveryLine.getPreparationStatus() != PreparationStatus.DELIVERED) {
      throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,
          "La linea de entrega no tiene el estado 'listo', 'en progreso' o 'entregado' de entrega");
    }

    boolean isReturnByChange = movementReturnRequest.isReturnByChange();
    int quantity = movementReturnRequest.getQuantity();

    /*
     * =======================
     * CASO 1: RETORNO POR DAÑO
     * =======================
     */
    if (!isReturnByChange) {

      int newDelivered = deliveryLine.getDeliveredQuantity() - quantity;
      if (newDelivered < 0) {
        throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,
            "La devolución excede la cantidad entregada");
      }

      deliveryLine.setDeliveredQuantity(newDelivered);
      
      // List<StockLot> stockLots = new ArrayList<>();
      // stockLots.add(stockLot);
      // deliveryLine.setStockLots(stockLots);

      //  deliveryLine.getStockLots();
      // TODO: REPARAR LA RELACION ENTRE STOCKLOT Y DELIVERYLINE

      // Descuenta la cantidad entregada desde StockLot
      // deliveryLine.getStockLot().setDeliveredTotal(deliveryLine.getStockLot().getDeliveredTotal() - quantity);
    } else {
      /*
       * ============================
       * CASO 2: CAMBIO EN LA ORDEN
       * ============================
       */

     // NO SE ACEPTAN DEVOLUCIONES
      if (deliveryLine.getPreparationStatus() == PreparationStatus.DELIVERED) {
        throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,
            "No se puede devolver porque esta línea ya fue entregada");
      }

      int newRequired = deliveryLine.getRequiredQuantity() - quantity;

      if (newRequired < deliveryLine.getDeliveredQuantity()) {
        throw new BusinessException(ResponseStatusCodes.DEFAULT_RESOURCE,
            "La cantidad requerida no puede ser menor a la ya entregada");
      }

      deliveryLine.setRequiredQuantity(newRequired);
    }

    /*
     * =======================
     * CANTIDAD PENDIENTE
     * =======================
     */
    int pending = deliveryLine.getRequiredQuantity()
        - deliveryLine.getDeliveredQuantity();

    deliveryLine.setPendingQuantity(pending);
    
    /*
     * =======================
     * ESTADO
     * =======================
     */
    if (deliveryLine.getDeliveredQuantity() >= deliveryLine.getRequiredQuantity()) {
      deliveryLine.setPreparationStatus(PreparationStatus.DELIVERED);
    } else {
      deliveryLine.setPreparationStatus(PreparationStatus.INPROGRESS);
    }

    deliveryLine.setUpdatedByUser(username);
    deliveryLineRepository.save(deliveryLine);

    /*
     * =======================
     * STOCK (solo por daño)
     * =======================
     */
    StockLot targetStockLot = null;
    Product product = deliveryLine.getProduct();

    // TODO: CORREGIR AQUI, DEBE CREAR UN NUEVO LOTE DE STOCK PARA ALMACENAR LA CANTIDAD DEVUELTA
    if (!isReturnByChange) {

      LocalDateTime limit = LocalDateTime.now().minusHours(24);

      targetStockLot = stockLotRepository
          .findTopByProductIdAndCreatedAtAfterOrderByCreatedAtDesc(
              product.getId(), limit)
          .orElseGet(() -> {
            StockLot lot = new StockLot();
            lot.setBatch("Devolución por daño DL " + deliveryLine.getId());
            lot.setProduct(product);
            lot.setQuantityAvailable(0);
            lot.setQuantityReceived(0);
            return lot;
          });

      targetStockLot.setQuantityAvailable(
          targetStockLot.getQuantityAvailable() + quantity);
      targetStockLot.setQuantityReceived(
          targetStockLot.getQuantityReceived() + quantity);

      stockLotRepository.save(targetStockLot);

      product.setStock(
          stockLotRepository.sumAvailableByProductId(product.getId()));
      productRepository.save(product);
    } else {
      // TODO: CORREGIR AQUI, EL STOCK NO SE ACTUALIZA
      LocalDateTime limit = LocalDateTime.now().minusHours(24);

      targetStockLot = stockLotRepository
          .findTopByProductIdAndCreatedAtAfterOrderByCreatedAtDesc(
              product.getId(), limit)
          .orElseGet(() -> {
            StockLot lot = new StockLot();
            lot.setBatch("Devolución por orden de entrega " + deliveryLine.getId());
            lot.setProduct(product);
            lot.setQuantityAvailable(0);
            lot.setQuantityReceived(0);
            return lot;
          });

      targetStockLot.setQuantityAvailable(
          targetStockLot.getQuantityAvailable() + quantity);
      targetStockLot.setQuantityReceived(
          targetStockLot.getQuantityReceived() + quantity);

      stockLotRepository.save(targetStockLot);

      product.setStock(
          stockLotRepository.sumAvailableByProductId(product.getId()));
      productRepository.save(product);

    }

    /*
     * =======================
     * MOVIMIENTO
     * =======================
     */
    Movement movement = new Movement();
    movement.setUsername_snapshot(username);
    movement.setComment(movementReturnRequest.getComment());
    movement.setProduct(product);
    movement.setUser(user);
    movement.setQuantity(quantity);
    movement.setDeliveryLine(deliveryLine);
    movement.setStockLot(targetStockLot);
    movement.setMovementType(
        isReturnByChange
            ? MovementType.RETURN_BY_CHANGE
            : MovementType.RETURN_BY_DAMAGE);

    movementRepository.save(movement);
  }




  // DEFINIR METODOS PRIVADOS PARA
  // 1. EXTRAER EL NOMBRE DEL USAURIO QUE HA INICIADO SESION
  // 2. RECALCULAR EL TOTAL DEL STOCK
  // 3.

  // private String getUsernameExistence(Long id_user) {
  //   User user = userRepository.findById(id_user).orElseThrow(
  //       () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe"));
  //   String username = user.getFirstname() + " " + user.getLastname();

  //   return username;
  // };
}


