package com.pe.inventoryapp.backend.movement.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatusCodes;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.delivery.model.data.PreparationStatus;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.delivery.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.delivery.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.data.Reason;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.request.MovementRequest;
import com.pe.inventoryapp.backend.movement.model.request.MovementSendRequest;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.stock.model.entity.Company;
import com.pe.inventoryapp.backend.stock.model.entity.StockLot;
import com.pe.inventoryapp.backend.stock.repository.CompanyRepository;
import com.pe.inventoryapp.backend.stock.repository.StockLotRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;
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
  private UserService userService;

  @Autowired
  private CompanyRepository companyRepository;

  // MOVIMIENTO DE AGREGAR AL STOCK UN PRODUCTO
  @Override
  public void saveMovementToStockLot(MovementSendRequest movementSendRequest, Long id_user) {

    DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
    String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "El usuario no existe")
    );

    Long id_product = movementSendRequest.getIdProduct();

    if (id_product == null) {
      throw new BusinessException(ResponseStatusCodes.COMMON_ERROR);
    }
    Product product = productRepository.findById(id_product)
        .orElseThrow(() -> new BusinessException(ResponseStatusCodes.ENTITY_NOT_FOUND, "La ubicación no existe"));

    // TODO: UNA OPERACIÓN PARA CALCULAR EL TOTAL DE STOCK SUMANDO LOS STOCKS DE LOS PRODUCTOS
    product.setStock(3000);

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

    movementRepository.save(movement);
  }
    // // Obtener la linea de entrega por id
    // DeliveryLine deliveryLine = deliveryLineRepository.findById(movementRequest.getIdDeliveryLine())
    //     .orElseThrow(() -> new RuntimeException("DeliveryLine no existe"));

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


  @Override
  public void movementToStockLot(MovementRequest movementRequest) {
  }

}
