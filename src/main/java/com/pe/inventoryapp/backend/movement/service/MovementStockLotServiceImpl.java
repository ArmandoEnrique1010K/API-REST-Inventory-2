package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.request.MovementAdjustmentRequest;
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
public class MovementStockLotServiceImpl implements MovementStockLotService{
  @Autowired
  private MovementRepository movementRepository;

  @Autowired
  private StockLotRepository stockLotRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CompanyRepository companyRepository;

  // MOVIMIENTO DE AGREGAR STOCK A UN PRODUCTO EXISTENTE EN EL ALMACEN
  @Override
  public void saveMovementReceive(MovementReceiveRequest movementReceiveRequest, Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = movementReceiveRequest.getQuantity();

    if (quantity <= 0) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La cantidad debe ser mayor a 0");
    }

    Long id_product = movementReceiveRequest.getIdProduct();

    if (id_product == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = productRepository.findById(id_product)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));

    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "El producto se encuentra desactivado");
    }

    Long id_company = movementReceiveRequest.getIdCompany();

    if (id_company == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Company company = companyRepository.findById(id_company)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El nombre de la empresa no existe"));

    // Fecha de hoy por partes
    LocalDateTime now = LocalDateTime.now();
    String date = now.getDayOfMonth() + "/" + now.getMonthValue() + "/" + now.getYear();
    String time = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();

    // Generar automaticamente el batch
    String batch = "LOT-" + product.getName().replace(" ", "-") + "-" + date + "-" + time;

    StockLot stockLot = new StockLot();
    stockLot.setBatch(batch);
    stockLot.setQuantityReceived(quantity);
    stockLot.setQuantityAvailable(quantity);
    // El total entregado es 0 porque aun no se ha entregado stock
    stockLot.setDeliveredTotal(0);
    // Indica si el stock es cero
    stockLot.setZeroStock(false);
    stockLot.setProduct(product);
    stockLot.setCompany(company);

    stockLotRepository.save(stockLot);

    Integer sumatoryStock = stockLotRepository.sumAvailableByProductId(id_product);
    product.setTotalQuantityAvailable(sumatoryStock);

    productRepository.save(product);

    Movement movement = new Movement();

    movement.setQuantity(quantity);
    movement.setComment(movementReceiveRequest.getComment());
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
  public void saveMovementIncrease(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = movementAdjustmentRequest.getQuantity();

    if (quantity <= 0) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La cantidad debe ser mayor a 0");
    }

    Long id_stock_lot = movementAdjustmentRequest.getIdStockLot();

    if (id_stock_lot == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Encontrar el producto por id de stockLot
    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock no existe"));

    // Se debe pasar la cantidad en la que se quiere incrementar el stock
    int newQuantityAvailable = stockLot.getQuantityAvailable() + quantity;

    stockLot.setQuantityAvailable(newQuantityAvailable);
    stockLot.setQuantityReceived(stockLot.getQuantityReceived() + quantity);
    stockLotRepository.save(stockLot);

    // Obtener el producto del stockLot
    Long idProduct = stockLot.getProduct().getId();

    if (idProduct == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    } 

    Product product = productRepository.findById(idProduct).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe")
    );
    
    product.setTotalQuantityAvailable(stockLotRepository.sumAvailableByProductId(idProduct));
    productRepository.save(product);

    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(movementAdjustmentRequest.getComment());
    movement.setMovementType(MovementType.ADD);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(stockLot);
    movement.setDeliveryLine(null);
    movement.setUser(user);
    movement.setProduct(product);

    movementRepository.save(movement);
  }

  @Override
  public void saveMovementDecrease(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = movementAdjustmentRequest.getQuantity();

    if (quantity <= 0) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La cantidad debe ser mayor a 0");
    }

    Long id_stock_lot = movementAdjustmentRequest.getIdStockLot();

    if (id_stock_lot == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock no existe"));

    // La nueva cantidad de stock se resta
    int newQuantityAvailable = stockLot.getQuantityAvailable() - quantity;

    if (newQuantityAvailable < 0) {
      throw new BusinessException(ResponseStatus.CONFLICT, "Stock insuficiente");
    }

    // La cantidad disponible se actualiza
    stockLot.setQuantityAvailable(newQuantityAvailable);

    if (newQuantityAvailable == 0){
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

    product.setTotalQuantityAvailable(stockLotRepository.sumAvailableByProductId(idProduct));
    productRepository.save(product);

    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(movementAdjustmentRequest.getComment());
    movement.setMovementType(MovementType.LOSS);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(stockLot);
    movement.setDeliveryLine(null);
    movement.setUser(user);
    movement.setProduct(product);
    movementRepository.save(movement);
  }

  @Override
  public void saveMovementRecovery(MovementAdjustmentRequest movementAdjustmentRequest, Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = movementAdjustmentRequest.getQuantity();

    if (quantity <= 0) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La cantidad debe ser mayor a 0");
    }

    Long id_stock_lot = movementAdjustmentRequest.getIdStockLot();

    if (id_stock_lot == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    StockLot stockLot = stockLotRepository.findById(id_stock_lot).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock emisor no existe"));

    // IMPLEMENTAR UNA LOGICA PARA OBTENER EL ULTIMO MOVIMIENTO DE TIPO LOSS DE UN PRODUCTO

    // 1° DEBE OBTENER TODOS LOS MOVIMIENTOS DE TIPO LOSS DEL PRODUCTO POR SU ID
    // 2° CALCULAR LA SUMATORIA DEL CAMPO QUANTITY DE LOS MOVIMIENTOS DE TIPO LOSS DE ESE PRODUCTO
    // 3° ESE VALOR RESULTANTE DEBE SER MENOR O IGUAL QUE LA CANTIDAD INTRODUCIDA PARA EL MOVIMIENTO DE TIPO RECOVERY
    Integer totalQuantityLoss = movementRepository.sumQuantityByProductAndType(
        stockLot.getProduct().getId(),
        MovementType.LOSS);

    Integer totalQuantityRecovery = movementRepository.sumQuantityByProductAndType(
        stockLot.getProduct().getId(),
        MovementType.RECOVERY 
    );

    // TODO: IMPLEMENTAR TAMBIEN LOS DE TIPO DAMAGE

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
    
    product.setTotalQuantityAvailable(stockLotRepository.sumAvailableByProductId(product.getId()));
    productRepository.save(product);

    Movement movement = new Movement();
    movement.setQuantity(quantity);
    movement.setComment(movementAdjustmentRequest.getComment());
    movement.setMovementType(MovementType.RECOVERY);
    movement.setStockLotEmitter(null);
    movement.setStockLotReceiver(stockLot);
    movement.setDeliveryLine(null);
    movement.setProduct(product);
    movement.setUser(user);
    movementRepository.save(movement);
  }

  @Override
  public void saveMovementTransfer(MovementTransferRequest movementTransferRequest, Long id_user) {
    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Integer quantity = movementTransferRequest.getQuantity();

    if (quantity <= 0) {
      throw new BusinessException(ResponseStatus.CONFLICT, "La cantidad debe ser mayor a 0");
    }

    Long id_stock_lot_emitter = movementTransferRequest.getIdStockLotEmitter();
    Long id_stock_lot_receiver = movementTransferRequest.getIdStockLotReceiver();

    if (id_stock_lot_emitter == null || id_stock_lot_receiver == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Encontrar el producto por id de stockLot
    StockLot stockLotEmitter = stockLotRepository.findById(id_stock_lot_emitter).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock emisor no existe")
    );
    StockLot stockLotReceiver = stockLotRepository.findById(id_stock_lot_receiver).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El lote de stock receptor no existe")
    );

    int newAvailableEmitter = stockLotEmitter.getQuantityAvailable() - movementTransferRequest.getQuantity();
    int newAvailableReceiver = stockLotReceiver.getQuantityAvailable() + movementTransferRequest.getQuantity();
    
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

    // Solamente si la cantidad del stock disponible es 0, se cambia el valor en zeroStock a true
    stockLotEmitter.setZeroStock(stockLotEmitter.getQuantityAvailable() == 0);
    stockLotReceiver.setZeroStock(stockLotReceiver.getQuantityAvailable() == 0);

    // Guarda los cambios en la base de datos
    stockLotRepository.save(stockLotEmitter);
    stockLotRepository.save(stockLotReceiver);

    Product productReceiver = productRepository.findById(id_product_receiver).orElseThrow(
      () -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto receptor no existe"));

    Movement movement = new Movement();
    movement.setQuantity(movementTransferRequest.getQuantity());
    movement.setComment(movementTransferRequest.getComment());
    movement.setMovementType(MovementType.TRANSFER);
    movement.setStockLotEmitter(stockLotEmitter);
    movement.setStockLotReceiver(stockLotReceiver);
    movement.setDeliveryLine(null);
    movement.setProduct(productReceiver);
    movement.setUser(user);
    movementRepository.save(movement);
    }
}
