package com.pe.inventoryapp.backend.movement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.request.MovementAdjustmentRequest;
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
import com.pe.inventoryapp.backend.user.service.UserService;

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

    // StockLot stockLot = stockLotRepository.findById(movementRequest.getIdStockLot())
    //     .orElseThrow(() -> new RuntimeException("StockLot no existe"));

    // MovementType movementType = movementRequest.getMovementType();
    // Reason reason = movementRequest.getReason();
    // Long idStockLot = movementRequest.getIdStockLot();
    // Long idDeliveryLine = movementRequest.getIdDeliveryLine();

    // Movement movement = new Movement();
    // movement.setQuantity(movementRequest.getQuantity());
    // movement.setComment(movementRequest.getComment());
    // movement.setMovementType(movementRequest.getMovementType());
    // movement.setReason(movementRequest.getReason());
    // movement.setDeliveryLine(deliveryLine);
    // movement.setReason(reason);
    // movement.setMovementType(movementType);

    // // TODO: CORREGIR ESTA LINEA (LLAMAR A LA ENTIDAD POR ID)
    // movement.setStockLot(stockLot);

    // // AQUI SE HACEN LAS MODIFICACIONES RESPECTIVAS
    // // SALIDA
    // if (movementType.equals(MovementType.OUT) && reason.equals(Reason.DELIVERY)) {
    //   // Cantidad entregada
    //   deliveryLine.setDeliveredQuantity(movementRequest.getQuantity());
    //   // Cantidad pendiente
    //   deliveryLine.setPendingQuantity(deliveryLine.getRequiredQuantity() - deliveryLine.getDeliveredQuantity());
    //   deliveryLine.setUpdatedAt(LocalDateTime.now());

    //   // Si la cantidad entregada es igual a la cantidad requerida
    //   if (deliveryLine.getPendingQuantity() == 0) {
    //     deliveryLine.setPreparationStatus(PreparationStatus.READY);
    //   }

    // }

    // movementRepository.save(movement);



}
