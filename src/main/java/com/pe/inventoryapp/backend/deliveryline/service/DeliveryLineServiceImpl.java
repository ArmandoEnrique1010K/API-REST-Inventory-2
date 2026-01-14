package com.pe.inventoryapp.backend.deliveryline.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.exception.FieldValidation;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.model.mapper.DeliveryLineMapper;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineRequest;
import com.pe.inventoryapp.backend.deliveryline.model.request.DeliveryLineUpdateRequest;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineDetailsResponse;
import com.pe.inventoryapp.backend.deliveryline.model.response.DeliveryLineListResponse;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder_Region;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Product_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Product_DeliveryOrder_RegionRepository;
import com.pe.inventoryapp.backend.location.model.entity.Location;
import com.pe.inventoryapp.backend.location.repository.LocationRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;
import com.pe.inventoryapp.backend.stocklot.repository.StockLotRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class DeliveryLineServiceImpl implements DeliveryLineService {
  @Autowired
  private DeliveryLineRepository deliveryLineRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private DeliveryOrderRepository deliveryOrderRepository;

  @Autowired
  private MovementRepository movementRepository;

  @Autowired
  private Product_DeliveryOrderRepository product_DeliveryOrderRepository;

  @Autowired
  private Product_DeliveryOrder_RegionRepository product_DeliveryOrder_RegionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private StockLotRepository stockLotRepository;

  @Override
  public void saveDeliveryLine(DeliveryLineRequest deliveryLineRequest, Long id_product_deliveryOrder, Long id_user) {

    Long id_location = deliveryLineRequest.getIdLocation();

    if (id_location == null || id_product_deliveryOrder == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    Location location = locationRepository.findById(
        id_location).orElseThrow(
      () -> new BusinessException(ResponseStatus.NOT_FOUND, "La ubicación no existe"));

    // Obtener el producto y orden de entrega desde Product_DeliveryOrder
    Product_DeliveryOrder product_DeliveryOrder = product_DeliveryOrderRepository.findById(id_product_deliveryOrder)
         .orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "La relación de producto y orden de entrega no existe"));

    // Validar pertenencia real de producto y orden de entrega
    if (!product_DeliveryOrderRepository
        .existsByIdAndDeliveryOrderId(
            id_product_deliveryOrder,
            product_DeliveryOrder.getDeliveryOrder().getId())) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "El producto no pertenece a la orden de entrega");
    }

    Long id_deliveryOrder = product_DeliveryOrder.getDeliveryOrder().getId();
    Long id_product = product_DeliveryOrder.getProduct().getId();

    if (id_deliveryOrder == null || id_product == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Regla: no permitir duplicados por ubicación
    boolean exists = deliveryLineRepository
        .existsDuplicate(id_deliveryOrder, id_product, id_location);

    if (exists) {
      throw new BusinessException(
          ResponseStatus.CONFLICT,
          "Ya existe una línea de entrega para este producto en esta ubicación");
    }

    // Crear la linea de entrega
    DeliveryLine deliveryLine = new DeliveryLine();

    deliveryLine.setOriginalQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setRequiredQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setDeliveredQuantity(0);
    deliveryLine.setPendingQuantity(deliveryLineRequest.getRequiredQuantity());
    deliveryLine.setLimitDate(deliveryLineRequest.getLimitDate());
    // La fecha de actualización se genera automaticamente
    deliveryLine.setLineStatus(LineStatus.PENDING);

    // Actualizar los usuarios creador y actualizador
    deliveryLine.setUserCreator(user);
    deliveryLine.setUserUpdater(user);

    deliveryLine.setLocation(location);
    deliveryLine.setProduct_DeliveryOrder(product_DeliveryOrder);
    deliveryLine.setProduct(product_DeliveryOrder.getProduct());
    deliveryLine.setDeliveryOrder(product_DeliveryOrder.getDeliveryOrder());

    deliveryLineRepository.save(deliveryLine);
    

    // Actualizar la orden de entrega
    DeliveryOrder deliveryOrder = product_DeliveryOrder.getDeliveryOrder();
    // 1° actualizar la fecha limite de deliveryOrder comparando todas las lineas de
    // entrega y tomar el valor con la fecha más cercana que no haya sido entregada
    deliveryOrder.setPriorityDate(getClosestLimitDate(deliveryOrder.getId()));

    // 2° actualizar el estado a PENDING cada vez que se guarde una nueva linea de
    // entrega
    deliveryOrder.setOrderStatus(OrderStatus.PENDING);
    deliveryOrderRepository.save(deliveryOrder);

    // 3° CALCULAR LA SUMATORIA DE LAS CANTIDADES REQUERIDAS DE TODAS LAS LINEAS DE ENTREGA POR ORDEN DE ENTREGA
    Integer totalRequired = deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(id_product_deliveryOrder);

    product_DeliveryOrder.setRequiredQuantityTotal(totalRequired);

    product_DeliveryOrderRepository.save(product_DeliveryOrder);

    // Agregar un registro en la entidad Product_DeliveryOrder_Region
    Integer regionTotal = deliveryLineRepository.sumRequiredByProductDeliveryOrderAndRegion(
        product_DeliveryOrder.getId(),
        location.getRegion().getId());

    Product_DeliveryOrder_Region entity = product_DeliveryOrder_RegionRepository
        .findByProduct_DeliveryOrderIdAndRegionId(
            product_DeliveryOrder.getId(),
            location.getRegion().getId())
        .orElseGet(() -> {
          Product_DeliveryOrder_Region e = new Product_DeliveryOrder_Region();
          e.setProduct_DeliveryOrder(product_DeliveryOrder);
          e.setRegion(location.getRegion());
          return e;
        });

    entity.setRequiredTotalQuantity(regionTotal);
    product_DeliveryOrder_RegionRepository.save(entity);
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
      Pageable pageable) {
    if (deliveryOrderId != null && !deliveryOrderRepository.existsById(deliveryOrderId)) {
      throw new BusinessException(
          ResponseStatus.NOT_FOUND,
          "La orden de entrega no existe");
    }

    Page<DeliveryLine> deliveryLines = deliveryLineRepository.searchAllByDeliveryOrderIdAndParams(
        deliveryOrderId, minRequiredQuantity, maxRequiredQuantity, minLimitDate,
        maxLimitDate, lineStatus, location, pageable);

    List<DeliveryLineListResponse> result = deliveryLines.getContent().stream().map(
      deliveryLine -> DeliveryLineMapper.builder()
      .setDeliveryLine(deliveryLine).buildDeliveryLineListResponse()
    ).toList();

    PageResponse<DeliveryLineListResponse> pageResponse = new PageResponse<>(
      result,
        deliveryLines.getNumber(),
        deliveryLines.getSize(),
        deliveryLines.getTotalElements(),
        deliveryLines.getTotalPages(),
        deliveryLines.isFirst(),
        deliveryLines.isLast()
    );

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

    return  DeliveryLineMapper.builder().setDeliveryLine(deliveryLine).buildDeliveryLineDetailsResponse();
  }


  // ESTE MÉTODO SIRVE PARA CAMBIAR LA CANTIDAD REQUERIDA Y LA FECHA LIMITE
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

    Integer oldRequired = deliveryLine.getRequiredQuantity();
    Integer newRequired = deliveryLineUpdateRequest.getRequiredQuantity();

    if (Objects.equals(oldRequired, newRequired)) {
      throw new BusinessException(ResponseStatus.CONFLICT,
          "No ha cambiado la cantidad requerida");
    }

    // Balance entre la cantidad original anterior y la nueva cantidad (puede ser un
    // numero positivo o negativo)
    Integer balance = newRequired - oldRequired;

    deliveryLine.setRequiredQuantity(newRequired);
    
    deliveryLine.setLimitDate(deliveryLineUpdateRequest.getLimitDate());
    deliveryLine.setUserUpdater(user);


    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryOrder_id)
        .orElseThrow(
            () -> new BusinessException(ResponseStatus.NOT_FOUND, "El pedido de entrega no existe"));

    // DEBE BUSCAR LA RELACION PRODUCTOS - ORDENES DE ENTREGA PARA ACTUALIZAR LA SUMATORIA DE LA CANTIDAD REQUERIDA
    Product_DeliveryOrder product_DeliveryOrder = deliveryLine.getProduct_DeliveryOrder();
    if (product_DeliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Long product_DeliveryOrder_id = product_DeliveryOrder.getId();

    if (product_DeliveryOrder_id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    Product product = product_DeliveryOrder.getProduct();

    if (product == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }


    // Llamar al método auxiliar para actualizar la linea de entrega
    updateLineStatus(deliveryLine);
    deliveryLineRepository.save(deliveryLine);

    // Recalcular la suma de las cantidades requeridas de las lineas de entrega

    // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
    product_DeliveryOrder.setRequiredQuantityTotal(
        deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder_id));

    product_DeliveryOrderRepository.save(product_DeliveryOrder);

    // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
    deliveryOrder.setLimitDate(getClosestLimitDate(deliveryOrder_id));
    deliveryOrderRepository.save(deliveryOrder);

    // RECALCULAR LA SUMATORIA DE CANTIDADES POR REGION
    recalculateProductDeliveryOrderRegions(deliveryLine.getDeliveryOrder().getId());


    Movement  movement = new Movement();
    movement.setQuantity(balance);

    movement.setMovementType(balance > 0 ? MovementType.ALTER : MovementType.CHANGE);

    movement.setComment(deliveryLineUpdateRequest.getComment());

    movement.setProduct(product);
    movement.setUser(user);
    movement.setStockLotReceiver(null);
    movement.setStockLotEmitter(null);
    movement.setDeliveryLine(deliveryLine);

    movementRepository.save(movement);
  }

  // Método para eliminar una linea de entrega (solamente si no hay cantidad entregada o si nunca hubo una relacion con StockLot_DeliveryLine)
  @Override
  public void deleteDeliveryLineById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La linea de entrega no existe"));

    if (deliveryLine == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Long deliveryLineId = deliveryLine.getId();

    // if (deliveryLineId == null) {
    //   throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    // }

    Long deliveryOrderId = deliveryLine.getDeliveryOrder().getId();

    if (deliveryOrderId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(
        deliveryOrderId).orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }


    Product_DeliveryOrder product_DeliveryOrder = product_DeliveryOrderRepository.findById(
        deliveryOrderId).orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La relacion producto-orden de entrega no existe"));

    if (deliveryLine == null || product_DeliveryOrder == null || deliveryOrder == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    } else {
      // SI HAY CANTIDAD ENTREGADA, ENTONCES YA NO SE PODRA ELIMINAR ESTE CAMPO
      if (deliveryLine.getDeliveredQuantity() > 0) {
        throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "No se puede eliminar porque ya hay una cantidad a entregar");
      }

      // Recordar que si hay una relación con StockLot_DeliveryLine no se puede eliminar
      if (deliveryLine.getStockLotDeliveryLines().size() > 0) {
        throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "No se puede eliminar porque ya hay una relacion con StockLot_DeliveryLine");
      }

      // RECORDAR QUE EN SISTEMAS QUE ALMACENAN DATOS HISTORICOS NO SE DEBEN ELIMINAR LOS DATOS, PERO COMO NO SE HA HECHO NINGUNA RELACION CON STOCKLOT_DELIVERYLINE, SE PUEDE ELIMINAR
      deliveryLineRepository.delete(deliveryLine);

      // RECALCULAR LAS CANTIDADES TOTALES EN PRODUCT_DELIVERYORDER
      product_DeliveryOrder.setRequiredQuantityTotal(
          deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(product_DeliveryOrder.getId()));

      product_DeliveryOrderRepository.save(product_DeliveryOrder);

      // RECALCULAR LA FECHA PRIORITARIA DE ENTREGA
      deliveryOrder.setLimitDate(getClosestLimitDate(deliveryOrder.getId()));
      deliveryOrderRepository.save(deliveryOrder);

      // RECALCULAR LA SUMATORIA DE CANTIDADES POR REGION
      recalculateProductDeliveryOrderRegions(deliveryLine.getDeliveryOrder().getId());

      // La linea de entrega no debe tener movimientos
      // TODO: PROBLEMA, NO SE PUEDE ELIMINAR UNA LINEA DE ENTREGA SI FUE EDITADA,
      // PORQUE YA HAY UN MOVIMIENTO
      
      // Operacion para verificar si todas las lineas de entrega de una orden de entrega han sido entregadas, es decir si todas tiene el estado READY
      // TODO: VERIFICAR ESTO
      if (deliveryLineRepository.allLinesAreReady(deliveryOrderId)) {
        deliveryOrder.setOrderStatus(OrderStatus.READY);
      } else {
        deliveryOrder.setOrderStatus(OrderStatus.PENDING);
      }

      deliveryOrderRepository.save(deliveryOrder);
    }
  }

  // @Override
  // public void changePreparationStatusDeliveryLineById(Long id, PreparationStatus preparationStatus, Long id_user){
  //   if (id == null) {
  //     throw new BusinessException(ResponseStatusCodes.INTERNAL_SERVER_ERROR);
  //   }


  //   DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
  //       () -> new BusinessException(ResponseStatusCodes.NOT_FOUND, "La orden de entrega no existe"));

  //   // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
  //   DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
  //   String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

  //   deliveryLine.setPreparationStatus(preparationStatus);
  //   deliveryLine.setUpdatedByUser(username);
  //   deliveryLineRepository.save(deliveryLine);
  // }

  @Override
  public void changeDeliveredStatusDeliveryLineById(Long id, Long id_user) {
    
    if (id == null || id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));


    if (deliveryLine.getLineStatus() != LineStatus.READY) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE, "La linea de entrega no puede ser entregada porque tiene el estado " + deliveryLine.getLineStatus());
    }
    
    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    // DetailUserResponse detailsUserResponse = userService.findUserById(id_user);
    // String username = detailsUserResponse.getFirstname() + " " + detailsUserResponse.getLastname();

    User user = userRepository.findById(id_user).orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    deliveryLine.setLineStatus(LineStatus.DELIVERED);
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);
  }


  // Metodo auxiliar
  // Busca si existe una linea de entrega que pertenezca a esa ubicación y tambien a esa misma orden de entrega
  private void existDeliveryLineByProduct_DeliveryOrder(Long idLocation, Long idProduct, Long idDeliveryOrder) {

    // VERIFICA SI EL MISMO PRODUCTO EXISTE EN ESA MISMA UBICACION
    // SE PUEDE TENER MÁS DE UN PRODUCTO EN ESA MISMA UBICACION
    if (deliveryLineRepository
        .existsByLocationAndProductAndDeliveryOrder(idLocation, idProduct, idDeliveryOrder)) {

      throw new FieldValidation(
          "idLocation",
          "Esta ubicación ya esta en uso");
    }
  }

  // Tomar la fecha mas cercana que no haya sido entregada
  private LocalDateTime getClosestLimitDate(Long idDeliveryOrder) {
    // 1° encontrar todas las lineas de entrega correspondientes a la orden de entrega
    // 2° tomar las fechas limites de cada linea de entrega cuyo estado sea INPROGRESS
    // 3° devolver la fecha más cercana que no haya sido entregada

    return deliveryLineRepository
        .findClosestLimitDate(idDeliveryOrder)
        .orElse(null); // o lanza excepción
    }


    // ESTO ES UN MOVIMIENTO DE CANCELACIÓN
  @Override
  public void changeCanceledStatusDeliveryLineById(Long id, Long id_user) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    if (deliveryLine.getLineStatus() != LineStatus.PENDING || deliveryLine.getLineStatus() != LineStatus.READY) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
          "La linea de entrega no puede ser cancelada porque tiene el estado " + deliveryLine.getLineStatus());
    }

    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    // String username = user.getFirstname() + " " + user.getLastname();

    // IMPLEMENTAR UNA LOGICA PARA REALIZAR ALGO CON LA CANTIDAD REQUERIDA

    // Si una linea de entrega fue cancelada
    // 1° debe descontar la cantidad entregada de la cantidad requerida
    Integer deliveredQuantity = deliveryLine.getDeliveredQuantity();

    deliveryLine.setRequiredQuantity(0);
    deliveryLine.setDeliveredQuantity(0);
    deliveryLine.setPendingQuantity(0);

    // La fecha limite de la linea de entrega debe ser la fecha actual
    deliveryLine.setLimitDate(LocalDateTime.now());

    // 2° debe cambiar el estado de la linea de entrega a CANCELADO, además del
    // usuario que la cancela
    deliveryLine.setLineStatus(LineStatus.CANCELED);
    deliveryLine.setUserUpdater(user);

    // TODO: Probar esta logica para ver si se actualiza el total de la cantidad requerida

    Integer totalRequiredQuantity = deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(id);


    deliveryLineRepository.save(deliveryLine);

    Long deliveryOrderId = deliveryLine.getProduct_DeliveryOrder().getId();

    if (deliveryOrderId == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }
  
      Product_DeliveryOrder product_DeliveryOrder = product_DeliveryOrderRepository.findById(deliveryOrderId).orElseThrow(
          () -> new BusinessException(ResponseStatus.NOT_FOUND, "El product_delivery_order no existe"));

      product_DeliveryOrder.setRequiredQuantityTotal(totalRequiredQuantity);
      product_DeliveryOrderRepository.save(product_DeliveryOrder);

    // 3° podria crear una nuevo StockLot con la cantidad que quedo en la linea de
    // entrega
    StockLot stockLot = new StockLot();
    stockLot.setQuantityReceived(deliveredQuantity);
    stockLot.setQuantityAvailable(deliveredQuantity);
    stockLot.setBatch("Linea de entrega con ID: "  + deliveryLine.getId() + " cancelada");
    stockLot.setProduct(deliveryLine.getProduct());
    stockLot.setZeroStock(false);

    // NOTA: POR DEFECTO EL STOCK QUE SE VA A DEVOLVER VA A PERTENECER A LA MISMA EMPRESA 
    Company firstCompany = companyRepository.findById(1L).orElseThrow(() -> new RuntimeException("No se encontro la empresa"));

    stockLot.setCompany(firstCompany);
    stockLot.setQuantityDelivered(0);
    stockLotRepository.save(stockLot);


        // 4° REGISTRARLO COMO MOVIMIENTO
    Movement movement = new Movement();
    movement.setMovementType(MovementType.CANCELED);
    movement.setQuantity(deliveredQuantity);
    movement.setStockLotReceiver(stockLot);
    movement.setComment("Linea de entrega con ID: " + deliveryLine.getId() + " cancelada");
    movement.setUser(user);
    movement.setDeliveryLine(deliveryLine);
    movementRepository.save(movement);


  }


  @Override
  public void changeMissingStatusDeliveryLineById(Long id, Long id_user) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    DeliveryLine deliveryLine = deliveryLineRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

    // Solamente podra declarar perdida si la linea de entrega se encuentra entregada
    if (deliveryLine.getLineStatus() != LineStatus.DELIVERED) {
      throw new BusinessException(ResponseStatus.DEFAULT_RESOURCE,
          "La linea de entrega no puede ser cancelada porque tiene el estado " + deliveryLine.getLineStatus());
    }

    if (id_user == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    // Obtener el ID del usuario que ha iniciado sesión se obtiene desde los headers
    User user = userRepository.findById(id_user).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

    // String username = user.getFirstname() + " " + user.getLastname();
    // IMPLEMENTAR UNA LOGICA PARA REALIZAR ALGO CON LA CANTIDAD REQUERIDA


    // EN ESTE CASO, DEBE CREAR UNA NUEVA LINEA DE ENTREGA CON LA CANTIDAD REQUERIDA
    DeliveryLine newDeliveryLine = new DeliveryLine();
    newDeliveryLine.setProduct(deliveryLine.getProduct());
    newDeliveryLine.setProduct_DeliveryOrder(deliveryLine.getProduct_DeliveryOrder());
    newDeliveryLine.setRequiredQuantity(deliveryLine.getRequiredQuantity());
    newDeliveryLine.setDeliveredQuantity(0);
    newDeliveryLine.setPendingQuantity(0);
    newDeliveryLine.setLimitDate(deliveryLine.getLimitDate());
    newDeliveryLine.setLineStatus(LineStatus.PENDING);
    newDeliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(newDeliveryLine);

      Long id_product_deliveryOrder = newDeliveryLine.getProduct_DeliveryOrder().getId();
      if (id_product_deliveryOrder == null) {
        throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
      } 


    Product_DeliveryOrder product_DeliveryOrder = product_DeliveryOrderRepository.findById(id_product_deliveryOrder).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El product_delivery_order no existe"));
    
    Integer totalRequiredQuantity = deliveryLineRepository.sumRequiredQuantityByProduct_DeliveryOrder(id);

    product_DeliveryOrder.setRequiredQuantityTotal(totalRequiredQuantity);
    product_DeliveryOrderRepository.save(product_DeliveryOrder);


    // 4° REGISTRARLO COMO MOVIMIENTO
    Movement movement = new Movement();
    movement.setMovementType(MovementType.MISSING);
    movement.setQuantity(deliveryLine.getRequiredQuantity());
    movement.setStockLotReceiver(null);
    movement.setComment("Linea de entrega con ID: " + deliveryLine.getId() + " perdida");
    movement.setUser(user);
    movement.setDeliveryLine(deliveryLine);
    movementRepository.save(movement);



    deliveryLine.setLineStatus(LineStatus.MISSING);
    deliveryLine.setUserUpdater(user);
    deliveryLineRepository.save(deliveryLine);
  }

  // ESTRATEGIA DE ACTUALIZACIÓN
  // MÉTODO AUXILIAR DE REPARACIÓN
  private void recalculateProductDeliveryOrderRegions(Long productDeliveryOrderId) {
    List<Product_DeliveryOrder_Region> regions = product_DeliveryOrder_RegionRepository
        .findAllByProduct_DeliveryOrderId(productDeliveryOrderId);

    for (Product_DeliveryOrder_Region entity : regions) {

      // Solamente hay un campo para la cantidad total requerida
      Integer requiredTotal = deliveryLineRepository.sumRequiredByProductDeliveryOrderAndRegion(
          productDeliveryOrderId,
          entity.getRegion().getId());

      entity.setRequiredTotalQuantity(requiredTotal);
    }

    // TODO: ¿FALTA ACTUALIZAR LA CANTIDAD ENTREGADA?
    if (regions == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR, "No se encontraron regiones para el product_delivery_order");
    }

    product_DeliveryOrder_RegionRepository.saveAll(regions);

  }

  private void updateLineStatus(DeliveryLine line) {

    int required = line.getRequiredQuantity();
    int delivered = line.getDeliveredQuantity();

    if (required > delivered) {
      line.setPendingQuantity(required - delivered);
      line.setLineStatus(LineStatus.PENDING);
      return;
    }

    if (required == delivered) {
      line.setPendingQuantity(0);
      line.setLineStatus(LineStatus.READY);
      return;
    }

    // required < delivered
    line.setPendingQuantity(required - delivered);
    line.setLineStatus(LineStatus.EXCESS);
  }

}
