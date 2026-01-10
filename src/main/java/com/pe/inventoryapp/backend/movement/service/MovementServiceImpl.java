package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.mapper.MovementMapper;
import com.pe.inventoryapp.backend.movement.model.request.MovementAllocateRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementReturnRequest;
import com.pe.inventoryapp.backend.movement.model.response.MovementListResponse;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
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

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private UserRepository userRepository;



  @Transactional
  @Override
  public void saveMovementAllocate(MovementAllocateRequest movementAllocateRequest, Long id_user) {

    // Verifica si se ha introducido una cantidad 0 o negativa
    if (movementAllocateRequest.getQuantity() <= 0) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La cantidad debe ser mayor a 0");
    }

    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));
    String username = user.getFirstname() + " " + user.getLastname();

    // Obtiene el lote de stock por id
    Long id_stock_lot = movementAllocateRequest.getIdStockLot();

    if (id_stock_lot == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock emisor no existe"));
    // Obtiene la linea de entrega por ID

    Long id_delivery_line = movementAllocateRequest.getIdDeliveryLine();

    if (id_delivery_line == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id_delivery_line).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La linea de entrega no existe"));
    
        // Verificar que corresponda al mismo producto
        if (stockLot.getProduct().getId() != deliveryLine.getProduct().getId()) {
          System.out.println("Lote de stock: " + stockLot.getProduct().getId() + " Linea de entrega: " + deliveryLine
              .getProduct().getId());
          throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,"El lote de stock y la linea de entrega deben pertenecer al mismo producto");
        }


    // Calcular la cantidad disponible
    int newAvailable = stockLot.getQuantityAvailable() - movementAllocateRequest.getQuantity();
    if (newAvailable < 0) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "Stock insuficiente");
    }
    stockLot.setQuantityAvailable(newAvailable);

    // Sumar la cantidad entregada
    stockLot.setQuantityDelivered(stockLot.getQuantityDelivered() + movementAllocateRequest.getQuantity());

    // TODO: NO FUNCIONO
    // List<DeliveryLine> deliveryLines = new ArrayList<>();
    // deliveryLines.add(deliveryLine);
    // stockLot.setDeliveryLines(deliveryLines);

    stockLotRepository.save(stockLot);


    // DEBE VERIFICARSE QUE LA LINEA DE ENTREGA TENGA EL ESTADO PENDING
    if (deliveryLine.getLineStatus() != LineStatus.PENDING) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
          "La linea de entrega no tiene el estado 'en progreso' de entrega");
    }

    // Calculo de la nueva cantidad pendiente
    int newPending = deliveryLine.getPendingQuantity() - movementAllocateRequest.getQuantity();
    if (newPending < 0) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
          "No se puede entregar esa cantidad porque excede a la cantidad requerida");
    }

    // SI SE HA COMPLETADO LA CANTIDAD REQUERIDA DE UNA LINEA DE ENTREGA, DEBE
    // CAMBIAR SU STATUS
    deliveryLine.setPendingQuantity(newPending);
    deliveryLine.setDeliveredQuantity(deliveryLine.getDeliveredQuantity() + movementAllocateRequest.getQuantity());

    if (deliveryLine.getPendingQuantity() == 0) {
      deliveryLine.setLineStatus(LineStatus.READY);
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
    product.setTotalQuantityAvailable(stockLotRepository.sumQuantityAvailableByProductId(product.getId()));

    productRepository.save(product);

    // MOVIMIENTO
    Movement movement = new Movement();
    movement.setComment(movementAllocateRequest.getComment());
    movement.setProduct(product);
    movement.setUser(user);
    movement.setMovementType(MovementType.ALLOCATE);
    movement.setStockLotReceiver(stockLot);
    movement.setQuantity(movementAllocateRequest.getQuantity());
    movement.setDeliveryLine(deliveryLine);
    movementRepository.save(movement);

  }

  // Movimiento de devolución de una linea de entrega con estado READY
  @Override
  @Transactional
  public void saveMovementReturn(MovementReturnRequest movementReturnRequest, Long id_user) {
    if (movementReturnRequest.getQuantity() <= 0) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La cantidad debe ser mayor a 0");
    }
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "El usuario no puede ser nulo");
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    // LINEA DE ENTREGA
    Long id_delivery_line = movementReturnRequest.getIdDeliveryLine();

    if (id_delivery_line == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id_delivery_line).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La linea de entrega no existe"));

    // DEBE VERIFICARSE QUE LA LINEA DE ENTREGA NO TENGA LOS ESTADOS MISSING NI CANCELED,
    // PUEDE TENER LOS ESTADOS READY, PENDING O DELIVERED
    if (deliveryLine.getLineStatus() != LineStatus.READY
        && deliveryLine.getLineStatus() != LineStatus.PENDING
        && deliveryLine.getLineStatus() != LineStatus.DELIVERED) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
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
        throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
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
      if (deliveryLine.getLineStatus() == LineStatus.DELIVERED) {
        throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
            "No se puede devolver porque esta línea ya fue entregada");
      }

      int newRequired = deliveryLine.getRequiredQuantity() - quantity;

      if (newRequired < deliveryLine.getDeliveredQuantity()) {
        throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
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
      deliveryLine.setLineStatus(LineStatus.DELIVERED);
    } else {
      deliveryLine.setLineStatus(LineStatus.PENDING);
    }

    deliveryLine.setUserUpdater(user);
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

      product.setTotalQuantityAvailable(
          stockLotRepository.sumQuantityAvailableByProductId(product.getId()));
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

      product.setTotalQuantityAvailable(
          stockLotRepository.sumQuantityAvailableByProductId(product.getId()));
      productRepository.save(product);

    }

    /*
     * =======================
     * MOVIMIENTO
     * =======================
     */
    Movement movement = new Movement();
    movement.setComment(movementReturnRequest.getComment());
    movement.setProduct(product);
    movement.setUser(user);
    movement.setQuantity(quantity);
    movement.setDeliveryLine(deliveryLine);
    movement.setStockLotReceiver(targetStockLot);
    movement.setMovementType(
        isReturnByChange
            ? MovementType.RETURN
            : MovementType.CHANGE);

    movementRepository.save(movement);
  }

  @Override
  public Page<MovementListResponse> findAllMovements(Integer minQuantity, Integer maxQuantity,
      LocalDateTime minCreatedAt, LocalDateTime maxCreatedAt, MovementType movementType, String username,
      String productName, Pageable pageable) {

      Page<Movement> movements = movementRepository.findAllByParams(minQuantity, maxQuantity, minCreatedAt,
          maxCreatedAt, movementType, username, productName, pageable); 
      
      return movements.map(movement -> MovementMapper.builder().setMovement(movement).buildMovementListResponse());
  }




  // DEFINIR METODOS PRIVADOS PARA
  // 1. EXTRAER EL NOMBRE DEL USAURIO QUE HA INICIADO SESION
  // 2. RECALCULAR EL TOTAL DEL STOCK
  // 3.

  // private String getUsernameExistence(Long id_user) {
  //   User user = userRepository.findById(id_user).orElseThrow(
  //       () -> new BusinessException(ResponseStatusCodes.NOT_FOUND, "El usuario no existe"));
  //   String username = user.getFirstname() + " " + user.getLastname();

  //   return username;
  // };
}


