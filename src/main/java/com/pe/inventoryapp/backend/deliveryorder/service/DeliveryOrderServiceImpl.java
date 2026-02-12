package com.pe.inventoryapp.backend.deliveryorder.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Product_DeliveryOrder_Region;
import com.pe.inventoryapp.backend.deliveryorder.model.mapper.DeliveryOrderMapper;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientListResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Product_DeliveryOrder_RegionRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;
import com.pe.inventoryapp.backend.stocklot.repository.StockLotRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

@Service
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

	@Autowired
	private DeliveryOrderRepository deliveryOrderRepository;

	@Autowired
	private DeliveryLineRepository deliveryLineRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private StockLotRepository stockLotRepository;

	@Autowired
	private MovementRepository movementRepository;

	@Autowired
	private Product_DeliveryOrder_RegionRepository product_DeliveryOrder_RegionRepository;


	private static final long BATCH_START = 10000L;

	@Override
	public void saveDeliveryOrder(DeliveryOrderRequest deliveryOrderRequest, Long id_user) {

		Long id_client = deliveryOrderRequest.getIdClient();

		if (id_user == null || id_client == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		User user = userRepository.findById(id_user).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		User userClient = userRepository.findById(id_client).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "El cliente no existe"));

		// TODO: ESTO ES IMPOSIBLE, SE SABE QUE CADA NUEVO USUARIO CREADO SIEMPRE VA A
		// TENER EL ROL DE USER
		// Verificar que el usuario seleccionado tenga el rol de USER (Cliente)
		// System.out.println(userClient.getRoles().stream().anyMatch(r ->
		// "ROLE_USER".equals(r.getName())));

		// if (!userClient.getRoles().stream().anyMatch(r ->
		// "ROLE_USER".equals(r.getName()))) {
		// throw new BusinessException(ResponseStatus.CONFLICT, "El usuario seleccionado
		// no es un cliente");
		// }

		DeliveryOrder deliveryOrder = new DeliveryOrder();

		deliveryOrder.setLimitDate(deliveryOrderRequest.getLimitDate());
		// La fecha limite prioritaria se establece en null porque aun no hay una fecha
		// de entrega de una linea de entrega
		deliveryOrder.setPriorityDate(null);
		deliveryOrder.setOrderStatus(OrderStatus.PENDING);
		deliveryOrder.setUserCreator(user);
		deliveryOrder.setUserUpdater(user);
		deliveryOrder.setUserClient(userClient);

		DeliveryOrder saved = deliveryOrderRepository.save(deliveryOrder);

		// Este numero debe ser generado automaticamente
		long newBatch = BATCH_START + saved.getId();
		String newBatchString = String.valueOf(newBatch);

		saved.setBatch(newBatchString);
		deliveryOrderRepository.save(saved);
	}

	@Override
	public PageResponse<DeliveryOrderListResponse> findAllDeliveryOrdersByParams(
			String batch,
			LocalDateTime startDate,
			LocalDateTime endDate,
			String userClientName,
			OrderStatus status,
			Pageable pageable) {
		Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllByParams(batch, startDate, endDate, status,
				userClientName, pageable);

		List<DeliveryOrderListResponse> result = deliveryOrders.getContent().stream().map(
				deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
						.buildDeliveryOrderListResponse())
				.toList();

		PageResponse<DeliveryOrderListResponse> pageResponse = new PageResponse<>(
				result,
				deliveryOrders.getNumber(),
				deliveryOrders.getSize(),
				deliveryOrders.getTotalElements(),
				deliveryOrders.getTotalPages(),
				deliveryOrders.hasNext(),
				deliveryOrders.hasPrevious());

		return pageResponse;
	}

	@Override
	public PageResponse<DeliveryOrderListResponse> findAllActiveDeliveryOrdersByParams(
			String batch,
			LocalDateTime startDate,
			LocalDateTime endDate,
			String userClientName,
			Pageable pageable) {
		Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllActiveByParams(batch, startDate, endDate,
				userClientName, pageable);

		List<DeliveryOrderListResponse> result = deliveryOrders.getContent().stream().map(
				deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
						.buildDeliveryOrderListResponse())
				.toList();

		PageResponse<DeliveryOrderListResponse> pageResponse = new PageResponse<>(
				result,
				deliveryOrders.getNumber(),
				deliveryOrders.getSize(),
				deliveryOrders.getTotalElements(),
				deliveryOrders.getTotalPages(),
				deliveryOrders.hasNext(),
				deliveryOrders.hasPrevious());

		return pageResponse;
	}

	@Override
	public PageResponse<DeliveryOrderClientListResponse> findAllDeliveryOrdesByClientId(Long id, String batch,
			LocalDateTime startDate, LocalDateTime endDate, OrderStatus status, Pageable pageable) {

		if (id == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllByUserClientId(id, batch, startDate,
				endDate,
				status, pageable);

		List<DeliveryOrderClientListResponse> result = deliveryOrders.getContent().stream().map(
				deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
						.buildDeliveryOrderClientListResponse())
				.toList();

		PageResponse<DeliveryOrderClientListResponse> pageResponse = new PageResponse<>(
				result,
				deliveryOrders.getNumber(),
				deliveryOrders.getSize(),
				deliveryOrders.getTotalElements(),
				deliveryOrders.getTotalPages(),
				deliveryOrders.hasNext(),
				deliveryOrders.hasPrevious());

		return pageResponse;

	}

	@Override
	public DeliveryOrderDetailsResponse findDeliveryOrderById(Long id) {
		if (id == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
			throw new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega ha sido cancelada");
		}

		return DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
				.buildDeliveryOrderDetailsResponse();
	}

	@Override
	public DeliveryOrderClientDetailsResponse findDeliveryOrderByIdAndValidateUserClient(Long id, Long id_user) {
		if (id == null || id_user == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
			throw new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega ha sido cancelada");
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		boolean isOnlyUserRole = user.getRoles().size() == 1 &&
				user.getRoles().stream()
						.anyMatch(r -> "ROLE_USER".equals(r.getName()));

		// System.out.println(isOnlyUserRole);
		// System.out.println(user.getRoles());
		// System.out.println(user.getRoles().size());

		// System.out.println(user.getId());
		// System.out.println(deliveryOrder.getUserClient().getId());
		// System.out.println(deliveryOrder.getUserClient().getId().equals(user.getId()));

		// Si el usuario tiene solamente el rol de USER, entonces solamente podra ver
		// una orden cuyo userClient sea el mismo usuario que ha iniciado sesión
		if (isOnlyUserRole) {
			if (deliveryOrder.getUserClient().getId().equals(user.getId())) {
				return DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
						.buildDeliveryOrderClientDetailsResponse();
			} else {
				throw new BusinessException(
						ResponseStatus.CONFLICT,
						"El usuario no es el cliente de la orden de entrega");
			}
		} else {
			throw new BusinessException(
					ResponseStatus.CONFLICT,
					"El usuario no tiene el rol de cliente");
		}

	}

	@Override
	public void changeLimitDate(Long id, LocalDateTime limitDate, Long id_user) {

		if (id == null || limitDate == null || id_user == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		deliveryOrder.setLimitDate(limitDate);
		deliveryOrder.setUserUpdater(user);

		deliveryOrderRepository.save(deliveryOrder);
	}

	// TODO: PENDIENTE IMPLEMENTAR UNA LOGICA PARA CAMBIAR EL ESTADO DE LA ORDEN
	// SOLAMENTE SI TODAS LAS LINEAS DE ENTREGAS TIENEN EL ESTADO READY

	// @Override
	// public void changeStatusOrderToCanceledById(Long id, Long id_user) {
	// if (id == null || id_user == null) {
	// throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
	// }

	// User user = userRepository.findById(id_user)
	// .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El
	// usuario no existe"));

	// DeliveryOrder deliveryOrder =
	// deliveryOrderRepository.findById(id).orElseThrow(
	// () -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no
	// existe"));

	// if (deliveryOrder.getOrderStatus() != OrderStatus.PENDING
	// && deliveryOrder.getOrderStatus() != OrderStatus.READY &&
	// deliveryOrder.getOrderStatus() != OrderStatus.CANCELED) {
	// throw new BusinessException(ResponseStatus.CONFLICT,
	// "La orden de entrega no puede ser cancelada");
	// }

	// deliveryOrder.setOrderStatus(OrderStatus.CANCELED);
	// deliveryOrder.setUserUpdater(user);
	// deliveryOrderRepository.save(deliveryOrder);
	// }

	@Override
	@Transactional
	public void processDeliveryOrderCancellation(Long id, Long id_user) {
		if (id == null || id_user == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (deliveryOrder.getOrderStatus() == OrderStatus.DELIVERED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ya ha sido entregada");
		}

		if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ha sido cancelada");
		}

		// ESTE MÉTODO DEBE "BORRAR" UNA ORDEN DE ENTREGA BAJO CIERTAS CONDICIONES
		// SI LO BORRA, DEBE CREAR UN NUEVO LOTE DE STOCK CON LA SUMATORIA DE LAS
		// CANTIDADES ENTREGADAS DE LAS LINEAS DE ENTREGA QUE ESTAN EN MODO READY,
		// PENDING Y EXCEEDED, CAMBIA EL ESTADO DE ESAS LINEAS A CANCELED

		// SI HAY LINEAS DE ENTREGA QUE TIENEN EL ESTADO DELIVERED, NO DEBEN SER
		// ALTERADAS Y NO LA ORDEN NO PODRA TENER EL ESTADO CANCELED

		// RECORDAR QUE MISSING ES EL ESTADO CUANDO UNA LINEA DE ENTREGA SE PIERDE LUEGO
		// DE SER ENTREGADA, SI TIENE EL ESTADO MISSING TAMPOCO PODRÁ SER CANCELADA

		List<DeliveryLine> deliveryLines = deliveryLineRepository.findAllByDeliveryOrderId(id);

		boolean hasDeliveredOrMissing = deliveryLines.stream()
				.anyMatch(dl -> dl.getLineStatus() == LineStatus.DELIVERED ||
						dl.getLineStatus() == LineStatus.MISSING);

		// SI NO HAY NINGUNA LINEA DE ENTREGA
		if (deliveryLines.isEmpty()) {
			throw new BusinessException(ResponseStatus.CONFLICT, "No hay lineas de entrega");
		}

		// Estados permitidos que debe tener la linea de entrega
		EnumSet<LineStatus> cancelableStates = EnumSet.of(LineStatus.READY, LineStatus.PENDING, LineStatus.EXCEEDED);

		// NOTA: UNA ORDEN DE ENTREGA TIENE VARIOS PRODUCTOS A LA VEZ
		// Nueva cantidad que se almacenara en el almacen
		// Mapeo de los productos y sus cantidades
		Map<Long, Integer> newStockRestoredQuantity = new HashMap<>();
		// Integer restoredQuantity = 0;

		// Solamente alterara el estado de las lineas de entrega que tengan los estados
		// mencionados
		// Las demás lineas de entrega no se alteran (estado MISSING, DELIVERED,
		// CANCELED)
		for (DeliveryLine deliveryLine : deliveryLines) {
			if (cancelableStates.contains(deliveryLine.getLineStatus())) {

				Long productId = deliveryLine.getProduct().getId();

				// Verificar que si no existe el key con el id del producto en
				// newStockRestoredQuantity
				if (!newStockRestoredQuantity.containsKey(productId)) {
					newStockRestoredQuantity.put(productId, deliveryLine.getDeliveredQuantity());
				} else {
					// Si el key existe, se suman las cantidades
					newStockRestoredQuantity.put(productId,
							newStockRestoredQuantity.get(productId) + deliveryLine.getDeliveredQuantity());
				}

				deliveryLine.setLineStatus(LineStatus.CANCELED);
				deliveryLine.setDeliveredQuantity(0);
				deliveryLine.setPendingQuantity(deliveryLine.getRequiredQuantity());
				deliveryLine.setUserUpdater(user);

				deliveryLineRepository.save(deliveryLine);

				// CREAR MOVIMIENTOS POR CADA LINEA DE ENTREGA
				Movement movement = new Movement();
				movement.setQuantity(deliveryLine.getDeliveredQuantity());
				movement.setComment("Se cancelo la linea de entrega");
				movement.setDeliveryLine(deliveryLine);
				movement.setProduct(deliveryLine.getProduct());
				movement.setUser(user);
				movement.setStockLotReceiver(null);
				movement.setStockLotReceiver(null);
				movement.setMovementType(MovementType.CANCELED);

				movementRepository.save(movement);
			}
		}

		// Verificar que si hay lineas de entrega que tiene el estado DELIVERED o
		// MISSING, ya no se podra cambiar el estado a CANCELED
		if (hasDeliveredOrMissing) {
			deliveryOrder.setOrderStatus(OrderStatus.DELIVERED);
		} else {
			deliveryOrder.setOrderStatus(OrderStatus.CANCELED);
		}

		// CREAR VARIOS LOTES DE ENTREGA POR CADA UNO DE LOS PRODUCTOS DEVUELTOS

		Company company = companyRepository.findById(1L).get();

		// Guardar el lote de stock
		for (Map.Entry<Long, Integer> entry : newStockRestoredQuantity.entrySet()) {

			Long productId = entry.getKey();

			if (productId == null) {
				throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
			}

			Product product = productRepository.findById(productId)
					.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe"));

			Integer restoredQuantity = entry.getValue();

			// CREAR EL LOTE DE STOCK
			StockLot stockLot = new StockLot();

			// Obtiene la fecha de hoy por partes
			LocalDateTime now = LocalDateTime.now();
			String date = now.getDayOfMonth() + "/" + now.getMonthValue() + "/" + now.getYear();
			String time = now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();

			String batch = "LOT-" + product.getName().replace(" ", "-") + "-" + date + "-" + time;

			stockLot.setBatch(batch);
			stockLot.setQuantityReceived(restoredQuantity);
			stockLot.setQuantityAvailable(restoredQuantity);
			stockLot.setQuantityDelivered(0);
			stockLot.setQuantityLost(0);
			stockLot.setQuantityRecovered(0);
			stockLot.setZeroStock(false);
			stockLot.setProduct(product);
			stockLot.setCompany(company);
			stockLotRepository.save(stockLot);

			// CREAR UN NUEVO MOVIMIENTO POR CADA PRODUCTO

			Movement movement = new Movement();
			movement.setQuantity(restoredQuantity);
			movement.setComment("Devolución");
			movement.setStockLotReceiver(stockLot);
			movement.setProduct(product);
			movement.setUser(user);
			movement.setDeliveryLine(null);
			movement.setMovementType(MovementType.CANCELED);

			movementRepository.save(movement);

		}

		// TODO: ALTERAR EL CAMPO DE PRODUCT_DELIVERYORDER_REGION
		recalculateProductDeliveryOrderRegions(deliveryOrder.getId());
			deliveryOrder.setUserUpdater(user);
			deliveryOrderRepository.save(deliveryOrder);

	}


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

    if (regions == null) {
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR,
          "No se encontraron regiones para el product_delivery_order");
    }

    product_DeliveryOrder_RegionRepository.saveAll(regions);

  }

	@Override
	@Transactional
	public void markDeliveryOrderAsDelivered(Long id, Long id_user) {
		if (id == null || id_user == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (deliveryOrder.getOrderStatus() == OrderStatus.DELIVERED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ya ha sido entregada");
		}

		if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ha sido cancelada");
		}

		List<DeliveryLine> deliveryLines = deliveryLineRepository.findAllByDeliveryOrderId(id);

		if (deliveryLines.isEmpty()) {
			throw new BusinessException(ResponseStatus.CONFLICT, "No hay líneas de entrega");
		}

		// VERIFICA QUE TODAS LAS LINEAS DE ENTREGA ASOCIADAS A ESTA ORDEN DE ENTREGA
		// TENGAN EL ESTADO READY, ADEMÁS DEL ESTADO DELIVERED
		assertAllLinesInAllowedStates(
				deliveryLines,
				EnumSet.of(LineStatus.READY, LineStatus.DELIVERED, LineStatus.CANCELED, LineStatus.MISSING));

		// Ninguna linea de entrega debe tener el estado "PENDING", "EXCEEDED"
		// Estados permitidos: "READY", "DELIVERED", "CANCELED", "MISSING"

		deliveryLines.stream()
				.filter(l -> l.getLineStatus() == LineStatus.READY)
				.forEach(l -> {
					l.setLineStatus(LineStatus.DELIVERED);
					l.setUserUpdater(user);

					Movement movement = new Movement();
					movement.setQuantity(l.getDeliveredQuantity());
					movement.setComment("Se entrego la linea de entrega");
					movement.setDeliveryLine(l);
					movement.setProduct(l.getProduct());
					movement.setUser(user);
					movement.setStockLotEmitter(null);			
					movement.setStockLotReceiver(null);
					movement.setMovementType(MovementType.DELIVERED);
					movementRepository.save(movement);
				});

		

		boolean allCompleted = deliveryLines.stream()
				.allMatch(l -> l.getLineStatus() == LineStatus.DELIVERED ||
						l.getLineStatus() == LineStatus.CANCELED ||
						l.getLineStatus() == LineStatus.MISSING);

		if (allCompleted) {
			deliveryOrder.setOrderStatus(OrderStatus.DELIVERED);
			deliveryOrder.setUserUpdater(user);
			deliveryOrderRepository.save(deliveryOrder);
		}

		// CONSTRUIR UN METODO PARA ACTUALIZAR EL ESTADO DE UNA ORDEN DE ENTREGA DE
		// FORMA AUTOMATICA CUANDO TODAS LAS LINEAS DE ENTREGA TENGAN EL ESTADO
		// DELIVERED, EN DELIVERYLINESERVICE

		// ESTO CAMBIARA EL ESTADO DE TODAS LAS LINEAS QUE TENGAN LOS ESTADOS
		// ESPECIFICADOS
		// A DELIVERED
		// changeDeliveryLinesStatusByDeliveryOrderId(id,
		// Arrays.asList(LineStatus.READY));

		// deliveryOrder.setOrderStatus(OrderStatus.DELIVERED);
		// deliveryOrder.setUserUpdater(user);
		// deliveryOrderRepository.save(deliveryOrder);
	}

	// Verifica que todas las lineas de entrega que pertenecen a una orden de
	// entrega tenga el estado lineStatus, si lo tienen no debe devolver nada, de lo
	// contrario un mensaje de error
	private void assertAllLinesInAllowedStates(
			List<DeliveryLine> lines,
			Set<LineStatus> allowedStates) {
		boolean invalid = lines.stream()
				.anyMatch(l -> !allowedStates.contains(l.getLineStatus()));

		if (invalid) {
			throw new BusinessException(
					ResponseStatus.CONFLICT,
					"No puedes entregar esta orden de entrega");
		}
	}

}
